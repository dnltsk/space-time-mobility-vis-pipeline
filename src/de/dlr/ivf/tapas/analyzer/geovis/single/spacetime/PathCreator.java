package de.dlr.ivf.tapas.analyzer.geovis.single.spacetime;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import de.dlr.ivf.tapas.analyzer.geovis.background.BackgroundLayerProxy;
import de.dlr.ivf.tapas.analyzer.geovis.background.shp.AdminShapefiles;
import de.dlr.ivf.tapas.analyzer.geovis.common.ActivityPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.MovementPojo;
import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;

public class PathCreator {

	PersonPojo person = null;
	Double zScale = null;
	
	/**
	 * 
	 * @param person
	 */
	public PathCreator(PersonPojo person) {
		//System.out.println("SpaceTimePath()");
		//System.out.println("person="+person.getP_id()+" As="+person.getActivities().size()+" Ms="+person.getMovements().size());
		this.person = person;
		this.zScale = calcZScale();
	}
	
	/**
	 * 
	 * @return
	 */
	public PathLayerPojo createSpaceTimePathLayer(){
		
		List<AbstractSegment> segments = new ArrayList<AbstractSegment>();
		AbstractSegment nextSegment = null;
		int i = 0;
		while((nextSegment = getNextSegment(segments)) != null){
			//System.out.println("nextSegment = from "+nextSegment.getFromMinute()+" to "+nextSegment.getToMinute());
			if(i>0){
				//correcting the gap of some Activities
				if(segments.get(i-1).getToMinute() < nextSegment.getFromMinute()){
					segments.get(i-1).setTo((Coordinate)nextSegment.getFrom().clone());
					segments.get(i-1).setToMinute(nextSegment.getFromMinute());
				}
			}
			segments.add(nextSegment);
			i++;
		}
		
		/* adding the plumbs */
		List<AbstractSegment> plumbs = getSpaceTimePlumbs(segments);
		segments.addAll(plumbs);
		
		return new PathLayerPojo(this.person, segments);
	}

	/**
	 * 
	 * @param segments
	 * @return
	 */
	private List<AbstractSegment> getSpaceTimePlumbs(List<AbstractSegment> segments) {
		List<AbstractSegment> plumbs = new ArrayList<AbstractSegment>();
		
		for (int i = 0; i < segments.size(); i++) {
			AbstractSegment segment = segments.get(i);
			
			Coordinate newGroundCoordinate = new Coordinate(segment.getFrom().x, segment.getFrom().y, 0);
			boolean dublicated = false;
			for(AbstractSegment recordedPlumb : plumbs){
				if(recordedPlumb.getTo().x == newGroundCoordinate.x
						&& recordedPlumb.getTo().y == newGroundCoordinate.y){
					dublicated=true;
					break;
				}
			}
			if(!dublicated){
				plumbs.add(new SegmentPlumb(-1,
											  segment.getFrom(), 
											  -1, 
											  newGroundCoordinate));
			}
			
			newGroundCoordinate = new Coordinate(segment.getTo().x, segment.getTo().y, 0);
			dublicated = false;
			for(AbstractSegment recordedPlumb : plumbs){
				if(recordedPlumb.getTo().x == newGroundCoordinate.x
						&& recordedPlumb.getTo().y == newGroundCoordinate.y){
					dublicated=true;
					break;
				}
			}
			if(!dublicated){
				plumbs.add(new SegmentPlumb(-1,
											  segment.getTo(), 
											  -1, 
											  newGroundCoordinate));
			}
		}
		return plumbs;
	}
	
	/**
	 * 
	 * @param segments
	 * @return
	 */
	private AbstractSegment getNextSegment(List<AbstractSegment> segments){
		
		List<ActivityPojo> activities = person.getActivities();
		List<MovementPojo> movements = person.getMovements();
		
		if(activities == null || activities.size() == 0
				|| movements == null || movements.size() == 0){
			return null;
		}
		
		
		if(segments == null || segments.isEmpty()){
			//erstes Segment festlegen
			ActivityPojo firstA = activities.get(0);
			MovementPojo firstM = movements.get(0);
			if(firstA.getActivity_start_min() < firstM.getStart_time_min()){
				return createSegment(firstA);
			}else{
				return createSegment(firstM);
			}
		}else{
			//Folgesegment finden
			AbstractSegment lastS = segments.get(segments.size()-1);
			for(ActivityPojo a : activities){
				//System.out.println("prüfe A "+lastS.getToMinute()+" = "+a.getActivity_start_min());
				if(lastS.getToMinute().equals(a.getActivity_start_min())){
					return createSegment(a);
				}
			}
			for(MovementPojo m : movements){
				//System.out.println("prüfe M "+lastS.getToMinute()+" = "+m.getStart_time_min());
				if(lastS.getToMinute().equals(m.getStart_time_min())){
					return createSegment(m);
				}
			}
			
			AbstractSegment nextSegmentWithGap = getNextSegmentWithGap(lastS);
			if(nextSegmentWithGap!=null){
				System.err.println("Warnung: "+(nextSegmentWithGap.getFromMinute()-lastS.getToMinute())+"min Lücke (hh_id="+person.getHh_id()+", p_id="+person.getP_id());
			}
			return nextSegmentWithGap;
		}
	}

