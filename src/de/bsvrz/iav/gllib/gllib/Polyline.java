package de.bsvrz.iav.gllib.gllib;

/**
 * Approximation einer Ganglinie mit Hilfe von Polylines
 * 
 * @author BitCtrl, Schumann
 * @version $Id: Polyline.java 160 2007-02-23 15:09:31Z Schumann $
 */
public class Polyline extends AbstractApproximation {

	/**
	 * @see AbstractApproximation
	 */
	Polyline() {
		super();
	}

	/**
	 * {@inheritDoc}.
	 * 
	 * Der Wert zum Zeitstempel wird nach folgender Formel berechnet:<br>
	 * <img src="doc-files/formel_polyline.png">
	 */
	public Stuetzstelle getStuetzstelle(long zeitstempel) {
		Stuetzstelle[] s = getNaechsteStuetzstellen(zeitstempel);
		Long wert;

		switch (s.length) {
		case 0:
			// Zeitstempel geh�rt nicht zu Ganglinie oder keine St�tzstellen
			return null;
		case 1:
			// St�tzstelle zum Zeitstempel ist in Ganglinie vorhanden
			return s[0];
		case 2:
			// St�tzstelle muss berechnet werden
			wert = s[0].wert + (s[1].wert - s[0].wert)
					/ (s[1].zeitstempel - s[0].zeitstempel)
					* (zeitstempel - s[0].zeitstempel);
			return new Stuetzstelle(zeitstempel, wert.intValue());

		default:
			throw new IllegalStateException();
		}
	}

}
