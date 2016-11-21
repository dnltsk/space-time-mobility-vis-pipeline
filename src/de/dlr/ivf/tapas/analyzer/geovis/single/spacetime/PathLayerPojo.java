package de.dlr.ivf.tapas.analyzer.geovis.single.spacetime;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.common.ILayer;
import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;

public class PathLayerPojo implements ILayer{

	private Integer azimuth = null;
	private Integer elevation = null;
	private PersonPojo person = null;
	private List<AbstractSegment> segments = null;

	public PathLayerPojo(PersonPojo person, List<AbstractSegment> segments) {
		this(-1, -1, person, segments);
	}
	
	public PathLayerPojo(Integer azimuth, Integer elevation,
			PersonPojo person, List<AbstractSegment> segments) {
		this.azimuth = azimuth;
		this.elevation = elevation;
		this.person = person;
		this.segments = segments;
	}
	
	public PathLayerPojo clone(){
		List<AbstractSegment> clonedSegments = new ArrayList<AbstractSegment>();
		for(AbstractSegment segment : this.segments){
			clonedSegments.add(segment.clone());
		}
		return new PathLayerPojo(new Integer(this.azimuth), 
									 new Integer(this.elevation), 
									 this.person.clone(), 
									 clonedSegments);
	}
	
	@Override
	public List<Coordinate> getCoordinatesOfAllFeatures() {
		List<Coordinate> allCoordinates = new ArrayList<Coordinate>();
		for(AbstractSegment segment : this.segments){
			allCoordinates.add(segment.getFrom());
			allCoordinates.add(segment.getTo());
		}
		return allCoordinates;
	}
	
	public List<AbstractSegment> getSegments() {
		return segments;
	}

	public void setSegments(List<AbstractSegment> segments) {
		this.segments = segments;
	}

	public Integer getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(Integer azimuth) {
		this.azimuth = azimuth;
	}

	public Integer getElevation() {
		return elevation;
	}

	public void setElevation(Integer elevation) {
		this.elevation = elevation;
	}

	public PersonPojo getPerson() {
		return person;
	}

	public void setPerson(PersonPojo person) {
		this.person = person;
	}
	
}
