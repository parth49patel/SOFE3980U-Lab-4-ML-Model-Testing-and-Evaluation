package com.ontariotechu.sofe3980U;


import java.io.FileReader;
import java.util.*;
import com.opencsv.*;
import java.util.Arrays;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
    public static void main( String[] args ) {
		String filePath="model.csv";
		FileReader filereader;
		List<String[]> allData;

		try {
			filereader = new FileReader(filePath);
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
			allData = csvReader.readAll();
		}
		catch(Exception e){
			System.out.println( "Error reading the CSV file: " + e.getMessage());
			return;
		}

		int numClasses = allData.get(0).length - 1;
		int[][] confusionMatrix = new int[numClasses][numClasses];
		double crossEntropy = 0.0;
		int totalSamples = allData.size();

		for (String[] row: allData) {
			try {
				int yTrue = Integer.parseInt(row[0]) - 1;
				double[] yPred = new double[numClasses];

				for (int i = 0; i < numClasses; i++) {
					yPred[i] = Double.parseDouble(row[i + 1]);
				}

				if (yPred[yTrue] > 0) {
					crossEntropy += -Math.log(yPred[yTrue]);
				} else {
					System.out.println("Warning: Encountered zero probability in row.");
				}

				int yPredClass = argMax(yPred);

				confusionMatrix[yTrue][yPredClass]++;

			} catch (Exception e) {
				System.out.println("Skipping malformed row: " + Arrays.toString(row));
			}
		}

		crossEntropy /= totalSamples;

		System.out.println("\nCross-Entropy (CE): " + crossEntropy);
		System.out.println("\nConfusion Matrix:");
		printConfusionMatrix(confusionMatrix);
	}

	public static int argMax(double[] array) {
		int maxIndex = 0;
		for (int i = 1; i < array.length; i++) {
			if (array[i] > array[maxIndex]) {
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public static void printConfusionMatrix(int[][] matrix) {
		System.out.println("        " + Arrays.toString(new int[]{1, 2, 3, 4, 5}));
		for (int i = 0; i < matrix.length; i++) {
			System.out.println("y^=" + (i + 1) + " " + Arrays.toString(matrix[i]));
		}
	}
}
