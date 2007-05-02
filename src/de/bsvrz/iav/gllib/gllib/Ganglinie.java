/**
 * 
 */
package de.bsvrz.iav.gllib.gllib;

import java.util.SortedSet;
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
public class Ganglinie extends TreeSet<Stuetzstelle> implements Approximation {

	/** Verfahren zur Berechnung der Punkte zwischen den St&uuml;tzstellen */
	private Approximation approximation = new BSpline(this);

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
	 * Legt das Approximationsverfahren fest, mit dem die Werte zwischen den
	 * St&uuml;tzstellen bestimmt werden soll
	 * 
	 * @param approximation
	 *            Klasse eines Approximationsverfahrens
	 */
	public void setApproximation(Class<? extends Approximation> approximation) {
		AbstractApproximation a;

		try {
			a = (AbstractApproximation) approximation.newInstance();
			a.setGanglinie(this);
			this.approximation = a;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalArgumentException(e.getLocalizedMessage());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalArgumentException(e.getLocalizedMessage());
		}
	}

	/**
	 * Gibt die St&uuml;tzstelle zu einem bestimmten Zeitstempel zur&uuml;ck.
	 * Existiert die St&uuml;tzstelle wird diese zur&uuml;ckgegeben. Andernfalls
	 * wird der Wert zum Zeitstempel approximiert.
	 * <p>
	 * TODO: Approximation einbauen
	 * <p>
	 * {@inheritDoc}
	 * 
	 * @param zeitstempel
	 *            Ein Zeitpunkt
	 * @return Die St&uuml;tzstelle zum Zeitpunkt
	 */
	public Stuetzstelle getStuetzstelle(long zeitstempel) {
		if (contains(zeitstempel)) {
			Stuetzstelle s;

			// Wenn echte Stützstelle vorhanden, diese benutzen
			s = new Stuetzstelle(zeitstempel);
			s = tailSet(s).first();
			if (s.zeitstempel == zeitstempel)
				return s;

			// Ansonsten genäherte Stützstelle verwenden
			return approximation.getStuetzstelle(zeitstempel);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<Stuetzstelle> getInterpolation(int anzahlIntervalle) {
		return approximation.getInterpolation(anzahlIntervalle);
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
