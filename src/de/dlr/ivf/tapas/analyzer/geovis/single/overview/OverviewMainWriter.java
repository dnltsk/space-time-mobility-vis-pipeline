package de.dlr.ivf.tapas.analyzer.geovis.single.overview;

import org.json.simple.JSONObject;

import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;

public class OverviewMainWriter extends SimpleDataWriter{

	/**
	 * 
	 * @param pageNumbers
	 */
	public OverviewMainWriter(String outputPath, Integer pageNumbers) {
		super(outputPath + "/GeoVis/web/json/pathlayers/overview/");
		writeOverviewMain(pageNumbers);
	}

	/**
	 * 
	 * @param pageNumbers
	 */
	@SuppressWarnings("unchecked")
	private void writeOverviewMain(Integer pageNumbers) {
		JSONObject jsonMainOverview = new JSONObject();
		jsonMainOverview.put("page_numbers", pageNumbers);
		
		writeJsonFile("overview_main.json", jsonMainOverview);
	}
	
}
