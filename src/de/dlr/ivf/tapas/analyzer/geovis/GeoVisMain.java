package de.dlr.ivf.tapas.analyzer.geovis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;
import de.dlr.ivf.tapas.analyzer.geovis.localhost.DbTripfileReaderLocalhost;
import de.dlr.ivf.tapas.analyzer.geovis.localhost.TapasTripPojo;
import de.dlr.ivf.tapas.analyzer.geovis.resources.GeoVisRessourceLocator;

public class GeoVisMain {

	public static long startTime = 0;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

		String outputPath = "C:/temp/";
		
		GeoVisOptions options = new GeoVisOptions();
		//options.selectBgType(BgType.BEZIRKE);
		//options.selectBgType(BgType.ORTSTEILE);
		//options.selectBgType(BgType.TVZ);
		//options.selectBgType(BgType.HEXAGON);
		//options.selectBgType(BgType.SQUARE);
		//options.selectBgType(BgType.TRIANGLE);
		//options.selectVisType(VisType.SINGLE);
		//options.selectVisType(VisType.AGG);
		options.selectVisType(VisType.ALL);
		
		int chunk = 1000;
		int step = 0;
		
		//copyWebFolder(outputPath);
		
		Integer personCount = new DbTripfileReaderLocalhost().countPersons();
		Integer tripCount = new DbTripfileReaderLocalhost().countTrips();
		
		
		GeoVisPersonCreatorLocalhost personCreator = new GeoVisPersonCreatorLocalhost(outputPath, options, personCount);
		personCreator.beforeProcessing();
		/*while(step*chunk <= (tripCount+chunk)){
		//while(step*chunk < chunk*432){//(3GB PathLayer)
		//while(step*chunk < chunk*1){
			handlePerformanceLog(step, chunk);
			System.out.println("Starte "+(step+1)+". Durchlauf");
			System.out.println("Bereits "+ new DecimalFormat("###.00").format((double)(((double)step*(double)chunk)/(double)tripCount)*100.0)+"% fertiggestellt");
			List<TapasTripPojo> trips = new DbTripfileReaderLocalhost().loadTrips(step, chunk);
			for(TapasTripPojo trip : trips){
				personCreator.addTrip(trip);
			}
			step++;
		}*/
		
		List<Integer> personIds = new DbTripfileReaderLocalhost().loadPersonIds();
		
		Random randomGenerator = new Random();
		List<Integer> addedPersonIds = new ArrayList<Integer>();
		int chainSize = 3000;
		for(int i=0; i<chainSize; i++){
			System.out.println("### "+i);
			int randomIndex = randomGenerator.nextInt(personIds.size());
			if(addedPersonIds.contains(personIds.get(randomIndex))){
				i--;
				continue;
			}
			addedPersonIds.add(personIds.get(randomIndex));
			List<TapasTripPojo> trips = new DbTripfileReaderLocalhost().loadTripsOfPerson(personIds.get(randomIndex));
			for(TapasTripPojo trip : trips){
				personCreator.addTrip(trip);
			}
			if(i==(chainSize-1)){
				int div = chainSize - personCreator.getQuickChain().getPersons().size();
				if(div > 0){
					i -= div;
				}
			}
		}
		handlePerformanceLog(step, (double)tripCount/(double)step);
		personCreator.afterProcessing();
		
	}
	
	/**
	 * 
	 * @param outputPath
	 */
	public static void copyWebFolder(String outputPath) throws IOException{
			System.out.println("GeoVis: cleaning output folder.. This may take a while..\n");
			File targetDirectory = new File(outputPath+"/GeoVis/");
			if(targetDirectory.exists()){
				try {
					FileUtils.deleteDirectory(targetDirectory);
				} catch (IOException e) {
					System.out.println("GeoVis: Error! cannot clean output folder "+targetDirectory.getAbsolutePath()+"\n");
					System.out.println("GeoVis:        -> please close all connections to this folder an its subfolders.\n");
					System.out.println("GeoVis:           and try again.\n");
					throw(e);
				}
			}
			System.out.println("GeoVis: done.\n\n");
			System.out.println("GeoVis: copying web-folder..\n");
			String srcFolder = GeoVisRessourceLocator.getPath();
			try {
				FileUtils.copyDirectory(new File(srcFolder+"GeoVis/"), targetDirectory);
			} catch (IOException e) {
				System.out.println("GeoVis: Error! cannot copy ressource folders "+srcFolder+"\n");
				System.out.println("GeoVis: 		to "+targetDirectory.getAbsolutePath()+"\n");
				System.out.println("GeoVis:        -> please close all connections to this folders an its subfolders.\n");
				System.out.println("GeoVis:           and try again.\n");
				throw(e);
			}
			System.out.println("GeoVis: done.\n\n");
	}
	
	/**
	 * 
	 * @param step
	 * @param chunk
	 */
	public static void handlePerformanceLog(int step, double chunk){
		
		try {
			
			if(step == 0){
				FileWriter fw = new FileWriter("C:/temp/GeoVis/performance.txt");
				fw.write("time;trips\n");
				fw.close();
				startTime = new Date().getTime();
				return;
			}
			
			long timeDiv = new Date().getTime() - startTime;
			FileWriter fw = new FileWriter("C:/temp/GeoVis/performance.txt", true);
			fw.write(timeDiv+";"+(step*chunk)+"\n");
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}
