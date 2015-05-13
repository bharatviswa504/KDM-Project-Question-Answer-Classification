package edu.umkc.sce.surveyperceptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JTextField;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.experimental.categories.Categories;

import cmu.arktweetnlp.impl.Model;
import edu.berkeley.nlp.util.StopWatch;
import edu.kdm.Result;
import edu.kdm.Result.Category;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.PlainText;
import weka.classifiers.functions.Logistic;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.gui.visualize.InstanceInfo;

public class TextClassifier {

	ArffSaver fileSaver;
	private ArrayList<Attribute> attributeList;
	private Attribute attributeQuestion;
	private Attribute attributeAnswer;
	private Attribute attributeClass;
	private ArrayList<String> attributeClassList;

	protected Instances instanceInfo;
	private ArrayList<ClassifierInfo> classifiers;// stores various classifiers
													// to train and test

	StopWatch watch;

	int trainSplitSize = 0;

	public TextClassifier() {

		classifiers = new ArrayList<ClassifierInfo>();

		fileSaver = new ArffSaver();

		watch = new StopWatch();
	}

	public void startClassification(String inputFile, String[] categories,
			boolean splitCategories, boolean splitQuestionAnswerPair) {

		if (!splitCategories) {
			String category = "all";
			// multi-class classification

			if (!splitQuestionAnswerPair) {
				performMultiClassClassification(inputFile, categories,
						category, QAPair.Both);
			}
			else
			{
				performMultiClassClassification(inputFile, categories,
						category, QAPair.QuestionOnly);
				
				performMultiClassClassification(inputFile, categories,
						category, QAPair.AnswerOnly);
			}

		} else {
			for (String category : categories) {

				performSingleClassClassification(inputFile, category,
						QAPair.Both);
			}
		}

	}

