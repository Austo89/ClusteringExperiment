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
public class DBScan {
    ArrayList<DBDataPoint> data;
    double theta;
    int minPts;
    ArrayList<ArrayList<Integer>> clusters;
    
    public DBScan(ArrayList<double[]> in_data, double in_theta, int in_minPts){
        theta = in_theta;
        minPts = in_minPts;
        data = new ArrayList();
        clusters = new ArrayList();
        
        for(int i = 0; i < in_data.size(); i++){
            data.add(new DBDataPoint(in_data.get(i)));
        }
    }
    
    public void cluster(){
        //label points based on theta and min_pts
        for(int i = 0; i < data.size() - 1; i++){
            //loop over the remaining points, don't double check a point
            for(int j = i+1; j < data.size(); j++){
                double dist = Double.MAX_VALUE;
                double d_sum = 0;
                for (int k = 0; k < data.get(0).location.length; k++) {
                    d_sum += (data.get(j).location[k] - data.get(i).location[k]) 
                            * (data.get(j).location[k] - data.get(i).location[k]);
                }
                dist = Math.sqrt(d_sum);
                
                //if less than threshhold theta, add to neighborhoods, increase degree
                if (dist < theta){
                    data.get(i).neighbors.add(j);
                    data.get(j).neighbors.add(i);
                    
                    data.get(i).degree++;
                    data.get(j).degree++;
                }
            }
            
            //check if it's a core point
            if(data.get(i).degree >= minPts){
                data.get(i).ptType = 2;
                
                //update all points to border points, as long as they're not already cores or borders
                for(int j = 0; j < data.get(i).neighbors.size();j++){
                    int temp = data.get(i).neighbors.get(j);
                    if (data.get(temp).ptType < 1){
                        data.get(temp).ptType = 1;
                    }
                }
            }
        }
        
        // label the clusters
        int currentCluster = 0;
        for (int i = 0; i < data.size(); i++){
            if (data.get(i).ptType == 2){
                if(data.get(i).cluster == -1){
                    clusters.add(new ArrayList());
                    recLabel(currentCluster,i);
                    currentCluster++;
                }
            }
        }
        
        printClusters();
    }
    
    public void printClusters(){
        for(int i = 0; i < clusters.size();i++){
            System.out.print("cluster " + i + ": ");
            for(int j = 0; j < clusters.get(i).size();j++){
                System.out.println(clusters.get(i).get(j) + " ");
            }
        }
    }
    
    private void recLabel(int label, int index){
        data.get(index).cluster = label;
        data.get(index).visited = true;
        clusters.get(label).add(index);
        for(int i = 0; i < data.get(index).neighbors.size();i++){
            if(data.get(data.get(index).neighbors.get(i)).visited == false){
                
                recLabel(label,data.get(index).neighbors.get(i));
            }
        }
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
                        double[] pt1 = data.get(clusters.get(i).get(j)).location;
                        double[] pt2 = data.get(clusters.get(i).get(k)).location;

                        //double dist = Double.MAX_VALUE;
                        double d_sum = 0;
                        for (int l = 0; l < pt1.length; l++) {
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
                            double[] pt1 = data.get(clusters.get(i).get(j)).location;
                            double[] pt2 = data.get(clusters.get(n).get(k)).location;

                            //double dist = Double.MAX_VALUE;
                            double d_sum = 0;
                            for (int l = 0; l < pt1.length; l++) {
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
    
    //private sub class defining data point attributes
    private class DBDataPoint{
        public double[] location;
        public ArrayList<Integer> neighbors;
        public int degree;
        public int ptType;
        public int cluster;
        public boolean visited;
        
        public DBDataPoint(double[] in_loc){
            location = in_loc;
            neighbors = new ArrayList();
            degree = 0;
            ptType = 0;
            cluster = -1;
            visited = false;
        }
    }
}
