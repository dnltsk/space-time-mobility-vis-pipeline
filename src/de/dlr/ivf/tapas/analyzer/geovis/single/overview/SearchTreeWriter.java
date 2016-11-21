package de.dlr.ivf.tapas.analyzer.geovis.single.overview;

import java.util.HashMap; 
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;

public class SearchTreeWriter extends SimpleDataWriter{

	/**
	 * 
	 * @param outputPath
	 * @param hhIds
	 */
	public SearchTreeWriter(String completeOutputPath, HashMap<Integer, Integer> hhIdPageLinks) {
		super(completeOutputPath);
		writeSearchTree(hhIdPageLinks);
	}
	
	/**
	 * 
	 * @param hhIds
	 */
	private void writeSearchTree(HashMap<Integer, Integer> hhIdPageLinks) {
		JSONObject treeRoot = new JSONObject();
		
		for(Integer hhId : hhIdPageLinks.keySet()){
			JSONObject treeLevel = treeRoot;
			String hhStr = hhId.toString();
			for(int i=0; i<hhStr.length(); i++){
				char number = hhStr.charAt(i);
				if(i < 6){
					//im tree aufsteigen
					if(!treeLevel.containsKey(number)){
						treeLevel.put(number, new JSONObject());
					}
					treeLevel = (JSONObject)treeLevel.get(number);
				}else{
					//endpunkt -> Seitenzahl eintragen
					treeLevel.put(number, hhIdPageLinks.get(hhId));
				}
			}
		}
		
		writeJsonFile("search_tree.json", treeRoot);
	}

}
