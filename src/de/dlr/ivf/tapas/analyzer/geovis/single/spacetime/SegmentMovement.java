package de.dlr.ivf.tapas.analyzer.geovis.single.spacetime;

import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.common.MovementPojo;

/**
 * Bereits an dieser Stelle mit Klarnamen arbeiten!
 * @author tee
 *
 */
public class SegmentMovement extends AbstractSegment{

	private MovementPojo movement = null;
	
	@Override
	public AbstractSegment clone() {
		return new SegmentMovement(new Integer(this.getFromMinute()), 
									 (Coordinate)this.getFrom().clone(), 
									 new Integer(this.getToMinute()), 
									 (Coordinate)this.getTo().clone(), 
									 this.movement.clone());
	}
	
	public SegmentMovement(Integer fromMinute, Coordinate from,
			 				 Integer toMinute, Coordinate to,
							 MovementPojo movement) {
		super(fromMinute, from, toMinute, to);
		this.movement = movement;
	}
	
	@Override
	public Map<String, String> getParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("mode", movement.getMode().toString());
		parameters.put("distance", movement.getDistance_real_m().toString());
		
		
		parameters.put("von_min", movement.getStart_time_min().toString());
		parameters.put("dauer_min", new Integer(movement.getActivity_start_min() - movement.getStart_time_min()).toString());
		parameters.put("bis_min", movement.getActivity_start_min().toString());
		
		int startMin = movement.getStart_time_min();
		parameters.put("von", (startMin / 60)+":"+minuteFormat.format(startMin % 60));
		
		int endMin = movement.getActivity_start_min();
		int dauerMin = endMin - startMin;
		parameters.put("dauer", (dauerMin / 60)+":"+minuteFormat.format(dauerMin % 60));
		parameters.put("bis", (endMin / 60)+":"+minuteFormat.format(endMin % 60));
		
		return parameters;
	}

	public MovementPojo getMovement() {
		return movement;
	}

	public void setMovement(MovementPojo movement) {
		this.movement = movement;
	}
	
}
