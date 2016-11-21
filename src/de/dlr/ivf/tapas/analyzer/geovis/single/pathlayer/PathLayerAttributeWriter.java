package de.dlr.ivf.tapas.analyzer.geovis.single.pathlayer;

import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;
import de.dlr.ivf.tapas.analyzer.geovis.single.spacetime.PathLayerPojo;

public class PathLayerAttributeWriter extends SimpleDataWriter{

	/**
	 * 
	 * @param unprojectedPathLayer
	 */
	public PathLayerAttributeWriter(String outputPath, PathLayerPojo unprojectedPathLayer) {
		super(outputPath + "/GeoVis/web/json/pathlayers/");
		writeAttributes(unprojectedPathLayer);
	}

	/**
	 * 
	 * @param unprojectedPathLayer
	 */
	private void writeAttributes(PathLayerPojo unprojectedPathLayer) {
		/*JSONObject jsonLayer = new JSONObject();
		JSONArray jsonSegments = new JSONArray();
		List<AbstractSegment> segments = unprojectedPathLayer.getSegments();
		for (int i = 0; i < segments.size(); i++) {
			AbstractSegment segment = segments.get(i);
			String segmentId = unprojectedPathLayer.getPerson().getHh_id()+"_"+unprojectedPathLayer.getPerson().getP_id()+"_"+i;
			JSONObject jsonSegment = new JSONObject();
			jsonSegment.put("id", segmentId);
			jsonSegment.putAll(segment.getParameters());
			jsonSegments.add(jsonSegment);
		}
		jsonLayer.put("p_id",unprojectedPathLayer.getPerson().getP_id());
		jsonLayer.put("hh_id",unprojectedPathLayer.getPerson().getHh_id());
		jsonLayer.put("segments", jsonSegments);
		
		String targetFilename = unprojectedPathLayer.getPerson().getHh_id()
								+"_"+unprojectedPathLayer.getPerson().getP_id()
								+"_attributes.json";
		writeJsonFile(targetFilename, jsonLayer);
		//System.out.println(targetPaths+targetFilename+" done");
		*/
	}
	

}
