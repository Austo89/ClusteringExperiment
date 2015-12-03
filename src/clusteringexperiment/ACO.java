package clusteringexperiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ACO {
	ArrayList<ArrayList<Integer>> clusters;
    ArrayList<double[]> mus;
    ArrayList<double[]> data;
    ArrayList<double[]> ants;
    int dataLength, antSize, gridSize;
    
    double[][] grid[][]; //this is to store data and then an ant on a two dimensional array
	public ACO(ArrayList<double[]> in_data, int in_ant, int in_grid){
		data = in_data;
		dataLength = data.get(0).length;
		clusters = new ArrayList<ArrayList<Integer>>();
		ants = new ArrayList<double[]>();
		gridSize = in_grid;
		grid = new double[gridSize][gridSize][2][dataLength];
		antSize=in_ant;
		//System.out.println(data.get(0)[0]);
	}
	
	public void cluster(){
		//set up grid an ants
		Collections.shuffle(data);
		int x,y;
		for(int i=0; i<antSize; i++){
			//pickup random object
			ants.add(data.get(i));
			//System.arraycopy(data.get(i), 0, ants.get(i), 0, dataLength);
			//get placed in random empty location
			do {
				x=(int) Math.floor(Math.random()*gridSize);
				y=(int) Math.floor(Math.random()*gridSize);
			} while(grid[x][y][1][0]!=0);
			//System.out.println(dataLength);
			System.arraycopy(ants.get(i), 0, grid[x][y][1], 0, dataLength);
		}
		scatterData(ants.size());
		printGrid();
	}

	private void scatterData(int start) {
		int x,y;
		for(int i=start; i<data.size(); i++){
			//get placed in random empty location
			do {
				x=(int) Math.floor(Math.random()*gridSize);
				y=(int) Math.floor(Math.random()*gridSize);
			} while(grid[x][y][0][0]!=0);
			System.arraycopy(data.get(i), 0, grid[x][y][0], 0, dataLength);
		}
	}
	private void printGrid(){
		for (int i = 0; i<gridSize; i++){
			for (int j=0; j<gridSize; j++){
				if (grid[i][j][1][0]!=0&&grid[i][j][0][0]!=0)
					System.out.println("X");
				else if(grid[i][j][0][0]!=0){//data
					System.out.print("D");
				}
				else if(grid[i][j][1][0]!=0){//ant
					System.out.print("A");
				}
				else{
					System.out.print(" ");
				}
			}
			System.out.println("");
		}
	}
	
}
