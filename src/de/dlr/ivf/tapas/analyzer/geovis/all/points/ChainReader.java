package de.dlr.ivf.tapas.analyzer.geovis.all.points;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ChainReader {

	private String outputPath = null;
	
	public ChainReader(String outputPath) {
		this.outputPath = outputPath + "/GeoVis/web/json/animation/";
	}
	
	/**
	 * 
	 * @return
	 */
	public JSONArray loadChain(){
		File file = new File(this.outputPath+"/location_chain.json");
		if(!file.exists()){
			return new JSONArray();
		}
		
		BufferedReader fileReader = null;
		JSONParser parser = new JSONParser();
		try {
			fileReader = new BufferedReader(new FileReader(file));
			JSONArray jsonChain = (JSONArray)parser.parse(fileReader);
			return jsonChain;
	 
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
