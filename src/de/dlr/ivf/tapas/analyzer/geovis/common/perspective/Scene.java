package de.dlr.ivf.tapas.analyzer.geovis.common.perspective;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import de.dlr.ivf.tapas.analyzer.geovis.background.BackgroundLayerProxy;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.AdminShapefiles;

public class Scene {

	public void translateToOrigin(List<Coordinate> coordinates){
		Envelope bbox = BackgroundLayerProxy.getShapefile(AdminShapefiles.instanceOfKontur()).getBbox();
		for(Coordinate coordinate : coordinates){
			coordinate.x = coordinate.x - bbox.centre().x;
			coordinate.y = coordinate.y - bbox.centre().y;
		}
	}

}
