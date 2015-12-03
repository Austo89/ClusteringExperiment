package clusteringexperiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

public class ACO {
	ArrayList<ArrayList<Integer>> clusters;
    ArrayList<double[]> mus;
    ArrayList<double[]> data;
    ArrayList<double[]> ants;
    int dataLength, antSize, gridSize, maxStepSize;
    
    double[][] grid[][]; //this is to store data and then an ant on a two dimensional array
	public ACO(ArrayList<double[]> in_data, int in_ant){
		data = in_data;
		dataLength = data.get(0).length;
		clusters = new ArrayList<ArrayList<Integer>>();
		ants = new ArrayList<double[]>();
		gridSize = (int) Math.sqrt(10*(data.size()+in_ant));
		grid = new double[gridSize][gridSize][2][dataLength];
		antSize=in_ant;
		maxStepSize=gridSize/2;//(int)Math.sqrt(20*(data.size()+in_ant));
		//System.out.println(gridSize+ " "+maxStepSize);
	}
	
	public void cluster(){
		//set up grid an ants
		Collections.shuffle(data);
		int x,y, j;
		boolean notEnd = true;
		for(int i=0; i<antSize; i++){
			//pickup random object
			//ants.add(data.get(i));
			//System.arraycopy(data.get(i), 0, ants.get(i), 0, dataLength);
			//get placed in random empty location
			do {
				x=(int) Math.floor(Math.random()*gridSize);
				y=(int) Math.floor(Math.random()*gridSize);
			} while(grid[x][y][1][0]!=0);
			//System.out.println(dataLength);
			System.arraycopy(data.get(i), 0, grid[x][y][1], 0, dataLength);
			ants.add(data.get(i));
			ants.add(new double[]{x, y});
		}
		scatterData(ants.size());
	
		int iter=0;
		int attempt=0;
		while (notEnd&&iter<100){ //50000
			j=(int)Math.floor(Math.random()*antSize);
			while (!step(j)&&attempt<=10){
				System.out.println(attempt);
				attempt++;
			}
			if (attempt<=10){//if the ant we chose could work
				
				
				
				iter++;
			}
			if (iter%10==0)
				printGrid();
			attempt=0;
		}
	}
	
	private boolean step(int j){
		int dir=(int)Math.floor(Math.random()*4);
		int stepSize=(int)Math.floor(Math.random()*maxStepSize);
		int x=(int)ants.get(j*2+1)[0];
		int y=(int)ants.get(j*2+1)[1];
		//System.out.print(ants.size());
		if (dir==0){
			x=x%gridSize;
		}else if (dir==1){
			if (stepSize>x){
				x=gridSize-(stepSize-x);
			}else{
			x-=stepSize;
			}
		}else if (dir==2){
			if (stepSize>y){
				y=gridSize-(stepSize-y);
			}else{
			y-=stepSize;
			}
		}else{
			y+=stepSize;
			y=y%gridSize;
		}
		//System.out.println("x,y,j, step, dir, sizes: "+x+", "+y+", "+j+", "+stepSize+", "+dir+", "+ants.get(j*2).length+" "+grid[x][y][1].length);
		if (grid[x][y][1][0]==0){
			System.arraycopy(ants.get(j*2), 0, grid[x][y][1], 0, dataLength);
			Arrays.fill(grid[(int)ants.get(j*2+1)[0]] [(int)ants.get(j*2+1)[1]] [1], 0);
			ants.get(j*2+1)[0]=x;
			ants.get(j*2+1)[1]=y;
			System.out.println(x+"  "+y);
			return true;
		}
		return false;
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
		System.out.println("\n\n-------------------------------------\n\n");
	}
	
}
