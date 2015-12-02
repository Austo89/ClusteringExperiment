/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clusteringexperiment;

import java.util.*;
import java.math.*;

/**
 *
 * @author Austo89
 */
public class KMeans {

    ArrayList<ArrayList<Integer>> clusters;
    ArrayList<double[]> mus;
    ArrayList<double[]> data;
    int K;

    public KMeans(ArrayList<double[]> in_data, int in_K) {
        data = in_data;
        K = in_K;
        clusters = new ArrayList();
        mus = new ArrayList();
    }

    public void cluster() {
        Random rand = new Random();

        for (int j = 0; j < K; j++) {
            int start_point = Math.abs(rand.nextInt()) % data.size();
            double[] temp_mu = new double[data.get(0).length];
            for (int i = 0; i < data.get(0).length; i++) {
                temp_mu[i] = (data.get(start_point)[i]);
            }
            mus.add(temp_mu);
        }

        //find initial clustering
        double prev_error = findClustering();
        double delta_error = Double.MAX_VALUE - prev_error;

        //loop until we find optimal mu values
        while (delta_error > .0001) {
            findMus();

            double new_error = findClustering();
            delta_error = Math.abs(prev_error - new_error);
            prev_error = new_error;
            printMus();
        }
    }

//////////////////////////////////////////////////////////////
// findClustering:
//   Given a set of Mu values, find a new set of cluster for the data
//////////////////////////////////////////////////////////////
    private double findClustering() {
        //clear old clusters
        ArrayList<ArrayList<Integer>> refresh = new ArrayList();
        clusters = refresh;

        for (int j = 0; j < K; j++) {

            ArrayList inner_array = new ArrayList();
            clusters.add(inner_array);
        }

        //associate data points with the mu values
        //look at every data point
        double error_sum = 0;
        for (int i = 0; i < data.size(); i++) {
            //compare it to every mu, to find the cluster it belongs to
            int index = 0;
            double min_dist = Double.MAX_VALUE;
            for (int j = 0; j < K; j++) {
                //find distance from mu
                double dist = Double.MAX_VALUE;
                double d_sum = 0;
                for (int k = 0; k < data.get(0).length; k++) {
                    d_sum += (mus.get(j)[k] - data.get(i)[k]) * (mus.get(j)[k] - data.get(i)[k]);
                }
                dist = Math.sqrt(d_sum);

                //check if this mu is the best so far
                //update index and minimum distance accordingly
                if (dist < min_dist) {
                    min_dist = dist;
                    index = j;
                }
            }

            // add the data point to its cluster
            clusters.get(index).add(i);
            error_sum += min_dist * min_dist;
        }
        return error_sum;
    }

///////////////////////////////////////////
// findMus:
//    Find the average points of the current clusters
///////////////////////////////////////////
    private void findMus() {
        //clear old Mus
        ArrayList<double[]> refresh = new ArrayList();
        mus = refresh;

        //calculate new cluster mus
        //there are K clusters, index j
        for (int j = 0; j < K; j++) {
            //vector of dimensions for mu
            double[] temp_vec = new double[data.get(0).length];

            //calculate the average of each dimension, i-th dimension over cluster x's
            for (int i = 0; i < data.get(0).length; i++) {
                //dimension total
                double xi_sum = 0;
                //grab each xi from data point in clusters
                for (int k = 0; k < clusters.get(j).size(); k++) {
                    //cluster j, index k in cluster, for dimension xi
                    //xi_sum += data[clusters[j][k]][i];
                    xi_sum += data.get(clusters.get(j).get(k))[i];
                }

                //push back the average of this dimension
                //temp_vec.push_back(xi_sum / (double) clusters[j].size());
                temp_vec[i] = xi_sum / (double) clusters.get(j).size();
            }

            mus.add(temp_vec);
        }
    }
    
    private void printMus(){
        for(int i = 0; i < mus.size(); i++){
            System.out.print("Mu " + i + ": ");
            for (int j = 0; j < mus.get(0).length;j++){
                System.out.print(mus.get(i)[j]);
                System.out.print(", ");
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
