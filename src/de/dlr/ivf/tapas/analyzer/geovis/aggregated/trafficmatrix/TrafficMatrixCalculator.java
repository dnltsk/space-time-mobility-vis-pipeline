package de.dlr.ivf.tapas.analyzer.geovis.aggregated.trafficmatrix;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TrafficMatrixCalculator {
	
	public TrafficMatrixCalculator() {
	
	}
	
	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public HashMap<String, Integer> calcValueBoundaries(JSONObject matrix){
		
		HashMap<String, Integer> valueBoundaries = new HashMap<String, Integer>();
		
		for(Object srcLocationType : matrix.keySet()){
			for(Object destLocationType : ((JSONObject)matrix.get(srcLocationType)).keySet()){
				ArrayList<Integer> values = new ArrayList<Integer>();
				for(Object srcCellIndex : ((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).keySet()){
					for(Object destCellIndex : ((JSONObject)((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).get(srcCellIndex)).keySet()){
						Long value = (Long)((JSONObject)((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).get(srcCellIndex)).get(destCellIndex);
						values.add(value.intValue());
					}
				}
				
				valueBoundaries.put(destLocationType+"_min", Collections.min(values));
				valueBoundaries.put(destLocationType+"_max", Collections.max(values));
				Integer sum = 0;
				for (Integer value : values) {
					sum += value;
				}
				valueBoundaries.put(destLocationType+"_avg", (int)(sum.doubleValue() / (double)values.size()));
			}	
		}
			
		return valueBoundaries;
	}
	
	/**
	 * 
	 * @param matrix
	 */
	@SuppressWarnings("unchecked")
	public void insertTop10s(JSONObject matrix){

		int maxCellIndex = findMaxCellIndex(matrix);
		
		for(Object srcLocationType : matrix.keySet()){
			for(Object destLocationType : ((JSONObject)matrix.get(srcLocationType.toString())).keySet()){
				if(destLocationType.toString().contains("tt")
						|| destLocationType.toString().contains("tts")
						|| destLocationType.toString().contains("ttd")
						|| destLocationType.toString().contains("_min") 
						|| destLocationType.toString().contains("_max")
						|| destLocationType.toString().contains("_avg")){
					continue;
				}
				for(Object srcCellIndex : ((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).keySet()){
					((JSONObject)((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).get(srcCellIndex)).put("tts", calcLocalTop10(matrix, srcLocationType, destLocationType, srcCellIndex, true, maxCellIndex));
					((JSONObject)((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).get(srcCellIndex)).put("ttd", calcLocalTop10(matrix, srcLocationType, destLocationType, srcCellIndex, false, maxCellIndex));
				}
				((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).put("tt", calcGlobalTop10(matrix, srcLocationType, destLocationType));
			}
		}
	}

	/**
	 * 
	 * @param matrix
	 * @param destLocationType
	 * @param srcCellIndex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONArray calcLocalTop10(JSONObject matrix, Object srcLocationType, Object destLocationType, Object srcCellIndex, boolean isOutgoing, int maxCellIndex) {
		ArrayList<Long> top10 = new ArrayList<Long>();
		ArrayList<Long> top10Value = new ArrayList<Long>();
		
		if(isOutgoing){
			for(Object destCellIndex : ((JSONObject)((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).get(srcCellIndex)).keySet()){
				
				Long value = (Long)((JSONObject)((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).get(srcCellIndex)).get(destCellIndex);
				if(top10.size()<10){
					//initiation
					top10.add(Long.parseLong(destCellIndex.toString()));
					top10Value.add(value);
				}else if((Long)value > (Long)Collections.min(top10Value)){
					//new top10 value found
					int indexOfLowerValue = top10Value.indexOf(Collections.min(top10Value));
					top10.remove(indexOfLowerValue);
					top10Value.remove(indexOfLowerValue);
					top10.add(Long.parseLong(destCellIndex.toString()));
					top10Value.add(value);
				}
			}
			
		}else{
			//incoming: source und dest vertauschen!!!
			for(int i=0; i<=maxCellIndex; i++){
				if(((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).get(new Integer(i).toString()) != null
						&& ((JSONObject)((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).get(new Integer(i).toString())).get(srcCellIndex) != null){
					
					System.out.println("checking: "+srcLocationType+", "+destLocationType+", "+srcCellIndex+", "+i+": "+((JSONObject)((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).get(new Integer(i).toString())).get(srcCellIndex));
					
					Long value = (Long)((JSONObject)((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).get(new Integer(i).toString())).get(srcCellIndex);
					if(top10.size()<10){
						//initiation
						top10.add(new Long(i));
						top10Value.add(value);
					}else if((Long)value > (Long)Collections.min(top10Value)){
						//new top10 value found
						int indexOfLowerValue = top10Value.indexOf(Collections.min(top10Value));
						top10.remove(indexOfLowerValue);
						top10Value.remove(indexOfLowerValue);
						top10.add(new Long(i));
						top10Value.add(value);
					}
				}
			}
		}
		
		JSONArray jsonDestinations = new JSONArray();
		jsonDestinations.addAll(top10);
		return jsonDestinations;
	}

	/**
	 * 
	 * @param matrix
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONArray calcGlobalTop10(JSONObject matrix, Object srcLocationType, Object destLocationType){
		ArrayList<Entry<Long, Long>> top10 = new ArrayList<Entry<Long, Long>>();
		ArrayList<Long> top10Value = new ArrayList<Long>();
		
		for(Object srcCellIndex : ((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).keySet()){
			for(Object destCellIndex : ((JSONObject)((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).get(srcCellIndex)).keySet()){
				if(destCellIndex.toString().equals("tt") 
						|| destCellIndex.toString().equals("tts")
						|| destCellIndex.toString().equals("ttd")){
					continue;
				}
				Long value = (Long)((JSONObject)((JSONObject)((JSONObject)matrix.get(srcLocationType)).get(destLocationType)).get(srcCellIndex)).get(destCellIndex);
				if(top10.size()<10){
					//initiation
					top10.add(new SimpleEntry<Long, Long>(Long.parseLong(srcCellIndex.toString()), Long.parseLong(destCellIndex.toString())));
					top10Value.add(value);
				}else if((Long)value > (Long)Collections.min(top10Value)){
					//new top10 value found
					int indexOfLowerValue = top10Value.indexOf(Collections.min(top10Value));
					top10.remove(indexOfLowerValue);
					top10Value.remove(indexOfLowerValue);
					top10.add(new SimpleEntry<Long, Long>(Long.parseLong(srcCellIndex.toString()), Long.parseLong(destCellIndex.toString())));
					top10Value.add(value);
				}
			}
		}
		
		JSONArray jsonDirections = new JSONArray();
		for(Entry<Long, Long> top10Stream : top10){
			JSONArray jsonDirection = new JSONArray();
			jsonDirection.add(top10Stream.getKey());
			jsonDirection.add(top10Stream.getValue());
			jsonDirections.add(jsonDirection);
		}
		
		return jsonDirections;
		
	}
	
	/**
	 * 
	 * @param matrix
	 * @return
	 */
	private int findMaxCellIndex(JSONObject matrix) {
		int maxCellIndex = 0;
		for(Object someSrcLocationType : matrix.keySet()){
			for(Object someDestLocationType: ((JSONObject)matrix.get(someSrcLocationType)).keySet()){
				for(Object someSrcCellIndex : ((JSONObject)((JSONObject)matrix.get(someSrcLocationType)).get(someDestLocationType)).keySet()){
					Integer someSrcCellIndexInt = Integer.parseInt(someSrcCellIndex.toString());
					if(maxCellIndex < someSrcCellIndexInt){
						maxCellIndex = someSrcCellIndexInt;
					}
					for(Object someDestCellIndex : ((JSONObject)((JSONObject)((JSONObject)matrix.get(someSrcLocationType)).get(someDestLocationType)).get(someSrcCellIndex)).keySet()){
						Integer someDestCellIndexInt = Integer.parseInt(someDestCellIndex.toString());
						if(maxCellIndex < someDestCellIndexInt){
							maxCellIndex = someDestCellIndexInt;
						}
					}
				}
			}
		}
		System.out.println("maxCellIndex = "+maxCellIndex);
		return maxCellIndex;
	}
	
}
