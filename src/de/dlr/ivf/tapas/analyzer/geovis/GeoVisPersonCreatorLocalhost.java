package de.dlr.ivf.tapas.analyzer.geovis;

import java.util.ArrayList;
import java.util.List;

import de.dlr.ivf.tapas.analyzer.geovis.aggregated.IterationProcessorForAggregated;
import de.dlr.ivf.tapas.analyzer.geovis.all.IterationProcessorForAll;
import de.dlr.ivf.tapas.analyzer.geovis.all.QuickChain;
import de.dlr.ivf.tapas.analyzer.geovis.background.IterationProcessorForBackrounds;
import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.MovementPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.codes.LocationType;
import de.dlr.ivf.tapas.analyzer.geovis.localhost.TapasTripPojo;
import de.dlr.ivf.tapas.analyzer.geovis.single.IterationProcessorForSingle;

public class GeoVisPersonCreatorLocalhost {

	private List<IPersonCreationVisitor> creationVisitors = null;
	private PersonPojo currentPerson = null;
	private int currentPersonIndex = -1;
	private Integer lastFilteredOutPerson = null;
	private int currentHouseholdIndex = -1;
	
	private List<PersonPojo> currentHousehold = null;
	private String outputPath = null;
	
	public GeoVisPersonCreatorLocalhost(String outputPath, GeoVisOptions options, Integer personCount) {
		this.outputPath = outputPath;
		this.creationVisitors = createCreationVisitors(options, personCount);
	}
	
	public QuickChain getQuickChain(){
		for(IPersonCreationVisitor visitor : creationVisitors){
			if(visitor instanceof QuickChain){
				return (QuickChain)visitor;
			}
		}
		return null;
	}
	
	/**
	 * will be executed when the GeoVisProcessor gets a new TapasTrip
	 * @param trip
	 */
	public void addTrip(TapasTripPojo trip){
		if(trip.getP_id().equals(1003053)){
			System.out.println("ACHTUNG!!!!!!");
		}
		
		handlePersonCreation(trip);
		handleMovement(trip);
		handleActivity(trip);
		synchronizePreviousActivity();
	}
	
