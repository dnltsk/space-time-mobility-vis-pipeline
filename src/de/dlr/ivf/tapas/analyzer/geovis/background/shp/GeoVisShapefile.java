package de.dlr.ivf.tapas.analyzer.geovis.background.shp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public abstract class GeoVisShapefile {

	public static final String AREA_ATTRIBUTE = "area";
	public static final String ALL_ATTRIBUTE = "ALL";
	public static final String POPULATION_ATTRIBUTE = "POPULATION";
	
	protected String name = null;
	private SimpleFeatureStore featureStoreOriginal = null;
	private SimpleFeatureStore featureStoreSimplified = null;
	private Envelope bbox = null;
	private ArrayList<HashMap<String, Integer>> featureAttributes = null;
	
	private ArrayList<Coordinate> insideCoordinateCache = null;
	private ArrayList<Integer> insideIndexCache = null;
	//private final Integer CACHE_SIZE = 20;
	
	public GeoVisShapefile(String name) {
		this.name = name;
		this.insideCoordinateCache = new ArrayList<Coordinate>();
		this.insideIndexCache = new ArrayList<Integer>();
	}
	
	protected void init(){
		if(this.featureStoreOriginal == null){
			this.featureStoreOriginal = initFeatureStore(false);
		}
		if(this.featureStoreSimplified == null){
			this.featureStoreSimplified = initFeatureStore(true);
		}
		if(this.featureAttributes == null){
			this.featureAttributes = initFeatureAttributes();
		}
	}
	
	abstract public Object getId();
	
	abstract public String getPathToOriginal();
	
	abstract public String getPathToSimplified();
	
	abstract public String getFilename();

	/**
	 * 
	 */
	public ArrayList<HashMap<String, Integer>> getFeatureAttributes(){
		return this.featureAttributes;
	}
	
	/**
	 * 
	 * @param featureIndex
	 * @param attributeName
	 * @param attributeValue
	 */
	public void setFeatureAttribute(Integer featureIndex, String attributeName, Integer attributeValue){
		if(this.featureStoreOriginal == null){
			init();
		}
		if(featureIndex == null || featureIndex < 0 || featureIndex > this.featureAttributes.size()-1){
			System.err.println("incrementFeatureAttribute() filename="+getFilename()+" featureIndex="+featureIndex+" key="+attributeName+" -> OUTSIDE!");
			return;
		}
		this.featureAttributes.get(featureIndex).put(attributeName, attributeValue);
	}
	
	/**
	 * Falls key noch nicht vorhanden -> auf 1 gesetzt
	 * 
	 * @param featureIndex
	 * @param attributeName
	 */
	public void incrementFeatureAttribute(Integer featureIndex, String attributeName){
		if(this.featureStoreOriginal == null){
			init();
		}
		if(featureIndex == null || featureIndex < 0 || featureIndex > this.featureAttributes.size()-1){
			System.err.println("incrementFeatureAttribute() filename="+getFilename()+" featureIndex="+featureIndex+" attributeName="+attributeName+" -> OUTSIDE!");
			return;
		}
		//System.out.println("incrementFeatureAttribute() filename="+getFilename()+" featureIndex="+featureIndex+" key="+key);
		if(this.featureAttributes.get(featureIndex).get(attributeName) == null){
			this.featureAttributes.get(featureIndex).put(attributeName, 0);
		}
		this.featureAttributes.get(featureIndex).put(attributeName, 1 + this.featureAttributes.get(featureIndex).get(attributeName));
	}

	/**
	 * Gibt die Geometrien in Form von Koordinaten zurück,
	 * genutzt werden ausschließlich die vereinfachten Geometrien 
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Coordinate>> getSimplifiedFeatureGeoms(){
		System.out.println("getSimplifiedFeatureGeoms() "+getFilename());
		if(this.featureStoreSimplified == null){
			init();
			if(this.featureStoreSimplified == null){
				System.err.println("no featureStoreSimplified found in getSimplifiedFeatureGeoms()");
				return new ArrayList<ArrayList<Coordinate>>();
			}
		}
		
		ArrayList<ArrayList<Coordinate>> featureGeoms = new ArrayList<ArrayList<Coordinate>>();
		
		SimpleFeatureIterator simpleFeatureIterator = null;
		try {
			
			simpleFeatureIterator = this.featureStoreSimplified.getFeatures().features();
			while(simpleFeatureIterator.hasNext()){
				SimpleFeature feature = simpleFeatureIterator.next();
				//System.out.println("FeatureType = "+feature.getFeatureType()); 
				MultiPolygon multiPolygon = (MultiPolygon) feature.getDefaultGeometry();
				if(multiPolygon != null && multiPolygon.getGeometryN(0) != null){
					Polygon polygon = (Polygon)multiPolygon.getGeometryN(0);
					ArrayList<Coordinate> polygonCoords = new ArrayList<Coordinate>(Arrays.asList(polygon.getCoordinates()));
					featureGeoms.add(polygonCoords);
				}else{
					//Feature ohne default Geometrie -> leere Koordinatenliste hinzufügen!
					featureGeoms.add(new ArrayList<Coordinate>());
				}
				
			} 
			
			//System.out.println(this.getClass().getCanonicalName()+".initFeatureGeoms() -> "+coordinates.size()+" extrahiert");
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(simpleFeatureIterator != null){
				simpleFeatureIterator.close();
			}
		}
		
		return featureGeoms;
	}
	
	/**
	 * Gibt die Geometrien in Form von Koordinaten zurück,
	 * genutzt werden ausschließlich die vereinfachten Geometrien 
	 * 
	 * @return
	 */
	public Double getSimplifiedAvgSquareM(){
		System.out.println("getSimplifiedAvgSquareM() "+getFilename());
		if(this.featureStoreSimplified == null){
			init();
			if(this.featureStoreSimplified == null){
				System.err.println("no featureStoreSimplified found in getSimplifiedAvgSquareM()");
				return new Double(0);
			}
		}
		
		
		double squareMeterSum = 0.0;
		int areaCounter = 0;
		
		SimpleFeatureIterator simpleFeatureIterator = null;
		try {
			
			simpleFeatureIterator = this.featureStoreSimplified.getFeatures().features();
			while(simpleFeatureIterator.hasNext()){
				SimpleFeature feature = simpleFeatureIterator.next();
				//System.out.println("FeatureType = "+feature.getFeatureType()); 
				MultiPolygon multiPolygon = (MultiPolygon) feature.getDefaultGeometry();
				if(multiPolygon != null && multiPolygon.getGeometryN(0) != null){
					Polygon polygon = (Polygon)multiPolygon.getGeometryN(0);
					//new MinimumBoundingCircle(polygon);
					squareMeterSum += polygon.getArea();
					areaCounter++;
				}else{
					//Feature ohne default Geometrie -> leere Koordinatenliste hinzufügen!
					squareMeterSum += 0.0;
					areaCounter++;
				}
				
			} 
			
			//System.out.println(this.getClass().getCanonicalName()+".initFeatureGeoms() -> "+coordinates.size()+" extrahiert");
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(simpleFeatureIterator != null){
				simpleFeatureIterator.close();
			}
		}
		
		double avgSquareM = squareMeterSum / areaCounter; 
		return avgSquareM;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Coordinate> getSimplifiedFeatureCentroids(){
		System.out.println("getSimplifiedFeatureCentroids() "+getFilename());
		if(this.featureStoreSimplified == null){
			init();
			if(this.featureStoreSimplified == null){
				System.err.println("no featureStoreSimplified found in getSimplifiedFeatureCentroids()");
				return new ArrayList<Coordinate>();
			}
		}
		
		ArrayList<Coordinate> featureGeoms = new ArrayList<Coordinate>();
		
		SimpleFeatureIterator simpleFeatureIterator = null;
		try {
			
			simpleFeatureIterator = this.featureStoreSimplified.getFeatures().features();
			while(simpleFeatureIterator.hasNext()){
				SimpleFeature feature = simpleFeatureIterator.next();
				//System.out.println("FeatureType = "+feature.getFeatureType()); 
				MultiPolygon multiPolygon = (MultiPolygon) feature.getDefaultGeometry();
				if(multiPolygon != null && multiPolygon.getGeometryN(0) != null){
					Polygon polygon = (Polygon)multiPolygon.getGeometryN(0);
					featureGeoms.add(polygon.getCentroid().getCoordinate());
				}else{
					//Feature ohne default Geometrie -> leere Koordinatenliste hinzufügen!
					featureGeoms.add(new Coordinate());
				}
				
			} 
			
			//System.out.println(this.getClass().getCanonicalName()+".initFeatureGeoms() -> "+coordinates.size()+" extrahiert");
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(simpleFeatureIterator != null){
				simpleFeatureIterator.close();
			}
		}
		
		return featureGeoms;
	}
	
	/**
	 * Liefert den Index des Features, in dem sich die Koordinate befindet.
	 * Genutzt wird immer das originale Shapefile, nicht das vereinfachte.
	 * 
	 * wenn Koordinate in keinem Feature liegt -> null
	 * wenn Koordinate in mehreren Features liegt -> erstes Feature
	 * 
	 *  
	 * @param coordinate
	 * @return
	 */
	public Integer inside(Coordinate coordinate){

		if(name.equals("kontur")){
			return 0;
		}
		
		if(this.featureStoreOriginal == null){
			init();
		}
		
		int cachedCoordinateIndex = this.insideCoordinateCache.indexOf(coordinate);
		if(cachedCoordinateIndex >= 0){
			Integer cachedIndex = this.insideIndexCache.get(cachedCoordinateIndex);
			//move cached Coordinate onto the top of the Cache
			//this.insideCoordinateCache.remove(coordinate);
			//this.insideCoordinateCache.add(coordinate);
			//this.insideIndexCache.remove(cachedIndex);
			//this.insideIndexCache.add(cachedIndex);
			return cachedIndex;
		}
		
		Query query = null;
		SimpleFeatureIterator simpleFeatureIterator = null;
		try {
    		
			query = new Query(this.getFilename(), CQL.toFilter("CONTAINS(the_geom, POINT("+(coordinate.x)+" "+(coordinate.y)+"))"));
			
			simpleFeatureIterator = featureStoreOriginal.getFeatures(query).features();
			if(simpleFeatureIterator.hasNext()){
				SimpleFeature cell = simpleFeatureIterator.next();
				
				Integer featureIndex = Integer.parseInt(cell.getID().replaceAll(getFilename()+".", ""));
				
				featureIndex--; //Shapefile starts with 1 so it has to be decremented! 
				
				if(this.insideCoordinateCache.size() > 20){
					this.insideCoordinateCache.remove(0);
					this.insideIndexCache.remove(0);
				}
				this.insideCoordinateCache.add(coordinate);
				this.insideIndexCache.add(featureIndex);
				
				return featureIndex;
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (CQLException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			query = null;
			if(simpleFeatureIterator!=null)
				simpleFeatureIterator.close();
		}
    	return null;//outside
	}

	/**
	 * genutzt wird immer das orignale Shapefile, nicht das vereifachte!
	 * 
	 * @return
	 */
	public Envelope getBbox(){
		if(this.featureStoreOriginal == null){
			init();
		}
		
		try {
			if(this.bbox == null){
				 this.bbox = this.featureStoreOriginal.getFeatures(Query.ALL).getBounds();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.bbox;
	}
	
	/**
	 * 
	 */
	private SimpleFeatureStore initFeatureStore(boolean simplified){
		System.out.println("initFeatureStore "+getFilename());
		
		try {
			
			File file = null;
			if(simplified){
				file = new File(this.getPathToSimplified());
			}else{
				file = new File(this.getPathToOriginal());
			}
			
			Map<String, Object> connectionParameters = new HashMap<String, Object>();
			connectionParameters.put("url", file.toURI().toURL());
			DataStore dataStore = DataStoreFinder.getDataStore(connectionParameters);
			
			return (SimpleFeatureStore) dataStore.getFeatureSource(this.getFilename());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * 
	 */
	private ArrayList<HashMap<String, Integer>> initFeatureAttributes(){
		System.out.println("initFeatureAttributes "+getFilename());
		
		ArrayList<HashMap<String, Integer>> featureAttributes = new ArrayList<HashMap<String, Integer>>();
		
		SimpleFeatureIterator simpleFeatureIterator = null;
		
		try {

			simpleFeatureIterator = this.featureStoreOriginal.getFeatures().features();
			while(simpleFeatureIterator.hasNext()){
				SimpleFeature cell = simpleFeatureIterator.next();
				
				MultiPolygon multiPolygon = (MultiPolygon) cell.getDefaultGeometry();
				if(multiPolygon != null && multiPolygon.getGeometryN(0) != null){
					Polygon polygon = (Polygon) multiPolygon.getGeometryN(0);
					HashMap<String, Integer> initAttributes = new HashMap<String, Integer>();
					initAttributes.put(AREA_ATTRIBUTE, (int)polygon.getArea());
					featureAttributes.add(initAttributes);
				}
				
			}
			
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(simpleFeatureIterator!=null)
				simpleFeatureIterator.close();
		}
		
		return featureAttributes;
	}
	
}
