package edu.nsf.machinelearning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import weka.core.*;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.classifiers.trees.J48;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.Classifier;

public class MachineLearningAppl {
	
	//String CSV_FILE =  "C:\\Users\\Bharat\\workspace1\\WekaMachineLearning\\data\\credit.csv";
	String ARFF_FILE = "C:\\Users\\Bharat\\workspace1\\WekaMachineLearning\\data\\credit-g.arff";
	ArrayList<LearningAlgos> classifiers = new ArrayList<LearningAlgos>();
	
	public MachineLearningAppl(){run();}
		
	public void run(){
		try {
		//	convertData(CSV_FILE,ARFF_FILE);
			buildClassifier();
			makeRecommendation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void makeRecommendation() throws Exception {
		LearningAlgos bestChoice = classifiers.get(0);
		double bestAccuracy = 0;
		
		System.out.println("***** Algorithms List *****\n");
		
		for (LearningAlgos algo : classifiers){
			double accuracy = algo.getAccuracy();
			System.out.println("Name: " + algo.getAlgoName() + ", Accuracy: " + accuracy);
			if (accuracy > bestAccuracy){
				bestAccuracy = accuracy;
				bestChoice = algo;
			}
		}
		
		System.out.println("\nThe best learning algorithm considered for this dataset is: " + bestChoice.getAlgoName() + " with an accuracy of " + (bestAccuracy*100) +"%\n");
		bestChoice.buildConfusionMatrix();
		
	}

	
	private void convertData(String csv_file, String arff_file) throws Exception{
				
		File input = new File(csv_file);
		
		if (!input.exists()){
			throw new NullPointerException();
		} else {
			CSVLoader csv = new CSVLoader();
			csv.setSource(input);
			Instances csvData = csv.getDataSet();
			
			ArffSaver arff = new ArffSaver();
			arff.setInstances(csvData);
			arff.setFile(new File(ARFF_FILE));
			arff.writeBatch();
		}			
	}
	
	private void buildClassifier() throws Exception{

		BufferedReader input = new BufferedReader(new FileReader(ARFF_FILE));
		Instances data = new Instances(input);
		data.setClassIndex(data.numAttributes() - 1); 
	
		Classifier j48 = new J48();    
	    j48.buildClassifier(data);
	    classifiers.add(new LearningAlgos("J48", j48, data));
	    	
		Classifier zeroR = new ZeroR();
		zeroR.buildClassifier(data);
		classifiers.add(new LearningAlgos("ZeroR", zeroR, data));
		
		Classifier oneR = new OneR();
		oneR.buildClassifier(data);
		classifiers.add(new LearningAlgos("OneR", oneR, data));
		
		Classifier logistic = new Logistic();
		logistic.buildClassifier(data);
		classifiers.add(new LearningAlgos("Logistic Regression", logistic, data));
		
		Classifier bayes = new NaiveBayes();
		bayes.buildClassifier(data);
		classifiers.add(new LearningAlgos("Naive Bayes", bayes, data));
		
		input.close();

	}

}
