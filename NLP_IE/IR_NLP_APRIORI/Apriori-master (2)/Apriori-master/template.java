package hw2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class template {
	String str;
	HashMap<String,ArrayList<String>> hm= new HashMap<String,ArrayList<String>>();
	ArrayList<String> Rules = new ArrayList<String>();
	int resCnt = 0;
	public template(String str, HashMap<String,ArrayList<String>> hm,ArrayList<String> Rules){
		this.str=str;
		this.hm = hm;
		this.Rules = Rules;
	}
	public void compute(){ 
		int i=0;
		int flag = 0;
		String[] sub = null;
		ArrayList<String> temple = new ArrayList<String>();
		ArrayList<String> finalList = new ArrayList<String>();
		String str1=null,str2 =null,str3 =null;
		if(str.contains("AND")||str.contains("and")||str.contains("And")||str.contains("OR")||str.contains("or")||str.contains("Or")){ //handles template 3 
			if(str.contains("OR")||str.contains("or")||str.contains("Or")){
				flag = 2;
			}else{
				flag = 1;
			}
			if(flag == 1)
				sub = str.split("\\s(AND|And|and)\\s");
			else
				sub = str.split("\\s(OR|Or|or)\\s");
			for(int j=0;j<sub.length;j++){
				Pattern p = Pattern.compile("([A-Za-z]+)\\s([HashHAS]+)\\s([0-9ALLNONEnoneallNAYy]+)\\s([ofOF]+)\\s(.+)");
				Matcher m=p.matcher(sub[j]);
				int c=0;
				if(m.find()){
					i=3;
					str1=m.group(1).toUpperCase().trim();
					str2=m.group(3).toUpperCase().trim();
					str3=m.group(5).toUpperCase().trim();
				}
				String[] spltitems=str3.split("\\s*\\,\\s*");
				for(int k=0; k<spltitems.length;k++){
					if(!spltitems[k].contains("G"))
					spltitems[k]=spltitems[k].replaceAll("\\s", "_");
				}
				switch(str1)
				{ 	case "RULE":
					if(str2.equals("NONE"))
					{
						for(String s: Rules)
						{	c=0;
						for(int k=0;k<spltitems.length;k++)
						{ if(s.contains(spltitems[k]))
						{ c++;
						break;
						}
						}
						if(c==0){
							if(flag==1){
								if(j==0)
									temple.add(s);
								else{
									if(finalList.contains(s)){
										temple.add(s);
									}
								}
							}else{
								if(!finalList.contains(s))	
									finalList.add(s);	
							}
						}
						}

					}

					else if(str2.equals("ANY"))
					{
						for(String s:Rules)
						{
							for(int k=0;k<spltitems.length;k++)
							{	if(s.contains(spltitems[k]))
							{
								if(flag==1){
									if(j==0)
										temple.add(s);
									else{
										if(finalList.contains(s)){
											temple.add(s);
										}
									}
								}else{
									if(!finalList.contains(s))	
										finalList.add(s);	
								}
								break;
							}
							}
						}
					}				
					else//number
					{
						for(String s:Rules)
						{	

							c=0;
							for(int k=0;k<spltitems.length;k++)
							{ if(s.contains(spltitems[k]))
								c++;

							}
							if(c==Integer.parseInt(str2)){
								if(flag==1){
									if(j==0)
										temple.add(s);
									else{
										if(finalList.contains(s)){
											temple.add(s);
										}
									}
								}else{
									if(!finalList.contains(s))	
										finalList.add(s);	
								}	
							}

						}
					}
					break;
				case "BODY":
					if(str2.equals("NONE"))
					{
						for(Entry<String,ArrayList<String>>e:hm.entrySet())
						{	
							c=0;
							for(int k=0;k<spltitems.length;k++)
							{ if(e.getKey().contains(spltitems[k]))
							{ c++;
							break;
							}
							}
							if(c==0)
							{
								if(flag==1){
									if(j==0){
										for(String s:e.getValue())
											temple.add(e.getKey().trim()+" --> "+s.trim());
									}
									else{
										for(String s:e.getValue()){
											if(finalList.contains(e.getKey().trim()+" --> "+s.trim())){
												temple.add(e.getKey().trim()+" --> "+s.trim());
											}
										}

									}
								}else{
									for(String s:e.getValue()){
										if(!finalList.contains(e.getKey().trim()+" --> "+s.trim()))	
											finalList.add(e.getKey().trim()+" --> "+s.trim());	
									}

								}
							}
						}
					}

					else if(str2.equals("ANY"))
					{
						for(Entry<String,ArrayList<String>>e:hm.entrySet())
						{
							for(int k=0;k<spltitems.length;k++)
							{	if(e.getKey().contains(spltitems[k]))
							{

								if(flag==1){
									if(j==0){
										for(String s:e.getValue())
											temple.add(e.getKey().trim()+" --> "+s.trim());
									}
									else{
										for(String s:e.getValue()){
											if(finalList.contains(e.getKey().trim()+" --> "+s.trim())){
												temple.add(e.getKey().trim()+" --> "+s.trim());
											}
										}

									}
								}else{
									for(String s:e.getValue()){
										if(!finalList.contains(e.getKey().trim()+" --> "+s.trim()))	
											finalList.add(e.getKey().trim()+" --> "+s.trim());	
									}

								}



								break;
							}
							}

						}

					}				
					else//number
					{
						for(Entry<String,ArrayList<String>>e:hm.entrySet())
						{	c=0;
						for(int k=0;k<spltitems.length;k++)
						{ if(e.getKey().contains(spltitems[k]))
							c++;
						}
						if(c==Integer.parseInt(str2))
						{
							if(flag==1){
								if(j==0){
									for(String s:e.getValue())
										temple.add(e.getKey().trim()+" --> "+s.trim());
								}
								else{
									for(String s:e.getValue()){
										if(finalList.contains(e.getKey().trim()+" --> "+s.trim())){
											temple.add(e.getKey().trim()+" --> "+s.trim());
										}
									}

								}
							}else{
								for(String s:e.getValue()){
									if(!finalList.contains(e.getKey().trim()+" --> "+s.trim()))	
										finalList.add(e.getKey().trim()+" --> "+s.trim());	
								}

							}
						}

						}
					}
					break;

				case "HEAD": 
					ArrayList<String> temp;
					if(str2.equals("NONE"))
					{
						for(Entry<String,ArrayList<String>>e:hm.entrySet())
						{	c=0;
						temp = e.getValue();
						for(String s : temp){
							c=0;
							for(int k=0;k<spltitems.length;k++){
								if(s.contains(spltitems[k])){
									c++;
									break;
								}
							}
							if(c==0){
								if(flag==1){
									if(j==0)
										temple.add(e.getKey()+" --> "+s);
									else{
										if(finalList.contains(e.getKey()+" --> "+s)){
											temple.add(e.getKey()+" --> "+s);
										}
									}
								}else{
									if(!finalList.contains(e.getKey()+" --> "+s))	
										finalList.add(e.getKey()+" --> "+s);	
								}
							}	
						}
						}
					}
					else if(str2.equals("ANY"))
					{
						for(Entry<String,ArrayList<String>>e:hm.entrySet())
						{
							temp = e.getValue();
							for(String s : temp){
								for(int k=0;k<spltitems.length;k++){
									if(s.contains(spltitems[k])){
										if(flag==1){
											if(j==0)
												temple.add(e.getKey()+" --> "+s);
											else{
												if(finalList.contains(e.getKey()+" --> "+s)){
													temple.add(e.getKey()+" --> "+s);
												}
											}
										}else{
											if(!finalList.contains(e.getKey()+" --> "+s))	
												finalList.add(e.getKey()+" --> "+s);	
										}
										break;
									}
								}	
							}
						}
					}				
					else//number
					{
						for(Entry<String,ArrayList<String>>e:hm.entrySet())
						{	c=0;
						temp = e.getValue();
						for(String s : temp){
							c=0;
							for(int k=0;k<spltitems.length;k++){
								if(s.contains(spltitems[k]))
									c++;
							}
							if(c==Integer.parseInt(str2))
							{
								if(flag==1){
									if(j==0)
										temple.add(e.getKey()+" --> "+s);
									else{
										if(finalList.contains(e.getKey()+" --> "+s)){
											temple.add(e.getKey()+" --> "+s);
										}
									}
								}else{
									if(!finalList.contains(e.getKey()+" --> "+s))	
										finalList.add(e.getKey()+" --> "+s);	
								}
							}
						}
						}	

					}
					break;	
				}
				if(flag==1){
					finalList = new ArrayList<String>();
					finalList.addAll(temple);
					temple = new ArrayList<String>();
				}
			}
			for(String s:finalList)
				System.out.println("result : "+ s);
			System.out.println("Total rule - count for given template :"+finalList.size());
		}else{
			Pattern p1 = Pattern.compile("([A-Za-z]+)\\s([HashHAS]+)\\s([0-9ALLNONEnoneallNAYy]+)\\s([ofOF]+)\\s(.+)");
			Matcher m=p1.matcher(str);
			Pattern p2 = Pattern.compile("^(SizeOf|sizeof|sizeOf|SIZEOF)\\s*(rule|Rule|RULE|Body|BODY|body|Head|head|HEAD)\\s*(<|>|=|<=|>=|\\!=)\\s*(\\d+)");
			Matcher m2 = p2.matcher(str);

			if(m.find())
			{  			//template 1 detected
				i=1;
				str1=m.group(1).toUpperCase().trim();
				str2=m.group(3).toUpperCase().trim();
				str3=m.group(5).toUpperCase().trim();
			}
			if(m2.find()){
						//template 2 detected
				i=2;
				str1 = m2.group(2).toUpperCase().trim();
				str2 = m2.group(3).trim();
				str3 = m2.group(4).trim();
			}
		}
		if(i==1)
		{ String[] spltitems=str3.split("\\s*\\,\\s*");
		int c=0;
		switch(str1)
		{ 	case "RULE":
			if(str2.equals("NONE"))
			{
				for(String s: Rules)
				{	c=0;
				for(int j=0;j<spltitems.length;j++)
				{ if(s.contains(spltitems[j]))
				{ c++;
				break;
				}
				}
				if(c==0){
					resCnt++;
					System.out.println(s);	
				}
				}
			}

			else if(str2.equals("ANY"))
			{
				for(String s:Rules)
				{
					for(int j=0;j<spltitems.length;j++)
					{	if(s.contains(spltitems[j]))
					{
						resCnt++;
						System.out.println(s);
						break;
					}
					}
				}
			}				
			else//number
			{
				for(String s:Rules)

				{	c=0;
				for(int j=0;j<spltitems.length;j++)
				{ if(s.contains(spltitems[j]))
					c++;

				}
				if(c==Integer.parseInt(str2)){
					resCnt++;
					System.out.println(s);		
				}
				}
			}
			break;
		case "BODY":
			if(str2.equals("NONE"))
			{
				for(Entry<String,ArrayList<String>>e:hm.entrySet())
				{	c=0;
				for(int j=0;j<spltitems.length;j++)
				{ if(e.getKey().contains(spltitems[j]))
				{ c++;
				break;
				}
				}
				if(c==0){
					resCnt = resCnt + e.getValue().size();
					printBody(e.getKey(),e.getValue());
				}
				}
			}

			else if(str2.equals("ANY"))
			{
				for(Entry<String,ArrayList<String>>e:hm.entrySet())
				{
					for(int j=0;j<spltitems.length;j++)
					{	if(e.getKey().contains(spltitems[j]))
					{	resCnt = resCnt + e.getValue().size();
					printBody(e.getKey(),e.getValue());		
					break;
					}
					}

				}

			}				
			else//number
			{
				for(Entry<String,ArrayList<String>>e:hm.entrySet())
				{	c=0;
				for(int j=0;j<spltitems.length;j++)
				{ if(e.getKey().contains(spltitems[j]))
					c++;
				}
				if(c==Integer.parseInt(str2)){
					printBody(e.getKey(),e.getValue());		
					resCnt = resCnt + e.getValue().size();
				}
				}
			}
			break;

		case "HEAD": 
			ArrayList<String> temp;
			if(str2.equals("NONE"))
			{
				for(Entry<String,ArrayList<String>>e:hm.entrySet())
				{	c=0;
				temp = e.getValue();
				for(String s : temp){
					c=0;
					for(int j=0;j<spltitems.length;j++){
						if(s.contains(spltitems[j])){
							c++;
							break;
						}
					}
					if(c==0){
						resCnt++;
						System.out.println(e.getKey()+" --> "+s);
					}
				}
				}
			}
			else if(str2.equals("ANY"))
			{
				for(Entry<String,ArrayList<String>>e:hm.entrySet())
				{
					temp = e.getValue();
					for(String s : temp){
						for(int j=0;j<spltitems.length;j++){
							if(s.contains(spltitems[j])){
								resCnt++;
								System.out.println(e.getKey()+ " --> "+s);
								break;
							}
						}	
					}
				}
			}				
			else//number
			{
				for(Entry<String,ArrayList<String>>e:hm.entrySet())
				{	c=0;
				temp = e.getValue();
				for(String s : temp){
					c=0;
					for(int j=0;j<spltitems.length;j++){
						if(s.contains(spltitems[j]))
							c++;
					}
					if(c==Integer.parseInt(str2)){
						resCnt++;
						System.out.println(e.getKey()+" --> "+s);
					}
				}
				}	

			}
			break;	
		}
		System.out.println("Total rule - count for given template :"+resCnt);
		}
		else if(i==2){
			switch(str1){
			case "RULE":
				for(String x:Rules){
					String y = x.replaceAll("\\s\\-\\-\\>","").trim();
					int size = y.split("\\sG").length;
					if(compare(str2,str3,size)){
						resCnt++;
						System.out.println(x);
					}
				}
				break;
			case "BODY":
				for(Entry<String,ArrayList<String>>e:hm.entrySet()){
					int size = e.getKey().trim().split("\\sG").length;
					if(compare(str2,str3,size)){
						resCnt = resCnt + e.getValue().size();
						printBody(e.getKey(), e.getValue());
					}
				}
				break;
			case "HEAD":
				ArrayList<String> temp;
				for(Entry<String,ArrayList<String>>e:hm.entrySet()){
					temp = e.getValue();
					for(String x:temp){
						int size = x.trim().split("\\sG").length;
						if(compare(str2,str3,size)){
							resCnt++;
							System.out.println(e.getKey()+" --> "+x);
						}}
				}
				break;
			}
			System.out.println("Total rule - count for given template :"+resCnt);
		}
		else if(i==3){

		}else{
			System.out.println("INVALID INPUT!");
			System.out.println("Please recheck the format of the template as apecified in READ ME file");
		}

	//	System.out.println("Total rule - count for given template :"+resCnt);
	}

	public void printBody(String x, ArrayList<String>y){
		for(String s:y){
			System.out.println(x + " --> " + s);
		}
	}

	public Boolean compare(String str, String number, int size){
		int num = Integer.parseInt(number);
		switch(str){
		case ">":
			if(size>num)
				return true;
			break;
		case "<":
			if(size<num)
				return true;
			break;
		case ">=":
			if(size>=num)
				return true;
			break;
		case "<=":
			if(size<=num)
				return true;
			break;
		case "=":
			if(size==num)
				return true;
			break;
		case "!=":
			if(size!=num)
				return true;
			break;
		}
		return false;
	}

}



