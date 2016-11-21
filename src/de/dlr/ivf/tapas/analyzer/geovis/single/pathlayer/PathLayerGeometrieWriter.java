package de.dlr.ivf.tapas.analyzer.geovis.single.pathlayer;

import java.util.List;

import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;
import de.dlr.ivf.tapas.analyzer.geovis.single.spacetime.PathLayerPojo;

public class PathLayerGeometrieWriter extends SimpleDataWriter{

	public PathLayerGeometrieWriter(String outputPath, List<PathLayerPojo> projectedPathLayers) {
		super(outputPath + "/GeoVis/web/json/pathlayers/");
		writeGeometries(projectedPathLayers);
	}

	private void writeGeometries(List<PathLayerPojo> projectedPathLayers) {
		/*JSONObject jsonPerson = new JSONObject();
		jsonPerson.put("p_id",projectedPathLayers.get(0).getPerson().getP_id());
		jsonPerson.put("hh_id",projectedPathLayers.get(0).getPerson().getHh_id());
		
		JSONArray jsonLayers = createJsonGeometries(projectedPathLayers);
		
		jsonPerson.put("geometries", jsonLayers);
		jsonPerson.put("attributes", jsonAttributes);
		
		String targetPath = projectedPathLayers.get(0).getPerson().getP_id().toString().substring(0, 3)
						  + projectedPathLayers.get(0).getPerson().getP_id().toString().substring(4, 5)
						  + projectedPathLayers.get(0).getPerson().getP_id().toString().substring(6, 7)
						  + "/";
		
		String targetFilename = projectedPathLayers.get(0).getPerson().getHh_id()
								+"_"+projectedPathLayers.get(0).getPerson().getP_id()
								+"_geoms.json";
		
		writeJsonFile(targetPath + targetFilename, jsonLayers);
		//System.out.println(targetPaths+targetFilename+" done");*/
	}

	
	
}
