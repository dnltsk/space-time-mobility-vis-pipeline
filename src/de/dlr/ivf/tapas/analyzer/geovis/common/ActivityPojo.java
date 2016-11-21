package de.dlr.ivf.tapas.analyzer.geovis.common;


public class ActivityPojo{

	private Integer activity = null;
	private Boolean is_home = null;
	private Integer activity_start_min = null;
	private Integer activity_duration_min = null;
	private Double loc_coord_x = null;
	private Double loc_coord_y = null;
	
	/*
	 * Timegap between activity_duration and start_time_min of next movement 
	 */
	private Integer discreet_timegap_min = 0;
	
	public ActivityPojo() {
	}
	
	public ActivityPojo clone(){
		ActivityPojo c = new ActivityPojo();
		c.setActivity(new Integer(this.activity));
		c.setIs_home(new Boolean(this.is_home));
		c.setActivity_start_min(new Integer(this.activity_start_min));
		c.setActivity_duration_min(new Integer(this.activity_duration_min));
		c.setLoc_coord_x(new Double(this.loc_coord_x));
		c.setLoc_coord_y(new Double(this.loc_coord_y));
		c.setDiscreet_timegap_min(new Integer(this.discreet_timegap_min));
		return c;
	}
	
	public Integer getActivity() {
		return activity;
	}

	public void setActivity(Integer activity) {
		this.activity = activity;
	}

	public Boolean getIs_home() {
		return is_home;
	}

	public void setIs_home(Boolean is_home) {
		this.is_home = is_home;
	}

	public Integer getActivity_start_min() {
		return activity_start_min;
	}

	public void setActivity_start_min(Integer activity_start_min) {
		this.activity_start_min = activity_start_min;
	}

	public Integer getActivity_duration_min() {
		return activity_duration_min;
	}

	public void setActivity_duration_min(Integer activity_duration_min) {
		this.activity_duration_min = activity_duration_min;
	}

	public Double getLoc_coord_x() {
		return loc_coord_x;
	}

	public void setLoc_coord_x(Double loc_coord_x) {
		this.loc_coord_x = loc_coord_x;
	}

	public Double getLoc_coord_y() {
		return loc_coord_y;
	}

	public void setLoc_coord_y(Double loc_coord_y) {
		this.loc_coord_y = loc_coord_y;
	}

	public Integer getDiscreet_timegap_min() {
		return discreet_timegap_min;
	}

	public void setDiscreet_timegap_min(Integer discreet_timegap_min) {
		this.discreet_timegap_min = discreet_timegap_min;
	}

}
