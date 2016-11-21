package de.dlr.ivf.tapas.analyzer.geovis.single.spacetime;

import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

public class SegmentPlumb extends AbstractSegment{

	public SegmentPlumb(Integer fromMinute, Coordinate from,
						 Integer toMinute, Coordinate to) {
		super(fromMinute, from, toMinute, to);
	}
	
	@Override
	public AbstractSegment clone() {
		return new SegmentPlumb(new Integer(this.fromMinute),
								 (Coordinate)from.clone(),
								 new Integer(this.toMinute),
								 (Coordinate)to.clone());
	}
	
	@Override
	public Map<String, String> getParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("dummy", "true");
		return parameters;
	}
	
	
}
