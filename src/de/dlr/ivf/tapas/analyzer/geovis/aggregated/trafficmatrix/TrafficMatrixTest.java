package de.dlr.ivf.tapas.analyzer.geovis.aggregated.trafficmatrix;

import java.util.Arrays;
import java.util.List;

import de.dlr.ivf.tapas.analyzer.geovis.background.BackgroundLayerProxy;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.AdminShapefiles;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.GeoVisShapefile;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.GridShapefiles;
import de.dlr.ivf.tapas.analyzer.geovis.common.codes.LocationType;

public class TrafficMatrixTest {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//performanceTest();
		
		BackgroundLayerProxy.getShapefile(AdminShapefiles.instanceOfBezirke());
		BackgroundLayerProxy.getShapefile(AdminShapefiles.instanceOfOrtsteile());
		BackgroundLayerProxy.getShapefile(AdminShapefiles.instanceOfTvz879());
		BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfHexagonsSmall());
		BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfHexagonsMedium());
		BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfHexagonsLarge());
		BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfSquaresSmall());
		BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfSquaresMedium());
		BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfSquaresLarge());
		BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfTrianglesSmall());
		BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfTrianglesMedium());
		BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfTrianglesLarge());
		
		TrafficMatrixManager matrix = new TrafficMatrixManager("D:/xTemp/");
		for(GeoVisShapefile shapefile : BackgroundLayerProxy.getAllLoadedShapefiles()){
			/* write centroids */
			//writeCentroids(shapefile);
			/* register files */
			registerMatrices(matrix, shapefile);
		}
		/* update boundaries of registered layers */
		matrix.flushCache();
		//matrix.updateValueBoundaries();
		matrix.updateTop10s();
		
	}

	private static void registerMatrices(TrafficMatrixManager matrix, GeoVisShapefile shapefile) {
		
		List<LocationType> locations = Arrays.asList(LocationType.EDUCATION,
				 LocationType.HOME,
				 LocationType.JOB,
				 LocationType.LEISURE,
				 LocationType.OTHER,
				 LocationType.SETTLEMENT,
				 LocationType.SHOPPING);
		
		for(LocationType srcLocationType : locations){
			for(LocationType destLocationType : locations){
				matrix.incrementEntry(shapefile.getFilename(), 
								  	  srcLocationType, 
								  	  destLocationType, 
								  	  0, 
								  	  0);
			}
		}
	}


	@SuppressWarnings("unused")
	private static void performanceTest() {
		TrafficMatrixManager matrix = new TrafficMatrixManager("D:/");
		GeoVisShapefile shp = GridShapefiles.instanceOfTrianglesMedium();
		int features = shp.getSimplifiedFeatureGeoms().size();
		List<LocationType> locations = Arrays.asList(LocationType.EDUCATION,
				 LocationType.HOME,
				 LocationType.JOB,
				 LocationType.LEISURE,
				 LocationType.OTHER,
				 LocationType.SETTLEMENT,
				 LocationType.SHOPPING);
		for(int i = 0; i < features; i++){
			System.out.println("processing "+i+" of "+features+" ("+((double)i/(double)features)*100+")%");
			for(int j = 0; j < features; j++){
				for(LocationType srcLocation : locations){
					for(LocationType destLocation : locations){
						matrix.incrementEntry("test", srcLocation, destLocation, i, j);
					}	
				}
			}
		}
		matrix.flushCache();
	}
	
}
