package edu.kdm;


import java.util.ArrayList;




import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.Produces;

import com.google.gson.Gson;

import edu.umkc.sce.surveyperceptor.DataPreparer;
import edu.umkc.sce.surveyperceptor.TextClassifier;


@Path("qaservice")
public class Rest {
 
		
	@GET
	@Path("/classify/{split}/{question}/{answer}")
	@Produces("application/json")
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getClassification(@PathParam("split") String split, @PathParam("question") String question, @PathParam("answer") String answer)
	{
		
			
		
		try {
			
			
			
			
			String[] categories = DataPreparer.readCategoriesFromFile(DataPreparer.projPath + "yahoocat.txt");
			
			boolean splitQA = split.equals("1");
			
			Result r = new TextClassifier().classifyQuestionAnswerPair(question, answer, categories,  splitQA);
			
			 Gson gson = new Gson();
			 String jsonResult = gson.toJson(r);
			 
			 System.out.println("Sending response: " + jsonResult);
			 
			 return Response.ok(jsonResult)
					 .header("Access-Control-Allow-Origin", "*")
				     .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				     .allow("OPTIONS").build();
			
		} catch (Exception ex) {
			System.out
					.println("Oops! text classification somehow failed. \r\n"
							+ ex.getMessage());
			
			
		}
		
		
		
		return Response.ok("failed").build();
			
		

	}
	
	@GET
	@Path("/train")
	@Produces("application/json")
	public String train()
	{
		String output = "reTrain";
		return "{\"result\": " + output + "}";
	}
	
	@GET
	@Path("/changeInputFile/{param1}")
	@Produces("application/json")
	public String ChangeInputFile(@PathParam("param1")String fileURL)
	{
		String output = fileURL;
		
		return "result: " + output;
	}
	
	
	
}
