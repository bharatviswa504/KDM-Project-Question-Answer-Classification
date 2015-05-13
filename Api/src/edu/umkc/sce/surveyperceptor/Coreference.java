package edu.umkc.sce.surveyperceptor;

import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.ling.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Coreference {
	
	public static void findCoreference()
	{
		 Properties props = new Properties();
		 props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		 StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		 
		 Annotation targetDoc = new Annotation("I like Providenciales best. Beautiful beaches are scattered on all sides of Providenciales, the most spectacular of which is a 12 mile stretch located on Grace Bay, which is protected by a healthy barrier reef. Provo has an 18 hole golf course, a casino, shopping centres, three marinas, a growing number of of bars and excellent restaurants. Provo is also a divers\' and water lovers\' paradise.");
		 
		 pipeline.annotate(targetDoc);
		 
		 
		 Map<Integer, CorefChain> graph = targetDoc.get(CorefChainAnnotation.class);
		 System.out.println(graph);
	}

}
