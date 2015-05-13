import java.io.BufferedReader;


import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.chunker.*;
import rita.wordnet.*;
import dragon.nlp.tool.lemmatiser.*;

public class ExtractionPhase {
	public static void main(String args[]) throws InvalidFormatException, IOException
	{
		try
		{
			
			System.out.println("TFIDF Input Preparing Started");
			TFIDFInputPrepare makeInput = new TFIDFInputPrepare();
			makeInput.inputPrepare();
			System.out.println("TFIDF Input Preparing End");
			
			System.out.println("TFIDF Started");
			TFIDF tfidfObj = new TFIDF();
			tfidfObj.main();
			System.out.println("TFID Ended");
			
			
			//Before start deleting old output generated file
			System.out.println("Extraction Phase and NLP Started");
			File output = new File("Output.csv");
			output.delete();
			extractTokens();
			System.out.println("End of Extraction Phase and NLP");
			
			
			
			System.out.println("Final Phase of Extraction Comparision NLP Tokens with TFIDF Output");
			tfIdfComponent();
			System.out.println("End of Final Phase");
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	
	}
	
	public static void tfIdfComponent()
	{
		
		//TFIdf output File
		String tfIdfOutputFile ="output1//TfIdf_ALGORITHM.txt";
		
		
		try {
		
			FileReader tfidfFile = new FileReader(tfIdfOutputFile);
			BufferedReader br = new BufferedReader(tfidfFile);
			String readLine ="";
			
			int counter = 0;
			int topMostWordCount = 1500;
			int k=0;
			int wordCounter = 0;
			
			//HashMap tfIdfWordMap = new HashMap();
			String[] tfIdfWordMap = new String[40000];
			while((readLine = br.readLine()) != null)
			{
				if(counter==topMostWordCount)
					break;
				readLine = readLine.replace("|", ",");
				String tokens[] = readLine.split(",");
				for(k=0;k<tokens.length-1;k++)
				{	
					tokens[k] = tokens[k].replace(" ", "");
				//	tfIdfWordMap.put(tokens[k], 1);
					
					tfIdfWordMap[wordCounter++] = tokens[k];
				}
				
				tokens[k] = tokens[k].replace("\t\t\t",",");
				String tokens1[] = tokens[k].split(",");
				//tfIdfWordMap.put(tokens1[0], 1);
				tfIdfWordMap[wordCounter++]= tokens1[0];
			
				counter++;
			}
			System.out.println("after hash");
			System.out.println(wordCounter);
			String outputFile = "output1/Output.csv";
			BufferedReader br1 = null;
			String cvsSplitBy = ",";
			FileReader outputReader = new FileReader(outputFile);
			br1 = new BufferedReader(outputReader);
			
			
			String finalOutputFile = "output1//FinalOutput.csv";
			
			FileWriter writer = new FileWriter(finalOutputFile);
			 
			
			while((readLine = br1.readLine()) != null)
			{
				String[] nlpTokens = readLine.split(cvsSplitBy);
				System.out.println("Tokens Length is" + nlpTokens.length);
				for(int i=0;i<nlpTokens.length;i++)
				{
					
					for(int l=0;l<wordCounter;l++)
					{
						//System.out.println(nlpTokens[i]);
						//System.out.println(tfIdfWordMap[l]);
					//if(tfIdfWordMap.containsKey(nlpTokens[i]))
						if(nlpTokens[i].contains(tfIdfWordMap[l]))
						{
							//System.out.println("matched : token " + nlpTokens[i]);
							writer.append(nlpTokens[i]);
							writer.append(",");
							break;
						}
					}
				}
				writer.append("\n");
				System.out.print(wordCounter);
			//	writer.flush();
			}
			
			writer.flush();
			writer.close();
			br.close();
			br1.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("error");
		}
		System.out.println("done");
	}
		
		

	
	public static void extractTokens() throws IOException {
		
		
		try {
			//	RiWordnet wordnet = new RiWordnet(null);
		
			//Parts of Speech
			POSModel model = new POSModelLoader()	
				.load(new File("en-pos-maxent.bin"));
			
			//Chunker
			InputStream chunkerStream = new FileInputStream("en-chunker.bin");
			ChunkerModel chunkerModel = new ChunkerModel(chunkerStream);
		 
			ChunkerME chunkerMe = new ChunkerME(chunkerModel);
			
			
			// Tokenizer
			InputStream is = new FileInputStream("en-token.bin");
			 
			TokenizerModel tokenModel = new TokenizerModel(is);
		 
			Tokenizer tokenizer = new TokenizerME(tokenModel);
		
			
			PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
			POSTaggerME tagger = new POSTaggerME(model);
		 
				
			int fileCount =1;
			
			
		//	String inputFile="input\\input";
			//String inputIterator = "";
			
			
			String csvOutputFile = "output1/Output.csv";
			FileWriter outputWriterCsv = new FileWriter(csvOutputFile);
			
	
			
			String input;
			perfMon.start();
			//VB VBG VBD VBN VBP VBZ
			String neededPos= "JJ JJR JJ NN NNP NNPS  RB RBR RBS";
			
		
			
			
		//	int inputFileCount=45;
			
			File folder = new File("input1");
			File[] listOfFiles = folder.listFiles();

			    for (int k = 0; k < listOfFiles.length; k++) {
			      if (listOfFiles[k].isFile()) {
				//	inputIterator = inputFile + fileCount + ".txt";
					File file = new File("input1\\"+listOfFiles[k].getName());
				//	System.out.println(listOfFiles[k].getName());
					FileInputStream fis = new FileInputStream(file);
					byte[] data = new byte[(int) file.length()];
					fis.read(data);
					fis.close();
		
					input = new String(data, "UTF-8");
				//	input = "Malignant Cancer Tumor a large family of diseases that involve abnormal cell growth with the potential to invade or spread to other parts of the body";
		
					
					String tokens[] = tokenizer.tokenize(input);
					
					String[] tags = tagger.tag(tokens);
					
					Lemmatize lemmatize = new Lemmatize();
					
			//		RiWordnet wordnet = new RiWordnet(null);
					
					
					HashMap<String,String[]> synonymsMap = new HashMap<String,String[]>();
					
					for(int i=0;i<tokens.length;i++)
					{
						if(neededPos.contains(tags[i])) {
						tokens[i]= lemmatize.getLemma(tokens[i], tags[i]);
					//	synonymsMap.put(tokens[i], wordnet.getAllSynonyms(tokens[i],tags[i]));
						}
					}
						
			
					String[] chunkerPos = chunkerMe.chunk(tokens, tags);
								
					Span[] span = chunkerMe.chunkAsSpans(tokens, tags);
					String[] chunkStrings = Span.spansToStrings(span, tokens);
				//	System.out.print("NLP" +chunkStrings.length+"         ");
			
					String[] topicName = listOfFiles[k].getName().split("-");
					
					outputWriterCsv.write(topicName[0]);
					outputWriterCsv.write(",");
					for(int i=0;i<chunkStrings.length;i++)
					{
						outputWriterCsv.write(chunkStrings[i]);
						outputWriterCsv.write(",");
			
					}
					
					outputWriterCsv.write("\n");
					
					perfMon.incrementCounter();
					fileCount++;
			
				}
			    }
				outputWriterCsv.flush();  
				outputWriterCsv.close();
				//br.close();
			    
			}
			catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
	}
}