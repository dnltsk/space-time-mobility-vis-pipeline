package de.dlr.ivf.tapas.analyzer.geovis.all;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.IPersonCreationVisitor;
import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.codes.LocationType;
import de.dlr.ivf.tapas.analyzer.geovis.common.perspective.PerspectiveManager;

public class QuickChain implements IPersonCreationVisitor{

	private List<PersonPojo> persons = null;
	private String outputPath = null;
	private final int JITTER_RADIUS_PX = 3;

	public List<PersonPojo> getPersons() {
		return persons;
	}

	/**
	 * 
	 */
	public QuickChain(String outputPath) {
		this.outputPath = outputPath;
		this.persons = new ArrayList<PersonPojo>();
	}
	
	public void beforeProcessing() {
		
	};
	
	/**
	 * 
	 */
	public void afterProcessing() {
		JSONArray jsonPossibilityChains = new JSONArray();
		for(PersonPojo person : persons){
			JSONArray jsonPerson = new JSONArray();
			for(ActivityPojo activity : person.getActivities()){
				JSONObject jsonLink = new JSONObject();
				jsonLink.put("from", activity.getActivity_start_min());
				jsonLink.put("to", activity.getActivity_start_min() 
									+ activity.getActivity_duration_min() 
									+ activity.getDiscreet_timegap_min());
				jsonLink.put("duration", activity.getActivity_duration_min() + activity.getDiscreet_timegap_min());
				jsonLink.put("activity_type", LocationType.getLocationType(activity).toString());
				JSONArray jsonCoordinate = new JSONArray();
				Coordinate coordinate = new Coordinate(activity.getLoc_coord_x(), activity.getLoc_coord_y(), 0); 
				PerspectiveManager.createParallelPerspective(Arrays.asList(coordinate), 0, 90);
				jsonCoordinate.add((int)coordinate.x + jitter());
				jsonCoordinate.add((int)coordinate.y + jitter());
				jsonLink.put("coordinate", jsonCoordinate);
				jsonPerson.add(jsonLink);
			}
			jsonPossibilityChains.add(jsonPerson);
			
			File targetFile = new File(outputPath+"/GeoVis/web/json/animation/possibilityChains.json");
			
			BufferedWriter out = null;
			try {
				File folder = targetFile.getParentFile();
				if(!folder.exists()){
					folder.mkdirs();
				}
				out = new BufferedWriter(new PrintWriter(targetFile, "UTF-8"),1024*1024);
				out.write(jsonPossibilityChains.toJSONString());
				//out.flush();
			} catch (IOException e) {
				System.err.println("Fehler beim Schreiben von Datei "+targetFile.getAbsolutePath()+" mit Inhalt "+jsonPossibilityChains.toJSONString());
				e.printStackTrace();
			} finally{
				if(out != null){
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		
	}
	
	/**
	 * 
	 */
	public void onHouseholdCreation(java.util.List<PersonPojo> household, int householdIndex) {
		
	};
	
	/**
	 * 
	 */
	public void onPersonCreation(PersonPojo person, int personIndex) {
		if(person.getActivities().size() > 3){
			persons.add(person);
		}
	}
	
	private int jitter() {
		int rand = (int)Math.round(((Math.random() * (double)JITTER_RADIUS_PX)));
		if(Math.random() < .5){
			rand = -rand;  
		}
		return  rand;
	}
	
}
