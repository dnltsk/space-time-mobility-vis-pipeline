package de.dlr.ivf.tapas.analyzer.geovis.common.perspective;

import com.vividsolutions.jts.geom.Coordinate;

public class PerspectiveProjection {

	int imageWidth;
	int imageHeight;
	
	public PerspectiveProjection(int imageWidth, int imageHeight) {
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public void projectPoint(Coordinate coordinate, double azimuth, double elevation, double distance) {
		
		checkNaN(coordinate);
		
		// compute coefficients for the projection
	    double theta = Math.toRadians(azimuth);
	    double phi = Math.toRadians(elevation);
	    double cosT = (float)Math.cos( theta ), sinT = (float)Math.sin( theta );
	    double cosP = (float)Math.cos( phi ), sinP = (float)Math.sin( phi );
	    double cosTcosP = cosT*cosP, cosTsinP = cosT*sinP;
	    double sinTcosP = sinT*cosP, sinTsinP = sinT*sinP;

	    // project vertices onto the 2D viewport
	    int scaleFactor = 1;
	    double near = distance * .4;  // distance from eye to near plane
	    double nearToObj = distance * .6;  // distance from near plane to center of object

	    double x0 = coordinate.x;
	    double y0 = coordinate.z; // simulate a axis-change (to get the correct rotations)
	    double z0 = coordinate.y; // simulate a axis-change (to get the correct rotations)

         // compute an orthographic projection
         double x1 = cosT*x0 + sinT*z0;
         double y1 = -sinTsinP*x0 + cosP*y0 + cosTsinP*z0;

         // now adjust things to get a perspective projection
         double z1 = cosTcosP*z0 - sinTcosP*x0 - sinP*y0;
         x1 = x1*near/(z1+near+nearToObj);
         y1 = y1*near/(z1+near+nearToObj);

         // the 0.5 is to round off when converting to int
         coordinate.x = (imageWidth/2 + scaleFactor*x1 + 0.5);
         coordinate.y = (imageHeight/2 - scaleFactor*y1 + 0.5);
         coordinate.z = 0;
	}
	
	private void checkNaN(Coordinate coordinate) {
		if(Double.isNaN(coordinate.x)){
			coordinate.x = 0.0;
		}
		if(Double.isNaN(coordinate.y)){
			coordinate.y = 0.0;
		}
		if(Double.isNaN(coordinate.z)){
			coordinate.z = 0.0;
		}
	}
	
}
