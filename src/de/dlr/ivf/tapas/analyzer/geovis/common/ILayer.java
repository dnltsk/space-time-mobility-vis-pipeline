package de.dlr.ivf.tapas.analyzer.geovis.common;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public interface ILayer {

	public List<Coordinate> getCoordinatesOfAllFeatures();
	
}
