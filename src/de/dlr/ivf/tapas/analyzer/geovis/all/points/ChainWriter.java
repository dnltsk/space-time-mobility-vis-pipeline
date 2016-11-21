package de.dlr.ivf.tapas.analyzer.geovis.all.points;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;

public class ChainWriter {

	private String outputPath = null;
	
	public ChainWriter(String outputPath) {
		this.outputPath = outputPath + "/GeoVis/web/json/animation/";
	}
	
	/**
	 * 
	 * @param jsonChain
	 */
	public void writeChain(JSONArray jsonChain){
		BufferedWriter fileWriter = null;
		
		try {
			File file = new File(outputPath+"location_chain.json");
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			fileWriter = new BufferedWriter(new FileWriter(file));
			fileWriter.write(jsonChain.toJSONString());
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(fileWriter != null){
				try {
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
