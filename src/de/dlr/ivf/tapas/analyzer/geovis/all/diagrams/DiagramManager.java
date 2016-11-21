package de.dlr.ivf.tapas.analyzer.geovis.all.diagrams;

import java.util.ArrayList;
import java.util.List;

import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;

public class DiagramManager {

	String outputPath = null; 
	List<AbstractDiagram> diagrams = null;
	
	public DiagramManager(String outputPath) {
		diagrams = new ArrayList<AbstractDiagram>();
		this.outputPath = outputPath;
	}
	
	public void addDiagram(AbstractDiagram diagram){
		diagrams.add(diagram);
	}
	
	public void addDiagramData(PersonPojo person){
		for(AbstractDiagram diagram : diagrams){
			diagram.addDiagramData(person);
		}
	}
	
	public void writeDiagramData(){
		
	}
	
}
