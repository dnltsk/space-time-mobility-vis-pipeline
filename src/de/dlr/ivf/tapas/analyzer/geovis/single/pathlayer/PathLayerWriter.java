package de.dlr.ivf.tapas.analyzer.geovis.single.pathlayer;

import java.util.List; 

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;
import de.dlr.ivf.tapas.analyzer.geovis.single.spacetime.AbstractSegment;
import de.dlr.ivf.tapas.analyzer.geovis.single.spacetime.PathLayerPojo;

public class PathLayerWriter extends SimpleDataWriter{

	/**
	 * 
	 * @param outputPath
	 * @param projectedPathLayers
	 */
	public PathLayerWriter(String outputPath, List<PathLayerPojo> projectedPathLayers) {
		super(outputPath + "/GeoVis/web/json/pathlayers/");
		writePathLayer(projectedPathLayers);
	}

	/**
	 * 
	 * @param projectedPathLayers
	 */
	@SuppressWarnings("unchecked")
	private void writePathLayer(List<PathLayerPojo> projectedPathLayers) {
		JSONObject jsonPerson = new JSONObject();
		jsonPerson.put("p_id",projectedPathLayers.get(0).getPerson().getP_id());
		jsonPerson.put("hh_id",projectedPathLayers.get(0).getPerson().getHh_id());
		
		JSONArray jsonLayers = createJsonGeometries(projectedPathLayers);
		JSONArray jsonAttributes = createJsonAttributes(projectedPathLayers.get(0));
		
		jsonPerson.put("geometries", jsonLayers);
		jsonPerson.put("attributes", jsonAttributes);
		
		String targetPath = projectedPathLayers.get(0).getPerson().getHh_id().toString().substring(0, 2)
						  + "/"+projectedPathLayers.get(0).getPerson().getHh_id().toString().substring(2, 4)
						  + "/"+projectedPathLayers.get(0).getPerson().getHh_id().toString().substring(4, 6)
						  + "/";
		
		String targetFilename = projectedPathLayers.get(0).getPerson().getHh_id()
								+"_"+projectedPathLayers.get(0).getPerson().getP_id()
								+".json";
		
		writeJsonFile(targetPath + targetFilename, jsonPerson);
	}
	
	/**
	 * 
	 * @param pathLayer
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONArray createJsonAttributes(PathLayerPojo pathLayer){
		JSONArray jsonSegments = new JSONArray();
		List<AbstractSegment> segments = pathLayer.getSegments();
		for (int i = 0; i < segments.size(); i++) {
			AbstractSegment segment = segments.get(i);
			String segmentId = pathLayer.getPerson().getHh_id()+"_"+pathLayer.getPerson().getP_id()+"_"+i;
			JSONObject jsonSegment = new JSONObject();
			jsonSegment.put("id", segmentId);
			jsonSegment.putAll(segment.getParameters());
			jsonSegments.add(jsonSegment);
		}
		return jsonSegments;
	}
	
	/**
	 * 
	 * @param projectedPathLayers
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONArray createJsonGeometries(List<PathLayerPojo> projectedPathLayers) {
		JSONArray jsonLayers = new JSONArray();
		for(PathLayerPojo projectedPathLayer : projectedPathLayers){
		
			JSONObject jsonLayer = new JSONObject();
			jsonLayer.put("elevation",projectedPathLayer.getElevation());
			jsonLayer.put("azimuth", projectedPathLayer.getAzimuth());
			
			JSONArray jsonSegments = new JSONArray();
			List<AbstractSegment> segments = projectedPathLayer.getSegments();
			for (int i = 0; i < segments.size(); i++) {
				String segmentId = projectedPathLayer.getPerson().getHh_id()+"_"+projectedPathLayer.getPerson().getP_id()+"_"+i;
				AbstractSegment segment = segments.get(i);
				JSONObject jsonSegment = new JSONObject();
				JSONArray jsonPointsArray = new JSONArray();
				JSONArray jsonPointArray = new JSONArray();
				jsonPointArray.add((int)segment.getFrom().x);
				jsonPointArray.add((int)segment.getFrom().y);
				jsonPointArray.add((int)segment.getFrom().z);
				jsonPointsArray.add(jsonPointArray);
				jsonPointArray = new JSONArray();
				jsonPointArray.add((int)segment.getTo().x);
				jsonPointArray.add((int)segment.getTo().y);
				jsonPointArray.add((int)segment.getTo().z);
				jsonPointsArray.add(jsonPointArray);
				jsonSegment.put("coordinates", jsonPointsArray);
				jsonSegment.put("id", segmentId);
				jsonSegments.add(jsonSegment);
			}
			jsonLayer.put("segments", jsonSegments);
			jsonLayers.add(jsonLayer);
		}
		return jsonLayers;
	}
	
}
