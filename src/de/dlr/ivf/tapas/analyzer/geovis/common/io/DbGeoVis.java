package de.dlr.ivf.tapas.analyzer.geovis.common.io;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.vividsolutions.jts.geom.Coordinate;

import de.dlr.ivf.tapas.analyzer.geovis.common.SimulationDetailsPojo;
import de.dlr.ivf.tapas.persistence.db.TPS_DB_Connector;

public class DbGeoVis {

	private final TPS_DB_Connector dbCon;
	private final SimulationDetailsPojo simulationDetails;
	
	/**
	 * 
	 * @param simulation
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public DbGeoVis(SimulationDetailsPojo simulationDetails) throws IOException, ClassNotFoundException {
		this.simulationDetails = simulationDetails;
		try {
			this.dbCon = TPS_DB_Connector.login();
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * loading coordinates of all households and locations
	 * 
	 * (subsetting the loc_ids and hh_id of simulation by WITH-clause brings performance loss)  
	 * 
	 * @return
	 */
	public HashMap<Integer, Coordinate> loadLocations(){
		
		HashMap<Integer, Coordinate> locations = new HashMap<Integer, Coordinate>();
		
		String query = null;
		int chunk = 100000;
		int step = 0;

		boolean found = true;
		
		try {
			while(found){
				found = false;
				
				System.out.println("load locations "+(chunk*step)+" - "+((chunk*(step+1))));
				
				query  = " WITH loc_ids AS( ";
				query += " 		SELECT DISTINCT ";
				query += " 			loc_id_end as id ";
				query += " 		FROM ";
				query += " 			public."+simulationDetails.getRegion()+"_trips_"+simulationDetails.getSimulation()+" ";
				query += " ) ";
				query += " SELECT ";
				query += " 		loc_id AS id, "; 
				query += " 		X(TRANSFORM(loc_coordinate, 25833)) AS x, "; 
				query += " 		Y(TRANSFORM(loc_coordinate, 25833)) AS y ";
				query += " FROM ";
				query += " 		"+simulationDetails.getSchema()+"."+simulationDetails.getRegion()+"_locations, ";
				query += " 		loc_ids ";
				query += " WHERE ";
				query += " 		loc_id in (loc_ids.id) ";
				query += " UNION ";
				query += " SELECT ";
				query += " 		-hh_id as id, "; 
				query += " 		X(TRANSFORM(hh_coordinate, 25833)) AS x, "; 
				query += " 		Y(TRANSFORM(hh_coordinate, 25833)) AS Y ";
				query += " FROM ";
				query += " 		"+simulationDetails.getSchema()+"."+simulationDetails.getRegion()+"_households, ";
				query += " 		loc_ids ";
				query += " WHERE ";
				query += " 		hh_key='"+simulationDetails.getHhkey()+"' ";
				query += " 		AND -hh_id in (loc_ids.id) ";
				query += " ORDER BY id ";
				query += " LIMIT "+chunk;
				query += " OFFSET "+(step*chunk);
				
				Connection conn = dbCon.getConnection(this);
				Statement stmt = conn.createStatement();
				ResultSet resultSet = stmt.executeQuery(query);
				
				while (resultSet.next()) {
					found = true;
					Integer id = resultSet.getInt("id");
					Coordinate location = new Coordinate(resultSet.getDouble("x"), 
														 resultSet.getDouble("y"), 
														 0);
					locations.put(id, location);
				}
				
				resultSet.close();
				stmt.close();
				conn.close();
				
				step++;
			}
		} catch (SQLException e) {
			System.out.println("Error in "+this.getClass().getCanonicalName()+".loadLocations() with query = "+query);
			e.printStackTrace();
		}
		
		return locations;
	}
	
	/**
	 * counting all persons in tripfile
	 * @param tripfilePath
	 * @return
	 */
	public Integer countPersons(){

		String query = null;
		Integer count = null;
		
		try {
				query  = " SELECT ";
				query += " 		COUNT(*) AS count ";
				query += " FROM ( ";
				query += " 	  	SELECT ";
				query += " 			DISTINCT p_id,  ";
				query += " 			hh_id ";
				query += " 		FROM ";
				query += " 			public."+simulationDetails.getRegion()+"_trips_"+simulationDetails.getSimulation();
				query += " ) AS sub ";
				
				ResultSet resultSet = dbCon.executeQuery(query, this);
				while (resultSet.next()) {
					count = resultSet.getInt("count");
				}
				resultSet.close();

		} catch (SQLException e) {
			System.out.println("Error in "+this.getClass().getCanonicalName()+".countPersons() with query = "+query);
			e.printStackTrace();
		}
		
		return count;
	}
	
}
