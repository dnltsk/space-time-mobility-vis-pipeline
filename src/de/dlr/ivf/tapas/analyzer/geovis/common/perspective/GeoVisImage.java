package de.dlr.ivf.tapas.analyzer.geovis.common.perspective;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import de.dlr.ivf.tapas.analyzer.geovis.background.BackgroundLayerProxy;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.AdminShapefiles;
import de.dlr.ivf.tapas.analyzer.geovis.common.GeoVisProperties;

public class GeoVisImage {

	private Envelope mainBbox = null;
	private Double imageWidth = null;
	private Double imageHeight = null;
	private Double scaleFactor = null;

	public GeoVisImage() {
		this.mainBbox = BackgroundLayerProxy.getShapefile(AdminShapefiles.instanceOfKontur()).getBbox();
		this.imageWidth = (double)Integer.parseInt(new GeoVisProperties("geovis_common").getProperty("scale_width"));
		this.imageHeight = (double)Integer.parseInt(new GeoVisProperties("geovis_common").getProperty("scale_height"));
		this.scaleFactor = getScaleFactor();
	}

	public void scaleToImage(List<Coordinate> coordinates){
		for(Coordinate coordinate : coordinates){
			coordinate.x = coordinate.x * this.scaleFactor;
			coordinate.y = coordinate.y * this.scaleFactor;
			coordinate.z = coordinate.z * this.scaleFactor;
		}
	}
	
	public Double getScaleFactor() {
		Double mainWidth = this.mainBbox.getWidth();
		Double mainHeigth = this.mainBbox.getHeight();
		double scaleWidth = this.imageWidth / mainWidth;
		double scaleHeight = this.imageHeight / mainHeigth;
		if(scaleWidth < scaleHeight){
			return scaleWidth;
		}
		return scaleHeight;
	}


	/**
	 * Vorher: Koordinaten auf 0,0
	 * Nachher: Koordinaten auf width/2, height/2
	 * @param coordinates
	 */
	public void translateToImageCenter(List<Coordinate> coordinates) {
		for(Coordinate coordinate : coordinates){
			coordinate.x = coordinate.x + (this.imageWidth / 2.0);
			coordinate.y = coordinate.y + (this.imageHeight / 2.0);
		}
	}
	
	/**
	 * Vorher: Lower-Left
	 * Nachher: Upper-Left
	 * @param coordinates
	 */
	public void tiltToImageCoordinates(List<Coordinate> coordinates) {
		for(Coordinate coordinate : coordinates){
			coordinate.y = this.imageHeight - coordinate.y;
		}
	}

	/**
	 * Vorher: Lower-Left
	 * Nachher: Upper-Left
	 * @param coordinates
	 */
	public void tiltCoordinates(List<Coordinate> coordinates) {
		for(Coordinate coordinate : coordinates){
			coordinate.y = coordinate.y * -1;
		}
	}
	
	
}
