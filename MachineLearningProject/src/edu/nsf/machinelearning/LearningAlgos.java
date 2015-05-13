package edu.nsf.machinelearning;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class LearningAlgos {
	
	private String algoName;
	private Classifier classifier;
	private Instances data;
	private Evaluation eval;
	
	public LearningAlgos(String name, Classifier c, Instances d) throws Exception{
		algoName = name;
		classifier = c;
		data = d;
		eval = new Evaluation(data);
	}
	
	public String getAlgoName(){
		return algoName;
	}
	
	public double getAccuracy() throws Exception{
		
		eval.evaluateModel(classifier, data);
		return eval.correct()/eval.numInstances();
	}
	
	public void buildConfusionMatrix(){
		
		int matrix[][] = {
			{(int)eval.numTruePositives(0), (int)eval.numFalseNegatives(0)},
			{(int)eval.numFalsePositives(0),  (int)eval.numTrueNegatives(0)}
		};
		
		System.out.println("******** Confusion Matrix ********");
		System.out.println("a = yes  b = no\n");

		System.out.println("|\ta\tb\t|");
		String str = "|\t";

		for (int i = 0; i < 2; i++) {
		    for (int j = 0; j < 2; j++) {
		    	 str += matrix[i][j] + "\t";
		    }
		    System.out.println(str + "|");
            str = "|\t";
		}
	}
}