package de.dlr.ivf.tapas.analyzer.geovis.aggregated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.IPersonCreationVisitor;
import de.dlr.ivf.tapas.analyzer.geovis.aggregated.trafficmatrix.TrafficMatrixManager;
import de.dlr.ivf.tapas.analyzer.geovis.background.BackgroundLayerProxy;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.GeoVisShapefile;
import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;
import de.dlr.ivf.tapas.analyzer.geovis.common.perspective.PerspectiveManager;
import de.dlr.ivf.tapas.analyzer.geovis.resources.GeoVisRessourceLocator;

public class IterationProcessorForAggregated implements IPersonCreationVisitor{

	private String outputPath = null;
	private TrafficMatrixManager trafficMatrixManager = null;
	
	/**
	 * 
	 * @param outputPath
	 * @param personCount
	 */
	public IterationProcessorForAggregated(String outputPath, Integer personCount) {
		this.outputPath = outputPath;
		this.trafficMatrixManager = new TrafficMatrixManager(this.outputPath);
	}
	
	/**
	 * 
	 */
	@Override
	public void beforeProcessing() {
		System.out.println("clearing target folder "+ GeoVisRessourceLocator.getPath() + "/GeoVis/web/json/traffic/");
		new SimpleDataWriter(Arrays.asList(this.outputPath + "/GeoVis/web/json/traffic/")).clearTargetPath();
	}
	
	@Override
	public void onPersonCreation(PersonPojo person, int personIndex) {
		for(int i=1; i<person.getActivities().size(); i++){
			ActivityPojo srcActivity = person.getActivities().get(i-1);
			ActivityPojo destActivity = person.getActivities().get(i);
			trafficMatrixManager.incrementMovement(srcActivity, destActivity);
		}
	}
	
	@Override
	public void onHouseholdCreation(List<PersonPojo> household, int householdIndex) {
		//do nothing
	}

	@Override
	public void afterProcessing() {
		System.out.println("IterationProcessorForAggregated: finalize ");
		System.out.println("flushing Cache..");
		trafficMatrixManager.flushCache();
		
		//System.out.println("updating value boundaries..");
		//trafficMatrixManager.updateValueBoundaries();
		
		System.out.println("updating top10s..");
		trafficMatrixManager.updateTop10s();
		
		System.out.println("writing Centroids..");
		CentroidsWriter centroidsWriter = new CentroidsWriter(this.outputPath);
		for(GeoVisShapefile shapefile : BackgroundLayerProxy.getAllLoadedShapefiles()){
			ArrayList<Coordinate> centroids = shapefile.getSimplifiedFeatureCentroids();
			PerspectiveManager.createParallelPerspective(centroids, 0, 90);
			centroidsWriter.writeCentroids(shapefile.getFilename(), centroids);
		}
	}
	
}
