package de.dlr.ivf.tapas.analyzer.geovis.background.shp;

import de.dlr.ivf.tapas.analyzer.geovis.resources.GeoVisRessourceLocator;

public class GridShapefiles extends GeoVisShapefile{

	public static GridShapefiles instanceOfTrianglesSmall(){
		return new GridShapefiles("triangles_small");
	}
	
	public static GridShapefiles instanceOfTrianglesMedium(){
		return new GridShapefiles("triangles_medium");
	}
	
	public static GridShapefiles instanceOfTrianglesLarge(){
		return new GridShapefiles("triangles_large");
	}
	
	public static GridShapefiles instanceOfSquaresSmall(){
		return new GridShapefiles("squares_small");
	}
	
	public static GridShapefiles instanceOfSquaresMedium(){
		return new GridShapefiles("squares_medium");
	}
	
	public static GridShapefiles instanceOfSquaresLarge(){
		return new GridShapefiles("squares_large");
	}
	
	public static GridShapefiles instanceOfHexagonsSmall(){
		return new GridShapefiles("hexagons_small");
	}
	
	public static GridShapefiles instanceOfHexagonsMedium(){
		return new GridShapefiles("hexagons_medium");
	}
	
	public static GridShapefiles instanceOfHexagonsLarge(){
		return new GridShapefiles("hexagons_large");
	}

	private GridShapefiles(String name) {
		super(name);
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
		return getShapefilePath();
	}
	
	@Override
	public String getPathToSimplified() {
		return getShapefilePath();
	}
	
	protected String getShapefilePath() {
		return GeoVisRessourceLocator.getPath() + "data_shp/grid/"+super.name+".shp";
	}
	
}
