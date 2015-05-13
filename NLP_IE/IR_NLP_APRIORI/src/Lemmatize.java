
import java.util.HashMap;
import java.util.Map;

import dragon.nlp.tool.lemmatiser.*;

public class Lemmatize {
	
	private EngLemmatiser lemmatizer;
	private Map<String, Integer> tagLookUp = new HashMap<String, Integer>();

	public Lemmatize() {
		init();
	}

	/**
	 * @param value original word
	 * @param pos the part of speech of the last word
	 * @return the lemma of original word
	 */
	public String getLemma(String value, String pos) {
		int POS = tagLookUp.get(pos);
		if (POS == 0)
			return lemmatizer.lemmatize(value);
		else
			return lemmatizer.lemmatize(value, POS);
	}

		private void init() {
		lemmatizer = new EngLemmatiser("C:/Users/Bharat/workspace1/IR_NLP_APRIORI/lib/lemmatizer",false,true);
		tagLookUp.put("NN", 1);
		tagLookUp.put("NNS", 1);
		tagLookUp.put("NNP", 1);
		tagLookUp.put("NNPS", 1);
		tagLookUp.put("VB", 2);
		tagLookUp.put("VBG", 2);
		tagLookUp.put("VBD", 2);
		tagLookUp.put("VBN", 2);
		tagLookUp.put("VBP", 2);
		tagLookUp.put("VBZ", 2);
		tagLookUp.put("JJ", 3);
		tagLookUp.put("JJR", 3);
		tagLookUp.put("JJS", 3);
		tagLookUp.put("RB", 4);
		tagLookUp.put("RBR", 4);
		tagLookUp.put("RBS", 4);
	}

}
