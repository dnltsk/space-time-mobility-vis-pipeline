package de.dlr.ivf.tapas.analyzer.geovis.common.perspective;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public class ParallelProjection {

	List<Coordinate> points = null;

	public ParallelProjection(List<Coordinate> points) {
		this.points = points;
		checkNaN(points);
	}
	
	
	public void viewFromX(){
		for(Coordinate point : points){
			double oldX = point.x;
			point.x = point.y;
			point.y = point.z;
			point.z = oldX;
		}
	}
	
	public void viewFromY(){
		for(Coordinate point : points){
			double oldY = point.y;
			point.x = point.x;
			point.y = point.z;
			point.z = oldY;
		}
	}
	
	public void viewFromZ(){
		//(nothing to do because: (see code))
		
		//for(Coordinate point : points){
		//	point.x = point.x;
		//	point.y = point.y;
		//	point.z = point.z;
		//}
	}
	
	
	private void checkNaN(List<Coordinate> points) {
		for(Coordinate p : points){
			if(Double.isNaN(p.x)){
				p.x = 0.0;
			}
			if(Double.isNaN(p.y)){
				p.y = 0.0;
			}
			if(Double.isNaN(p.z)){
				p.z = 0.0;
			}
		}
	}
	
}
