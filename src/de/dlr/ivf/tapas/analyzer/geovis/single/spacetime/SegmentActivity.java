package de.dlr.ivf.tapas.analyzer.geovis.single.spacetime;

import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.codes.LocationType;

/**
 * Bereits an dieser Stelle mit Klarnamen arbeiten!
 * @author tee
 *
 */
public class SegmentActivity extends AbstractSegment{

	private ActivityPojo activity = null;
	
	@Override
	public AbstractSegment clone() {
		return new SegmentActivity(new Integer(this.getFromMinute()), 
									 (Coordinate)this.getFrom().clone(), 
									 new Integer(this.getToMinute()), 
									 (Coordinate)this.getTo().clone(), 
									 this.activity.clone());
	}
	
	public SegmentActivity(Integer fromMinute, Coordinate from,
							 Integer toMinute, Coordinate to,
							 ActivityPojo activity) {
		super(fromMinute, from, toMinute, to);
		this.activity = activity;
	}
	
	@Override
	public Map<String, String> getParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("activity", activity.getActivity().toString());
		parameters.put("is_home", activity.getIs_home().toString());
		
		parameters.put("von_min", activity.getActivity_start_min().toString());
		parameters.put("dauer_min", activity.getActivity_duration_min().toString());
		parameters.put("bis_min", new Integer(activity.getActivity_start_min() + activity.getActivity_duration_min() + activity.getDiscreet_timegap_min()).toString());
		
		int startMin = activity.getActivity_start_min();
		parameters.put("von", (startMin / 60)+":"+minuteFormat.format(startMin % 60));
		
		int dauerMin = activity.getActivity_duration_min();
		parameters.put("dauer", (dauerMin / 60)+":"+minuteFormat.format((dauerMin) % 60));
		parameters.put("bis", ((startMin+dauerMin) / 60)+":"+minuteFormat.format((startMin+dauerMin) % 60));
		
		Integer unbekannt = activity.getDiscreet_timegap_min();
		parameters.put("unbekannt_min", unbekannt.toString());
		parameters.put("bis_unbekannt", ((startMin+dauerMin+unbekannt) / 60)+":"+minuteFormat.format((startMin+dauerMin+unbekannt) % 60));
		
		parameters.put("activity_type", LocationType.getLocationType(activity).toString());
		
		return parameters;
	}

	public ActivityPojo getActivity() {
		return activity;
	}

	public void setActivity(ActivityPojo activity) {
		this.activity = activity;
	}
	
}
