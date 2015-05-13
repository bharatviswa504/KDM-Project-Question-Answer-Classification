package edu.umkc.sce.surveyperceptor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import cmu.arktweetnlp.POSTagger;
import cmu.arktweetnlp.Token;

public class PerceptionLearner extends JFrame implements ActionListener {

	static ArffSaver fileSaver;
	private static int percentSplit = 70;
	private ArrayList<String> featureWords;
	private ArrayList<Attribute> attributeList;
	private Instances inputDataset;
	private POSTagger posTagger;
	private ArrayList<String> sentimentClassList;

	private Instances instanceInfo;
	private ArrayList<ClassifierInfo> classifiers;// stores various classifiers
													// to train and test

	// UI elements

	JLabel lblInputFile = new JLabel("sample_interview1013_annotated4.csv");
	JButton btnChangeFile = new JButton("Change File");
	JFileChooser inputFile = new JFileChooser();
	DefaultTableModel model = new javax.swing.table.DefaultTableModel();
	JTable tblResults = new JTable(model);

	int trainSplitSize = 0;

	public PerceptionLearner() {
		attributeList = new ArrayList<>();
		posTagger = new POSTagger();

		classifiers = new ArrayList<ClassifierInfo>();

	}

	public static void main(String[] args) {
		try {

			// here we need to keep indices 6 and 7 and throw everything else
			// out.
			// DataPreparer.IsolateCSVFields(new int[]{6,7},
			// "sample_interview1013_annotated5.csv");

			classifyText();

			//assessTextOpinion();

		} catch (Exception ex) {

			System.err.println(ex.getMessage());
		}
	}

	private static void assessTextOpinion() {

		try {
			PerceptionLearner pl = new PerceptionLearner();

			
			pl.PrepareAttributeList();

			pl.PrepareStudyInstances();

			pl.ExtractFeature(pl.lblInputFile.getText());

			pl.TrainClassifiers();

			pl.TestClassifiers();

			pl.ShowResult();
		} catch (Exception ex) {

			System.err.println(ex.getMessage());
		}

	}

	private static void classifyText() {

		// here we will attempt to classify the document into broader categories

		TextClassifier textClassifier = new TextClassifier();
		textClassifier
				.startClassification("x2_sample_interview1013_annotated5.csv", 
						new String[]{"hospital","staff", "facility", "pain", "safety"}, true, false);

	
	}

	private void PrepareAttributeList() {

		ObjectInputStream ois = null;
		try {
			// reads the feature words list to a hashset
			ois = new ObjectInputStream(new FileInputStream(
					"FeatureWordsList.dat"));
			featureWords = (ArrayList<String>) ois.readObject();
		} catch (Exception ex) {
			System.out.println("Exception in Deserialization");
		} finally {
			try {
				ois.close();
			} catch (IOException ex) {
				System.out
						.println("Exception while closing file after Deserialization");
			}
		}

		// creating an attribute list from the list of feature words
		sentimentClassList = new ArrayList<>();
		sentimentClassList.add("negative");
		sentimentClassList.add("positive");
		sentimentClassList.add("neutral");
		for (String featureWord : featureWords) {
			attributeList.add(new Attribute(featureWord));
		}
		// the last attribute reprsents ths CLASS (Sentiment) of the tweet
		attributeList.add(new Attribute("Sentiment", sentimentClassList));

	}

	private void PrepareStudyInstances() {

		instanceInfo = new Instances("TRANING_INSTANCES", attributeList, 0);
		instanceInfo.setClassIndex(instanceInfo.numAttributes() - 1);
	}

	private void ExtractFeature(String fileName) throws IOException {

		CSVLoader trainingLoader = new CSVLoader();
		trainingLoader.setSource(new File(fileName));
		inputDataset = trainingLoader.getDataSet();

		for (Instance currentInstance : inputDataset) {
			// find and return the feature vector for the current instance
			Instance currentFeatureVector = getFeatureVector(currentInstance);

			// add feature vector to the study Instances
			currentFeatureVector.setDataset(instanceInfo);
			instanceInfo.add(currentFeatureVector);
		}

	}

	private Instance getFeatureVector(Instance currentInstance) {

		Map<Integer, Double> featureMap = new TreeMap<>();
		List<Token> tokens = posTagger.runPOSTagger(currentInstance
				.stringValue(0));// tokenize first field (consumer responses)

		for (Token token : tokens) {

			switch (token.getPOS()) {
			case "A":
			case "V":
			case "R":
			case "N":
				// System.out.println(token.getWord());

				// remove anything but alphanumeric
				String word = token.getWord().replaceAll("[^0-9A-Za-z]", "");

				if (featureWords.contains(word)) {
					featureMap.put(featureWords.indexOf(word), 1.0); // found
					// System.out.println(word);
				}
			}
		}
		int indices[] = new int[featureMap.size() + 1];
		double values[] = new double[featureMap.size() + 1];
		int i = 0;

		for (Map.Entry<Integer, Double> entry : featureMap.entrySet()) {
			indices[i] = entry.getKey();
			values[i] = entry.getValue();
			i++;
		}
		indices[i] = featureWords.size();
		values[i] = (double) sentimentClassList.indexOf(currentInstance
				.stringValue(1));
		return new SparseInstance(1.0, values, indices, featureWords.size());
	}