	private void performSingleClassClassification(String inputFile,
			String category, QAPair use) {

		try {
			PrepareAttributeList(category, use);

			PrepareStudyInstances(inputFile, category, use);

			EvaluateClassifiers(category, use);

			TrainClassifiers(category, use);

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

	}

	private void performMultiClassClassification(String inputFile,
			String[] categories, String category, QAPair use) {

		try {

			PrepareAttributeList(categories, use);

			PrepareStudyInstances(inputFile, category, use);

			EvaluateClassifiers(category, use);

			TrainClassifiers(category, use);

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

	}

	protected void PrepareAttributeList(String currentCategory, QAPair use) {

		// creating an attribute list
		attributeClassList = new ArrayList<>();
		attributeList = new ArrayList<>();

		attributeClassList.add(currentCategory);
		attributeClassList.add("not_" + currentCategory);

		if (use == QAPair.Both) {
			attributeQuestion = new Attribute("question", (List<String>) null);
			attributeAnswer = new Attribute("answer", (List<String>) null);

			attributeList.add(attributeQuestion);
			attributeList.add(attributeAnswer);
		} else if (use == QAPair.QuestionOnly) {
			attributeQuestion = new Attribute("question", (List<String>) null);
			// attributeAnswer = new Attribute("answer", (List<String>) null);

			attributeList.add(attributeQuestion);
			// attributeList.add(attributeAnswer);
		} else if (use == QAPair.AnswerOnly) {
			// attributeQuestion = new Attribute("question", (List<String>)
			// null);
			attributeAnswer = new Attribute("answer", (List<String>) null);

			// attributeList.add(attributeQuestion);
			attributeList.add(attributeAnswer);
		}

		attributeClass = new Attribute("classi", attributeClassList);

		attributeList.add(attributeClass);

	}

	protected void PrepareAttributeList(String[] categories, QAPair use) {

		// creating an attribute list
		attributeClassList = new ArrayList<>();
		attributeList = new ArrayList<>();
		attributeClassList = new ArrayList<String>(Arrays.asList(categories));

		if (use == QAPair.Both) {
			attributeQuestion = new Attribute("question", (List<String>) null);
			attributeAnswer = new Attribute("answer", (List<String>) null);

			attributeList.add(attributeQuestion);
			attributeList.add(attributeAnswer);
			
		} else if (use == QAPair.QuestionOnly) {
			attributeQuestion = new Attribute("question", (List<String>) null);
			// attributeAnswer = new Attribute("answer", (List<String>) null);

			attributeList.add(attributeQuestion);
			// attributeList.add(attributeAnswer);
		} else if (use == QAPair.AnswerOnly) {
			// attributeQuestion = new Attribute("question", (List<String>)
			// null);
			attributeAnswer = new Attribute("answer", (List<String>) null);

			// attributeList.add(attributeQuestion);
			attributeList.add(attributeAnswer);
		}

		attributeClass = new Attribute("classi", attributeClassList);
		attributeList.add(attributeClass);

	}

	protected void PrepareStudyInstances(String fileName, String currentCategory,
			QAPair use) throws IOException {

		instanceInfo = new Instances("SURVEY_RELATION", attributeList, 1);// init
																			// one
																			// instance

		// here we read the csv file, parse it and construct our own instances
		// for a given caterogy
		CSVParser csv = DataPreparer.getCSV(fileName);

		/**/
		DenseInstance customInstance;
		if (use == QAPair.Both)
			customInstance = new DenseInstance(3);
		else
			customInstance = new DenseInstance(2);

		for (CSVRecord record : csv.getRecords()) {
			if ((record.get(0) != null || record.get(1) != null)
					&& !record.get(0).trim().equals("question")) {

				if (use == QAPair.Both || use == QAPair.QuestionOnly) {
					customInstance.setValue(attributeQuestion, record.get(0)
							.trim());
				}

				if (use == QAPair.Both || use == QAPair.AnswerOnly) {
					customInstance.setValue(attributeAnswer, record.get(1)
							.trim()); /**/
				}

				// only get class defined as nominal - otherwise WEKA will
				// complain
				if (attributeClassList.contains(record.get(2).trim()))
					customInstance.setValue(attributeClass, record.get(2)
							.trim());
				else if (record.get(2).trim().length() > 0)
					customInstance.setValue(attributeClass, "not_"
							+ currentCategory);
				// else if it nothing was defined, leave it as ? for WEKA
				else
					continue;

				// now add it to the instance
				instanceInfo.add(customInstance);

			}

		}

		// set the class index as the last attribute
		instanceInfo.setClassIndex(instanceInfo.numAttributes() - 1);

	}

	protected void PrepareStudyInstances(String question, String answer,
			String currentCategory, QAPair use) {

		// init dataset with one instance
		instanceInfo = new Instances("SURVEY_RELATION", attributeList, 1);

		DenseInstance customInstance = null;
		
		if(use == QAPair.Both)
			{
			customInstance = new DenseInstance(3);
			}
		else
		{
			customInstance = new DenseInstance(2);
		}

		if (question != null || answer != null) {

			if(use == QAPair.Both || use == QAPair.QuestionOnly)
					customInstance.setValue(attributeQuestion, question);
			
			if(use == QAPair.Both || use == QAPair.AnswerOnly)
				customInstance.setValue(attributeAnswer, answer);

			// now add it to the instance
			instanceInfo.add(customInstance);

		}

		// set the class index as the last attribute
		instanceInfo.setClassIndex(instanceInfo.numAttributes() - 1);

	}

	private void EvaluateClassifiers(String currentCategory, QAPair use) {

		try {
			System.out.println(" Starting evaluation...");

			watch.start();
			// System.out.println("Total Instances: " +
			// instanceInfo.numInstances());

			// must convert text to word vector for ML
			StringToWordVector filter = new StringToWordVector();

			if (use == QAPair.Both) {
				filter.setAttributeIndicesArray(new int[] { 0, 1 });

				// store the constructed arff file per category
				PerceptionLearner.saveInputARFF(
						String.format(DataPreparer.projPath+"input_both_%s.arff", currentCategory),
						this.instanceInfo);
			} else if (use == QAPair.QuestionOnly) {
				filter.setAttributeIndicesArray(new int[] { 0 });

				// store the constructed arff file per category
				PerceptionLearner.saveInputARFF(String.format(
						DataPreparer.projPath+"input_questionOnly_%s.arff", currentCategory),
						this.instanceInfo);
			} else if (use == QAPair.AnswerOnly) {
				filter.setAttributeIndicesArray(new int[] { 0 });

				// store the constructed arff file per category
				PerceptionLearner.saveInputARFF(String.format(
						DataPreparer.projPath+"input_answerOnly_%s.arff", currentCategory),
						this.instanceInfo);
			}

			filter.setInputFormat(instanceInfo);
			filter.setTFTransform(true);
			filter.setIDFTransform(true);

			//filter.setDoNotOperateOnPerClassBasis(true);
			filter.setWordsToKeep(10000);
			filter.setOutputWordCounts(true);
			filter.setStopwords(new File(DataPreparer.projPath+"stoplist.txt"));

			NGramTokenizer ngTokenizer = new NGramTokenizer();
			ngTokenizer.setNGramMinSize(1);
			ngTokenizer.setNGramMaxSize(1);
			ngTokenizer.setDelimiters("\\W");

			filter.setTokenizer(ngTokenizer);

			FilteredClassifier fbayes = new FilteredClassifier();

			fbayes.setFilter(filter);
			fbayes.setClassifier(new NaiveBayes());

			StringBuffer output = new StringBuffer();
			PlainText display = new PlainText();
			display.setBuffer(output);

			ClassifierInfo ci = new ClassifierInfo("Naive Bayes", fbayes);
			ci.evaluation = new Evaluation(instanceInfo);
			ci.evaluation.crossValidateModel(fbayes, instanceInfo, 4,
					new Random(1), display);

			// reset classifier for now - this may change later
			classifiers = new ArrayList<ClassifierInfo>();
			classifiers.add(ci);

			System.out.println(ci.evaluation.toSummaryString());
			System.out.println(ci.evaluation.toClassDetailsString());

			// System.out.println(output.toString());

			System.out.println(ci.evaluation.toMatrixString(currentCategory));

			watch.stop();
			System.out.println("Evaluation took " + watch.toString());		
			

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

	}

	private void TrainClassifiers(String currentCategory, QAPair use)
			throws Exception {

		try {
			
			String modelName = DataPreparer.projPath;
			
			if (use == QAPair.Both) {
				modelName+= String.format("category_both_%s.model",
						currentCategory);
			}
			else if (use == QAPair.QuestionOnly) {

				modelName += String.format("category_questionOnly_%s.model",
						currentCategory);
			}
			if (use == QAPair.AnswerOnly) {
				modelName = String.format("category_answerOnly_%s.model",
						currentCategory);
			}
			
			
			for (ClassifierInfo ci : classifiers) {
				ci.filteredClassifier.buildClassifier(instanceInfo);
				
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
							modelName));				

				out.writeObject(ci.filteredClassifier);
				out.close();

				System.out.println(String.format(
						"successfully saved model %s", modelName));
			}

		} catch (IOException e) {
			System.out.println("Could not save model for category: "
					+ currentCategory);
		}

	}

	private FilteredClassifier GetTrainedModel(String currentCategory, QAPair use) {
		try {
			
			String modelName = DataPreparer.projPath;
			if(use == QAPair.Both)
			{
				modelName += String.format("category_both_%s.model", currentCategory);
			}
			else if(use == QAPair.QuestionOnly)
			{
				modelName += String.format("category_questionOnly_%s.model", currentCategory);
			}
			else if(use == QAPair.AnswerOnly)
			{
				modelName += String.format("category_answerOnly_%s.model", currentCategory);
			}
			
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(
					modelName));
			
			Object model = is.readObject();

			is.close();

			return (FilteredClassifier) model;
		} catch (Exception ex) {
			System.err.println("Could not get the trained model for category "
					+ currentCategory);

			return null;
		}

	}

