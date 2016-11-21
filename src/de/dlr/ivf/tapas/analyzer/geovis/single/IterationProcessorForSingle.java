package de.dlr.ivf.tapas.analyzer.geovis.single;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dlr.ivf.tapas.analyzer.geovis.IPersonCreationVisitor;
import de.dlr.ivf.tapas.analyzer.geovis.common.GeoVisProperties;
import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;
import de.dlr.ivf.tapas.analyzer.geovis.resources.GeoVisRessourceLocator;
import de.dlr.ivf.tapas.analyzer.geovis.single.overview.OverviewMainWriter;
import de.dlr.ivf.tapas.analyzer.geovis.single.overview.OverviewPageWriter;
import de.dlr.ivf.tapas.analyzer.geovis.single.pathlayer.PathLayerProjector;
import de.dlr.ivf.tapas.analyzer.geovis.single.pathlayer.PathLayerWriter;
import de.dlr.ivf.tapas.analyzer.geovis.single.spacetime.PathCreator;
import de.dlr.ivf.tapas.analyzer.geovis.single.spacetime.PathLayerPojo;

public class IterationProcessorForSingle implements IPersonCreationVisitor{

	private List<Integer> azimuths = null;
	private List<Integer> elevations = null;
	private List<List<PersonPojo>> households = null;
	private Integer currentPageNumber = null;
	private String outputPath = null;
	private OverviewPageWriter overviewPageWriter = null; 
	
	/**
	 * 
	 * @param outputPath
	 */
	public IterationProcessorForSingle(String outputPath) {
		this.outputPath = outputPath;
		this.azimuths = new ArrayList<Integer>();
		for(String azimuth : new GeoVisProperties("geovis_common").getProperty("azimuths").split(",")){
			this.azimuths.add(Integer.parseInt(azimuth));
		}
		this.elevations = new ArrayList<Integer>();
		for(String elevation : new GeoVisProperties("geovis_common").getProperty("elevations").split(",")){
			this.elevations.add(Integer.parseInt(elevation));
		}
		this.households = new ArrayList<List<PersonPojo>>();
		this.currentPageNumber = 0;
		this.overviewPageWriter = new OverviewPageWriter(outputPath);
	}
	
	/**
	 * 
	 */
	@Override
	public void beforeProcessing() {
		System.out.println("clearing target folder "+GeoVisRessourceLocator.getPath() + "/GeoVis/web/json/pathlayers/");
		new SimpleDataWriter(Arrays.asList(
				this.outputPath + "/GeoVis/web/json/pathlayers/"))
				.clearTargetPath();
	}
	
	/**
	 * 
	 */
	@Override
	public void onPersonCreation(PersonPojo person, int personIndex) {
		//System.out.println("IterationProcessorForSingle: processing Person "+person.getP_id());
		PathLayerPojo unprojectedPathLayer = new PathCreator(person).createSpaceTimePathLayer();
		//write attributes
		//new PathLayerAttributeWriter(this.outputPath, unprojectedPathLayer);
		
		List<PathLayerPojo> projectedPathLayers = new ArrayList<PathLayerPojo>();
		PathLayerProjector projector = new PathLayerProjector(unprojectedPathLayer);
		for(Integer azimuth : this.azimuths){
			for(Integer elevation : this.elevations){
				PathLayerPojo projectedPathLayer = projector.createProjectedLayer(azimuth, elevation);
				projectedPathLayers.add(projectedPathLayer);
			}
		}
		//new PathLayerGeometrieWriter(this.outputPath, projectedLayers);
		new PathLayerWriter(outputPath, projectedPathLayers);
	}

	/**
	 * 
	 */
	@Override
	public void onHouseholdCreation(List<PersonPojo> household, int householdIndex) {
		//System.out.println("IterationProcessorForSingle: processing Household:"+household+", households size="+this.households.size());
		this.households.add(household);
		if(this.households.size() == 5){
			this.overviewPageWriter.writeOverviewPage(this.households, this.currentPageNumber);
			this.currentPageNumber++;
			//start with new List
			this.households.clear();
		}
	}

	/**
	 * 
	 */
	@Override
	public void afterProcessing() {
		if(this.households.size() > 0){
			this.overviewPageWriter.writeOverviewPage(this.households, this.currentPageNumber);
		}
		System.out.println("IterationProcessorForSingle: Schreibe Search-Tree..");
		this.overviewPageWriter.writeSearchTree();

		new OverviewMainWriter(this.outputPath, (this.currentPageNumber + 1));
		System.out.println("IterationProcessorForSingle: finalize ");
	}
	
}
