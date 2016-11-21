package de.dlr.ivf.tapas.analyzer.geovis.common.codes;

import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.inputfileconverter.Activity;
import de.dlr.ivf.tapas.analyzer.inputfileconverter.TripIntention;

public enum LocationType{

	HOME,
	EDUCATION,
	JOB,
	SETTLEMENT,
	SHOPPING,
	LEISURE,
	OTHER;
	
	/**
	 * 
	 * @param activity
	 * @return
	 */
	public static LocationType getLocationType(ActivityPojo activity){
		if(activity.getIs_home()){
			return HOME;
		}
		Activity tapasActivity = Activity.getById(activity.getActivity());
		TripIntention tripIntention = TripIntention.getByActivity(tapasActivity);
		switch(tripIntention){
			case TRIP_31:
			case TRIP_38:
				return EDUCATION;
			case TRIP_32:
				return JOB;
			case TRIP_33:
				return SETTLEMENT;
			case TRIP_34:
				return SHOPPING;
			case TRIP_35:
				return LEISURE;
			case TRIP_36:
			case TRIP_MISC:
				return OTHER;
			case TRIP_37:
				return HOME;
		}
		return OTHER;
	}
	
}