	public Result classifyQuestionAnswerPair(String question, String answer,
			String[] categories,  boolean splitQuestionAnswerPair) throws Exception {

		// this will probably be multi-threaded, but for simplicity we will do
		// it sequential

		String currentCategory = "all";
		
		Result theResult = new Result(question, answer);

		if (!splitQuestionAnswerPair) {
			
			this.PrepareAttributeList(categories, QAPair.Both);

			this.PrepareStudyInstances(question, answer, currentCategory,
					QAPair.Both);

			// now we are all set to test our model against this instance :)
			FilteredClassifier classifier = this.GetTrainedModel(
					currentCategory, QAPair.Both);

			String predicted = this.instanceInfo.classAttribute().value(
					(int) classifier.classifyInstance(this.instanceInfo
							.instance(0)));

			System.out.println("Predicted: " + predicted);		
			
			

			double[] distribution = classifier
					.distributionForInstance(this.instanceInfo.instance(0)); // we
																				// only
																				// have
																				// one
																				// question/answer
																				// pair
			
			int classPositionIndex = 0;		
			for (double d : distribution) {

				
				
				String p = this.instanceInfo.classAttribute().value(
						(int) classPositionIndex);

				classPositionIndex++;
				
				if(d<=0.0) continue; //skip
				theResult.categories.add(theResult.new Category(String.format("%s", d), p));
				
				System.out.println(String.format(
						"Could also be:  %s with confidence %.15f (%s)", p, d, d));
			}
			
			return theResult;

		}
		else
		{
			
			this.PrepareAttributeList(categories, QAPair.QuestionOnly);

			this.PrepareStudyInstances(question, answer, currentCategory,
					QAPair.QuestionOnly);

			// now we are all set to test our model against this instance :)
			FilteredClassifier classifier = this.GetTrainedModel(
					currentCategory, QAPair.QuestionOnly);

			String predictedQ = this.instanceInfo.classAttribute().value(
					(int) classifier.classifyInstance(this.instanceInfo
							.instance(0)));

			System.out.println("Question predicted as: " + predictedQ);

			double[] distribution_q = classifier
					.distributionForInstance(this.instanceInfo.instance(0)); // we
																				// only
																				// have
																				// one
																				// question/answer
																				// pair
			this.PrepareAttributeList(categories, QAPair.AnswerOnly);

			this.PrepareStudyInstances(question, answer, currentCategory,
					QAPair.AnswerOnly);

			// now we are all set to test our model against this instance :)
			classifier = this.GetTrainedModel(
					currentCategory, QAPair.AnswerOnly);
			
			String predictedA = this.instanceInfo.classAttribute().value(
					(int) classifier.classifyInstance(this.instanceInfo
							.instance(0)));

			System.out.println("Answer predicted as: " + predictedQ);

			double[] distribution_a = classifier
					.distributionForInstance(this.instanceInfo.instance(0)); // we
																				// only
																				// have
																				// one
																				// question/answer
																				// pair

			int classPositionIndex = 0;
			for (double d : distribution_q) {

				String p = this.instanceInfo.classAttribute().value(
						(int) classPositionIndex);
				
				d = d+distribution_a[classPositionIndex];
				
				d=d/2;

				classPositionIndex++;
				
				if(d<=0.0) continue; //skip
				theResult.categories.add(theResult.new Category(String.format("%s", d), p));

				System.out.println(String.format(
						"Could also be:  %s with confidence %.15f (%s)", p, d, d));
				System.out.println(String.format(
						"Could also be:  %s with confidence %.15f (%s)", p, distribution_a[classPositionIndex-1], distribution_a[classPositionIndex-1]));
			}
		}
		return theResult;

	}



}
