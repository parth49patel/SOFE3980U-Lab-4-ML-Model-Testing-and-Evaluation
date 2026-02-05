package com.ontariotechu.sofe3980U;


import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

import javax.xml.crypto.Data;
import javax.xml.transform.Source;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App {
	public static void main (String[] args) {

		String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};

		String bestMSEModel = "";
		String bestMAEModel = "";
		String bestMAREModel = "";

		double minMSE = Double.MAX_VALUE;
		double minMAE = Double.MAX_VALUE;
		double minMARE = Double.MAX_VALUE;

		for (String filePath: filePaths) {
			double[] metrics = evaluateModel(filePath);

			System.out.printf("Results for %s:\n", filePath);
			System.out.printf("MSE: %.5f\n", metrics[0]);
			System.out.printf("MAE: %.5f\n", metrics[1]);
			System.out.printf("MARE: %.5f%%\n\n", metrics[2]);

			// Update the best models
			if (metrics[0] < minMSE) {
				minMSE = metrics[0];
				bestMSEModel = filePath;
			}

			if (metrics[1] < minMAE) {
				minMAE = metrics[1];
				bestMAEModel = filePath;
			}

			if (metrics[2] < minMARE) {
				minMARE = metrics[2];
				bestMAREModel = filePath;
			}
		}

		System.out.println("Best MSE Model: " + bestMSEModel);
		System.out.println("Best MAE Model: " + bestMAEModel);
		System.out.println("Best MARE Model: " + bestMAREModel);

		// Final Recommendation
		if (bestMSEModel.equals(bestMAEModel) && bestMAEModel.equals(bestMAREModel)) {
			System.out.println("\n**Final Recommendation: " + bestMSEModel + "(Best Overall Model)**");
		} else {
			System.out.println("\n**Final Recommendation: Consider either " + bestMSEModel + ", " + bestMAEModel + ", or " + bestMAREModel + " based on priority.**");
		}
    }

	public static double[] evaluateModel(String filePath) {
		try(FileReader fileReader = new FileReader(filePath);
			CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build()) {

			List<String[]> allData = csvReader.readAll();
			double mse = 0, mae = 0, mare = 0;
			int size = allData.size();
			double epsilon = 1e-10;

			for (String[] row: allData) {
				double y_true = Double.parseDouble(row[0]);
				double y_pred = Double.parseDouble(row[1]);
				double error = y_true - y_pred;

				mse += error * error;
				mae += Math.abs(error);
				mare += (Math.abs(error) / (Math.abs(y_true) + epsilon)) * 100;
			}

			return new double[]{ mse/size, mae/size, mare/size };

		} catch (Exception e) {
			System.out.println("Error reading the CSV file: " + filePath);
			return new double[] { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE };
		}
	}
}
