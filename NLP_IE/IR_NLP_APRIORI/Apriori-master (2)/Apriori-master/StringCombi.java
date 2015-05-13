package hw2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

public class StringCombi {
	public static List<String> permutations(final List<String> words)
	{
		final List<String> perms = new ArrayList<String>();

		if (words.size() == 1)
		{
			perms.add(words.get(0));
		}
		else
		{
			for (final String head : words)
			{
				for (final String permutation : permutations(subList(head, words)))
				{
					perms.add(head + " "+permutation);
				}
			}
		}
		return perms;        
	}

	public static List<String> subList(final String elementToRemove, final List<String> elements)
	{
		final List<String> subList = new ArrayList<String>();
		for (final String s : elements)
		{
			if (!s.equals(elementToRemove))
			{
				subList.add(s);
			}
		}
		return subList;
	}


	public TreeMap<String, ArrayList<String>> findCombinations(String t,LinkedHashMap<String, TreeSet<Integer>> finalFreq,int minConf)
	{
		/***
		 *  head variable represents body of rule and body variable represents head of the rule.
		 */
		
		TreeMap<String, ArrayList<String>> rules = new TreeMap<>();
		//String t = "G1 UP G2 DOWN G3 UP G4 UP";
		String tempstr[] = t.split(" ");
		List l = new ArrayList<String>();
		for(int i=0;i<tempstr.length;i++)
		{
			String temps = tempstr[i]+" "+tempstr[i+1];
			temps=temps.trim();
			l.add(temps);
			i++;
		}

		for (final String s : permutations(l))
		{
			String temp[] = s.split(" ");
			String[] temp1= new String[temp.length/2];
			for(int i=0,j=0;i<temp1.length;i=i+1,j=j+2)
			{
				temp1[i]=temp[j]+" "+temp[j+1];
			}

			int j=0;
			int i;
			String head=null,body=null;
			while(j!=temp1.length-1)
			{
				head=null;body=null;
				for(i = 0;i<=j;i++)
				{
					if(head==null)
						head = temp1[i]+" ";
					else
						head = head+temp1[i]+" ";
				}

				head = Sort(head);
				head = head.trim(); ///// body of the rule
				int denom = finalFreq.get(head).size();
				head.replaceAll("^\\s+", "");
				for(i=j+1;i<temp1.length;i++)
				{
					if(body==null)
						body=temp1[i]+" ";
					else
						body=body+temp1[i]+" ";
				}
				body = Sort(body);
				body = body.trim();///// head of the rule

				String wholeRuleForFindingConfi = head+" "+body;
				wholeRuleForFindingConfi = Sort(wholeRuleForFindingConfi).trim();
				int support = finalFreq.get(wholeRuleForFindingConfi).size();
				if(wholeRuleForFindingConfi.split(" ").length>4)
				{
					System.out.print("");
				}
				if(((float)support/denom) >=((float)minConf/100)) // if the confidence of the rule is greater than or equal to minConf then only it is strong rule
				{
					if(rules.containsKey(head))  // if the rule with current body already exists add the head into arraylist of that that body 
					{					
						ArrayList<String> bodiesForHead = rules.get(head);
						if(!bodiesForHead.contains(body))
							bodiesForHead.add(body);
					}
					else
					{
						ArrayList<String> bdy = new ArrayList<String>();
						bdy.add(body);
						rules.put(head, bdy);
					}
				}
				j++;
			}
		}
		//System.out.println(rules);
		return rules;
	}

	
	// Sorting of the given string
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
}
