package de.dlr.ivf.tapas.analyzer.geovis;

import java.util.List;

import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;

public interface IPersonCreationVisitor {

	/**
	 * Methode wird beim Lesen einer einzelnen Person ausgeführt
	 * @param person
	 */
	public void onPersonCreation(PersonPojo person, int personIndex);
	
	/**
	 * Methode wird nach dem Lesen aller Personen ausgeführt
	 */
	public void onHouseholdCreation(List<PersonPojo> household, int householdIndex);
	
	/**
	 * Methode wird nach dem Lesen aller Personen ausgeführt
	 */
	public void beforeProcessing();
	
	/**
	 * Methode wird nach dem Lesen aller Personen ausgeführt
	 */
	public void afterProcessing();
	
}
