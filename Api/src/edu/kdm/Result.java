package edu.kdm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
 
@XmlRootElement
public class Result {
 
	public class Category{
		
		 public Category(String accuracy, String category) {
			 
		        this.accuracy = accuracy;
		        this.category = category;
		       
		    }
		 

		
		 public String getcategory() {
		        return category;
		    }
		    public void setcategory(String category) {
		        this.category = category;
		    }
		    

		    
		    private String category;
		    private String accuracy;
		     
		
	}
   
    public String getquestion() {
        return question;
    }
    public void setquestion(String question) {
        this.question = question;
    }
    public String getanswer() {
        return answer;
    }
    public void setanswer(String answer) {
        this.answer = answer;
    }
     


    public Result() {
    	
    	categories = new ArrayList<Result.Category>(); 
        
        question = "";
        answer = "";
         
    }
 
    public Result(String question, String answer) {
 
       
        
    	categories = new ArrayList<Result.Category>();     	
        this.question = question;
        this.answer = answer;
    }
 
     
   
    
    private String question;
    private String answer;
    public List<Category> categories;
         
}
