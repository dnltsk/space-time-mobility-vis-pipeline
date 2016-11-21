package de.dlr.ivf.tapas.analyzer.geovis.aggregated.trafficmatrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;

public class TrafficMatrixWriter extends SimpleDataWriter{

	public TrafficMatrixWriter(String outputPath) {
		super(outputPath + "/GeoVis/web/json/traffic/");
	}

	public void writeMatrix(String matrixName, JSONObject jsonMatrix) {
		
		BufferedWriter fileWriter = null;
		try {
			File file = new File(super.targetPaths.get(0)+matrixName+".json");
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			fileWriter = new BufferedWriter(new FileWriter(file));
			fileWriter.write(jsonMatrix.toJSONString());
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

	/**
	 * 
	 * @param matrixName
	 * @param loadedMatrix
	 * @param keyData
	 */
	@SuppressWarnings("unchecked")
	public void appendValueBoundaries(
			String matrixName,
			JSONObject loadedMatrix,
			HashMap<String, Integer> keyData) {
		
		BufferedWriter fileWriter = null;
		try {
			File file = new File(super.targetPaths.get(0)+matrixName+".json");
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			fileWriter = new BufferedWriter(new FileWriter(file));
			JSONObject jsonMatrix = new JSONObject();
			jsonMatrix.putAll(loadedMatrix);
			jsonMatrix.putAll(keyData);
			fileWriter.write(jsonMatrix.toJSONString());
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
	
	/**
	 * 
	 * @param matrixName
	 * @param loadedMatrix
	 * @param keyData
	 */
	@SuppressWarnings("unchecked")
	public void appendTop10(
			String matrixName,
			JSONObject jsonMatrix,
			HashMap<String, ArrayList<Entry<Long, Long>>> top10s) {
		
		BufferedWriter fileWriter = null;
		try {
			File file = new File(super.targetPaths.get(0)+matrixName+".json");
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			fileWriter = new BufferedWriter(new FileWriter(file));

			JSONObject jsonTop10s = new JSONObject();
			for(String destLocationType : top10s.keySet()){
				JSONArray jsonTop10 = new JSONArray();
				for(Entry<Long, Long> top10Stream : top10s.get(destLocationType)){
					JSONArray jsonSingleTop10 = new JSONArray();
					jsonSingleTop10.add(top10Stream.getKey());
					jsonSingleTop10.add(top10Stream.getValue());
					jsonTop10.add(jsonSingleTop10);
				}
				jsonTop10s.put(destLocationType, jsonTop10);
			}
			jsonMatrix.put("tt", jsonTop10s);
			
			fileWriter.write(jsonMatrix.toJSONString());
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
