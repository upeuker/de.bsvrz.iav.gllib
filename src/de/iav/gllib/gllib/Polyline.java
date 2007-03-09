package de.iav.gllib.gllib;


/**
 * Approximation einer Ganglinie mit Hilfe von Polylines
 * 
 * @author BitCtrl, Schumann
 * @version $Id: Polyline.java 160 2007-02-23 15:09:31Z Schumann $
 */
public class Polyline extends AbstractApproximation {	
	
	/**
	 * @see AbstractApproximation
	 * @param ganglinie
	 */
	public Polyline(Ganglinie ganglinie) {
		super(ganglinie);
	}

	/**
	 * {@inheritDoc}.
	 * 
	 * Der Wert zum Zeitstempel wird nach folgender Formel berechnet:<br>
	 * <img src="doc-files/formel_polyline.png"> 
	 */
	public Stuetzstelle getWert(long zeitstempel) {
		Stuetzstelle[] s = getNextStuetzstellen(zeitstempel);
		Long wert;
		
		// Sonderf&auml;lle
		if (s.length == 0) {		
			// Zeitstempel ist falsch oder keine St&uuml;tzstellen
			return null; //
		} else if (s.length == 1) {
			// St&uuml;tzstelle ist in Ganglinie vorhanden
			return s[0];
		}
		
		wert = s[1].getWert()
			+ (s[1].getWert() - s[0].getWert())
			/ (s[1].getZeitstempel() - s[0].getZeitstempel())
			* (zeitstempel - s[0].getZeitstempel());
		
		return new Stuetzstelle(zeitstempel, wert.intValue());
	}

}
