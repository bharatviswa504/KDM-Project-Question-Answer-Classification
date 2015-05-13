package hw2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

public class apriori {

	public static void main(String[] args) throws IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		System.out.println("Enter the filepath");
		try
		{
			input = reader.readLine();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		String filePath = input;
		filePath = filePath.replace("\\", "/");
		System.out.println("Please enter the minimum support to calculate Frequent itemsets");
		//try to get users input, if there is an error print the message
		try
		{
			input = reader.readLine();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		int supp = Integer.parseInt(input);
		
		System.out.println("Please enter the minimum Confidence to calculate strong Association Rules");
		reader = new BufferedReader(new InputStreamReader(System.in));
		input ="";
		try
		{
			input = reader.readLine();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		int conf = Integer.parseInt(input);
		
		
		LinkedHashMap<String, ArrayList<String>> rules = new LinkedHashMap<String, ArrayList<String>>(); // Hashmap to store all the frequent rules
		LinkedHashMap<String, TreeSet<Integer>> freq = new LinkedHashMap<String, TreeSet<Integer>>();    //Hashmap to store ith level frequent itemsets 
		LinkedHashMap<String, TreeSet<Integer>> finalFreq = new LinkedHashMap<String, TreeSet<Integer>>();// Hashmap to store all frequent itemsets
		LinkedHashMap<String, TreeSet<Integer>> tempFreq = new LinkedHashMap<String, TreeSet<Integer>>();// Hashmap to store ith level frequent itemsets temporarily
		Set<String>itemsetForRules = new TreeSet<String>();// set to store rules in body ---> head form
		String line;
		String wholeFile [][] =new String[100][102] ; 
		BufferedReader br = new BufferedReader(new FileReader(filePath+"/association-rule-test-data.txt"));
		int i=0,j=0;
		while((line = br.readLine())!=null)
		{	
			String columns[] = line.split("\t");
			i=0;
			for (String string : columns) {
				wholeFile[j][i]=string;
				i++;
			}
			j++;
		}
		TreeSet<Integer> temp= new TreeSet<Integer>();
		
		
		//level 1 
		for(j=1;j<102;j++)
		{
			for(i=0;i<100;i++)
			{
				String key1 = "G"+j+" UP";
				String key2 = "G"+j+" DOWN";
				if(wholeFile[i][j].equalsIgnoreCase("UP"))
				{
					if(freq.containsKey(key1))
					{
						temp = freq.get(key1);
						temp.add(i+1);
					}
					else
					{
						temp =new TreeSet<Integer>();
						temp.add(i+1);
						freq.put(key1, temp);
					}
				}
				else if(wholeFile[i][j].equalsIgnoreCase("DOWN"))
				{
					if(freq.containsKey(key2))
					{
						//	temp=new ArrayList<Integer>();
						temp = freq.get(key2);
						temp.add(i+1);
					}
					else
					{
						temp =new TreeSet<Integer>();
						temp.add(i+1);
						freq.put(key2, temp);
					}
				}else{ //code for disease column
					String temp1 = wholeFile[i][j].replaceAll("\\s", "_");
					String keyx = "G"+j+" "+temp1.toUpperCase();				//change!

					if(freq.containsKey(keyx))
					{
						temp = freq.get(keyx);
						temp.add(i+1);
					}
					else
					{
						temp =new TreeSet<Integer>();
						temp.add(i+1);
						freq.put(keyx, temp);
					}
				}
			}
		}

		Prune(freq, supp);

		System.out.println("NUMBER OF 1-ITEMSETS CALCULATED : "+freq.size());
		//finished creating 1-itemsets

		int z = 1;
		while(true){
			int count = 0;
			tempFreq = new LinkedHashMap<String, TreeSet<Integer>>();
			finalFreq.putAll(freq);
			tempFreq.putAll(freq);
			freq = new LinkedHashMap<>();



			Set cand =  tempFreq.keySet();
			String newkey = null;
			TreeSet<Integer> x1 ;
			TreeSet<Integer> x2 ;

			for(i=0;i<cand.size();i++){
				for (j=i+1;j<cand.size();j++){
					String m1 = Sort((String)cand.toArray()[i]);
					String m2 = Sort((String)cand.toArray()[j]);
					newkey = Join(m1.trim(),m2.trim());

					if(newkey!=null)
					{
						if(!newkey.equals("continue"))  
						{
							x1 = tempFreq.get(cand.toArray().clone()[i]);
							x2 = tempFreq.get(cand.toArray().clone()[j]);

							TreeSet<Integer> ts = new TreeSet<Integer>();
							ts = Check(x1,x2);
							if(ts!=null){

								if(ts.size()>=supp){
									freq.put(newkey,ts); //putting selfjoined itemsets
									count++;
								}
							}
						}

					}
					else
						break;
				}
			}

			if(freq.size()==0){ // if hashmap contains 0 frequent ittemsets after pruning break
				System.out.println("TOTAL NUMBER OF FREQUENT ITEMSETS : "+ finalFreq.size());
				break;
			}

			z++;

			System.out.println("NUMBER OF "+z+"-ITEMSETS CALCULATED : "+count);
			
		}
		
		itemsetForRules = finalFreq.keySet();
		StringCombi strcmb = new StringCombi();
		System.out.println("\nStrong association rules are\n");

		for (String itemset : itemsetForRules) {
			if(itemset.split(" ").length>2)
			{
				TreeMap<String, ArrayList<String>> tempRuleMap  = strcmb.findCombinations(itemset,finalFreq,conf);// find all permutations of given itemset

				// Add the generated rules into rules hashmap
				for (Entry<String, ArrayList<String>> entry : tempRuleMap.entrySet()) {
					if(rules.containsKey(entry.getKey()))
					{
						ArrayList<String> tempArrayList1 = rules.get(entry.getKey());
						ArrayList<String> tempArrayList2 = tempRuleMap.get(entry.getKey());
						tempArrayList1.addAll(tempArrayList2);
					}
					else
					{
						rules.put(entry.getKey(),entry.getValue());
					}
				}

			}
		}
		int rulecount =0;
		
		
		for (Entry<String, ArrayList<String>> entry : rules.entrySet()) {
			rulecount = rulecount+entry.getValue().size();
		}
		
		//generate rules of the form body --> head from rules hashmap
		ArrayList<String> listOfCompleteRules = new ArrayList<String>();
		for (Entry<String, ArrayList<String>> entry : rules.entrySet()) {
			ArrayList<String> listOfHeads = entry.getValue();
			for (String head : listOfHeads) {
				listOfCompleteRules.add(entry.getKey()+" --> "+head);
			}
		}
	
		for (String string : listOfCompleteRules) {
			System.out.println(string);
		}
		System.out.println("\nTotal number of strong association rules are = "+listOfCompleteRules.size()+"\n");
		//
		System.out.println("Please enter the template");
		reader = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			input = reader.readLine();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		if(input!=null){
			template t = new template(input,rules,listOfCompleteRules);
			t.compute();
		}
	}
	
	
	/*
	 * Pruning of level 1 frequent itemsets.
	 */
	public static void Prune(LinkedHashMap<String, TreeSet<Integer>> upHash, int supp)
	{
		for(Iterator<Map.Entry<String,TreeSet<Integer>>>it=upHash.entrySet().iterator();it.hasNext();){
			Map.Entry<String, TreeSet<Integer>> entry = it.next();
			if (entry.getValue().size() < supp) {
				it.remove();
			}
		}
	}

	
	/*
	 * Joining of two (i-1)level itemsets to generate ith level itemset
	 */
	public static String Join(String str1, String str2){
		String result = "";
		String[] x1=str1.split("\\s");
		String[] x2=str2.split("\\s");
		for(int i=0; i<x1.length-2;i++){
			if(x1[i].equals(x2[i])){
				result = result +" "+ x1[i];
			}else {
				return null;
			}
		}
		if(x1[x1.length-2].equals(x2[x1.length-2])){
			return "continue";
		}else{
			String temps=x2[x1.length-2]+" "+x2[x1.length-1]+" "+x1[x1.length-2]+" "+x1[x1.length-1];
			temps = Sort(temps);
			result = result.trim() +" "+ temps.trim();
		}

		return result.trim();
	}

	public static String Sort(String key1){
		TreeMap<Integer, String> sortedMap = new TreeMap<Integer, String>();
		String[] tempsplit1 = key1.split("\\s");

		String temp =null;
		int x;
		for(int i =0; i<tempsplit1.length; i=i+2){

			temp = tempsplit1[i].replace("G","");
			x = Integer.parseInt(temp);
			sortedMap.put(x, tempsplit1[i]+" "+tempsplit1[i+1]);
		}
		key1 = "";
		for(Entry<Integer,String> e: sortedMap.entrySet()){
			key1 = key1 +" "+ e.getValue();
		}

		return key1;
	}

	
	//find the intersection of two frequent itemsets. 
	public static TreeSet Check(TreeSet x1,TreeSet x2){
		TreeSet<Integer> ts = new TreeSet<Integer>();
		for(Iterator<Integer>it=x1.iterator();it.hasNext();){
			int t = it.next();
			if(x2.contains(t))
				ts.add(t);
		}
		if(ts.size()==0)
			ts=null;
		return ts;
	}
	
}
