package de.dlr.ivf.tapas.analyzer.geovis.single.pathlayer;

import de.dlr.ivf.tapas.analyzer.geovis.common.perspective.PerspectiveManager;
import de.dlr.ivf.tapas.analyzer.geovis.single.spacetime.PathLayerPojo;

public class PathLayerProjector {

	private PathLayerPojo unprojectedPathLayer = null;
	
	public PathLayerProjector(PathLayerPojo unprojectedPathLayer) {
		this.unprojectedPathLayer = unprojectedPathLayer;
	}
	
	public PathLayerPojo createProjectedLayer(Integer azimuth, Integer elevation) {
		PathLayerPojo projectedPathLayer = this.unprojectedPathLayer.clone();
		projectedPathLayer.setAzimuth(azimuth);
		projectedPathLayer.setElevation(elevation);
		PerspectiveManager perspectiveManager = new PerspectiveManager(projectedPathLayer);
		perspectiveManager.createParallelPerspective(azimuth, elevation);
		return projectedPathLayer;
	}
	
}
