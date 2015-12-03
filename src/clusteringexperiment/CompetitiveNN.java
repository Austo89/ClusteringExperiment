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
                t_order.add(i);
            }

            while (!t_order.isEmpty()) {
                int temp = Math.abs(rand.nextInt()) % t_order.size();
                order.add(t_order.get(temp));
                t_order.remove(t_order.get(temp));
            }

            //run through the points to adjust weights and find clusters
            for (int j = 0; j < order.size(); j++) {
                System.out.println("data point " + j);
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
            }
        }

    }
}