	/**
	 * deligates the created person to the creationVisitors
	 * @param person
	 */
	private void personCreatedEvent(PersonPojo person){
		boolean valid = validatePerson(person);
		if(!valid){
			//schwerer Fehler -> unsichtbar
			person.setActivities(new ArrayList<ActivityPojo>());
			person.setMovements(new ArrayList<MovementPojo>());
		}
		if(person.getP_id().equals(1008569)){
			System.out.println("ACHTUNG!!!");
		}
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
		if(household.size() == 0){
			//Haushalt enthält keine validen Personen
			return;
		}
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
			System.out.println("GeoVis: finalizing outputs "+(this.creationVisitors.indexOf(creationVisitor)+1)+"/"+this.creationVisitors.size()+"..\n");
			creationVisitor.afterProcessing();
		}
		System.out.println("GeoVis: outputs finalized.\n");
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
			//creationVisitors.add(new IterationProcessorForAll(this.outputPath, personCount));
			creationVisitors.add(new QuickChain(this.outputPath));
		}
		return creationVisitors;
	}
	
	/**
	 * 
	 * @param trip
	 */
	private void handleActivity(TapasTripPojo trip) {
		if(isVeryFirstMovement()){
			ActivityPojo startingActivity = createStartingActivity(trip);
			currentPerson.addActivity(startingActivity);
		}
		ActivityPojo activity = createCurrentActivity(trip);
		currentPerson.addActivity(activity);//add to activity chain
	}

	
	/**
	 * 
	 * @param trip
	 * @return
	 */
	private ActivityPojo createCurrentActivity(TapasTripPojo trip) {
		ActivityPojo activity = new ActivityPojo();
		
		activity.setActivity(trip.getActivity());
		activity.setIs_home(trip.getIs_home());
		activity.setLoc_coord_x(trip.getLoc_coord_x_end());
		activity.setLoc_coord_y(trip.getLoc_coord_y_end());
		
		activity.setActivity_start_min(trip.getActivity_start_min());
		activity.setActivity_duration_min(trip.getActivity_duration_min().intValue());
		return activity;
	}

	/**
	 * 
	 * @param trip
	 */
	private void handleMovement(TapasTripPojo trip) {
		MovementPojo movement = createMovement(trip);
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
	private MovementPojo createMovement(TapasTripPojo trip) {
		MovementPojo movement = new MovementPojo();
		movement.setMode(trip.getMode());
		movement.setActivity_start_min(trip.getActivity_start_min());
		movement.setStart_time_min((int)trip.getStart_time_min());
		//movement.setTravel_time_sec((float)trip.getTT());
		movement.setTravel_time_sec((float)(movement.getActivity_start_min() - movement.getStart_time_min()) * 60);
		movement.setDistance_real_m(trip.getDistance_real().floatValue());
		movement.setLoc_coord_x_start(trip.getLoc_coord_x_start());
		movement.setLoc_coord_y_start(trip.getLoc_coord_y_start());
		movement.setLoc_coord_x_end(trip.getLoc_coord_x_end());
		movement.setLoc_coord_y_end(trip.getLoc_coord_y_end());
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
	private ActivityPojo createStartingActivity(TapasTripPojo trip) {
		ActivityPojo startingActivity = new ActivityPojo(); 
		startingActivity.setActivity(-1);
		startingActivity.setActivity_start_min(0);
		startingActivity.setActivity_duration_min(0);
		startingActivity.setDiscreet_timegap_min((int)trip.getStart_time_min());
		startingActivity.setIs_home(true);
		startingActivity.setLoc_coord_x(trip.getLoc_coord_x_start());
		startingActivity.setLoc_coord_y(trip.getLoc_coord_y_start());
		return startingActivity;
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
	private boolean handlePersonCreation(TapasTripPojo trip) {
		if(this.lastFilteredOutPerson != null && this.lastFilteredOutPerson.equals(trip.getP_id())){
			//person of this trip is filtered out
			return true;
		}
		
		if(currentPerson == null){
			//this ist the very first trip
			PersonPojo newPerson = initNewPerson(trip);
			this.currentPerson = newPerson; 
			this.currentPersonIndex = 0;
			manageHouseholdMembership(this.currentPerson);
			
		}else if(!currentPerson.getP_id().equals(trip.getP_id())){
			//this trip belongs to a new person
			PersonPojo newPerson = initNewPerson(trip);
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
	 */
	private void manageHouseholdMembership(PersonPojo person) {
		if(currentHousehold == null || currentHousehold.size() == 0){
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
	 * @param trip
	 * @return
	 */
	private PersonPojo initNewPerson(TapasTripPojo trip) {
		//System.out.println("initNewPerson");
		//System.out.println("p_id = "+trip.getIdPers());
		//System.out.println("hh_id = "+trip.getIdHh());
		PersonPojo newPerson = new PersonPojo();
		newPerson.setHh_cars(trip.getHh_cars());
		newPerson.setHh_has_child(trip.getHh_has_child());
		newPerson.setHh_id(trip.getHh_id());
		newPerson.setHh_income(trip.getHh_income());
		newPerson.setHh_persons(trip.getHh_persons());
		newPerson.setP_abo((trip.getP_abo()==1)?true:false);
		newPerson.setP_age(trip.getP_age());
		newPerson.setP_driver_license((trip.getP_driver_license()==1)?true:false);
		newPerson.setP_group(trip.getP_group());
		newPerson.setP_has_bike(trip.getP_has_bike());
		newPerson.setP_id(trip.getP_id());
		newPerson.setP_sex(trip.getP_sex());
		newPerson.setScheme_id(trip.getScheme_id());
		newPerson.setScore_combined(trip.getScore_combined().floatValue());
		newPerson.setScore_finance(trip.getScore_finance().floatValue());
		newPerson.setScore_time(trip.getScore_time().floatValue());
		return newPerson;
	}

	private boolean validatePerson(PersonPojo person) {
		ActivityPojo firstActivity = person.getActivities().get(0);
		ActivityPojo lastActivity = person.getActivities().get(person.getActivities().size()-1);
		//Muss in HOME beginnen
		if(!LocationType.getLocationType(firstActivity).equals(LocationType.HOME)){
			System.err.println(this.getClass().getCanonicalName()+"validatePerson(): Beginnt nicht im HOME! -> "+person.getP_id());
			return false;
		}
		//Muss in HOME enden
		if(!LocationType.getLocationType(lastActivity).equals(LocationType.HOME)){
			System.err.println(this.getClass().getCanonicalName()+"validatePerson(): Endet nicht im HOME! -> "+person.getP_id());
			return false;
		}
		//keine Aktivität beginnen nach 5 vor 1440min!
		if(lastActivity.getActivity_start_min() > (1440 - 5)){
			System.err.println(this.getClass().getCanonicalName()+"validatePerson(): letzte Aktivität startet nach 24:00! -> "+person.getP_id());
			return false;
		}
		//HOME-HOME-Folgen untersagt!
		for(int i=1; i>person.getActivities().size(); i++){
			LocationType sourceLocationType = LocationType.getLocationType(person.getActivities().get(i-1));
			LocationType destLocationType = LocationType.getLocationType(person.getActivities().get(i));
			if(sourceLocationType.equals(LocationType.HOME) && destLocationType.equals(LocationType.HOME)){
				System.err.println(this.getClass().getCanonicalName()+"validatePerson(): HOME-HOME-Folge verboten! -> "+person.getP_id());
				return false;
			}
		}
		//letzte HOME endet um 1440 --> ggf. Anpassen!
		if(lastActivity.getActivity_start_min() + lastActivity.getActivity_duration_min() > 1440){
			lastActivity.setActivity_duration_min(1440 - lastActivity.getActivity_start_min());
			lastActivity.setDiscreet_timegap_min(0);
		}
		if(lastActivity.getActivity_start_min() + lastActivity.getActivity_duration_min() + lastActivity.getDiscreet_timegap_min() > 1440){
			lastActivity.setDiscreet_timegap_min(1440 - (lastActivity.getActivity_start_min() + lastActivity.getActivity_duration_min()));
		}
		return true;
	}
	
}
