package de.dlr.ivf.tapas.analyzer.geovis.background;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.BgType;
import de.dlr.ivf.tapas.analyzer.geovis.GeoVisOptions;
import de.dlr.ivf.tapas.analyzer.geovis.IPersonCreationVisitor;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.AdminShapefiles;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.BackgroundLayersDataWriter;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.GeoVisShapefile;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.GridShapefiles;
import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.GeoVisProperties;
import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.codes.LocationType;
import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;
import de.dlr.ivf.tapas.analyzer.geovis.common.perspective.PerspectiveManager;
import de.dlr.ivf.tapas.analyzer.geovis.resources.GeoVisRessourceLocator;

public class IterationProcessorForBackrounds implements IPersonCreationVisitor{

	private String outputPath = null;
	private List<Integer> azimuths = null;
	private List<Integer> elevations = null;
	private GeoVisOptions options = null;
	
	public IterationProcessorForBackrounds(String outputPath, GeoVisOptions options) {
		this.outputPath = outputPath;
		this.options = options;
		this.azimuths = new ArrayList<Integer>();
		for(String azimuth : new GeoVisProperties("geovis_common").getProperty("azimuths").split(",")){
			this.azimuths.add(Integer.parseInt(azimuth));
		}
		this.elevations = new ArrayList<Integer>();
		for(String elevation : new GeoVisProperties("geovis_common").getProperty("elevations").split(",")){
			this.elevations.add(Integer.parseInt(elevation));
		}
	}

	/**
	 * 
	 */
	@Override
	public void beforeProcessing() {
		System.out.println("clearing target folder "+ GeoVisRessourceLocator.getPath() + "/GeoVis/web/json/backgroundlayers/");
		new SimpleDataWriter(Arrays.asList(outputPath + "/GeoVis/web/json/backgroundlayers/")).clearTargetPath();
		
		/* preloading BackgroundLayers */
		initBackgroundLayerProxy();
	}
	
	/**
	 * 
	 */
	@Override
	public void onPersonCreation(PersonPojo person, int personIndex) {
		for(GeoVisShapefile layer : BackgroundLayerProxy.getAllLoadedShapefiles()){
			List<ActivityPojo> activities = person.getActivities();
			for (int i = 0; i < activities.size(); i++) {
				ActivityPojo activity = activities.get(i);
				Coordinate coord = new Coordinate(activity.getLoc_coord_x(), activity.getLoc_coord_y(), 0);
				Integer featureIndex = layer.inside(coord);
				layer.incrementFeatureAttribute(featureIndex, LocationType.getLocationType(activity).toString());
				if(!LocationType.getLocationType(activity).equals(LocationType.HOME)){
					layer.incrementFeatureAttribute(featureIndex, GeoVisShapefile.ALL_ATTRIBUTE);
				}
				if(i == 0){
					layer.incrementFeatureAttribute(featureIndex, GeoVisShapefile.POPULATION_ATTRIBUTE);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onHouseholdCreation(List<PersonPojo> household, int householdIndex) {
		// do nothing
	}
	
	/**
	 * 
	 */
	@Override
	public void afterProcessing() {
		System.out.println(this.getClass().getCanonicalName()+".afterProcessing()");
		
		//adding the thumbs
		BackgroundLayerProxy.addShapefile(AdminShapefiles.instanceOfBezirkeThumb());
		
		for(GeoVisShapefile shapefile : BackgroundLayerProxy.getAllLoadedShapefiles()){
			ArrayList<HashMap<String, Integer>> featureAttributes = shapefile.getFeatureAttributes();
			ArrayList<ArrayList<Coordinate>> featureGeoms = shapefile.getSimplifiedFeatureGeoms();
			
			
			BackgroundLayersDataWriter writer = new BackgroundLayersDataWriter(outputPath, 
																			   shapefile.getFilename(), 
																			   shapefile.getFilename());
			
			Double avgSquareM = shapefile.getSimplifiedAvgSquareM();
			writer.writeAttributes(featureAttributes, avgSquareM);
			
			for(Integer azimuth : this.azimuths){
				for(Integer elevation : this.elevations){
					ArrayList<ArrayList<Coordinate>> clonedFeatureGeoms = cloneFeatureGeoms(featureGeoms);
					//BackgroundLayerPojo layerProjected = layer.clone();
					for(List<Coordinate> coordinates : clonedFeatureGeoms){
						PerspectiveManager.createParallelPerspective(coordinates, azimuth, elevation);
					}
					
					writer.writeGeometries(clonedFeatureGeoms, azimuth, elevation);
				}
			}
		}
		System.out.println("export fertig");
	}

	/**
	 * 
	 * @param featureGeoms
	 * @return
	 */
	private ArrayList<ArrayList<Coordinate>> cloneFeatureGeoms(ArrayList<ArrayList<Coordinate>> featureGeoms) {
		ArrayList<ArrayList<Coordinate>> clonedFeatureGeoms = new ArrayList<ArrayList<Coordinate>>();
		for(ArrayList<Coordinate> singleFeature : featureGeoms){
			ArrayList<Coordinate> clonedSingleFeature = new ArrayList<Coordinate>();
			for(Coordinate singleCoordinate : singleFeature){
				clonedSingleFeature.add((Coordinate)singleCoordinate.clone());
			}
			clonedFeatureGeoms.add(clonedSingleFeature);
		}
		return clonedFeatureGeoms;
	}

	/**
	 * 
	 */
	private void initBackgroundLayerProxy() {
		
		for(BgType selectedBgType : options.getSelectedBgTypes()){
			switch(selectedBgType){
				case BEZIRKE:
					BackgroundLayerProxy.getShapefile(AdminShapefiles.instanceOfBezirke());
					break;
				case ORTSTEILE:
					BackgroundLayerProxy.getShapefile(AdminShapefiles.instanceOfOrtsteile());
					break;
				case TVZ:
					BackgroundLayerProxy.getShapefile(AdminShapefiles.instanceOfTvz879());
					break;
				case HEXAGON:
					BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfHexagonsSmall());
					BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfHexagonsMedium());
					BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfHexagonsLarge());
					break;
				case SQUARE:
					BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfSquaresSmall());
					BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfSquaresMedium());
					BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfSquaresLarge());
					break;
				case TRIANGLE:
					BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfTrianglesSmall());
					BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfTrianglesMedium());
					BackgroundLayerProxy.getShapefile(GridShapefiles.instanceOfTrianglesLarge());
				break;
			}
		}
		
	}
	
}
