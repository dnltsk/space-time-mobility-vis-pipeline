package de.dlr.ivf.tapas.analyzer.geovis.aggregated.trafficmatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TrafficMatrixReader{

	private String outputPath = null;
	
	public TrafficMatrixReader(String outputPath) {
		this.outputPath = outputPath + "/GeoVis/web/json/traffic/";
	}
	
	public JSONObject loadMatrix(String matrixName){

		File file = new File(this.outputPath+matrixName+".json");
		if(!file.exists()){
			return new JSONObject();
		}
		
		FileReader fileReader = null;
		JSONParser parser = new JSONParser();
		try {
			fileReader = new FileReader(file);
			JSONObject jsonObject = (JSONObject)parser.parse(fileReader);
			return jsonObject;
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally{
			if(fileReader != null){
				try{
					fileReader.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}

		return null;
		
	}
	
}
