package de.dlr.ivf.tapas.analyzer.geovis.aggregated.trafficmatrix;

import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.background.BackgroundLayerProxy;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.GeoVisShapefile;
import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.codes.LocationType;

public class TrafficMatrixManager {

	private TrafficMatrixReader reader = null;
	private TrafficMatrixWriter writer = null;

	private HashMap<String, HashMap<String, HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>>>> trafficMatricesCache = null;
	private HashMap<String, Integer> trafficMatricesCacheCounter = null;
	private final Integer CACHE_SIZE_PER_MATRIX = 5000;
	
	/**
	 * 
	 */
	public TrafficMatrixManager(String outputPath) {
		this.reader = new TrafficMatrixReader(outputPath);
		this.writer = new TrafficMatrixWriter(outputPath);
		this.trafficMatricesCache = new HashMap<String, HashMap<String, HashMap<String,HashMap<Integer,HashMap<Integer,Integer>>>>>();
		this.trafficMatricesCacheCounter = new HashMap<String, Integer>();
	}
	
	/**
	 * 
	 * @param srcActivity
	 * @param destActivity
	 */
	public void incrementMovement(ActivityPojo srcActivity, ActivityPojo destActivity) {
		List<GeoVisShapefile> allLoadedShapefiles = BackgroundLayerProxy.getAllLoadedShapefiles();
		for(GeoVisShapefile shapefile : allLoadedShapefiles){
			String layerName = shapefile.getFilename();
			Integer srcCellIndex = shapefile.inside(new Coordinate(srcActivity.getLoc_coord_x(), srcActivity.getLoc_coord_y()));
			Integer destCellIndex = shapefile.inside(new Coordinate(destActivity.getLoc_coord_x(), destActivity.getLoc_coord_y()));
			LocationType srcLocationType = LocationType.getLocationType(srcActivity);
			LocationType destLocationType = LocationType.getLocationType(destActivity);
			this.incrementEntry(layerName, srcLocationType, destLocationType, srcCellIndex, destCellIndex);
		}
	}
	
	/**
	 * 
	 * bezirke_HOME.json -> {"destLocationTypeX" : {"srcCellIndexY" : {"destCellIndexZ" : 123}}}
	 * 
	 * @param layerName
	 * @param srcCellIndex
	 * @param destCellIndex
	 * @param srcLocationType
	 * @param destLocationType
	 */
	public void incrementEntry(String layerName, LocationType srcLocationType, LocationType destLocationType, Integer srcCellIndex, Integer destCellIndex){
		if(layerName == null || srcCellIndex == null || destCellIndex == null || srcLocationType == null || destLocationType == null){
			return;
		}

		//if(srcLocationType.equals(LocationType.SHOPPING)
		//		|| destLocationType.equals(LocationType.SHOPPING)){
		//	int x=0;
		//}
		
		String matrixName = layerName;
		
		incrementEntryInCache(matrixName, srcLocationType.toString(), destLocationType.toString(), srcCellIndex, destCellIndex);
		incrementEntryInCache(matrixName, "ALL", destLocationType.toString(), srcCellIndex, destCellIndex);
		incrementEntryInCache(matrixName, srcLocationType.toString(), "ALL", srcCellIndex, destCellIndex);
		incrementEntryInCache(matrixName, "ALL", "ALL", srcCellIndex, destCellIndex);
		
		checkFlushCache(matrixName);
	}
	
	/**
	 * 
	 * @param matrixName
	 */
	private void checkFlushCache(String matrixName) {
		if(this.CACHE_SIZE_PER_MATRIX < this.trafficMatricesCacheCounter.get(matrixName)){
			flushCache(matrixName);
			this.trafficMatricesCache.remove(matrixName);
			this.trafficMatricesCache.put(matrixName, new HashMap<String, HashMap<String, HashMap<Integer,HashMap<Integer,Integer>>>>());
			this.trafficMatricesCacheCounter.put(matrixName, 0);
		}
	}

	/**
	 * 
	 */
	public void flushCache(){
		for(String layerName : this.trafficMatricesCache.keySet()){
			flushCache(layerName);
		}
	}
	
	/**
	 * 
	 * @param filename
	 */
	private void flushCache(String matrixName) {
		HashMap<String, HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>>> cachedMatrix = this.trafficMatricesCache.get(matrixName);
		JSONObject loadedMatrix = this.reader.loadMatrix(matrixName);
		JSONObject mergedMatrix = mergeMatrices(loadedMatrix, cachedMatrix);
		this.writer.writeMatrix(matrixName, mergedMatrix);
	}

