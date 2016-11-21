package de.dlr.ivf.tapas.analyzer.geovis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.aggregated.IterationProcessorForAggregated;
import de.dlr.ivf.tapas.analyzer.geovis.all.IterationProcessorForAll;
import de.dlr.ivf.tapas.analyzer.geovis.background.IterationProcessorForBackrounds;
import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.EstimatedTimeMessage;
import de.dlr.ivf.tapas.analyzer.geovis.common.MovementPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.SimulationDetailsPojo;
import de.dlr.ivf.tapas.analyzer.geovis.single.IterationProcessorForSingle;
import de.dlr.ivf.tapas.analyzer.inputfileconverter.TapasTrip;
import de.dlr.ivf.tapas.analyzer.tum.databaseConnector.DBPersonReader;

public class GeoVisPersonCreator {

	private List<IPersonCreationVisitor> creationVisitors = null;
	private PersonPojo currentPerson = null;
	private int currentPersonIndex = -1;
	private Integer lastFilteredOutPerson = null;
	private int currentHouseholdIndex = -1;
	
	private List<PersonPojo> currentHousehold = null;
	private DBPersonReader dbPersonReader = null;
	private Integer personCount = null;
	private JTextArea console = null;
	private String outputPath = null;
	private GeoVisPersonFilter personFilter = null;
	
