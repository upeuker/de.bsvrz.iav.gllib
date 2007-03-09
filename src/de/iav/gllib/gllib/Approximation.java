package de.iav.gllib.gllib;

import java.util.TreeSet;


/**
 * Schnittstelle f&uuml;r alle Approximationsmethoden von Ganglinien
 * 
 * @author BitCtrl, Schumann
 * @version $Id: Approximation.java 160 2007-02-23 15:09:31Z Schumann $
 */
public interface Approximation { 
	
	/**
	 * Gibt die Ganglinie zur&uuml;ck, die der Approximation zugrunde liegt
	 * 
	 * @return Sortierte Liste der St&uuml;tzstellen
	 */
	public Ganglinie getGanglinie();
	
	/**
	 * Gibt den Wert (als St&uuml;tzstelle) zum angegebenen Zeitstempel zur&uuml;ck
	 * 
	 * @param zeitstempel Zeitstempel 
	 * @return Wert als St&uuml;tzstelle
	 */
	public Stuetzstelle getWert(long zeitstempel);
	
	/**
	 * Gibt eine Interpolation der Approximation zur&uuml;ck. N&uuml;tzlich f&uuml;r die
	 * grafische Darstellung von Ganglinien, indem
	 * <code>anzahlIntervalle</code>+1 St&uuml;tzstellen berechnet werden, die als
	 * Polygonzug darstellbar sind.
	 * 
	 * @param anzahlIntervalle Anzahl gew&uuml;nschter Intervalle; je h&ouml;her die
	 * Anzahl der Intervalle, um so genauer n&auml;hert sich der Polygonzug der
	 * tats&auml;chlichen Approximationsfunktion
	 * @return Nach Zeitstempel sortierte Liste der St&uuml;tzstellen 
	 */
	public TreeSet<Stuetzstelle> getInterpolation(int anzahlIntervalle);
	
}
