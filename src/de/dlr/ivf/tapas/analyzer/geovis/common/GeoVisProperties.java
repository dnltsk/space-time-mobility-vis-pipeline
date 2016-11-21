package de.dlr.ivf.tapas.analyzer.geovis.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Properties;

import de.dlr.ivf.tapas.analyzer.geovis.resources.GeoVisRessourceLocator;

public class GeoVisProperties extends Properties{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Properties properties = null;
	
	public GeoVisProperties(String propertiesName) {
		try{
			this.properties = new Properties();
			FileInputStream fis = new FileInputStream(GeoVisRessourceLocator.getPath() + propertiesName + ".properties");
			BufferedInputStream bis = new BufferedInputStream(fis);
			this.properties.load(bis);
			bis.close();
			fis.close();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}
	
	@Override
	public String getProperty(String key, String defaultValue) {
		return this.properties.getProperty(key, defaultValue);
	}
	
}
