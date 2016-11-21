package de.dlr.ivf.tapas.analyzer.geovis.background.shp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;
import de.dlr.ivf.tapas.analyzer.geovis.common.perspective.GeoVisImage;

public class BackgroundLayersDataWriter extends SimpleDataWriter{

	private String name = null;
	private String title = null;
	
	/**
	 * 
	 * @param outputPath
	 * @param polygonCoordinates
	 * @param polygonAttributes
	 */
	public BackgroundLayersDataWriter(String outputPath, String name, String title) {
		super(Arrays.asList(outputPath + "/GeoVis/web/json/backgroundlayers/"));
		this.name = name;
		this.title = title;
	}
	
	/**
	 * 
	 * @param polygonsCoordinates
	 * @param azimuth
	 * @param elevation
	 */
	public void writeGeometries(ArrayList<ArrayList<Coordinate>> polygonsCoordinates, Integer azimuth, Integer elevation) {
		writeBackgroundLayerGeoms(polygonsCoordinates, azimuth, elevation);
	}
	
	/**
	 * 
	 * @param featureAttributes
	 */
	public void writeAttributes(ArrayList<HashMap<String, Integer>> featureAttributes, Double avgSquareM) {
		writeBackgroundLayerAttributes(featureAttributes);
		writeBackgroundLayersOverview(featureAttributes, avgSquareM);
	}
	
	/**
	 * 
	 * @param polygonCoordinates
	 * @param polygonAttributes
	 */
	@SuppressWarnings("unchecked")
	private void writeBackgroundLayerGeoms(ArrayList<ArrayList<Coordinate>> polygonsCoordinates, Integer azimuth, Integer elevation) {
		
		JSONArray jsonFeatures = new JSONArray();
		for(int i=0; i<polygonsCoordinates.size(); i++){
			JSONObject jsonFeature = new JSONObject();
			JSONArray jsonVertices = new JSONArray();
			for(Coordinate p : polygonsCoordinates.get(i)){
				JSONArray vertexArray = new JSONArray();
				vertexArray.add((int)p.x);
				vertexArray.add((int)p.y);
				jsonVertices.add(vertexArray);
			}
			jsonFeature.put("vertices",jsonVertices);
			jsonFeatures.add(jsonFeature);
			
		}
		JSONObject jsonLayer = new JSONObject();
		jsonLayer.put("name", this.name);
		jsonLayer.put("elevation", elevation);
		jsonLayer.put("azimuth", azimuth);
		jsonLayer.put("title", this.title);
		jsonLayer.put("features",jsonFeatures);
		
		String targetFilename = this.name+"_"+azimuth+"_"+elevation+".json";
		writeJsonFile(targetFilename, jsonLayer);
		System.out.println(targetPaths+targetFilename+" done.");
		
	}


	/**
	 * 
	 * @param polygonCoordinates
	 * @param polygonAttributes
	 */
	@SuppressWarnings("unchecked")
	private void writeBackgroundLayerAttributes(ArrayList<HashMap<String, Integer>> featuresAttributes){
		if(featuresAttributes==null){
			return;
		}
		
		JSONArray jsonFeatures = new JSONArray();
		for(HashMap<String, Integer> attributes : featuresAttributes){
			JSONObject jsonFeature = new JSONObject();
			double squareKm = attributes.get(GeoVisShapefile.AREA_ATTRIBUTE)/1000000.0;
			jsonFeature.put("squarekm", squareKm);
			for(String attributeName : attributes.keySet()){
				if(attributeName.equals(GeoVisShapefile.AREA_ATTRIBUTE)){
					continue;
				}
				double value = attributes.get(attributeName).doubleValue();
				double relValue = value / squareKm; 
				jsonFeature.put(attributeName+"_psk", relValue);
			}
			jsonFeatures.add(jsonFeature);
			
		}
		JSONObject jsonLayer = new JSONObject();
		jsonLayer.put("name", this.name);
		jsonLayer.put("title", this.title);
		jsonLayer.put("features", jsonFeatures);
		
		String targetFilename = this.name+"_attributes.json";
		writeJsonFile(targetFilename, jsonLayer);
		System.out.println(targetPaths+targetFilename+" done.");
		
	}

	/**
	 * 
	 * @param polygonCoordinates
	 * @param polygonAttributes
	 */
	@SuppressWarnings("unchecked")
	private void writeBackgroundLayersOverview(ArrayList<HashMap<String, Integer>> featureAttributes, Double avgSquareM) {
		if(featureAttributes == null){
			return;
		}
		
		JSONObject jsonOverview = new JSONObject();
		
		List<String> registeredAttributes = new ArrayList<String>();
		for(HashMap<String, Integer> attributes : featureAttributes){
			for(String attributeName : attributes.keySet()){
				if(attributeName.equals(GeoVisShapefile.AREA_ATTRIBUTE)){
					continue;
				}
				if(!registeredAttributes.contains(attributeName)){
					insertValueBoundaries(jsonOverview, attributeName, featureAttributes);
				}
				registeredAttributes.add(attributeName);
			}
		}
		
		
		jsonOverview.put("avg_square_km", (avgSquareM / 1000000.0));
		Double scaleFactor = new GeoVisImage().getScaleFactor();
		jsonOverview.put("avg_square_px", (Math.sqrt(avgSquareM) * scaleFactor));
		
		String targetFilename = this.name+"_overview.json";
		writeJsonFile(targetFilename, jsonOverview);
		System.out.println(targetPaths+targetFilename+" done.");
		
	}
	
	/**
	 * 
	 * @param jsonOverview
	 * @param attributeName
	 * @param featuresAttributes
	 */
	@SuppressWarnings("unchecked")
	private void insertValueBoundaries(JSONObject jsonOverview, String attributeName, ArrayList<HashMap<String, Integer>> featuresAttributes) {
		ArrayList<Double> squareKmValues = new ArrayList<Double>();
		for(HashMap<String, Integer> featureAttribute : featuresAttributes){
			if(featureAttribute.containsKey(attributeName)){
				double squareKm = featureAttribute.get(GeoVisShapefile.AREA_ATTRIBUTE).doubleValue() / 1000000.0;
				double attributeValueRel = featureAttribute.get(attributeName).doubleValue() / squareKm;
				squareKmValues.add(attributeValueRel);
				/*if(minValueRel == null){
					minValueRel = attributeValueRel;
					maxValueRel = attributeValueRel;
					continue;
				}
				if(attributeValueRel < minValueRel){
					minValueRel = attributeValueRel;
				}
				if(attributeValueRel > maxValueRel){
					maxValueRel = attributeValueRel;
				}*/
			}
		}
		
		int ignore = (int)Math.round((double)squareKmValues.size() * 0.05);
		
		Collections.sort(squareKmValues);

		System.out.println("insertValueBoundaries() attributeName="+attributeName);
		System.out.println("size="+squareKmValues.size()+" ignore="+ignore);
		for(int i=0; i<=ignore; i++){
			System.out.print(squareKmValues.get(i)+", ");
		}
		System.out.print("\n");
		for(int i=squareKmValues.size()-1; i>=squareKmValues.size()-1-ignore; i--){
			System.out.print(squareKmValues.get(i)+", ");
		}
		
		Double maxValueRel = squareKmValues.get(squareKmValues.size()-1-ignore);
		Double minValueRel = squareKmValues.get(0 + ignore);
		
		jsonOverview.put("min_"+attributeName+"_psk", minValueRel);
		jsonOverview.put("max_"+attributeName+"_psk", maxValueRel);
	}
	
}
