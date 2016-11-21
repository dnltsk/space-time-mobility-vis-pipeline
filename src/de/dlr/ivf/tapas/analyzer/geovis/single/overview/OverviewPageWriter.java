package de.dlr.ivf.tapas.analyzer.geovis.single.overview;

import java.util.Collections; 
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.MovementPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.codes.LocationType;
import de.dlr.ivf.tapas.analyzer.geovis.common.io.SimpleDataWriter;

public class OverviewPageWriter extends SimpleDataWriter{

	private String outputPath = null;
	private HashMap<Integer, Integer> hhIdPageLinks = null;
	
	/**
	 * 
	 * @param household
	 * @param pageNumber
	 */
	public OverviewPageWriter(String outputPath) {
		super(outputPath + "/GeoVis/web/json/pathlayers/overview/");
		this.outputPath = outputPath;
		hhIdPageLinks = new HashMap<Integer, Integer>();
	}

	/**
	 * 
	 * @param households
	 * @param pageNumber
	 */
	@SuppressWarnings("unchecked")
	public void writeOverviewPage(List<List<PersonPojo>> households, int pageNumber) {
		
		JSONArray jsonOverviewPage = new JSONArray();
		for(List<PersonPojo> household : households){
			hhIdPageLinks.put(household.get(0).getHh_id(), pageNumber);//register
			sortHouseholdMembers(household);
			JSONObject jsonHousehold = new JSONObject();
			addHaushaltOverview(jsonHousehold, household.get(0));
			JSONArray jsonHouseholdMembers = new JSONArray();
			for(PersonPojo person : household){
				JSONObject jsonPerson = new JSONObject();
				addPersonOverview(jsonPerson, person);
				addPersonRoutes(jsonPerson, person);
				jsonHouseholdMembers.add(jsonPerson);
			}
			jsonHousehold.put("members", jsonHouseholdMembers);
			jsonOverviewPage.add(jsonHousehold);
		}
		
		char[] letters = new Integer(pageNumber).toString().toCharArray();
		String path = "";
		for(int i=0; i<letters.length-2; i++){
			path+=letters[i]+"/";
		}
		
		writeJsonFile(path+"page_"+pageNumber+".json", jsonOverviewPage);
	}
	
	public void writeSearchTree(){
		new SearchTreeWriter(this.outputPath + "/GeoVis/web/json/pathlayers/overview/", hhIdPageLinks);
	}

	/**
	 * 
	 * @param jsonHousehold
	 * @param person
	 */
	@SuppressWarnings("unchecked")
	private void addHaushaltOverview(JSONObject jsonHousehold, PersonPojo person) {
		jsonHousehold.put("hh_id", person.getHh_id());
		jsonHousehold.put("hh_members", person.getHh_persons());
		jsonHousehold.put("hh_cars", person.getHh_cars());
		jsonHousehold.put("hh_has_child", person.getHh_has_child());
		jsonHousehold.put("hh_income", person.getHh_income());
	}

	/**
	 * 
	 * @param jsonPerson
	 * @param person
	 */
	@SuppressWarnings("unchecked")
	private void addPersonOverview(JSONObject jsonPerson, PersonPojo person) {
		jsonPerson.put("p_id", person.getP_id());
		jsonPerson.put("p_group", person.getP_group());
		jsonPerson.put("p_age", person.getP_age());
		jsonPerson.put("p_sex", person.getP_sex());
		jsonPerson.put("p_abo", person.getP_abo());
		jsonPerson.put("scheme_id", person.getScheme_id());
	}
	
	/**
	 * 
	 * @param jsonPerson
	 * @param person
	 */
	@SuppressWarnings("unchecked")
	private void addPersonRoutes(JSONObject jsonPerson, PersonPojo person){
		JSONArray jsonActivities = new JSONArray();
		JSONArray jsonPossibilities = new JSONArray();
		for(ActivityPojo activity : person.getActivities()){
			jsonActivities.add(activity.getActivity());
			jsonPossibilities.add(LocationType.getLocationType(activity).toString());
		}
		jsonPerson.put("activities", jsonActivities);
		jsonPerson.put("possibilities", jsonPossibilities);
		
		Float totalDistance = 0.0f;
		for(MovementPojo movement : person.getMovements()){
			totalDistance += movement.getDistance_real_m();
		}
		jsonPerson.put("total_distance", totalDistance);
	}

	/**
	 * 
	 * @param household
	 */
	private void sortHouseholdMembers(List<PersonPojo> household){
		Comparator<PersonPojo> householdComparator = new Comparator<PersonPojo>() {
			@Override
			public int compare(PersonPojo person1, PersonPojo person2) {
				return person2.getP_age() - person1.getP_age();
			}
		};
		Collections.sort(household, householdComparator);
	}
	
}
