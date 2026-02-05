package com.ontariotechu.sofe3980U;


import java.io.FileReader; 
import java.util.*;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
	public static void main (String[] args) {

		String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};
		Map<String, double[]> results = new HashMap<>();

		for(String filePath: filePaths) {
			double[] metrics = evaluateMethod(filePath);
			results.put(filePath, metrics);

			System.out.printf("Results for %s:\n", filePath);
			System.out.printf("BCE: %.7f\n", metrics[0]);
			System.out.printf("Confusion Matrix:\n         y=1   y=0\n y^=1  %5.0f  %5.0f\n y^=0  %5.0f  %5.0f\n", metrics[6], metrics[7], metrics[8], metrics[9]);
			System.out.printf("Accuracy: %.4f\n", metrics[1]);
			System.out.printf("Precision: %.8f\n", metrics[2]);
			System.out.printf("Recall: %.8f\n", metrics[3]);
			System.out.printf("F1 Score: %.8f\n", metrics[4]);
			System.out.printf("AUC-ROC: %.8f\n\n", metrics[5]);
		}
		determineBestModel(results);
	}

	public static double[] evaluateMethod(String filePath) {
		List<Double> yTrue = new ArrayList<>();
		List<Double> yPred = new ArrayList<>();

		try(CSVReader reader = new CSVReader(new FileReader(filePath))) {
			reader.readNext();
			String[] line;
			while ((line = reader.readNext()) != null) {
				yTrue.add(Double.parseDouble(line[0]));
				yPred.add(Double.parseDouble(line[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		int TP = 0, FP = 0, TN = 0, FN = 0;
		double BCE = 0.0;
		int size = yTrue.size();

		for (int i = 0; i < size ; i++) {
			double y = yTrue.get(i);
			double yHat = yPred.get(i) >= 0.5 ? 1.0 : 0.0;
			BCE += y * Math.log(yPred.get(i)) + (1 - y) * Math.log(1 - yPred.get(i));

			if (y == 1.0 && yHat == 1.0) TP++;
			else if (y == 0.0 && yHat == 1.0) FP++;
			else if (y == 0.0 && yHat == 0.0) TN++;
			else if (y == 1.0 && yHat == 0.0) FN++;
		}
		BCE = -BCE / size;

		double accuracy = (double) (TP + TN) / (TP + TN + FP + FN);
		double precision = TP / (double) (TP + FP);
		double recall = TP / (double) (TP + FN);
		double f1Score = 2 * (precision * recall) / (precision + recall);
		double aucRoc = calculateAUC(yTrue, yPred);

		return new double[]{BCE, accuracy, precision, recall, f1Score, aucRoc, TP, FP, FN, TN};
	}

	public static double calculateAUC(List<Double> yTrue, List<Double> yPred) {
		int nPos = (int) yTrue.stream().filter(y -> y == 1.0).count();
		int nNeg = yTrue.size() - nPos;

		List<Double[]> scores = new ArrayList<>();
		for (int i = 0; i < yTrue.size(); i++) {
			scores.add(new Double[]{yPred.get(i), yTrue.get(i)});
		}
		scores.sort((a, b) -> Double.compare(b[0], a[0]));

		int TP = 0, FP = 0;
		double auc = 0.0, prevX = 0.0, prevY = 0.0;

		for (Double[] score : scores) {
			if (score[1] == 1.0) TP++;
			else FP++;

			double TPR = (double) TP / nPos;
			double FPR = (double) FP / nNeg;
			auc += (FPR - prevX) * (TPR + prevY) / 2;
			prevX = FPR;
			prevY = TPR;
		}
		return auc;
	}

	public static void determineBestModel(Map<String, double[]> results) {
		String bestBCE = "", bestAcc = "", bestPrec = "", bestRec = "", bestF1 = "", bestAUC = "";
		double minBCE = Double.MAX_VALUE, maxAcc = 0, maxPrec = 0, maxRec = 0, maxF1 = 0, maxAUC = 0;

		for (Map.Entry<String, double[]> entry : results.entrySet()) {
			String model = entry.getKey();
			double[] metrics = entry.getValue();

			if (metrics[0] < minBCE) { minBCE = metrics[0]; bestBCE = model; }
			if (metrics[1] > maxAcc) { maxAcc = metrics[1]; bestAcc = model; }
			if (metrics[2] > maxPrec) { maxPrec = metrics[2]; bestPrec = model; }
			if (metrics[3] > maxRec) { maxRec = metrics[3]; bestRec = model; }
			if (metrics[4] > maxF1) { maxF1 = metrics[4]; bestF1 = model; }
			if (metrics[5] > maxAUC) { maxAUC = metrics[5]; bestAUC = model; }
		}

		System.out.println("Best models based on different criteria:");
		System.out.println("Best BCE: " + bestBCE);
		System.out.println("Best Accuracy: " + bestAcc);
		System.out.println("Best Precision: " + bestPrec);
		System.out.println("Best Recall: " + bestRec);
		System.out.println("Best F1 Score: " + bestF1);
		System.out.println("Best AUC-ROC: " + bestAUC);
	}
}
