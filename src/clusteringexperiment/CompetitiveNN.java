/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clusteringexperiment;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Austo89
 */
public class CompetitiveNN {

    ArrayList<ArrayList<Integer>> clusters;
    ArrayList<double[]> mus;
    ArrayList<double[]> data;
    int K;

    public CompetitiveNN(ArrayList<double[]> in_data, int in_K) {
        data = in_data;
        K = in_K;

        Random rand = new Random();

        // assuming normalized data between -1 and 1
        // create random starting weights
        mus = new ArrayList();
        for (int j = 0; j < K; j++) {
            int start_point = Math.abs(rand.nextInt()) % data.size();
            double[] temp_mu = new double[data.get(0).length];
            for (int i = 0; i < data.get(0).length; i++) {
                temp_mu[i] = (data.get(start_point)[i]);
            }
            mus.add(temp_mu);
        }

        //initialize cluster containers
        clusters = new ArrayList();
        for (int j = 0; j < K; j++) {
            clusters.add(new ArrayList());
        }
    }

    public void cluster() {

        //run the points through the neural network n times
        for (int i = 0; i < 1; i++) {
            //create a random ordering
            ArrayList<Integer> order = new ArrayList();
            ArrayList<Integer> t_order = new ArrayList();
            Random rand = new Random();
            for (int j = 0; j < data.size(); j++) {
                t_order.add(j);
            }

            while (!t_order.isEmpty()) {
                int temp = Math.abs(rand.nextInt()) % t_order.size();
                order.add(t_order.get(temp));
                t_order.remove(t_order.get(temp));
            }

            //run through the points to adjust weights and find clusters
            for (int j = 0; j < order.size(); j++) {
                //System.out.println("data point " + j);
                double best = Double.MAX_VALUE;
                int best_mu = 0;
                int ind = order.get(j);
                
                //feed the input into the nodes
                for (int k = 0; k < K; k++) {

                    double dist = Double.MAX_VALUE;
                    double d_sum = 0;
                    for (int l = 0; l < data.get(0).length; l++) {
                        d_sum += (mus.get(k)[l] - data.get(ind)[l]) * (mus.get(k)[l] - data.get(ind)[l]);
                    }
                    dist = Math.sqrt(d_sum);
                    
                    //check if it's the best
                    if (dist < best){
                        best = dist;
                        best_mu = k;
                    }
                }
                //add to this cluster
                clusters.get(best_mu).add(ind);
                
                //adjust weights toward this point
                for(int l = 0; l < data.get(0).length; l++){
                    double update = mus.get(best_mu)[l] - data.get(ind)[l];
                    if (update != 0){
                        // minimize update if the point is already servicing
                        // many data points
                        update = update / (clusters.get(best_mu).size());
                    }
                    mus.get(best_mu)[l] += update;
                }
                
                printMus();
            }
        }
        
        printClusters();
    }
    
    public Eval evaluate() {
        Eval eval = new Eval();

        //calculate cohesion
        double avgCohesionTotal = 0;
        double avgCohesionCluster = 0;
        double cohesSum = 0;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j < clusters.get(i).size(); j++) {
                double min = Double.MAX_VALUE;
                for (int k = 0; k < clusters.get(i).size(); k++) {
                    double curr = 0;
                    if (k == j) {
                        curr = Double.MAX_VALUE;
                    } else {
                        double[] pt1 = data.get(clusters.get(i).get(j));
                        double[] pt2 = data.get(clusters.get(i).get(k));

                        //double dist = Double.MAX_VALUE;
                        double d_sum = 0;
                        for (int l = 0; l < data.get(0).length; l++) {
                            d_sum += (pt1[l] - pt2[l]) * (pt1[l] - pt2[l]);
                        }
                        curr = Math.sqrt(d_sum);

                    }
                    if (curr < min) {
                        min = curr;
                    }
                }
                cohesSum += min;
            }
            avgCohesionCluster = cohesSum / clusters.get(i).size();
            avgCohesionTotal += avgCohesionCluster;
        }
        avgCohesionTotal = avgCohesionTotal / clusters.size();
        eval.cohesion = avgCohesionTotal;

        //calculate average separation
        double avgSeparation = 0;
        double separationCluster = 0;

        //every cluster
        for (int i = 0; i < clusters.size(); i++) {
            //every point in that cluster
            //find minDist for this cluster to another
            double minDist = Double.MAX_VALUE;
            for (int j = 0; j < clusters.get(i).size(); j++) {
                //every other cluster
                for (int n = 0; n < clusters.size(); n++) {
                    if (i == n) {
                        //don't look at yourself
                    } else {
                        //every point in the other clusters
                        for (int k = 0; k < clusters.get(n).size(); k++) {
                            double curr = 0;
                            double[] pt1 = data.get(clusters.get(i).get(j));
                            double[] pt2 = data.get(clusters.get(n).get(k));

                            //double dist = Double.MAX_VALUE;
                            double d_sum = 0;
                            for (int l = 0; l < data.get(0).length; l++) {
                                d_sum += (pt1[l] - pt2[l]) * (pt1[l] - pt2[l]);
                            }
                            curr = Math.sqrt(d_sum);
                            
                            if (curr < minDist){
                                minDist = curr;
                            }
                        }
                    }
                }
            }
            avgSeparation += minDist;
        }
        avgSeparation = avgSeparation / clusters.size();
        eval.separation = avgSeparation;
        
        return eval;
    }
    
    public void printClusters(){
        for(int i = 0; i < clusters.size();i++){
            System.out.print("cluster " + i + ": ");
            for(int j = 0; j < clusters.get(i).size();j++){
                System.out.println(clusters.get(i).get(j) + " ");
            }
        }
    }
    
    private void printMus() {
        for (int i = 0; i < mus.size(); i++) {
            System.out.print("Mu " + i + ": ");
            for (int j = 0; j < mus.get(0).length; j++) {
                System.out.print(mus.get(i)[j]);
                System.out.print(", ");
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
