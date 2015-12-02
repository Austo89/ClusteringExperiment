/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clusteringexperiment;
import java.io.*;
import java.util.*;

/**
 *
 * @author Austo89
 */
public class ClusteringExperiment {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<double[]> data = new ArrayList();
        BufferedReader CSVFile;
        
        try{
            //read in the csv file comma delimited
            CSVFile = new BufferedReader(new FileReader("C:\\Users\\Austo89\\Documents\\Homework\\Fall 2015\\Soft Computing\\project3\\datasets norm\\wine_n.csv"));
            
            //loop over all the datas
            boolean noProblem = true;
            while(noProblem){
                String dataLine = "";
                try{
                    dataLine = CSVFile.readLine();
                    //System.out.println(dataLine);
                    String datas[] = dataLine.split(",");
                    
                    double[] dubData = new double[datas.length];
                    for (int i=0; i < datas.length; i++){
                        dubData[i] = Double.parseDouble(datas[i]);
                        //System.out.println(dubData[i]);
                    }
                    data.add(dubData);
                }catch (IOException|NullPointerException e){
                    System.out.println("Reached end of file.");
                    noProblem = false;
                }
                
            }
            
            /////////////////////////////////////
            //pass the data to the experiment here
            /////////////////////////////////////
            KMeans test = new KMeans(data,5);
            test.cluster();
            
            DBScan test2 = new DBScan(data,.05,4);
            test2.cluster();
            
        } catch (IOException e){
            System.out.println("Couldn't find your file.");
        }
        
        
    }
    
}
