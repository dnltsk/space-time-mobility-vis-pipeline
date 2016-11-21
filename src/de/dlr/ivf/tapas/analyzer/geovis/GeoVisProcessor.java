package de.dlr.ivf.tapas.analyzer.geovis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.common.SimulationDetailsPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.io.DbGeoVis;
import de.dlr.ivf.tapas.analyzer.geovis.common.io.DbSimulationDetails;
import de.dlr.ivf.tapas.analyzer.geovis.resources.GeoVisRessourceLocator;
import de.dlr.ivf.tapas.analyzer.gui.AbstractCoreProcess;
import de.dlr.ivf.tapas.analyzer.inputfileconverter.TapasTrip;

public class GeoVisProcessor extends AbstractCoreProcess {

	private GeoVisPersonCreator personCreator = null;
	private HashMap<Integer, Coordinate> preloadedCoordinates = null;
	private Integer personCount = null;
	private JTextArea console = null;
	private GeoVisOptions options = null;
	private String outputPath = null;
	//private ScriptEngine jsEngine = null;
	
	/**
	 * 
	 * @param options
	 */
	public GeoVisProcessor(GeoVisOptions options) {
		this.options = options;
		//this.jsEngine  = new ScriptEngineManager().getEngineByName("JavaScript");
	}

	
	@Override
	public boolean init(String outputPath, JTextArea console) {
		this.console = console;
		this.outputPath = outputPath;
		this.console.append("\nGeoVis: init..\n\n");
		try {
			copyWebFolder(outputPath);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}


	@Override
	public boolean prepare(String filePath, TapasTrip trip) {
		if(this.personCreator == null){
			//very first trip -> prepare the PersonCreator;
			SimulationDetailsPojo simulationDetails = loadSimulationDetails(filePath);
			preloadExtraData(simulationDetails);
			initPersonCreator(simulationDetails);
			this.personCreator.beforeProcessing();
		}
		Coordinate coordStart = preloadedCoordinates.get(trip.getLocIdStart());
		Coordinate coordEnd = preloadedCoordinates.get(trip.getLocIdEnd());
		this.personCreator.addTrip(trip, coordStart, coordEnd);
		return true;
	}
	
	/**
	 * 
	 * @param filePath
	 */
	private SimulationDetailsPojo loadSimulationDetails(String filePath) {
		try {
			return new DbSimulationDetails().loadSimulationDetails(filePath);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param filePath
	 */
	private void initPersonCreator(SimulationDetailsPojo simulationDetails) {
		this.personCreator = new GeoVisPersonCreator(outputPath,
													 options, 
													 simulationDetails, 
													 this.personCount, 
													 console);
	}



	@Override
	public boolean finish() {
		//System.out.println(this.getClass().getCanonicalName()+".finish()");
		this.personCreator.afterProcessing();
		return false;
	}


	/**
	 * 
	 * @param outputPath
	 */
	private void copyWebFolder(String outputPath) throws IOException{
			this.console.append("GeoVis: cleaning output folder.. This may take a while..\n");
			File targetDirectory = new File(outputPath+"/GeoVis/");
			if(targetDirectory.exists()){
				try {
					FileUtils.deleteDirectory(targetDirectory);
				} catch (IOException e) {
					this.console.append("GeoVis: Error! cannot clean output folder "+targetDirectory.getAbsolutePath()+"\n");
					this.console.append("GeoVis:        -> please close all connections to this folder an its subfolders.\n");
					this.console.append("GeoVis:           and try again.\n");
					throw(e);
				}
			}
			this.console.append("GeoVis: done.\n\n");
			this.console.append("GeoVis: copying web-folder..\n");
			String srcFolder = GeoVisRessourceLocator.getPath();
			try {
				FileUtils.copyDirectory(new File(srcFolder+"GeoVis/"), targetDirectory);
			} catch (IOException e) {
				this.console.append("GeoVis: Error! cannot copy ressource folders "+srcFolder+"\n");
				this.console.append("GeoVis: 		to "+targetDirectory.getAbsolutePath()+"\n");
				this.console.append("GeoVis:        -> please close all connections to this folders an its subfolders.\n");
				this.console.append("GeoVis:           and try again.\n");
				throw(e);
			}
			this.console.append("GeoVis: done.\n\n");
	}
	
	/**
	 * 
	 * @param filePath
	 */
	private void preloadExtraData(SimulationDetailsPojo simulationDetails) {
		try {
			
			DbGeoVis db = new DbGeoVis(simulationDetails);
			
			console.append("GeoVis: counting persons..\n");
			this.personCount = db.countPersons();
			console.append("GeoVis: "+this.personCount+" persons counted.\n\n");
			
			console.append("GeoVis preloading coordinates of locations and households.. This may take a while..\n");
			this.preloadedCoordinates = db.loadLocations();
			console.append("GeoVis: "+this.preloadedCoordinates.size()+" coordinates loaded.\n\n");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
