package de.dlr.ivf.tapas.analyzer.geovis;

import java.util.List;

import de.dlr.ivf.tapas.analyzer.geovis.common.PersonPojo;

public interface IPersonCreationVisitor {

	/**
	 * Methode wird beim Lesen einer einzelnen Person ausgef�hrt
	 * @param person
	 */
	public void onPersonCreation(PersonPojo person, int personIndex);
	
	/**
	 * Methode wird nach dem Lesen aller Personen ausgef�hrt
	 */
	public void onHouseholdCreation(List<PersonPojo> household, int householdIndex);
	
	/**
	 * Methode wird nach dem Lesen aller Personen ausgef�hrt
	 */
	public void beforeProcessing();
	
	/**
	 * Methode wird nach dem Lesen aller Personen ausgef�hrt
	 */
	public void afterProcessing();
	
}
