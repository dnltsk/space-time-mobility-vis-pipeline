package de.dlr.ivf.tapas.analyzer.geovis.common.io;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.dlr.ivf.tapas.analyzer.geovis.common.SimulationDetailsPojo;
import de.dlr.ivf.tapas.persistence.db.TPS_DB_Connector;

public class DbSimulationDetails {

	private final TPS_DB_Connector dbCon;
	
	public DbSimulationDetails() throws ClassNotFoundException, IOException{
		try {
			this.dbCon = TPS_DB_Connector.login();
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}
	
	public SimulationDetailsPojo loadSimulationDetails(String simulation){

		System.out.println("load simulation details...");
		
		SimulationDetailsPojo simulationDetails = new SimulationDetailsPojo();
		String query = null;
		
		try {
			query  = " SELECT ";
			query += " 		sim_par[1] as region,";
			query += "		sim_par[2] as hhkey ";
			query += " FROM ";
			query += " 		simulations ";
			query += " WHERE ";
			query += " 		sim_key = '"+simulation+"'";

			ResultSet rs = dbCon.executeQuery(query, this);
			
			while (rs.next()) {
				simulationDetails.setRegion(rs.getString("region"));
				simulationDetails.setHhkey(rs.getString("hhkey"));
				simulationDetails.setSchema("core");
				simulationDetails.setSimulation(simulation);
				System.out.println("load simulation details = "+simulationDetails.getRegion()+", "+simulationDetails.getHhkey());
			}
			
		} catch (SQLException e) {
			System.out.println("SQLException in "+this.getClass().getCanonicalName()+".loadSimulationDetails() with query = "+query);
			e.printStackTrace();
		}
		
		return simulationDetails;
		
	}
	
}