	public GeoVisPersonCreator(String outputPath, GeoVisOptions options, SimulationDetailsPojo simulationDetails, Integer personCount, JTextArea console) {
		this.outputPath = outputPath;
		this.personCount = personCount;
		this.console = console;
		this.creationVisitors = createCreationVisitors(options, personCount);
		this.personFilter = new GeoVisPersonFilter(options);
		try {
			this.dbPersonReader = new DBPersonReader(simulationDetails);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * will be executed when the GeoVisProcessor gets a new TapasTrip
	 * @param trip
	 */
	public void addTrip(TapasTrip trip, Coordinate coordStart, Coordinate coordEnd){
		boolean isFilteredOut = handlePersonCreation(trip);
		if(!isFilteredOut){
			handleMovement(trip, coordStart, coordEnd);
			handleActivity(trip, coordEnd);
			synchronizePreviousActivity();
		}
	}
	
	/**
	 * deligates the created person to the creationVisitors
	 * @param person
	 */
	private void personCreatedEvent(PersonPojo person){
		System.out.println("processing person "+person.getP_id()+"..");
		for(IPersonCreationVisitor creationVisitor : creationVisitors){
			creationVisitor.onPersonCreation(person, currentPersonIndex);
		}
	}
	
	/**
	 * deligates the created household to the creationVisitors 
	 * @param household
	 */
	private void householdCreatedEvent(List<PersonPojo> household){
		System.out.println("processing household "+household.get(0).getHh_id()+"..");
		for(IPersonCreationVisitor creationVisitor : creationVisitors){
			creationVisitor.onHouseholdCreation(household, currentHouseholdIndex);
		}
	}

	/**
	 * 
	 */
	public void beforeProcessing(){
		//System.out.println("beforeProcessing!");
		for(IPersonCreationVisitor creationVisitor : creationVisitors){
			creationVisitor.beforeProcessing();
		}
	}
	
	/**
	 * 
	 */
	public void afterProcessing(){
		//finalizing currentPerson and currentHousehold
		personCreatedEvent(this.currentPerson);
		householdCreatedEvent(this.currentHousehold);
		
		//System.out.println("afterProcessing!");
		
		for(IPersonCreationVisitor creationVisitor : this.creationVisitors){
			this.console.append("GeoVis: finalizing outputs "+(this.creationVisitors.indexOf(creationVisitor)+1)+"/"+this.creationVisitors.size()+"..\n");
			creationVisitor.afterProcessing();
		}
		this.console.append("GeoVis: outputs finalized.\n");
	}
	
	/**
	 * 
	 * @param options
	 */
	private List<IPersonCreationVisitor> createCreationVisitors(GeoVisOptions options, Integer personCount) {
		List<IPersonCreationVisitor> creationVisitors = new ArrayList<IPersonCreationVisitor>();
		creationVisitors.add(new IterationProcessorForBackrounds(this.outputPath, options));
		if(options.getSelectedVisTypes().contains(VisType.SINGLE)){
			creationVisitors.add(new IterationProcessorForSingle(this.outputPath));
		}
		if(options.getSelectedVisTypes().contains(VisType.AGG)){
			creationVisitors.add(new IterationProcessorForAggregated(this.outputPath, personCount));
		}
		if(options.getSelectedVisTypes().contains(VisType.ALL)){
			creationVisitors.add(new IterationProcessorForAll(this.outputPath, this.personCount));
		}
		creationVisitors.add(new EstimatedTimeMessage(this.console, this.personCount));
		return creationVisitors;
	}
	
	/**
	 * 
	 * @param trip
	 */
	private void handleActivity(TapasTrip trip, Coordinate coord) {
		ActivityPojo activity = createCurrentActivity(trip, coord);
		currentPerson.addActivity(activity);//add to activity chain
	}

	
	/**
	 * 
	 * @param trip
	 * @return
	 */
	private ActivityPojo createCurrentActivity(TapasTrip trip, Coordinate coord) {
		ActivityPojo activity = new ActivityPojo();
		
		activity.setActivity(trip.getActCode());
		activity.setIs_home(trip.isBackHome());
		activity.setLoc_coord_x(coord.x);
		activity.setLoc_coord_y(coord.y);
		
		activity.setActivity_start_min(trip.getActStart());
		activity.setActivity_duration_min(trip.getActDur());
		return activity;
	}

	/**
	 * 
	 * @param trip
	 */
	private void handleMovement(TapasTrip trip, Coordinate coordStart, Coordinate coordEnd) {
		if(isVeryFirstMovement()){
			createStartingActivity(trip, coordStart);
		}
		MovementPojo movement = createMovement(trip, coordStart, coordEnd);
		currentPerson.addMovement(movement);
	}

	/**
	 * 
	 * @return
	 */
	private boolean isVeryFirstMovement() {
		return currentPerson.getActivities().size()==0;
	}
	
	/**
	 * 
	 * @param trip
	 * @return
	 */
	private MovementPojo createMovement(TapasTrip trip, Coordinate coordStart, Coordinate coordEnd) {
		MovementPojo movement = new MovementPojo();
		movement.setMode(trip.getIdMode());
		movement.setActivity_start_min(trip.getActStart());
		movement.setStart_time_min((int)trip.getStartTime());
		//movement.setTravel_time_sec((float)trip.getTT());
		movement.setTravel_time_sec((float)(movement.getActivity_start_min() - movement.getStart_time_min()) * 60);
		movement.setDistance_real_m((float)(trip.getDistNet()));
		movement.setLoc_coord_x_start(coordStart.x);
		movement.setLoc_coord_y_start(coordStart.y);
		movement.setLoc_coord_x_end(coordEnd.x);
		movement.setLoc_coord_y_end(coordEnd.y);
		return movement;
	}
	
	/**
	 * because of privacy protection the stay on the location is longer than the activity duration
	 * so a discreet time gap must be added  
	 * 
	 * @param activity
	 */
	private void synchronizePreviousActivity() {
		if(currentPerson.getActivities().size() < 2){
			//keine Vorgänger-Aktivität vorhanden
			return;
		}
		ActivityPojo previousActivity = currentPerson.getActivities().get(currentPerson.getActivities().size()-2);
		MovementPojo currentMovement = currentPerson.getMovements().get(currentPerson.getMovements().size()-1);
		
		int previousActivityEnd = previousActivity.getActivity_start_min() + previousActivity.getActivity_duration_min();
		if(previousActivityEnd < currentMovement.getStart_time_min()){
			int timegap = currentMovement.getStart_time_min() - previousActivityEnd;
			previousActivity.setDiscreet_timegap_min(timegap);
		}
	}

	/**
	 * creating the very first activity before first Movement
	 * => an undefined home activity from 0:00 to first startTime
	 */
	private void createStartingActivity(TapasTrip trip, Coordinate coord) {
		ActivityPojo startingActivity = new ActivityPojo(); 
		startingActivity.setActivity(-1);
		startingActivity.setActivity_start_min(0);
		startingActivity.setDiscreet_timegap_min((int)trip.getStartTime());
		startingActivity.setIs_home(true);
		startingActivity.setLoc_coord_x(coord.x);
		startingActivity.setLoc_coord_y(coord.y);
	}
	
	/**
	 * 
	 * @param trip
	 */
	private void manageHouseholdMembership(PersonPojo person) {
		if(currentHousehold == null){
			this.currentHousehold = new ArrayList<PersonPojo>();
			this.currentHouseholdIndex = 0;
		}else if(!person.getHh_id().equals(currentHousehold.get(currentHousehold.size()-1).getHh_id())){
			householdCreatedEvent(currentHousehold);
			this.currentHousehold = new ArrayList<PersonPojo>();
			this.currentHouseholdIndex++;
		}
		this.currentHousehold.add(currentPerson);
	}
	
	/**
	 * 
	 * creates a new Person if needed 
	 * - and fires the personCreatedEvent 
	 * - and calls the manageHouseholdMembership-Methode
	 * 
	 * returns true if the trip belongs to a filtered out person 
	 * 
	 * @param trip
	 */
	private boolean handlePersonCreation(TapasTrip trip) {
		if(this.lastFilteredOutPerson != null && this.lastFilteredOutPerson.equals(trip.getIdPers())){
			//person of this trip is filtered out
			return true;
		}
		
		if(currentPerson == null){
			//this ist the very first trip
			PersonPojo newPerson = initNewPerson(trip);
			if(this.personFilter.isFilteredOut(newPerson)){
				this.lastFilteredOutPerson = newPerson.getP_id();
				return true;
			}
			this.currentPerson = newPerson; 
			this.currentPersonIndex = 0;
			manageHouseholdMembership(this.currentPerson);
			
		}else if(!currentPerson.getP_id().equals(trip.getIdPers())){
			//this trip belongs to a new person
			PersonPojo newPerson = initNewPerson(trip);
			if(this.personFilter.isFilteredOut(newPerson)){
				this.lastFilteredOutPerson = newPerson.getP_id();
				return true;
			}
			personCreatedEvent(this.currentPerson);
			this.currentPerson = newPerson;
			this.currentPersonIndex++;
			manageHouseholdMembership(this.currentPerson);
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param trip
	 * @return
	 */
	private PersonPojo initNewPerson(TapasTrip trip) {
		//System.out.println("initNewPerson");
		//System.out.println("p_id = "+trip.getIdPers());
		//System.out.println("hh_id = "+trip.getIdHh());
		PersonPojo newPerson = dbPersonReader.loadPersonMetadata(trip.getIdPers(), trip.getIdHh());
		return newPerson;
	}


}