	/**
	 * 
	 * @param loadedMatrix
	 * @param cachedMatrix
	 * @return
	 */
	
	@SuppressWarnings("unchecked")
	private JSONObject mergeMatrices(JSONObject loadedMatrix, HashMap<String, HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>>> cachedMatrix) {
		
		for(String srcLocationType : cachedMatrix.keySet()){
			for(String destLocationType : cachedMatrix.get(srcLocationType).keySet()){
				for(Integer srcCellIndex : cachedMatrix.get(srcLocationType).get(destLocationType).keySet()){
					for(Integer destCellIndex : cachedMatrix.get(srcLocationType).get(destLocationType).get(srcCellIndex).keySet()){
						Integer cachedValue = cachedMatrix.get(srcLocationType).get(destLocationType).get(srcCellIndex).get(destCellIndex);
						registerJsonEntry(loadedMatrix, srcLocationType, destLocationType, srcCellIndex, destCellIndex);
						Long loadedValue = (Long)((JSONObject)((JSONObject)((JSONObject)loadedMatrix.get(srcLocationType)).get(destLocationType)).get(srcCellIndex.toString())).get(destCellIndex.toString());
						Integer newValue = loadedValue.intValue() + cachedValue;
						((JSONObject)((JSONObject)((JSONObject)loadedMatrix.get(srcLocationType)).get(destLocationType)).get(srcCellIndex.toString())).put(destCellIndex.toString(), newValue);
					}
				}
			}
		}
		return loadedMatrix;
		
	}

	/**
	 * 
	 * @param layerName
	 * @param srcLocationType
	 * @param destLocationType
	 * @param srcCellIndex
	 * @param destCellIndex
	 */
	private void incrementEntryInCache(String matrixName, String srcLocationType, String destLocationType, Integer srcCellIndex, Integer destCellIndex) {
		HashMap<String, HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>>> cachedMatrix = this.trafficMatricesCache.get(matrixName);
		if(cachedMatrix==null){
			cachedMatrix = new HashMap<String, HashMap<String, HashMap<Integer,HashMap<Integer,Integer>>>>();
			this.trafficMatricesCache.put(matrixName, cachedMatrix);
			this.trafficMatricesCacheCounter.put(matrixName, 0);
		}
		boolean newRegistered = registerHashMapEntry(cachedMatrix, srcLocationType, destLocationType, srcCellIndex, destCellIndex);
		if(newRegistered){
			this.trafficMatricesCacheCounter.put(matrixName, 1+this.trafficMatricesCacheCounter.get(matrixName));
		}
		incrementEntry(cachedMatrix, srcLocationType, destLocationType, srcCellIndex, destCellIndex);
	}

	/**
	 * 
	 * @param cachedMatrix
	 * @param destLocationType
	 * @param srcCellIndex
	 * @param destCellIndex
	 */
	private void incrementEntry(HashMap<String, HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>>> cachedMatrix, String srcLocationType, String destLocationType, Integer srcCellIndex, Integer destCellIndex) {
		Integer value = cachedMatrix.get(srcLocationType).get(destLocationType).get(srcCellIndex).get(destCellIndex);
		value++;
		cachedMatrix.get(srcLocationType).get(destLocationType).get(srcCellIndex).put(destCellIndex, value);
	}
	
	
	
