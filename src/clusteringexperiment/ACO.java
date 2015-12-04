
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
public class ACO {

    ArrayList<Ant> ants;
    ArrayList<ArrayList<Integer>> clusters;
    ArrayList<double[]> data;
    int[][] grid;
    int iterations;
    double basePickUp;
    int[] marker;

    public ACO(ArrayList<double[]> in_data, int ant_num, int in_iterations, double in_basePickUp) {
        Random rand = new Random();

        data = in_data;
        iterations = in_iterations;
        clusters = new ArrayList();
        basePickUp = in_basePickUp;

        //give approximately 4x the number of spaces needed
        double gridSizeD = Math.sqrt(data.size() * 4);
        int gridSize = (int) Math.round(gridSizeD);
        grid = new int[gridSize][gridSize];

        //initialize ant positions
        ants = new ArrayList();
        for (int i = 0; i < ant_num; i++) {
            int rX = Math.abs(rand.nextInt()) % gridSize;
            int rY = Math.abs(rand.nextInt()) % gridSize;
            Ant drone = new Ant(rX, rY);
            ants.add(drone);
        }

        //initialize grid to empty
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = -1;
            }
        }

        //place data on the grid randomly
        for (int i = 0; i < data.size(); i++) {
            boolean placed = false;
            while (!placed) {
                int rX = Math.abs(rand.nextInt()) % gridSize;
                int rY = Math.abs(rand.nextInt()) % gridSize;
                if (grid[rX][rY] == -1) {
                    grid[rX][rY] = i;
                    placed = true;
                }
            }
        }

        marker = new int[data.size()];
    }

    public void cluster() {

        Random rand = new Random();

        //run some iterations
        for (int b = 0; b < iterations; b++) {

            //for all ants in population
            for (int a = 0; a < ants.size(); a++) {
                //best...pun...EVAR
                Ant currAnt = ants.get(a);

                //pick up or drop
                if (currAnt.dataIndex == -1 && grid[currAnt.xloc][currAnt.yloc] != -1) {
                    //get the data point
                    double[] pt = data.get(grid[currAnt.xloc][currAnt.yloc]);
                    //compare it to it's friends
                    double add = neighborCompare(pt, currAnt.xloc, currAnt.yloc);
                    double prob = rand.nextDouble();
                    if (prob < (1 - basePickUp) - add) {
                        currAnt.dataIndex = grid[currAnt.xloc][currAnt.yloc];
                        grid[currAnt.xloc][currAnt.yloc] = -1;
                    }
                } else if (currAnt.dataIndex != -1 && grid[currAnt.xloc][currAnt.yloc] == -1) {
                    //get the data point
                    double[] pt = data.get(currAnt.dataIndex);
                    //compare it to it's friends
                    double add = neighborCompare(pt, currAnt.xloc, currAnt.yloc);
                    double prob = rand.nextDouble();
                    if (prob < basePickUp + add) {
                        grid[currAnt.xloc][currAnt.yloc] = currAnt.dataIndex;
                        currAnt.dataIndex = -1;
                    }
                }

                //move around
                boolean moved = false;
                while (!moved) {
                    double move = rand.nextDouble();

                    if (move < 0.25 && currAnt.xloc - 1 >= 0) {
                        currAnt.xloc--;
                        moved = true;
                    } else if (move < 0.5 && currAnt.yloc - 1 >= 0) {
                        currAnt.yloc--;
                        moved = true;
                    } else if (move < 0.75 && currAnt.yloc + 1 < grid.length) {
                        currAnt.yloc++;
                        moved = true;
                    } else if (currAnt.xloc + 1 < grid.length) {
                        currAnt.xloc++;
                        moved = true;
                    }
                }
            }
            
            if (b % 500000 == 0){
                printGrid();
            }
        }

        //mark the clusters
        int clusterLabel = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] != -1) {
                    int ind = grid[i][j];
                    if (marker[ind] == 0) {
                        marker[ind] = 1;
                        clusters.add(new ArrayList());
                        clusters.get(clusterLabel).add(ind);
                        recMark(clusterLabel, i, j);
                        clusterLabel++;
                    }
                }
            }
        }
        
        //printClusters();
        //System.out.println(clusters.size());
        
    }

    public double neighborCompare(double[] pt, int x, int y) {
        double addProb = 0;
        double theta = .8;
        double alpha = 1.0;

        if (x - 1 >= 0) {
            if (grid[x - 1][y] != -1) {
                double[] pt2 = data.get(grid[x - 1][y]);

                //double dist = Double.MAX_VALUE;
                double d_sum = 0;
                for (int l = 0; l < data.get(0).length; l++) {
                    d_sum += (pt[l] - pt2[l]) * (pt[l] - pt2[l]);
                }
                double dist = Math.sqrt(d_sum);

                if (dist < alpha) {
                    double p = (alpha - dist) * theta;
                    addProb += p;
                }
            }
        }

        if (x + 1 < grid.length) {
            if (grid[x + 1][y] != -1) {
                double[] pt2 = data.get(grid[x + 1][y]);

                //double dist = Double.MAX_VALUE;
                double d_sum = 0;
                for (int l = 0; l < data.get(0).length; l++) {
                    d_sum += (pt[l] - pt2[l]) * (pt[l] - pt2[l]);
                }
                double dist = Math.sqrt(d_sum);

                if (dist < alpha) {
                    double p = (alpha - dist) * theta;
                    addProb += p;
                }
            }
        }

        if (y - 1 >= 0) {
            if (grid[x][y - 1] != -1) {
                double[] pt2 = data.get(grid[x][y - 1]);

                //double dist = Double.MAX_VALUE;
                double d_sum = 0;
                for (int l = 0; l < data.get(0).length; l++) {
                    d_sum += (pt[l] - pt2[l]) * (pt[l] - pt2[l]);
                }
                double dist = Math.sqrt(d_sum);

                if (dist < alpha) {
                    double p = (alpha - dist) * theta;
                    addProb += p;
                }
            }
        }

        if (y + 1 < grid.length) {
            if (grid[x][y + 1] != -1) {
                double[] pt2 = data.get(grid[x][y + 1]);

                //double dist = Double.MAX_VALUE;
                double d_sum = 0;
                for (int l = 0; l < data.get(0).length; l++) {
                    d_sum += (pt[l] - pt2[l]) * (pt[l] - pt2[l]);
                }
                double dist = Math.sqrt(d_sum);

                if (dist < alpha) {
                    double p = (alpha - dist) * theta;
                    addProb += p;
                }
            }
        }

        return addProb;
    }

    public void recMark(int cluster, int x, int y) {
        if (x - 1 >= 0) {
            if (grid[x - 1][y] != -1) {
                int temp = grid[x - 1][y];
                if (marker[temp] == 0) {
                    marker[temp] = 1;
                    clusters.get(cluster).add(grid[x-1][y]);
                    recMark(cluster, x - 1, y);
                }
            }
        }

        if (x + 1 < grid.length) {
            if (grid[x + 1][y] != -1) {
                int temp = grid[x + 1][y];
                if (marker[temp] == 0) {
                    marker[temp] = 1;
                    clusters.get(cluster).add(grid[x+1][y]);
                    recMark(cluster, x + 1, y);
                }
            }
        }

        if (y - 1 >= 0) {
            if (grid[x][y - 1] != -1) {
                int temp = grid[x][y - 1];
                if (marker[temp] == 0) {
                    marker[temp] = 1;
                    clusters.get(cluster).add(grid[x][y-1]);
                    recMark(cluster, x, y - 1);
                }
            }
        }

        if (y + 1 < grid.length) {
            if (grid[x][y + 1] != -1) {
                int temp = grid[x][y + 1];
                if (marker[temp] == 0) {
                    marker[temp] = 1;
                    clusters.get(cluster).add(grid[x][y+1]);
                    recMark(cluster, x, y + 1);
                }
            }
        }
    }
    
    public void printGrid(){
        for(int i = 0; i < grid.length;i++){
            for(int j = 0; j < grid[i].length;j++){
                if(grid[i][j] == -1){
                    System.out.print("+ ");
                } else {
                    System.out.print("O ");
                }
            }
            System.out.println("");
        }
        
        System.out.println("");
        System.out.println("");
    }
    
    public void printClusters(){
        for(int i = 0; i < clusters.size();i++){
            System.out.print("cluster " + i + ": ");
            for(int j = 0; j < clusters.get(i).size();j++){
                System.out.println(clusters.get(i).get(j) + " ");
            }
        }
    }

    public Eval evaluate() {
        Eval eval = new Eval();
        
        int sheers = 0;
        while(sheers < clusters.size()){
            ArrayList<Integer> clu = clusters.get(sheers);
            if (clu.size() < 4){
                clusters.remove(clu);
            } else {
                sheers++;
            }
        }
        
        printClusters();
        
        System.out.print(clusters.size() + ", ");
        
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
            //System.out.println(cohesSum);
            if(cohesSum > 100000){
                cohesSum = 0;
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

                            if (curr < minDist) {
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

    private class Ant {

        public int xloc, yloc;
        public int dataIndex;

        public Ant(int in_xloc, int in_yloc) {
            xloc = in_xloc;
            yloc = in_yloc;

            //indicates no data in hand when constructed
            dataIndex = -1;
        }
    }

}
