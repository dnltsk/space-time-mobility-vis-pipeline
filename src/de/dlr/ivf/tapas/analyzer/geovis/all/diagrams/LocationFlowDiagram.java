package de.dlr.ivf.tapas.analyzer.geovis.all.diagrams;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.codes.LocationType;

public class LocationFlowDiagram extends AbstractDiagram{

	private final int FRAME_INTERVAL = 15;//Datenpunkt aller X Minuten
	@SuppressWarnings("unused")
	private final int TIME_WINDOW_WIDTH = 15; // 1/2 nach links, 1/2 nach rechts
	@SuppressWarnings("unused")
	private final int FRAMES = 24 * 60 / FRAME_INTERVAL;
	
	private ArrayList<String> nodesList = null;
	private JSONArray jsonNodes = null;
	private JSONArray jsonLinks = null;
	
	public LocationFlowDiagram() {
		this.nodesList = new ArrayList<String>();
		this.jsonNodes = new JSONArray();
		this.jsonLinks = new JSONArray();
	}
	
	@Override
	public void addDiagramData(PersonPojo person) {
		
		for(int i=1; i<person.getActivities().size(); i++){
			
			ActivityPojo sourceActivity = person.getActivities().get(i-1);
			ActivityPojo targetActivity = person.getActivities().get(i);
			
			String sourceLocation = LocationType.getLocationType(sourceActivity).toString();
			String targetLocation = LocationType.getLocationType(targetActivity).toString();

			/* verifying */
			verifyNode(sourceLocation);
			verifyNode(targetLocation);
			verifyLink(sourceLocation, targetLocation);
			
			for(int frame=0; frame <= (24 * 60); frame += FRAME_INTERVAL){
				/* incrementing */
				incrementNodeFrame(sourceLocation, frame);
				incrementNodeFrame(targetLocation, frame);
				incrementLinkFrame(sourceLocation, targetLocation, frame);
			}
		}
			
	}

	/**
	 * 
	 * @param sourceLocation
	 * @param targetLocation
	 * @param frame
	 */
	@SuppressWarnings("unchecked")
	private void incrementLinkFrame(String sourceLocation, String targetLocation, int frame) {
		for(Object objLink : jsonLinks){
			JSONObject jsonLink = (JSONObject)objLink;
			String source = (String)jsonLink.get("source");
			String target = (String)jsonLink.get("target");
			if(source.equals(sourceLocation) && target.equals(targetLocation)){
				JSONObject jsonFrame = (JSONObject)((JSONArray)jsonLink.get("frames")).get((int)(frame / FRAME_INTERVAL));
				Long containsValue = (Long)jsonFrame.get("contains");
				jsonFrame.put("contains", ++containsValue);
				break;
			}
		}
	}

	/**
	 * 
	 * @param location
	 * @param frame
	 */
	@SuppressWarnings("unchecked")
	private void incrementNodeFrame(String location, int frame) {
		JSONObject jsonNode = (JSONObject)this.jsonNodes.get(nodesList.indexOf(location));
		JSONObject jsonFrame = (JSONObject)((JSONArray)jsonNode.get("frames")).get((int)(frame / FRAME_INTERVAL));
		Long containsValue = (Long)jsonFrame.get("contains");
		jsonFrame.put("contains", ++containsValue);
	}


	/**
	 * 
	 * @param location
	 */
	@SuppressWarnings("unchecked")
	private void verifyNode(String location) {
		if(!this.nodesList.contains(location)){
			/* register new node */
			this.nodesList.add(location);
			/* create new node */
			JSONObject newJsonNode = new JSONObject();
			newJsonNode.put("name", location);
			newJsonNode.put("group", nodesList.indexOf(location));
			newJsonNode.put("value", 0);
			if(location.equals(LocationType.HOME.toString())){
				newJsonNode.put("px", 125);
				newJsonNode.put("py", 125);
				newJsonNode.put("fixed", true);
			}
			/* create empty frames */
			JSONArray jsonFrames = new JSONArray();
			for(int frame=0; frame <= (24 * 60); frame += FRAME_INTERVAL){
				JSONObject jsonFrame = new JSONObject();
				jsonFrame.put("frame", frame*FRAME_INTERVAL);
				jsonFrame.put("contains", 0);
			}
			newJsonNode.put("frames", jsonFrames);
			
			this.jsonNodes.add(newJsonNode);
		}
	}
	
	/**
	 * 
	 * @param sourceLocation
	 * @param targetLocation
	 */
	@SuppressWarnings("unchecked")
	private void verifyLink(String sourceLocation, String targetLocation) {
		boolean found = false;
		for(Object objLink : jsonLinks){
			JSONObject jsonLink = (JSONObject)objLink;
			String source = (String)jsonLink.get("source");
			String target = (String)jsonLink.get("target");
			if(source.equals(sourceLocation) && target.equals(targetLocation)){
				found = true;
				break;
			}
		}
		if(!found){
			/* create json link */
			JSONObject newJsonLink = new JSONObject();
			newJsonLink.put("source", sourceLocation);
			newJsonLink.put("target", targetLocation);
			newJsonLink.put("value", 0);
			/* create empty frames */
			JSONArray jsonFrames = new JSONArray();
			for(int frame=0; frame <= (24 * 60); frame += FRAME_INTERVAL){
				JSONObject jsonFrame = new JSONObject();
				jsonFrame.put("frame", frame*FRAME_INTERVAL);
				jsonFrame.put("changes", 0);
			}
			newJsonLink.put("frames", jsonFrames);
		}
	}
	
}
