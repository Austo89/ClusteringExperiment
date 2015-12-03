package clusteringexperiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ACO {
	ArrayList<ArrayList<Integer>> clusters;
    ArrayList<double[]> mus;
    ArrayList<double[]> data;
    ArrayList<double[]> ants;
    int dataLength;
    int gridSize;
    double[][] grid[][]; //this is to store data and then an ant on a two dimensional array
	public ACO(ArrayList<double[]> in_data, int antSize, int in_grid){
		dataLength = data.get(0).length;
		data = in_data;
		clusters = new ArrayList<ArrayList<Integer>>();
		ants = new ArrayList<double[]>();
		gridSize = in_grid;
		grid = new double[dataLength][2][gridSize][gridSize];
		gridSize = in_grid;
	}
	
	public void cluster(){
		//set up grid an ants
		Collections.shuffle(data);
		int x,y;
		for(int i=0; i<ants.size(); i++){
			//pickup random object
			System.arraycopy(data.get(i), 0, ants.get(i), 0, dataLength);
			//get placed in random empty location
			do {
				x=(int) Math.floor(Math.random()*gridSize);
				y=(int) Math.floor(Math.random()*gridSize);
			} while(grid[0][1][x][y]!=0);
			System.arraycopy(data.get(i), 0, ants.get(i), 0, dataLength);
		}
		scatterData(ants.size());
		
	}

	private void scatterData(int start) {
		int x,y;
		for(int i=start; i<data.size(); i++){
			//get placed in random empty location
			do {
				x=(int) Math.floor(Math.random()*gridSize);
				y=(int) Math.floor(Math.random()*gridSize);
			} while(grid[0][0][x][y]!=0);
			System.arraycopy(data.get(i), 0, ants.get(i), 0, dataLength);
		}
	}
	
}
