package de.dlr.ivf.tapas.analyzer.geovis.single.spacetime;

import java.text.DecimalFormat;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

abstract public class AbstractSegment {

	protected Integer fromMinute = null;
	protected Coordinate from = null;
	protected Integer toMinute = null;
	protected Coordinate to = null;
	protected DecimalFormat minuteFormat = new DecimalFormat("00");
	
	public AbstractSegment(Integer fromMinute, Coordinate from,
									    Integer toMinute, Coordinate to) {
		this.fromMinute = fromMinute;
		this.from = from;
		this.toMinute = toMinute;
		this.to = to;
	}

	abstract public Map<String, String> getParameters();
	
	abstract public AbstractSegment clone();

	public Integer getFromMinute() {
		return fromMinute;
	}

	public void setFromMinute(Integer fromMinute) {
		this.fromMinute = fromMinute;
	}

	public Coordinate getFrom() {
		return from;
	}

	public void setFrom(Coordinate from) {
		this.from = from;
	}

	public Integer getToMinute() {
		return toMinute;
	}

	public void setToMinute(Integer toMinute) {
		this.toMinute = toMinute;
	}

	public Coordinate getTo() {
		return to;
	}

	public void setTo(Coordinate to) {
		this.to = to;
	}

}
