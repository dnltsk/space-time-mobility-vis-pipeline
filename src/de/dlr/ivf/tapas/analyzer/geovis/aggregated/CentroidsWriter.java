package de.dlr.ivf.tapas.analyzer.geovis.aggregated;

import java.util.List;

import org.json.simple.JSONArray;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;

public class CentroidsWriter extends SimpleDataWriter{

	public CentroidsWriter(String outputPath) {
		super(outputPath + "/GeoVis/web/json/traffic/");
	}
	
	@SuppressWarnings("unchecked")
	public void writeCentroids(String backgroundlayerName, List<Coordinate> cellCentroids) {
		JSONArray jsonCellCentroids = new JSONArray();
		for(Coordinate cellCentroid : cellCentroids){
			JSONArray jsonCellCentroid = new JSONArray();
			jsonCellCentroid.add((int)cellCentroid.x);
			jsonCellCentroid.add((int)cellCentroid.y);
			jsonCellCentroids.add(jsonCellCentroid);
		}
		writeJsonFile(backgroundlayerName+"_centroids.json", jsonCellCentroids);
	}
	
}