package de.dlr.ivf.tapas.analyzer.geovis.background;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dlr.ivf.tapas.analyzer.geovis.background.shp.GeoVisShapefile;

public class BackgroundLayerProxy {

	/**
	 * CopyOnWriteArrayList is thread-safe instead of ArrayList! 
	 */
	private static CopyOnWriteArrayList<GeoVisShapefile> shapefilesProxy = new CopyOnWriteArrayList<GeoVisShapefile>();
	
	/**
	 * 
	 * @param shapefile
	 */
	public static void addShapefile(GeoVisShapefile shapefile){
		getShapefile(shapefile);
	}
	
	/**
	 * 
	 * @param shapefile
	 * @return
	 */
	public static GeoVisShapefile getShapefile(GeoVisShapefile shapefile){
		
		/* use proxy */
		for(GeoVisShapefile shapefileProxy : shapefilesProxy){
			if(shapefile.getId().equals(shapefileProxy.getId())){
				return shapefileProxy;
			}
		}
		
		/* new! add to proxy */
		shapefilesProxy.add(shapefile);
		return shapefile;
	}
	

	/**
	 * 
	 * @return
	 */
	public static List<GeoVisShapefile> getAllLoadedShapefiles(){
		return shapefilesProxy;
	}
	
}
