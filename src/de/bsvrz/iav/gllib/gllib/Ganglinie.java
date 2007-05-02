/**
 * 
 */
package de.bsvrz.iav.gllib.gllib;

import java.util.TreeSet;

/**
 * Repr&auml;sentiert eine allgemeine Ganglinie, bestehend aus einer sortierten
 * Menge von St&uuml;tzstellen und der Angabe eines Interpolationsverfahren.
 * Wird kein Approximationsverfahren festgelegt, wird ein
 * {@link BSpline B-Spline} mit Standardordnung angenommen.
 * 
 * @author BitrCtrl, Schumann
 * @version $Id: Ganglinie.java 160 2007-02-23 15:09:31Z Schumann $
 */
@SuppressWarnings("serial")
public class Ganglinie extends TreeSet<Stuetzstelle> {

	/**
	 * Gibt das Zeitintervall der Ganglinie zur&uuml;ck
	 * 
	 * @return Ein {@link Intervall} oder {@code null}, wenn keine
	 *         Stz&uuml;tzstellen vorhanden sind
	 */
	public Intervall getIntervall() {
		if (size() == 0)
			return null;

		return new Intervall(first().zeitstempel, last().zeitstempel);
	}

	/**
	 * Pr&uuml;ft ob ein Zeitstempel im Definitionsbereich der Ganglinie liegt
	 * 
	 * @param zeitstempel
	 *            zu pr&uuml;fender Zeitstempel
	 * @return <code>true</code>, wenn <code>zeitstempel</code> zwischen
	 *         den Zeitstempeln der ersten und letzten St&uuml;tzstelle liegt,
	 *         sonst <code>false</code>
	 */
	public boolean contains(long zeitstempel) {
		if (size() == 0) {
			return false;
		}

		return first().zeitstempel <= zeitstempel
				&& zeitstempel <= last().zeitstempel;
	}

	/**
	 * Gibt die St&uuml;tzstelle zu einem bestimmten Zeitstempel zur&uuml;ck.
	 * Existiert die St&uuml;tzstelle wird diese zur&uuml;ckgegeben. Andernfalls
	 * wird der Wert zum Zeitstempel approximiert.
	 * <p>
	 * TODO: Approximation einbauen
	 * 
	 * @param zeitstempel
	 *            Ein Zeitpunkt
	 * @return Die St&uuml;tzstelle zum Zeitpunkt
	 */
	public Stuetzstelle getStuetzstelle(long zeitstempel) {
		if (contains(zeitstempel)) {
			Stuetzstelle s;

			s = new Stuetzstelle(zeitstempel);
			return tailSet(s).first();
		}

		return null;
	}

	/**
	 * Gibt Zeilenweise die St&uuml;tzstellen zur&uuml;ck
	 * 
	 * @see java.util.AbstractCollection#toString()
	 */
	@Override
	public String toString() {
		String result;

		result = "Ganglinie " + getIntervall() + ":\n";
		for (Stuetzstelle s : this) {
			result += s + "\n";
		}

		return result;
	}

}
