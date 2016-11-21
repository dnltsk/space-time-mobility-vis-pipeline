package de.dlr.ivf.tapas.analyzer.geovis.background.shp;

import de.dlr.ivf.tapas.analyzer.geovis.resources.GeoVisRessourceLocator;

public class AdminShapefiles extends GeoVisShapefile{

	public static AdminShapefiles instanceOfKontur(){
		return new AdminShapefiles("kontur", true);
	}
	
	public static AdminShapefiles instanceOfBezirke(){
		return new AdminShapefiles("bezirke");
	}
	
	public static AdminShapefiles instanceOfOrtsteile(){
		return new AdminShapefiles("ortsteile");
	}
	
	public static AdminShapefiles instanceOfTvz879(){
		return new AdminShapefiles("TVZ_879");
	}
	
	public static AdminShapefiles instanceOfBezirkeThumb(){
		return new AdminShapefiles("bezirke_thumb", true);
	}
	
	private boolean simplifiedOnly = false;
	
	private AdminShapefiles(String name) {
		super(name);
	}
	
	private AdminShapefiles(String name, Boolean simplifiedOnly) {
		super(name);
		this.simplifiedOnly = simplifiedOnly;
	}
	
	@Override
	public Object getId() {
		return super.name;
	}
	
	@Override
	public String getFilename() {
		return super.name;
	}
	
	@Override
	public String getPathToOriginal() {
		if(this.simplifiedOnly){
			return getShapefilePath(true);
		}
		return getShapefilePath(false);
	}
	
	@Override
	public String getPathToSimplified() {
		return getShapefilePath(true);
	}
	
	/**
	 * 
	 * @param simplified
	 * @return
	 */
	public String getShapefilePath(boolean simplified) {
		
		String filename = GeoVisRessourceLocator.getPath() + "data_shp/";
		filename += "admin/";
		if(simplified){
			filename += "simplified/";
		}else{
			filename += "orig/"; 
		}
		filename += super.name;
		filename += ".shp";
		return filename;
	}

}