	/**
	 * 
	 * @param lastS
	 * @return
	 */
	private AbstractSegment getNextSegmentWithGap(AbstractSegment lastS) {
		ActivityPojo nextA = findNextAAfterGap(lastS);
		MovementPojo nextM = findNextMAfterGap(lastS);
		if(nextA == null && nextM == null){
			return null;
		}else if(nextA != null && nextM == null){
			return createSegment(nextA);
		}else if(nextA == null && nextM != null){
			return createSegment(nextM);
		}else{
			if(nextA.getActivity_start_min() < nextM.getStart_time_min()){
				return createSegment(nextA);
			}else{
				return createSegment(nextM);
			}
		}
	}

	/**
	 * 
	 * @param lastS
	 * @return
	 */
	private MovementPojo findNextMAfterGap(AbstractSegment lastS) {
		for(MovementPojo m : this.person.getMovements()){
			if(lastS.getToMinute() < m.getStart_time_min()){
				return m;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param lastS
	 * @return
	 */
	private ActivityPojo findNextAAfterGap(AbstractSegment lastS) {
		for(ActivityPojo a : this.person.getActivities()){
			if(lastS.getToMinute() < a.getActivity_start_min()){
				return a;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param movement
	 * @return
	 */
	private AbstractSegment createSegment(MovementPojo movement) {
		try{
			Coordinate from = new Coordinate(movement.getLoc_coord_x_start(), movement.getLoc_coord_y_start(), minutesToZ(movement.getStart_time_min()));
			Coordinate to = new Coordinate(movement.getLoc_coord_x_end(), movement.getLoc_coord_y_end(), minutesToZ(movement.getActivity_start_min()));
			//System.out.println("Erstelle M-Segment von "+movement.getStart_time_min()+" bis "+movement.getActivity_start_min());
			return new SegmentMovement(movement.getStart_time_min(), from, 
										 movement.getActivity_start_min(), to,
										 movement);
		}catch(Exception e){
			System.err.println("ERROR: "+e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param activity
	 * @return
	 */
	private AbstractSegment createSegment(ActivityPojo activity) {
		Coordinate from = new Coordinate(activity.getLoc_coord_x(), 
				 					     activity.getLoc_coord_y(), 
				 					     minutesToZ(activity.getActivity_start_min()));
		Coordinate to = new Coordinate(activity.getLoc_coord_x(), 
								       activity.getLoc_coord_y(), 
								       minutesToZ(activity.getActivity_start_min() + activity.getActivity_duration_min() + activity.getDiscreet_timegap_min()));
		//System.out.println("Erstelle A-Segment von "+activity.getActivity_start_min()+" bis "+(activity.getActivity_start_min()+activity.getActivity_duration_min()+activity.getDiscreet_timegap_min()));
		return new SegmentActivity(activity.getActivity_start_min(), 
				 				   from, 
								   activity.getActivity_start_min() + activity.getActivity_duration_min() + activity.getDiscreet_timegap_min(), 
								   to,
								   activity);
	}
	
	/**
	 * 
	 * @param minutes
	 * @return
	 */
	private Double minutesToZ(Integer minutes){
		return minutes * this.zScale * .75;//zusätzlich um xx% stauchen!
	}
	
	/**
	 * 
	 * @param bbox
	 * @return
	 */
	private Double calcZScale() {
		Envelope bbox = BackgroundLayerProxy.getShapefile(AdminShapefiles.instanceOfKontur()).getBbox();
		Integer minutesPerDay = 60*24;
		return  Math.sqrt(bbox.getArea()) / minutesPerDay;
	}
	
}
