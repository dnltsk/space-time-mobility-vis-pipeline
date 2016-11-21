package de.dlr.ivf.tapas.analyzer.geovis.common;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

import de.dlr.ivf.tapas.analyzer.geovis.IPersonCreationVisitor;

public class EstimatedTimeMessage implements IPersonCreationVisitor{

	private JTextArea console = null;
	
	private final Long messageIntervalMillis = 1L *1000L;// 30sec
	private Long firstPersonTimeMillis = null;
	private Integer personCount = null;
	private long lastTimeLeftMillis = 0; 
	
	public EstimatedTimeMessage(JTextArea console, Integer personCount) {
		this.console = console;
		this.personCount = personCount;
	}
	
	@Override
	public void beforeProcessing() {
	}
	
	@Override
	public void onPersonCreation(PersonPojo person, int personIndex) {
		
		if(firstPersonTimeMillis == null){
			appendInitMessage();
		}else{
			updateMessage(personIndex);
		}
		
	}

	private void updateMessage(int personIndex) {
		long currentTimeMillis = System.currentTimeMillis();
		long timeLeftMillis = currentTimeMillis - this.firstPersonTimeMillis;
		if(timeLeftMillis > (this.lastTimeLeftMillis + this.messageIntervalMillis)){
			
			this.lastTimeLeftMillis = timeLeftMillis;
			
			int personsLeft = personIndex + 1;
			long iterationSpeed = (long)(timeLeftMillis / personsLeft);
			long timeEstimatedMillis = iterationSpeed * (this.personCount - personsLeft);
			
			float progress = ((float)personsLeft / (float)this.personCount) * 100f;
			
			SimpleDateFormat sdf = new SimpleDateFormat("DD:HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));   
			
			String message = "";
			message += "GeoVis: >>>\n";
			message += "GeoVis: "+progress+"% done ("+personsLeft+" of "+this.personCount+" persons)\n";
			message += "GeoVis: time left:      "+sdf.format(timeLeftMillis)+"\n";
			message += "GeoVis: time estimated: "+sdf.format(timeEstimatedMillis)+"\n";
			message += "GeoVis: <<<";
			
			Pattern pattern = Pattern.compile("GeoVis: >>>.+GeoVis: <<<", Pattern.DOTALL);
			String consoleText = this.console.getText();
			Matcher matcher = pattern.matcher(consoleText);
			String newConsoleText = matcher.replaceAll(message);
			this.console.setText(newConsoleText);
		}
	}

	private void appendInitMessage() {
		firstPersonTimeMillis = System.currentTimeMillis();
		this.console.append("GeoVis: >>>\n");
		this.console.append("GeoVis: 0.0% done (1 of "+this.personCount+" persons)\n");
		this.console.append("GeoVis: time left:      00:00:00\n");
		this.console.append("GeoVis: time estimated: ??:??:??\n");
		this.console.append("GeoVis: <<<\n\n");
	}
	
	@Override
	public void onHouseholdCreation(List<PersonPojo> household, int householdIndex) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void afterProcessing() {
		// TODO Auto-generated method stub
	}
	
}
