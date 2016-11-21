package de.dlr.ivf.tapas.analyzer.geovis.common;

public class MovementPojo{

	private Double loc_coord_x_start = null;
	private Double loc_coord_y_start = null;
	private Double loc_coord_x_end = null;
	private Double loc_coord_y_end = null;
	private Integer start_time_min = null;
	private Float travel_time_sec = null;
	private Integer activity_start_min = null;
	private Integer mode = null;
	private Float distance_real_m = null;
	
	public MovementPojo() {
	}
	
	public MovementPojo clone(){
		MovementPojo c = new MovementPojo();
		c.setLoc_coord_x_start(new Double(this.loc_coord_x_start));
		c.setLoc_coord_y_start(new Double(this.loc_coord_y_start));
		c.setLoc_coord_x_end(new Double(this.loc_coord_x_end));
		c.setLoc_coord_y_end(new Double(this.loc_coord_y_end));
		c.setStart_time_min(new Integer(this.start_time_min));
		c.setTravel_time_sec(new Float(this.travel_time_sec));
		c.setActivity_start_min(new Integer(this.activity_start_min));
		c.setMode(new Integer(this.mode));
		c.setDistance_real_m(new Float(this.distance_real_m));
		return c;
	}

	public Double getLoc_coord_x_start() {
		return loc_coord_x_start;
	}

	public void setLoc_coord_x_start(Double loc_coord_x_start) {
		this.loc_coord_x_start = loc_coord_x_start;
	}

	public Double getLoc_coord_y_start() {
		return loc_coord_y_start;
	}

	public void setLoc_coord_y_start(Double loc_coord_y_start) {
		this.loc_coord_y_start = loc_coord_y_start;
	}

	public Double getLoc_coord_x_end() {
		return loc_coord_x_end;
	}

	public void setLoc_coord_x_end(Double loc_coord_x_end) {
		this.loc_coord_x_end = loc_coord_x_end;
	}

	public Double getLoc_coord_y_end() {
		return loc_coord_y_end;
	}

	public void setLoc_coord_y_end(Double loc_coord_y_end) {
		this.loc_coord_y_end = loc_coord_y_end;
	}

	public Integer getStart_time_min() {
		return start_time_min;
	}

	public void setStart_time_min(Integer start_time_min) {
		this.start_time_min = start_time_min;
	}

	public Float getTravel_time_sec() {
		return travel_time_sec;
	}

	public void setTravel_time_sec(Float travel_time_sec) {
		this.travel_time_sec = travel_time_sec;
	}
	
	public Integer getActivity_start_min() {
		return activity_start_min;
	}

	public void setActivity_start_min(Integer activity_start_min) {
		this.activity_start_min = activity_start_min;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public Float getDistance_real_m() {
		return distance_real_m;
	}

	public void setDistance_real_m(Float distance_real_m) {
		this.distance_real_m = distance_real_m;
	}
	
}
