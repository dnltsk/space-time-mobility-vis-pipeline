package de.dlr.ivf.tapas.analyzer.geovis.common.perspective;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public class Transformation {

	List<Coordinate> points = null;
	
	public Transformation(List<Coordinate> points) {
		checkNaN(points);
		this.points = points;
	}
	
	public void rotateX(double theta){
		theta = Math.toRadians(theta);
        double Ry[][] = { { 1,               0,                0 },
				 		  { 0, Math.cos(theta), -Math.sin(theta) },
				 		  { 0, Math.sin(theta),  Math.cos(theta) }
						};
        for(Coordinate point : this.points){
          double[] rotatedPoint = getMultiplyMatrix(Ry, new double[]{point.x, point.y, point.z});
          point.x = rotatedPoint[0];
          point.y = rotatedPoint[1];
          point.z = rotatedPoint[2];
        }
	}
	
	public void rotateY(double psi){
		psi = Math.toRadians(psi);
        double Ry[][] = { {  Math.cos(psi), 0, Math.sin(psi) },
                          {              0, 1,             0 },
                          { -Math.sin(psi), 0, Math.cos(psi) }
                        };
        for(Coordinate point : this.points){
          double[] rotatedPoint = getMultiplyMatrix(Ry, new double[]{point.x, point.y, point.z});
          point.x = rotatedPoint[0];
          point.y = rotatedPoint[1];
          point.z = rotatedPoint[2];
        }
	}
	
	public void rotateZ(double phi){
		phi = Math.toRadians(phi);
		double Rz[][] = { {  Math.cos(phi), Math.sin(phi), 0 },
		                  { -Math.sin(phi), Math.cos(phi), 0 },
		                  {              0,             0, 1 }
		                };
        for(Coordinate point : this.points){
          double[] rotatedPoint = getMultiplyMatrix(Rz, new double[]{point.x, point.y, point.z});
          point.x = rotatedPoint[0];
          point.y = rotatedPoint[1];
          point.z = rotatedPoint[2];
        }
	}
	

	private void checkNaN(List<Coordinate> points3d) {
		for(Coordinate p : points3d){
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
	
	  private double[] getMultiplyMatrix (double matrix_1[][], double matrix_2[]){
	    double result[] = zeros(matrix_1.length);
	    if (matrix_1[0].length == matrix_2.length){
	      for (int i=0; i<matrix_1.length; i++){
	        for (int k=0; k<matrix_1[0].length; k++){
	          result[i] += matrix_1[i][k] * matrix_2[k];
	        }
	      }
	      return result;
	    }
	    else {
	      return null;
	    }
	  }
	  
	  private double[] zeros(int o){
	    double[] A = new double[o];
	    for (int i=0; i<o; i++)
	      A[i]=0.0;
	    return A;
	  }

}