	/**
	 * 
	 * @param destLocationType
	 * @param srcCellIndex
	 * @param destCellIndex
	 * @param matrix
	 */
	private boolean registerHashMapEntry(HashMap<String, HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>>> matrix, String srcLocationType, String destLocationType, Integer srcCellIndex, Integer destCellIndex) {
		HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> srcLocationMatrix = matrix.get(srcLocationType);
		if(srcLocationMatrix == null){
			/* first srcLocationType-destLocationType-entry */
			srcLocationMatrix = new HashMap<String, HashMap<Integer, HashMap<Integer,Integer>>>();
			HashMap<Integer, HashMap<Integer,Integer>> destLocationMatrix = new HashMap<Integer, HashMap<Integer,Integer>>();
			HashMap<Integer, Integer> srcCellMatrix = new HashMap<Integer, Integer>();
			srcCellMatrix.put(destCellIndex, 0);//register entry
			destLocationMatrix.put(srcCellIndex, srcCellMatrix);
			srcLocationMatrix.put(destLocationType, destLocationMatrix);
			matrix.put(srcLocationType, srcLocationMatrix);
			return true;
		}else{
			HashMap<Integer, HashMap<Integer, Integer>> destLocationMatrix = srcLocationMatrix.get(destLocationType);
			if(destLocationMatrix == null){
				/* first srcLocationType-destLocationType-entry */
				destLocationMatrix = new HashMap<Integer, HashMap<Integer,Integer>>();
				HashMap<Integer, Integer> srcCellMatrix = new HashMap<Integer, Integer>();
				srcCellMatrix.put(destCellIndex, 0);//register entry
				destLocationMatrix.put(srcCellIndex, srcCellMatrix);
				srcLocationMatrix.put(destLocationType, destLocationMatrix);
				return true;
			}else{
				HashMap<Integer, Integer> srcCellMatrix = destLocationMatrix.get(srcCellIndex);
				if(srcCellMatrix == null){
					/* first destLocationType-srcCellIndex-entry */
					srcCellMatrix = new HashMap<Integer, Integer>();
					srcCellMatrix.put(destCellIndex, 0);//register entry
					destLocationMatrix.put(srcCellIndex, srcCellMatrix);
					return true;
				}else{
					Integer value = srcCellMatrix.get(destCellIndex);
					if(value == null){
						/* first srcCellIndex-destCellIndex-entry */
						srcCellMatrix.put(destCellIndex, 0);//register entry
						return true;
					}else{
						return false;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param srcLocationType
	 * @param destLocationType
	 * @param srcCellIndex
	 * @param destCellIndex
	 * @param matrix
	 */
	@SuppressWarnings("unchecked")
	private boolean registerJsonEntry(JSONObject matrix, String srcLocationType, String destLocationType, Integer srcCellIndex, Integer destCellIndex) {
		JSONObject srcLocationMatrix = (JSONObject)matrix.get(srcLocationType.toString());
		if(srcLocationMatrix == null){
			JSONObject srcCellMatrix = new JSONObject();
			srcCellMatrix.put(destCellIndex.toString(), new Long(0));//register entry
			JSONObject destLocationMatrix = new JSONObject();
			destLocationMatrix.put(srcCellIndex.toString(), srcCellMatrix);
			srcLocationMatrix = new JSONObject();
			srcLocationMatrix.put(destLocationType.toString(), destLocationMatrix);
			matrix.put(srcLocationType.toString(), srcLocationMatrix);
			return true;
		}else{
			JSONObject destLocationMatrix = (JSONObject)srcLocationMatrix.get(destLocationType.toString());
			if(destLocationMatrix == null){
				/* first srcLocationType-destLocationType-entry */
				destLocationMatrix = new JSONObject();
				JSONObject srcCellMatrix = new JSONObject();
				srcCellMatrix.put(destCellIndex.toString(), new Long(0));//register entry
				destLocationMatrix.put(srcCellIndex.toString(), srcCellMatrix);
				srcLocationMatrix.put(destLocationType.toString(), destLocationMatrix);
				return true;
			
			}else{
				JSONObject srcCellMatrix = (JSONObject)destLocationMatrix.get(srcCellIndex.toString());
				if(srcCellMatrix == null){
					/* first destLocationType-srcCellIndex-entry */
					srcCellMatrix = new JSONObject();
					srcCellMatrix.put(destCellIndex.toString(), new Long(0));//register entry
					destLocationMatrix.put(srcCellIndex.toString(), srcCellMatrix);
					return true;
			
				}else{
					Object value = srcCellMatrix.get(destCellIndex.toString());
					if(value == null){
						/* first srcCellIndex-destCellIndex-entry */
						srcCellMatrix.put(destCellIndex.toString(), new Long(0));//register entry
						return true;
				
					}else{
						return false;
					}
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public void updateValueBoundaries() {
		for(String matrixName : trafficMatricesCache.keySet()){
			JSONObject loadedMatrix = this.reader.loadMatrix(matrixName);
			HashMap<String, Integer> valueBoundaries = new TrafficMatrixCalculator().calcValueBoundaries(loadedMatrix);
			this.writer.appendValueBoundaries(matrixName, loadedMatrix, valueBoundaries);
		}
	}
	
	/**
	 * 
	 */
	public void updateTop10s() {
		for(String matrixName : trafficMatricesCache.keySet()){
			JSONObject loadedMatrix = this.reader.loadMatrix(matrixName);
			new TrafficMatrixCalculator().insertTop10s(loadedMatrix);
			this.writer.writeMatrix(matrixName, loadedMatrix);
		}
	}
	
	
	
}
