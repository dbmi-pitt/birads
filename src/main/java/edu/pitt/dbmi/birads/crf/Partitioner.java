package edu.pitt.dbmi.birads.crf;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Partitioner {
	
	private Random randomGenerator = new Random((new Date()).getTime());
	
	private int numberOfPartitions;
	private int numberOfDataItems;
	private int numberOfIndicesPerBin;
	
	private int testIndex = 0;
	
	private ArrayList<ArrayList<Integer>> partitions = new ArrayList<ArrayList<Integer>>();
	
	public static void main(String[] args) {
		Partitioner partitioner = new Partitioner();
		partitioner.setNumberOfPartitions(10);
		partitioner.setNumberOfDataItems(41);
		partitioner.initialize();
		int foldNumber = 0;
		while (partitioner.hasMoreFolds()) {
			ArrayList<Integer> trainIndices = partitioner.getTrainIndices();
			ArrayList<Integer> testIndices = partitioner.getTestIndices();
			System.out.println("Fold " + foldNumber);
			System.out.println(trainIndices);
			System.out.println(testIndices);
			partitioner.nextFold();
			foldNumber++;
		}
		
	}
	
	public Partitioner() {
	}
	
	public void initialize() {
		calculateNumberOfIndicesPerBin();
		System.out.println("Number of indices per bin is " + this.numberOfIndicesPerBin);
		final ArrayList<Integer> allIndices = generateAllIndices();
		ArrayList<Integer> bin = new ArrayList<Integer>();
		while (!allIndices.isEmpty()) {
			int randomIndex = randomGenerator.nextInt(allIndices.size());
			bin.add(allIndices.remove(randomIndex));
			if (bin.size() == numberOfIndicesPerBin) {
				partitions.add(bin);
				bin = new ArrayList<Integer>();
			}
		}
		System.out.println(displayPartitions());
	}
	
	public ArrayList<Integer> getTestIndices() {
		return partitions.get(testIndex);
	}
	
	public ArrayList<Integer> getTrainIndices() {
		final ArrayList<Integer> trainIndices = new ArrayList<Integer>();
		for (int idx = 0; idx < partitions.size(); idx++) {
			if (idx != testIndex) {
				trainIndices.addAll(partitions.get(idx));
			}
		}
		return trainIndices;
	}
	
	public boolean hasMoreFolds() {
		return testIndex < partitions.size();
	}
	
	public void nextFold() {
		testIndex++;
	}
	
	private ArrayList<Integer> generateAllIndices() {
		final ArrayList<Integer> allIndices = new ArrayList<Integer>();
		for (int idx = 0; idx < numberOfDataItems; idx++) {
			allIndices.add(new Integer(idx));
		}
		return allIndices;
	}
	
	private void calculateNumberOfIndicesPerBin() {
		float p = (float) numberOfPartitions;
		float n = (float) numberOfDataItems;
		numberOfIndicesPerBin = Math.round(n / p);
	}
	
	private String displayPartitions() {
		StringBuffer sb = new StringBuffer();
		int pIdx = 0;
		for (ArrayList<Integer> partition : partitions) {
			sb.append("Partition " + pIdx + " (");
			pIdx++;
			for (Integer index : partition) {
				sb.append(index + ", ");
			}
			sb.append(")\n");
		}
		return sb.toString();
	}

	public int getNumberOfPartitions() {
		return numberOfPartitions;
	}

	public void setNumberOfPartitions(int numberOfPartitions) {
		this.numberOfPartitions = numberOfPartitions;
	}

	public int getNumberOfDataItems() {
		return numberOfDataItems;
	}

	public void setNumberOfDataItems(int numberOfDataItems) {
		this.numberOfDataItems = numberOfDataItems;
	}

}
