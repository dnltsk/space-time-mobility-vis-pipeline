package de.dlr.ivf.tapas.analyzer.geovis.common;

import java.util.ArrayList;
import java.util.List;

public class PersonPojo {

	private Integer p_id = null;
	private Integer p_group = null;
	private Integer p_age = null;
	private Integer p_sex = null;
	private Boolean p_driver_license  = null;
	private Boolean p_abo  = null;
	private Boolean p_has_bike  = null;
	
	private Integer hh_id = null;
	private Integer hh_persons = null;
	private Boolean hh_has_child  = null;
	private Integer hh_cars = null;
	private Integer hh_income = null;
	
	private Integer scheme_id = null;
	private Float score_combined = null;
	private Float score_finance = null;
	private Float score_time = null;
	
	private List<ActivityPojo> activities = null;
	private List<MovementPojo> movements = null;
	
	public PersonPojo() {
		this.activities = new ArrayList<ActivityPojo>();
		this.movements = new ArrayList<MovementPojo>();
	}
	
	public PersonPojo clone(){
		PersonPojo c = new PersonPojo();
		c.setP_id(new Integer(this.p_id));
		c.setP_group(new Integer(this.p_group));
		c.setP_age(new Integer(this.p_age));
		c.setP_sex(new Integer(this.p_sex));
		c.setP_driver_license(new Boolean(this.p_driver_license));
		c.setP_abo(new Boolean(this.p_abo));
		//c.setP_has_bike(new Boolean(this.p_has_bike));
		
		c.setHh_id(new Integer(this.hh_id));
		c.setHh_persons(new Integer(this.hh_persons));
		c.setHh_has_child(new Boolean(this.hh_has_child));
		c.setHh_cars(new Integer(this.hh_cars));
		c.setHh_income(new Integer(this.hh_income));
		c.setScheme_id(new Integer(this.scheme_id));
		c.setScore_combined(new Float(this.score_combined));
		c.setScore_finance(new Float(this.score_finance));
		c.setScore_time(new Float(this.score_time));
		
		List<ActivityPojo> cAs = new ArrayList<ActivityPojo>();
		for(ActivityPojo a : this.activities){
			cAs.add(a.clone());
		}
		c.setActivities(cAs);
		
		List<MovementPojo> cMs = new ArrayList<MovementPojo>();
		for(MovementPojo m : this.movements){
			cMs.add(m.clone());
		}
		c.setMovements(cMs);
		
		return c;
	}
	
	public Integer getP_id() {
		return p_id;
	}

	public void setP_id(Integer p_id) {
		this.p_id = p_id;
	}

	public Integer getP_group() {
		return p_group;
	}

	public void setP_group(Integer p_group) {
		this.p_group = p_group;
	}

	public Integer getP_age() {
		return p_age;
	}

	public void setP_age(Integer p_age) {
		this.p_age = p_age;
	}

	public Integer getP_sex() {
		return p_sex;
	}

	public void setP_sex(Integer p_sex) {
		this.p_sex = p_sex;
	}

	public Boolean getP_abo() {
		return p_abo;
	}

	public void setP_abo(Boolean p_abo) {
		this.p_abo = p_abo;
	}
	
	public Boolean getP_has_bike() {
		return p_has_bike;
	}

	public void setP_has_bike(Boolean p_has_bike) {
		this.p_has_bike = p_has_bike;
	}

	public Boolean getP_driver_license() {
		return p_driver_license;
	}

	public void setP_driver_license(Boolean p_driver_license) {
		this.p_driver_license = p_driver_license;
	}

	public Integer getHh_id() {
		return hh_id;
	}

	public void setHh_id(Integer hh_id) {
		this.hh_id = hh_id;
	}

	public Integer getHh_persons() {
		return hh_persons;
	}

	public void setHh_persons(Integer hh_persons) {
		this.hh_persons = hh_persons;
	}

	public Boolean getHh_has_child() {
		return hh_has_child;
	}

	public void setHh_has_child(Boolean hh_has_child) {
		this.hh_has_child = hh_has_child;
	}

	public Integer getHh_cars() {
		return hh_cars;
	}

	public void setHh_cars(Integer hh_cars) {
		this.hh_cars = hh_cars;
	}

	public Integer getHh_income() {
		return hh_income;
	}

	public void setHh_income(Integer hh_income) {
		this.hh_income = hh_income;
	}

	public Integer getScheme_id() {
		return scheme_id;
	}

	public void setScheme_id(Integer scheme_id) {
		this.scheme_id = scheme_id;
	}

	public Float getScore_combined() {
		return score_combined;
	}

	public void setScore_combined(Float score_combined) {
		this.score_combined = score_combined;
	}

	public Float getScore_finance() {
		return score_finance;
	}

	public void setScore_finance(Float score_finance) {
		this.score_finance = score_finance;
	}

	public Float getScore_time() {
		return score_time;
	}

	public void setScore_time(Float score_time) {
		this.score_time = score_time;
	}

	public List<ActivityPojo> getActivities() {
		return activities;
	}
	
	public void addActivity(ActivityPojo activity){
		if(this.activities == null){
			this.activities = new ArrayList<ActivityPojo>();
		}
		this.activities.add(activity);
	}

	public void setActivities(List<ActivityPojo> activities) {
		this.activities = activities;
	}

	public List<MovementPojo> getMovements() {
		return movements;
	}
	
	public void addMovement(MovementPojo movement){
		if(this.movements == null){
			this.movements = new ArrayList<MovementPojo>();
		}
		this.movements.add(movement);
	}

	public void setMovements(List<MovementPojo> movements) {
		this.movements = movements;
	}
	
}
