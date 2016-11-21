package de.dlr.ivf.tapas.analyzer.geovis.all.points;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.codes.LocationType;
import de.dlr.ivf.tapas.analyzer.geovis.common.perspective.PerspectiveManager;

public class ChainManager {

	private final int JITTER_RADIUS_PX = 3;
	private final int CACHE_SIZE = 500;
	private ArrayList<JSONArray> cachedChains = null;
	private ChainReader reader = null;
	private ChainWriter writer = null;
	
	public ChainManager(String outputPath) {
		this.cachedChains = new ArrayList<JSONArray>();
		this.reader = new ChainReader(outputPath);
		this.writer = new ChainWriter(outputPath);
	}
	
	public void addPerson(PersonPojo person){
		addIntoCache(person);
		if(this.cachedChains.size() > this.CACHE_SIZE){
			flushCache();
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void flushCache(){
		JSONArray loadedChain = reader.loadChain();
		loadedChain.addAll(cachedChains);
		writer.writeChain(loadedChain);
		this.cachedChains = new ArrayList<JSONArray>();
	}
	
	/**
	 * 
	 * @param person
	 */
	@SuppressWarnings("unchecked")
	private void addIntoCache(PersonPojo person) {
		JSONArray jsonChainLink = new JSONArray();
		for(ActivityPojo activity : person.getActivities()){
			JSONObject jsonActivity = new JSONObject();
			jsonActivity.put("location_type", LocationType.getLocationType(activity).toString());
			Coordinate c = new Coordinate(activity.getLoc_coord_x(), activity.getLoc_coord_y());
			PerspectiveManager.createParallelPerspective(Arrays.asList(c), 0, 90);
			JSONArray jsonCoordinate = new JSONArray();
			jsonCoordinate.add((int)c.x + jitter());
			jsonCoordinate.add((int)c.y + jitter());
			jsonActivity.put("coordinate", jsonCoordinate);
			jsonActivity.put("from", activity.getActivity_start_min());
			jsonActivity.put("duration", activity.getActivity_start_min() - activity.getActivity_duration_min() - activity.getDiscreet_timegap_min());
			jsonActivity.put("to", activity.getActivity_duration_min() + activity.getDiscreet_timegap_min());
			jsonChainLink.add(jsonActivity);
		}
		cachedChains.add(jsonChainLink);
	}
	
	private int jitter() {
		return (int)((Math.random() * (double)JITTER_RADIUS_PX) - JITTER_RADIUS_PX*2);
	}
	
}
