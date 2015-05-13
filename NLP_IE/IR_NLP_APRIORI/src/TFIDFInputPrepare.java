import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class TFIDFInputPrepare {
	public void inputPrepare()
	{
		
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	 
		int topic =1;
		int counter = 1;
		String inputFiles[] ={"yahooInput\\yahooqa_CarsTransportation.csv",
				"yahooInput\\yahooqa_ComputersInternet.csv",
				"yahooInput\\yahooqa_EducationReference.csv",
				"yahooInput\\yahooqa_FoodDrink.csv",
				"yahooInput\\yahooqa_NewsEvents.csv",
				"yahooInput\\yahooqa_Travel.csv"};
						
							
								
		for( String csvFile :inputFiles ) {
		try {
	 
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
	 
			        // use comma as separator
				String[] qa = line.split(cvsSplitBy);
	 
				File file = new File("input1//topic"+topic+"-"+counter+".txt");
				if (!file.exists()) {
					file.createNewFile();
				}
				
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(line);
				bw.flush();
				bw.close();
				counter++;
			}
			counter = 1;
			topic++;
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		}
	 
		System.out.println("Done");
	  }
	}

