package edu.pitt.dbmi.birads;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import org.apache.commons.io.FileUtils;

import edu.pitt.dbmi.birads.crf.ctakes.BiradsUtils;


public class BiRADS_Evaluate {
	
	public static void main(String[] args) throws IOException {
		String goldPath = "/home/tseytlin/Data/BiRADS/gold/result.test";
		String testPath = "/home/tseytlin/Data/BiRADS/data/exp_10.10.2016/final.test";

		BiRADS_Evaluate eval = new BiRADS_Evaluate();
		Map<String,ConfusionMatrix> ee = eval.evaluate(new File(goldPath), new File(testPath));
		
		ConfusionMatrix.printHeader(System.out);
		double totalF = 0, totalCount = 0;
		for(String type: ee.keySet()){
			ConfusionMatrix cm = ee.get(type);
			cm.print(System.out,type);
			if("Category".equals(type)){
				totalCount = cm.TP;
			}else{
				double num = (cm.getFscore()*cm.getTotal()/totalCount);
				totalF += Double.isNaN(num)?0:num;
			}
		}
		
		System.out.println("\nWeighted F1 score:\t"+totalF);
		
	}
	
	private boolean isUseful(BiRADS birads) {
		int x = BiradsUtils.parseIntegerValue(birads.category);
		//return 3 <= x && x <= 5;
		return true;
	}
	

	public Map<String,ConfusionMatrix> evaluate(File gold, File test) throws IOException {
		Map<String,BiRADS> goldMap = readResults(gold);
		Map<String,BiRADS> testMap = readResults(test);
		
		Map<String,ConfusionMatrix> confusion = new LinkedHashMap<String,ConfusionMatrix>();
		confusion.put("Category",new ConfusionMatrix());
		confusion.put("Left",new ConfusionMatrix());
		confusion.put("Right",new ConfusionMatrix());
		confusion.put("Bilateral",new ConfusionMatrix());
		confusion.put("Overall",new ConfusionMatrix());
		confusion.put("",new ConfusionMatrix());
		
		
		// go over gold set first
		for(String anchor: goldMap.keySet()){
			BiRADS g = goldMap.get(anchor);
			BiRADS t = testMap.get(anchor);
			
			if(t != null){
				confusion.get("Category").TP ++;
				if(g.type.equals(t.type)){
					confusion.get(g.type).TP ++;
				}else{
					confusion.get(g.type).FN ++;
					confusion.get(t.type).FP ++;
				}
			}else{
				confusion.get("Category").FN ++;
			}
			
		}
	
		// go over test set
		for(String anchor: testMap.keySet()){
			if(goldMap.get(anchor) == null){
				confusion.get("Category").FP ++;
			}
		}
		
		
		return confusion;
	}

	
	
	private Map<String, BiRADS> readResults(File dir) throws IOException {
		Map<String,BiRADS> map = new HashMap<String,BiRADS>();
		for(File f: dir.listFiles()){
			if(f.isFile()){
				for(String line : FileUtils.readLines(f)){
					String [] parts = line.split("\t");
					if(parts.length > 3){
						// setup anchor filename+offset
						String anchor = f.getName()+":"+parts[3].trim();
						BiRADS birads = new BiRADS(parts[1].trim(),parts[2].trim());
						if(isUseful(birads))
							map.put(anchor,birads);
					}
				}
			}
		}
		return map;
	}



	



	public static class BiRADS {
		public BiRADS(String c, String t) {
			this.category = c;
			this.type = t;
		}
		public String category, type;  
	}
	
	public static class ConfusionMatrix {
		public static final int MAX_LABEL_SIZE = 20;
		public double TP,FP,FN,TN;
		public void append(ConfusionMatrix c){
			TP += c.TP;
			FP += c.FP;
			FN += c.FN;
			TN += c.TN;
		}
		
		public double getTotal() {
			return TP+FN;
		}

		public double getPrecision(){
			return TP / (TP+ FP);
		}
		public double getRecall(){
			return  TP / (TP+ FN);
		}
		public double getFscore(){
			double precision = getPrecision();
			double recall = getRecall();
			return (2*precision*recall)/(precision + recall);
		}
		public double getAccuracy(){
			return (TP+TN) / (TP+TN+FP+FN);
		}
		
		public static void printHeader(PrintStream out){
			out.println(String.format("%1$-"+MAX_LABEL_SIZE+"s","Label")+"\tTP\tFP\tFN\tTN\tPrecis\tRecall\tAccur\tF1-Score");
		}
		public void print(PrintStream out,String label){
			out.println(String.format("%1$-"+MAX_LABEL_SIZE+"s",label)+"\t"+
					String.format("%.0f",TP)+"\t"+String.format("%.0f",FP)+"\t"+
					String.format("%.0f",FN)+"\t"+String.format("%.0f",TN)+"\t"+
					String.format("%.4f", getPrecision())+"\t"+
					String.format("%.4f", getRecall())+"\t"+
					String.format("%.4f",getAccuracy())+"\t"+
					String.format("%.4f",getFscore()));
		}
	}
}