	private void TrainClassifiers() throws Exception {

		// Filter thisFilter = new
		// weka.filters.unsupervised.instance.RemoveFrequentValues();
		// //thisFilter.setInputFormat(instanceInfo)
		// thisFilter.setInputFormat(instanceInfo);
		//
		StringToWordVector filter = new StringToWordVector();
		filter.setInputFormat(instanceInfo);
		filter.setAttributeIndicesArray(new int[] { 0 });
		filter.setIDFTransform(true);
		filter.setTFTransform(true);

		instanceInfo = Filter.useFilter(instanceInfo, filter);

		saveInputARFF("arff_input.txt", instanceInfo);

		System.out.println("Start training classifiers");
		trainSplitSize = instanceInfo.numInstances() * percentSplit / 100;

		Instances train = new Instances(instanceInfo, 0, trainSplitSize);

		// Instances test = new Instances(instanceInfo, trainSplitSize,
		// instanceInfo.numInstances()-trainSplitSize);

		// add Naive Bayes
		Classifier bayes = new NaiveBayes();
		bayes.buildClassifier(train);
		ClassifierInfo ci = new ClassifierInfo("Naive Bayes", bayes);
		ci.evaluation = new Evaluation(train);
		ci.evaluation.evaluateModel(bayes, train);
		classifiers.add(ci);

		// add ZeroR
		Classifier zeroR = new ZeroR();
		zeroR.buildClassifier(train);
		ci = new ClassifierInfo("ZeroR", zeroR);
		ci.evaluation = new Evaluation(train);
		ci.evaluation.evaluateModel(zeroR, train);
		classifiers.add(ci);

		// add OneR
		Classifier oneR = new OneR();
		oneR.buildClassifier(train);
		ci = new ClassifierInfo("OneR", oneR);
		ci.evaluation = new Evaluation(train);
		ci.evaluation.evaluateModel(oneR, train);
		classifiers.add(ci);

		// add J48
		Classifier j48 = new J48();
		j48.buildClassifier(train);
		ci = new ClassifierInfo("J48", j48);
		ci.evaluation = new Evaluation(train);
		ci.evaluation.evaluateModel(j48, train);
		classifiers.add(ci);

		// add Logistic
		Classifier logistic = new Logistic();
		logistic.buildClassifier(train);
		ci = new ClassifierInfo("Logistic Regression", logistic);
		ci.evaluation = new Evaluation(train);
		ci.evaluation.evaluateModel(logistic, train);
		classifiers.add(ci);

		// add Random Forest
		Classifier randomForest = new RandomForest();
		randomForest.buildClassifier(train);
		ci = new ClassifierInfo("Random Forest", randomForest);
		ci.evaluation = new Evaluation(train);
		ci.evaluation.evaluateModel(randomForest, train);
		classifiers.add(ci);

	}

	public static void saveInputARFF(String destinationARFF,
			Instances sourceInstance) throws IOException {

		fileSaver = new ArffSaver();
		fileSaver.setInstances(sourceInstance);
		fileSaver.setFile(new File(destinationARFF));
		fileSaver.writeBatch();

	}

	private void TestClassifiers() throws Exception {

		Instance cInstance;

		// test instances starts at the offset where training instances end
		// (size/length)
		for (int i = trainSplitSize; i < instanceInfo.numInstances(); i++) {
			cInstance = instanceInfo.instance(i);

			double predictedClass;
			for (ClassifierInfo c : classifiers) {
				predictedClass = c.classifier.classifyInstance(cInstance);
				if (instanceInfo.instance(i).classValue() == predictedClass) {
					c.correctlyClassified++;
				}
			}

		}
	}

	private void ShowResult() {

		System.out.println("Showing results");
		int testSplitSize = instanceInfo.numInstances() - trainSplitSize;
		for (ClassifierInfo c : classifiers) {
			String result = String
					.format("Algorithm: %s \t Accuracy: %.0f%% for %d instances @ %d%% split",
							c.classifierName,
							((double) ((double) c.correctlyClassified / (double) testSplitSize)) * 100.0,
							instanceInfo.numInstances(), percentSplit);

			// model.insertRow(0, new Object[]{c.classifierName, ((double)
			// ((double) c.correctlyClassified / (double) testSplitSize)) *
			// 100.0});

			System.out.println(result);

			if (c.evaluation != null) {
				result = String
						.format("Algorithm: %s \t Accuracy: %.0f%% for %d instances @ %d%% split",
								c.classifierName,
								((double) ((double) c.evaluation.correct() / c.evaluation
										.numInstances())) * 100.0, instanceInfo
										.numInstances(), percentSplit);

				System.out.println(result);
				c.evaluation.toSummaryString();
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == btnChangeFile) {
			JFileChooser chooser = new JFileChooser();
			// this.getContentPane().add(chooser);
			chooser.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
			int returnVal = chooser.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				lblInputFile.setText(chooser.getSelectedFile().getName());
				System.out.println("Input file changed to: "
						+ lblInputFile.getText());
			}
		}

	}
}
