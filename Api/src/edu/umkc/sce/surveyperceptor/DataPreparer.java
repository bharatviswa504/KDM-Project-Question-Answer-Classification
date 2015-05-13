package edu.umkc.sce.surveyperceptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stax.StAXSource;

import org.apache.commons.csv.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.berkeley.nlp.util.StringUtils;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class DataPreparer {
	
	//public static String projPath = "C:/Users/mali/SurveyClassifier/.metadata/.plugins/org.eclipse.wst.server.core/tmp1/wtpwebapps/KDMPro1/WEB-INF/classes/";
	                                 
	public static String projPath = "/home/group1/KdmPro//";
	
//	String basefile = this.getClass().getClassLoader()
//			.getResource("yahoocat.txt").toString().replace("file:/", "").replace("yahoocat.txt", "");
//	

	public static void IsolateCSVFields(int[] indices, String fileName) {

		String[] line = new String[indices.length];

		try {

			CSVParser csv = CSVParser.parse(new File(fileName),
					Charset.defaultCharset(), CSVFormat.EXCEL);

			CSVPrinter print = new CSVPrinter(new FileWriter("clean_"
					+ fileName), CSVFormat.EXCEL);
			
			

			for (CSVRecord record : csv.getRecords()) {
				for (int i = 0; i < indices.length; i++) {
					if (record.get(indices[i]).length() > 1)
						line[i] = record.get(indices[i]);// .trim().replaceAll("[^a-zA-z0-9 ]","");
					else
						line[i] = "";

				}

				print.printRecord(line);
			}
			
			print.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Prep done");
	}

	public static CSVParser getCSV(String fileName) throws IOException {
		
		return CSVParser.parse(new File(fileName),
				Charset.defaultCharset(), CSVFormat.DEFAULT);
	}
	

	public static void ExtractTrainingData(int startAt, int instanceNum) throws Exception
	{

		
		FileWriter fw = new FileWriter(new File(DataPreparer.projPath+"yahooqa_Testing.csv"));
		FileWriter cw = new FileWriter(new File(DataPreparer.projPath+"yahoocat.txt"));
		
		int position = 0;
		
		String lines = "";
		ArrayList<String> cats = new ArrayList<>();
		int countLines = 0;
		XMLInputFactory xif = XMLInputFactory.newInstance();
		//small_sample.xml
		//"C:\\Users\\mali\\CS5560\\dataset\\Webscope_L6-1\\FullOct2007.xml.part1\\FullOct2007.xml.part1"
		 XMLStreamReader xsr = xif.createXMLStreamReader(new FileReader("C:\\Users\\mali\\CS5560\\dataset\\Webscope_L6-1\\FullOct2007.xml.part1\\FullOct2007.xml.part1"));
	        xsr.nextTag(); // Advance to statements element

	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer t = tf.newTransformer();
	        while(xsr.nextTag() == XMLStreamConstants.START_ELEMENT) {
	        	
      	
	            DOMResult result = new DOMResult();
	            t.transform(new StAXSource(xsr), result);
	            Node domNode = result.getNode();
	            if(domNode.getNodeName().contains("document"))
	            {
	            	
	            	Node doc = domNode.getFirstChild().getFirstChild();
	            	NodeList node = doc.getChildNodes();
	            	
	            	String line = "";
	            	String subject = null, question = null, answer = null, cat = null;
	            	for(int i=0; i<node.getLength(); i++)
	            	{
	            		
	            		if(node.item(i).getNodeName().contains("content"))
		            		question =  node.item(i).getTextContent().replaceAll("\"", "");
	            		if(node.item(i).getNodeName().contains("subject"))
		            		subject =  node.item(i).getTextContent().replaceAll("\"", "");
	            		else if(node.item(i).getNodeName().equals("bestanswer"))
		            		answer =  node.item(i).getTextContent().replaceAll("\"","");
	            		else if(node.item(i).getNodeName().equals("maincat"))
	            		{
		            		cat = StringUtils.stripNonAlphaNumerics(node.item(i).getTextContent());
		            		
		            		if(org.apache.commons.lang.StringUtils.isBlank(cat))
		            			break; //skip unlabeled instance
		            		
		            		//add category if not already
		            		if(!cats.contains(cat))
		            		{
		            			cats.add(cat);
		            		}
		            		break;//I got all I needed. proceed to next
	            		}
	            	}
	           
		        	if(++position < startAt) //skip all lines until you hit StartAt
		        	{
		        		System.out.println("skipping element @ " + position);
		        		continue;
		        		
		        	}
		        	else
		        	{
		  
		            	line = edu.stanford.nlp.util.StringUtils.toCSVString(new String[]{(question!=null? question : subject), (answer!=null?answer:""), (cat!=null?cat:"")});//*/String.format("%s,%s,%s", (question!=null? question : subject), answer, cat);
		            	line = line.replaceAll("\\s+|<br />", " ");
		            	//if(cat.contains("News"))
		            	lines += StringEscapeUtils.unescapeXml(line)+"\r\n";
		            	countLines++;
		        	}
	            	
	            	if(countLines % 100==0) //write each 100 lines once
	            	{
	            		
	            		fw.write(lines);
	            		lines = "";
	            	}
		        	
	            	
	            	if(countLines % instanceNum==0) // we have acheived the number of instances wanted.
	            	{
	            		fw.close();
	            		break;
	            	}

	            }
	            
	           
	        }
	        
	        //now write categories separated by comma
	        cw.write(StringUtils.join(cats, ","));
	        cw.close();
	        
	        System.out.println("input successfully extracted and ready");
	        
	}
	
	public Instances loadDatasetFromArff(String fileName) throws IOException {

		BufferedReader breader = new BufferedReader(new FileReader(fileName));
		ArffReader arff = new ArffReader(breader);
		Instances returnDataSet = arff.getData();
		breader.close();
		
		return returnDataSet;

	}
	
	public static String[] readCategoriesFromFile(String fileName) throws Exception{
		
		//expected file contains one line of comma separated category labels
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String[] rtn = br.readLine().split(",");		
		br.close();
		
		return rtn;
		
	}
}
