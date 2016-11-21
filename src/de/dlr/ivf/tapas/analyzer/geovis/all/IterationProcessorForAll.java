package de.dlr.ivf.tapas.analyzer.geovis.all;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import de.dlr.ivf.tapas.analyzer.geovis.IPersonCreationVisitor;
import de.dlr.ivf.tapas.analyzer.geovis.all.diagrams.DiagramManager;
import de.dlr.ivf.tapas.analyzer.geovis.all.diagrams.LocationFlowDiagram;
import de.dlr.ivf.tapas.analyzer.geovis.all.points.ChainManager;
import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;
import de.dlr.ivf.tapas.analyzer.geovis.resources.GeoVisRessourceLocator;

public class IterationProcessorForAll implements IPersonCreationVisitor{

	private List<Integer> randomPersons = null; 
	private Integer personIterationCounter = null;
	private ChainManager chainManager = null;
	private DiagramManager diagramManager = null;
	private String outputPath = null;
	
	/**
	 * 
	 * @param outputPath
	 * @param personCount
	 */
	public IterationProcessorForAll(String outputPath, Integer personCount) {
		this.outputPath = outputPath;
		this.chainManager = new ChainManager(outputPath);
		this.diagramManager = new DiagramManager(outputPath);
		this.diagramManager.addDiagram(new LocationFlowDiagram());
		//this.diagramManager.addDiagram(new LocationDiagram());
		//this.diagramManager.addDiagram(new ModeDiagram());
		initRandomPersons(personCount);
	}
	
	@Override
	public void beforeProcessing() {
		System.out.println("clearing target folder "+ GeoVisRessourceLocator.getPath() + "/GeoVis/web/json/animation/");
		new SimpleDataWriter(Arrays.asList(this.outputPath + "/GeoVis/web/json/animation/")).clearTargetPath();
	}

	@Override
	public void onHouseholdCreation(List<PersonPojo> household, int householdIndex) {
	}
	
	@Override
	public void onPersonCreation(PersonPojo person, int personIndex) {
		//System.out.println("IterationProcessorForAll: processing Person "+person.getP_id());
		if(this.personIterationCounter==null){
			this.personIterationCounter = 0;
		}else{
			this.personIterationCounter++;
		}
		this.diagramManager.addDiagramData(person);
		if(this.randomPersons.contains(personIterationCounter)){
			chainManager.addPerson(person);
		}
		
	}

	@Override
	public void afterProcessing() {
		chainManager.flushCache();
		//diagramManager.flushCache();
	}

	/**
	 * 
	 * @param personCount
	 */
	private void initRandomPersons(Integer personCount) {
		this.randomPersons = new ArrayList<Integer>();
		if(personCount <= 3000){
			for(int i=0; i<=personCount; i++){
				this.randomPersons.add(i);
			}
		}else{
			int maximum = personCount;
			int minimum = 0;
			int range = maximum - minimum + 1;
			Random random = new Random();
			while(this.randomPersons.size() <= 3000){
				int randomNumber =  random.nextInt(range) + minimum;
				if(!this.randomPersons.contains(randomNumber)){
					this.randomPersons.add(randomNumber);
				}
			}
		}
	}
	
}
