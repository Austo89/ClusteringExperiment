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
        int currentCluster = 1;
        for (int i = 0; i < data.size(); i++){
            if (data.get(i).ptType == 2){
                if(data.get(i).cluster == 0){
                    //data.get(i).cluster = currentCluster;
                    recLabel(currentCluster,i);
                    currentCluster++;
                }
            }
        }
    }
    
    private void recLabel(int label, int index){
        data.get(index).cluster = label;
        for(int i = 0; i < data.get(index).neighbors.size();i++){
            if(data.get(data.get(index).neighbors.get(i)).visited == false){
                recLabel(label,data.get(index).neighbors.get(i));
            }
        }
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
            cluster = 0;
            visited = false;
        }
    }
}
