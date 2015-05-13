package edu.umkc.sce.surveyperceptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;




import weka.classifiers.meta.FilteredClassifier;
import edu.berkeley.nlp.util.StopWatch;


/*******************************************
 * This class verifies/validates text classification. It will use the text
 * classifer to construct input and prepare dataset (testing in this case).
 * Using the trained model, it will then use the testing data to verify
 * algorithm.
 ********************************************/

public class TextValidator {

	FileWriter fw;

	StopWatch watch;	

	TextClassifier tclassifier;

	public TextValidator() {

		tclassifier = new TextClassifier();
		try {
			fw = new FileWriter(new File("yahooqa_Testing_Result.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		watch = new StopWatch();
	}

	public void startVerification(String inputFile, String[] categories,
			boolean splitCategories, boolean splitQuestionAnswerPair) {

		if (!splitCategories) {
			String category = "all";
			// multi-class classification

			if (!splitQuestionAnswerPair) {
				verifyMultiClassClassification(inputFile, categories, category,
						QAPair.Both);
			} else {
				verifyMultiClassClassification(inputFile, categories, category,
						QAPair.QuestionOnly);

				verifyMultiClassClassification(inputFile, categories, category,
						QAPair.AnswerOnly);
			}

		} else {
			for (String category : categories) {

				verifySingleClassClassification(inputFile, category,
						QAPair.Both);
			}
		}

	}

	private void verifySingleClassClassification(String inputFile,
			String category, QAPair use) {

		try {
			this.tclassifier.PrepareAttributeList(category, use);

			this.tclassifier.PrepareStudyInstances(inputFile, category, use);

			

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

	}

	
	private void verifyMultiClassClassification(String inputFile,
			String[] categories, String category, QAPair use) {

		try {

			this.tclassifier.PrepareAttributeList(categories, use);

			this.tclassifier.PrepareStudyInstances(inputFile, category, use);
			
			FilteredClassifier trainedClassifier = this.GetTrainedModel("all",
					QAPair.Both);
			
			int correct =0;
			
			String lines = "question,answer,actual,predicted,verified";

			for(int i = 0; i< this.tclassifier.instanceInfo.numInstances(); i++)
			{
				String actual = this.tclassifier.instanceInfo.classAttribute()
						.value((int)this.tclassifier.instanceInfo
						.instance(i).classValue());
				
			String predicted = this.tclassifier.instanceInfo.classAttribute()
					.value((int) trainedClassifier
							.classifyInstance(this.tclassifier.instanceInfo
									.instance(i)));
			
			String question = this.tclassifier.instanceInfo.instance(i).stringValue(0);
			String answer = this.tclassifier.instanceInfo.instance(i).stringValue(1);
			
			if(predicted == actual)
			{
				correct++;
				lines +=  edu.stanford.nlp.util.StringUtils.toCSVString(new String[]{question, answer, actual, predicted, "1"})+"\r\n";
			}
			else
			{
				lines +=  edu.stanford.nlp.util.StringUtils.toCSVString(new String[]{question, answer, actual, predicted,""}) +"\r\n";
			}
			
			
			
			System.out.println(String.format("Question= %s Actual = %s Predicted = %s",question, actual, predicted));
			
			
//			double[] distribution = trainedClassifier
//					.distributionForInstance(this.tclassifier.instanceInfo
//											.instance(i));
//			String otherPredictions="";
//			
//			for(double d: distribution)
//			{
//				
//				String p =this.tclassifier.instanceInfo.classAttribute()
//						.value((int)d);
//				if(predicted != p)
//				{
//					otherPredictions += "[" + p + "] ";
//				}
//			}
			
			//System.out.println("Could also be: " + otherPredictions);
			}
			
			System.out.println("Overall accuracy: " + correct *100/this.tclassifier.instanceInfo.numInstances() + "%");
			
        	fw.write(lines);
        	lines = "";
        	fw.close();
        	

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

	}

	private FilteredClassifier GetTrainedModel(String currentCategory,
			QAPair use) {
		try {

			String modelName = null;
			if (use == QAPair.Both) {
				modelName = String.format("category_both_%s.model",
						currentCategory);
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

	// public void classifyQuestionAnswerPair(String question, String answer,
	// String[] categories, QAPair use)
	// throws Exception {
	//
	// // this will probably be multi-threaded, but for simplicity we will do
	// // it sequential
	//
	// String currentCategory = "all";
	// this.PrepareAttributeList(categories, QAPair.Both);
	//
	// this.PrepareStudyInstances(question, answer, currentCategory,
	// QAPair.Both);
	//
	// // now we are all set to test our model against this instance :)
	// FilteredClassifier classifier = this.GetTrainedModel(currentCategory,
	// QAPair.Both);
	//
	// String predicted = this.instanceInfo.classAttribute()
	// .value((int) classifier.classifyInstance(this.instanceInfo
	// .instance(0)));
	//
	//
	//
	// System.out.println("Predicted: " + predicted);
	//
	// }

}
