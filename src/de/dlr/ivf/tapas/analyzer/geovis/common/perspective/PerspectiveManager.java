package de.dlr.ivf.tapas.analyzer.geovis.common.perspective;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.common.ILayer;

public class PerspectiveManager {

	private ILayer layer = null;
	
	public PerspectiveManager(ILayer layer) {
		this.layer = layer;
	}
	
	public void createParallelPerspective(Integer azimuth, Integer elevation) {
		List<Coordinate> coordinates = layer.getCoordinatesOfAllFeatures();
		createParallelPerspective(coordinates, azimuth, elevation);
	}
	
	public static void createParallelPerspective(List<Coordinate> coordinates, Integer azimuth, Integer elevation) {
		Scene scene = new Scene();
		scene.translateToOrigin(coordinates);
		
		Transformation threeD = new Transformation(coordinates);
		if(azimuth != null){
			threeD.rotateZ(azimuth);
		}
		if(elevation != null){
			threeD.rotateX(elevation);
		}
		new ParallelProjection(coordinates).viewFromY();
		
		GeoVisImage image = new GeoVisImage();
		image.scaleToImage(coordinates);
		image.tiltCoordinates(coordinates);
	}

}
