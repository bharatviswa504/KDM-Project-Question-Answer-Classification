package edu.umkc.sce.surveyperceptor;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;


public class ClassifierInfo {
	
	
	String classifierName;
	Classifier classifier;
	FilteredClassifier filteredClassifier;
	Instances instances;
	int correctlyClassified;
	
	Evaluation evaluation;
	
public ClassifierInfo(String name, Classifier cl) {
		this.classifier = cl;
		this.classifierName = name;
	}

//overload ctor
public ClassifierInfo(String name, FilteredClassifier cl) {
	this.filteredClassifier = cl;
	this.classifierName = name;
}

}
