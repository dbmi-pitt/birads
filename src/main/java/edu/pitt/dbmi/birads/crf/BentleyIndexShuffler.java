package edu.pitt.dbmi.birads.crf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class BentleyIndexShuffler {
	
	/*
	 * Programming Pearls by Jon Bentley
	 */
	
	private Random randomGenerator = new Random((new Date()).getTime());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BentleyIndexShuffler shuffler = new BentleyIndexShuffler();
		ArrayList<Integer> indexSubset = shuffler.generateShuffledArrayList(15, 100);
		shuffler.displayArray(indexSubset);
		
		indexSubset = shuffler.generateShuffledArrayList(50, 10000);
		shuffler.displayArray(indexSubset);
		
		indexSubset = shuffler.generateShuffledArrayList(15, 30);
		shuffler.displayArray(indexSubset);
	}
	
	public BentleyIndexShuffler() {
	}
	
	public ArrayList<Integer> generateShuffledArrayList(int m, int n) {
		int[] sortedIndices = generateShuffledIndices(m,n);
		final ArrayList<Integer> result = new ArrayList<Integer>();
		for (int sortedIndex : sortedIndices) {
			result.add(sortedIndex);
		}
		return result;
	}
	
	public void displayArray(ArrayList<Integer> values) {
		for (Integer value : values) {
			System.out.print(value + " ");
		}
		System.out.println();
		
	}

	private int[] generateShuffledIndices(int m, int n) {
		int i = 0;
		int j = 0;
		int[] x = new int[n];
		for (i = 0; i < n; i++) {
			x[i] = i;
		}
		for (i = 0; i < m; i++) {
			j = chooseRandomlyWithinRange(i, n - 1);
			swap(x, i, j);
		}
		return sort(x, m);
	}
	
	private void swap(int[] x, int i, int j) {
		int t = x[i];
		x[i] = x[j];
		x[j] = t;
	}

	private int chooseRandomlyWithinRange(int sIdx, int eIdx) {
		int range = eIdx - sIdx;
		return sIdx + randomGenerator.nextInt(range);
	}

	private int[] sort(int[] x, int m) {
		int[] sortedArray = Arrays.copyOf(x, m);
		Arrays.sort(sortedArray);
		return sortedArray;
	}

}
