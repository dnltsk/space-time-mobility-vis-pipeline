package de.dlr.ivf.tapas.analyzer.geovis.all.diagrams;

import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;

public class LocationDiagram extends AbstractDiagram{

	public LocationDiagram() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void addDiagramData(PersonPojo person) {
		// TODO Auto-generated method stub
		/*JSONArray jsonChainLink = new JSONArray();
		for(ActivityPojo activity : person.getActivities()){
			for(int frame=0; frame <= (24 * 60); frame += FRAME_INTERVAL){
				JSONObject jsonActivity = new JSONObject();
				jsonActivity.put("activity_type", activity.getActivity());
				Coordinate c = new Coordinate(activity.getLoc_coord_x(), activity.getLoc_coord_y());
				PerspectiveManager.createParallelPerspective(Arrays.asList(c), 0, 90);
				JSONArray jsonCoordinate = new JSONArray();
				//jsonCoordinate.add((int)c.x + jitter());
				//jsonCoordinate.add((int)c.y + jitter());
				jsonActivity.put("coordinate", jsonCoordinate);
				jsonActivity.put("from", activity.getActivity_start_min());
				jsonActivity.put("duration", activity.getActivity_start_min() - activity.getActivity_duration_min() - activity.getDiscreet_timegap_min());
				jsonActivity.put("to", activity.getActivity_duration_min() + activity.getDiscreet_timegap_min());
				jsonChainLink.add(jsonActivity);
			}
		}*/
	}
	
}
