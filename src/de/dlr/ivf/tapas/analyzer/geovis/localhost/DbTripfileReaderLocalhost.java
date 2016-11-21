package de.dlr.ivf.tapas.analyzer.geovis.localhost;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;
import de.unipotsdam.db.postgis.ConnPojo;
import de.unipotsdam.db.postgis.DbPostgresConnection;

public class DbTripfileReaderLocalhost {

	/**
	 * 
	 * @param step
	 * @param chunk
	 * @return
	 */
	public List<TapasTripPojo> loadTrips(Integer step, Integer chunk){
		
		List<TapasTripPojo> result = null;
		String query = null;
		
		if(chunk == null){
			chunk = 1000;
		}
		if(step == null){
			step = 0;
		}
		
		try {
			
			query =  " 		SELECT ";
			query += " 			t_id,";
			query += " 			p_id,";
			query += " 			hh_id,";
			query += " 			p_group,";
			query += " 			start_time_min,";
			query += " 			mode,";
			query += " 			distance,";
			query += " 			distance_real,";
			query += " 			activity,";
			query += " 			activity_start_min,";
			query += " 			activity_duration_min,";
			query += " 			is_home,";
			query += " 			taz_id_start,";
			query += " 			taz_id_end,";
			query += " 			loc_id_start,";
			query += " 			loc_id_end,";
			query += " 			taz_bbr_type_start,";
			query += " 			bbr_type_home,hh_cars,";
			query += " 			hh_income,";
			query += " 			hh_has_child,";
			query += " 			hh_persons,";
			query += " 			p_age,";
			query += " 			p_driver_license,";
			query += " 			p_abo,";
			query += " 			p_has_bike,";
			query += " 			p_sex,";
			query += " 			scheme_id,";
			query += " 			score_combined,";
			query += " 			score_finance,";
			query += " 			score_time,";
			query += " 			loc_coord_x_start,";
			query += " 			loc_coord_y_start,";
			query += " 			loc_coord_x_end,";
			query += " 			loc_coord_y_end";
			query += " 		FROM ";
			query += " 			whole_trips ";
			query += " 		WHERE t_id >= "+(chunk*step)+" AND t_id < "+(chunk*(step+1));
			//query += " 			  AND t_id <= 3 ";
			query += " 		ORDER BY t_id ";

			System.out.println(this.getClass().getCanonicalName()+".loadTrips() query = "+query);
			
			DbPostgresConnection conn = new DbPostgresConnection(getConnPojo());
			result = conn.select(query, conn.connect(), new TapasTripPojo());
			
			conn.close();
			
			return result;
			
		} catch (SQLException e) {
			System.out.println("Error in "+this.getClass().getCanonicalName()+".loadPersonMetadata() with query = "+query);
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param step
	 * @param chunk
	 * @return
	 */
	public List<TapasTripPojo> loadTripsOfPerson(Integer p_id){
		
		List<TapasTripPojo> result = null;
		String query = null;
		
		try {
			
			query =  " 		SELECT ";
			query += " 			t_id,";
			query += " 			p_id,";
			query += " 			hh_id,";
			query += " 			p_group,";
			query += " 			start_time_min,";
			query += " 			mode,";
			query += " 			distance,";
			query += " 			distance_real,";
			query += " 			activity,";
			query += " 			activity_start_min,";
			query += " 			activity_duration_min,";
			query += " 			is_home,";
			query += " 			taz_id_start,";
			query += " 			taz_id_end,";
			query += " 			loc_id_start,";
			query += " 			loc_id_end,";
			query += " 			taz_bbr_type_start,";
			query += " 			bbr_type_home,hh_cars,";
			query += " 			hh_income,";
			query += " 			hh_has_child,";
			query += " 			hh_persons,";
			query += " 			p_age,";
			query += " 			p_driver_license,";
			query += " 			p_abo,";
			query += " 			p_has_bike,";
			query += " 			p_sex,";
			query += " 			scheme_id,";
			query += " 			score_combined,";
			query += " 			score_finance,";
			query += " 			score_time,";
			query += " 			loc_coord_x_start,";
			query += " 			loc_coord_y_start,";
			query += " 			loc_coord_x_end,";
			query += " 			loc_coord_y_end";
			query += " 		FROM ";
			query += " 			whole_trips ";
			query += " 		WHERE p_id = "+p_id;
			query += " 		ORDER BY t_id ";

			System.out.println(this.getClass().getCanonicalName()+".loadTrips() query = "+query);
			
			DbPostgresConnection conn = new DbPostgresConnection(getConnPojo());
			result = conn.select(query, conn.connect(), new TapasTripPojo());
			
			conn.close();
			
			return result;
			
		} catch (SQLException e) {
			System.out.println("Error in "+this.getClass().getCanonicalName()+".loadPersonMetadata() with query = "+query);
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer countPersons(){
		Integer anz = null;
		String query = "";
		try {
			query  = " SELECT ";
			query += " 		count(distinct p_id) as anz";
			query += " FROM whole_trips ";

			System.out.println(this.getClass().getCanonicalName()+".countPersons() query = "+query);
			
			DbPostgresConnection conn = new DbPostgresConnection(getConnPojo());
			ResultSet rs = conn.connect().createStatement().executeQuery(query);
			rs.next();
			anz = new Double(rs.getDouble(1)).intValue();
			
			conn.close();
			
			
		} catch (SQLException e) {
			System.out.println("Error in "+this.getClass().getCanonicalName()+".countPersons() with query = "+query);
			e.printStackTrace();
		}
		return anz;
	}
	
	public Integer countTrips() {
		Integer anz = null;
		String query = "";
		try {
			query  = " SELECT ";
			query += " 		count(*) as anz";
			query += " FROM whole_trips ";

			System.out.println(this.getClass().getCanonicalName()+".countTrips() query = "+query);
			
			DbPostgresConnection conn = new DbPostgresConnection(getConnPojo());
			ResultSet rs = conn.connect().createStatement().executeQuery(query);
			rs.next();
			anz = new Double(rs.getDouble(1)).intValue();
			
			conn.close();
			
			
		} catch (SQLException e) {
			System.out.println("Error in "+this.getClass().getCanonicalName()+".countTrips() with query = "+query);
			e.printStackTrace();
		}
		return anz;
	}

	/**
	 * 
	 * @return
	 */
	private ConnPojo getConnPojo(){
		ConnPojo connPojo = new ConnPojo();
		connPojo.setDb("TAPAS");
		connPojo.setHost("localhost");
		connPojo.setPort("5432");
		connPojo.setUser("postgres");
		connPojo.setPass("pg");
		return connPojo;
	}

	public List<Integer> loadPersonIds() {
		List<Integer> personIds = new ArrayList<Integer>();
		String query = "";
		try {
			query  = " SELECT ";
			query += " 		distinct hh_id, p_id";
			query += " FROM whole_trips ";

			System.out.println(this.getClass().getCanonicalName()+".loadPersonIds() query = "+query);
			
			DbPostgresConnection conn = new DbPostgresConnection(getConnPojo());
			ResultSet rs = conn.connect().createStatement().executeQuery(query);
			while(rs.next()){
				personIds.add(new Integer(rs.getInt(2)));
			}
			
			conn.close();
			
			
		} catch (SQLException e) {
			System.out.println("Error in "+this.getClass().getCanonicalName()+".loadPersonIds() with query = "+query);
			e.printStackTrace();
		}
		
		return personIds;
	}

	/*
	public static void main(String[] args) {
		DbTripfileReaderLocalhost reader = new DbTripfileReaderLocalhost();
		System.out.println("countPersons: " + reader.countPersons());
		System.out.println("loadTrips (first 100): " + reader.loadTrips(0, 100).size());
	}*/
	
}
