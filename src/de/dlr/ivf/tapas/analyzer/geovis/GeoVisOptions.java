package de.dlr.ivf.tapas.analyzer.geovis;

import java.util.ArrayList;
import java.util.List;

public class GeoVisOptions {

	private List<VisType> selectedVisTypes = null;
	private List<BgType> selectedBgTypes = null;
	private String includeFilter = null;
	private String excludeFilter = null;
	
	public GeoVisOptions() {
		this.selectedVisTypes = new ArrayList<VisType>();
		this.selectedBgTypes = new ArrayList<BgType>();
	}

	public void selectVisType(VisType visType){
		if(!selectedVisTypes.contains(visType)){
			selectedVisTypes.add(visType);
		}
	}
	
	public void deselectVisType(VisType visType){
		if(selectedVisTypes.contains(visType)){
			selectedVisTypes.remove(visType);
		}
	}
	
	public void selectBgType(BgType bgType){
		if(!selectedBgTypes.contains(bgType)){
			selectedBgTypes.add(bgType);
		}
	}
	
	public void deselectBgType(BgType bgType){
		if(selectedBgTypes.contains(bgType)){
			selectedBgTypes.remove(bgType);
		}
	}

	public String getIncludeFilter() {
		return includeFilter;
	}

	public void setIncludeFilter(String includeFilter) {
		this.includeFilter = includeFilter;
	}

	public String getExcludeFilter() {
		return excludeFilter;
	}

	public void setExcludeFilter(String excludeFilter) {
		this.excludeFilter = excludeFilter;
	}

	public List<VisType> getSelectedVisTypes() {
		return selectedVisTypes;
	}
	
	public void setSelectedVisTypes(List<VisType> visTypes) {
		this.selectedVisTypes = visTypes;
	}
	
	public List<BgType> getSelectedBgTypes() {
		return selectedBgTypes;
	}
	
	public void setSelectedBgTypes(List<BgType> bgTypes) {
		this.selectedBgTypes = bgTypes;
	}


}
