package de.dlr.ivf.tapas.analyzer.geovis.background.shp;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.ServiceInfo;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.filter.text.cql2.CQL;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;

public class GeotoolsDemo {

	DataStore dataStore = null;
	
	public GeotoolsDemo(String shpFileURL) {
		try {
			this.load(shpFileURL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void load(String shpFileURL) throws Exception {
		File file = new File(shpFileURL);
		Map<String, Object> connectionParameters = new HashMap<String, Object>();
		connectionParameters.put("url", file.toURI().toURL());
		this.dataStore = DataStoreFinder.getDataStore(connectionParameters);
		System.out.println("dataStore = "+dataStore);
		
		ServiceInfo info = dataStore.getInfo();
	    
	    // Human readable name and description
	    String title = info.getTitle();
	    String text = info.getDescription();
	    System.out.println("title = "+title);
	    System.out.println("text = "+text);
	    
	    // keywords (dublin core keywords like a web page)
	    Set<String> keywords = info.getKeywords();
	    System.out.println("keywords = "+keywords);
	    
	    // formal metadata
	    URI publisher = info.getPublisher(); // authority publishing data
	    URI schema = info.getSchema(); // used for data conforming to a standard
	    URI source = info.getSource(); // location where information is published from
	    System.out.println("publisher = "+publisher);
	    System.out.println("schema = "+schema);
	    System.out.println("source = "+source);
	    
	    SimpleFeatureStore featureStore = (SimpleFeatureStore) dataStore.getFeatureSource("hexagons1");
	    
	    int count = featureStore.getCount(Query.ALL);
	    if(count == -1){
	        count = featureStore.getFeatures(Query.ALL).size();
	    }
	    System.out.println("count all = "+count);
	   
	    String typeName = featureStore.getSchema().getTypeName();
	    System.out.println("typeName = "+typeName);
	    Query query = new Query(typeName, CQL.toFilter("name = 'Mitte'"));
	    count = featureStore.getCount(query);
	    if(count == -1){
	        count = featureStore.getFeatures(query).size();
	    }
	    System.out.println("count name = "+count);
	    
	    List<Coordinate> points = new ArrayList<Coordinate>(Arrays.asList(
	    		new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
				new Coordinate(389938, 5815272),//
				new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
			    new Coordinate(389938, 5815272),
				new Coordinate(389938, 5815272)));
	    
	    long startTime = System.currentTimeMillis();

	    for(Coordinate point : points){
	    	query = new Query(typeName, CQL.toFilter("CONTAINS(the_geom, POINT("+(point.x+new Random().nextFloat())+" "+(point.y+new Random().nextFloat())+"))"));
	    	SimpleFeatureCollection cells = featureStore.getFeatures(query);
	    	cells.features().hasNext();
	    	SimpleFeature cell = cells.features().next();
	    	System.out.println("count point = "+cell.getID());//hexagons1.647
	    }

	    long endTime = System.currentTimeMillis();
	    System.out.println("That took " + (endTime - startTime) + " milliseconds");
	    //ohne 944, 1019, 1025, 1041, 976
	    //mit 
   }
   
	   
}
