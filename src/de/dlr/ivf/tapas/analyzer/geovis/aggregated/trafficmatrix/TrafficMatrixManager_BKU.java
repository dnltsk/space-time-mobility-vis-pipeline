package de.dlr.ivf.tapas.analyzer.geovis.aggregated.trafficmatrix;

import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.background.BackgroundLayerProxy;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.GeoVisShapefile;
import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.codes.LocationType;

public class TrafficMatrixManager_BKU {

	private TrafficMatrixReader reader = null;
	private TrafficMatrixWriter writer = null;

	private HashMap<String, HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>>> trafficMatricesCache = null;
	private HashMap<String, Integer> trafficMatricesCacheCounter = null;
	private final Integer CACHE_SIZE_PER_MATRIX = 5000;
	
	/**
	 * 
	 */
	public TrafficMatrixManager_BKU(String outputPath) {
		this.reader = new TrafficMatrixReader(outputPath);
		this.writer = new TrafficMatrixWriter(outputPath);
		this.trafficMatricesCache = new HashMap<String, HashMap<String,HashMap<Integer,HashMap<Integer,Integer>>>>();
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
		
		String matrixNameSrc = layerName+"_"+srcLocationType.toString()+"_src";
		String matrixAllSrc = layerName+"_ALL_src";
		String matrixNameDest = layerName+"_"+destLocationType.toString()+"_dest";
		String matrixAllDest = layerName+"_ALL_dest";
		
		/* Source-Traffic */
		incrementEntryInCache(matrixNameSrc, destLocationType.toString(), srcCellIndex, destCellIndex);//source-Traffic
		incrementEntryInCache(matrixNameSrc, "ALL", srcCellIndex, destCellIndex);//source-Traffic
		incrementEntryInCache(matrixAllSrc, destLocationType.toString(), srcCellIndex, destCellIndex);//source-Traffic
		incrementEntryInCache(matrixAllSrc, "ALL", srcCellIndex, destCellIndex);//source-Traffic
		
		/* Destination-Traffic */
		incrementEntryInCache(matrixNameDest, srcLocationType.toString(), srcCellIndex, destCellIndex);//destination-Traffic
		incrementEntryInCache(matrixNameDest, "ALL", srcCellIndex, destCellIndex);//destination-Traffic
		incrementEntryInCache(matrixAllDest, srcLocationType.toString(), srcCellIndex, destCellIndex);//destination-Traffic
		incrementEntryInCache(matrixAllDest, "ALL", srcCellIndex, destCellIndex);//destination-Traffic
		
		checkFlushCache(matrixNameSrc);
		checkFlushCache(matrixAllSrc);
		checkFlushCache(matrixNameDest);
		checkFlushCache(matrixAllDest);
		
	}
	
	/**
	 * 
	 * @param matrixName
	 */
	private void checkFlushCache(String matrixName) {
		if(this.CACHE_SIZE_PER_MATRIX < this.trafficMatricesCacheCounter.get(matrixName)){
			flushCache(matrixName);
			this.trafficMatricesCache.put(matrixName, new HashMap<String, HashMap<Integer,HashMap<Integer,Integer>>>());
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
		HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> cachedMatrix = this.trafficMatricesCache.get(matrixName);
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
	private JSONObject mergeMatrices(JSONObject loadedMatrix, HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> cachedMatrix) {
		
		for(String destLocationType : cachedMatrix.keySet()){
			for(Integer srcCellIndex : cachedMatrix.get(destLocationType.toString()).keySet()){
				for(Integer destCellIndex : cachedMatrix.get(destLocationType).get(srcCellIndex).keySet()){
					Integer cachedValue = cachedMatrix.get(destLocationType).get(srcCellIndex).get(destCellIndex);
					registerJsonEntry(loadedMatrix, destLocationType, srcCellIndex, destCellIndex);
					Long loadedValue = (Long)((JSONObject)((JSONObject)loadedMatrix.get(destLocationType.toString())).get(srcCellIndex.toString())).get(destCellIndex.toString());
					Integer newValue = loadedValue.intValue() + cachedValue;
					((JSONObject)((JSONObject)loadedMatrix.get(destLocationType.toString())).get(srcCellIndex.toString())).put(destCellIndex.toString(), newValue);
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
	private void incrementEntryInCache(String matrixName, String destLocationType, Integer srcCellIndex, Integer destCellIndex) {
		HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> cachedMatrix = this.trafficMatricesCache.get(matrixName);
		if(cachedMatrix==null){
			cachedMatrix = new HashMap<String, HashMap<Integer,HashMap<Integer,Integer>>>();
			this.trafficMatricesCache.put(matrixName, cachedMatrix);
			this.trafficMatricesCacheCounter.put(matrixName, 0);
		}
		boolean newRegistered = registerHashMapEntry(cachedMatrix, destLocationType, srcCellIndex, destCellIndex);
		if(newRegistered){
			this.trafficMatricesCacheCounter.put(matrixName, 1+this.trafficMatricesCacheCounter.get(matrixName));
		}
		incrementEntry(cachedMatrix, destLocationType, srcCellIndex, destCellIndex);
	}

	/**
	 * 
	 * @param cachedMatrix
	 * @param destLocationType
	 * @param srcCellIndex
	 * @param destCellIndex
	 */
	private void incrementEntry(HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> cachedMatrix, String destLocationType, Integer srcCellIndex, Integer destCellIndex) {
		Integer value = cachedMatrix.get(destLocationType).get(srcCellIndex).get(destCellIndex);
		value++;
		cachedMatrix.get(destLocationType).get(srcCellIndex).put(destCellIndex, value);
	}
	
	
	
	/**
	 * 
	 * @param destLocationType
	 * @param srcCellIndex
	 * @param destCellIndex
	 * @param matrix
	 */
	private boolean registerHashMapEntry(HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> matrix, String destLocationType, Integer srcCellIndex, Integer destCellIndex) {
		HashMap<Integer, HashMap<Integer, Integer>> destLocationMatrix = matrix.get(destLocationType);
		if(destLocationMatrix == null){
			/* first srcLocationType-destLocationType-entry */
			destLocationMatrix = new HashMap<Integer, HashMap<Integer,Integer>>();
			HashMap<Integer, Integer> srcCellMatrix = new HashMap<Integer, Integer>();
			srcCellMatrix.put(destCellIndex, 0);//register entry
			destLocationMatrix.put(srcCellIndex, srcCellMatrix);
			matrix.put(destLocationType, destLocationMatrix);
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

	/**
	 * 
	 * @param destLocationType
	 * @param srcCellIndex
	 * @param destCellIndex
	 * @param matrix
	 */
	@SuppressWarnings("unchecked")
	private boolean registerJsonEntry(JSONObject matrix, String destLocationType, Integer srcCellIndex, Integer destCellIndex) {
		JSONObject destLocationMatrix = (JSONObject)matrix.get(destLocationType.toString());
		if(destLocationMatrix == null){
			/* first srcLocationType-destLocationType-entry */
			destLocationMatrix = new JSONObject();
			JSONObject srcCellMatrix = new JSONObject();
			srcCellMatrix.put(destCellIndex.toString(), new Long(0));//register entry
			destLocationMatrix.put(srcCellIndex.toString(), srcCellMatrix);
			matrix.put(destLocationType, destLocationMatrix);
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
