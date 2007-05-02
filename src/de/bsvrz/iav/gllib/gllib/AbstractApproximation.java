package de.bsvrz.iav.gllib.gllib;

import java.util.SortedSet;
import java.util.TreeSet;

import de.bsvrz.sys.funclib.bitctrl.i18n.Messages;

/**
 * Implementiert nur die Property <code>Ganglinie</code> der Schnittstelle
 * 
 * @author BitCtrl, Schumann
 * @version $Id: AbstractApproximation.java 160 2007-02-23 15:09:31Z Schumann $
 */
public abstract class AbstractApproximation implements Approximation {

	/** Die der Approximation zugrunde liegende Ganglinie */
	private Ganglinie ganglinie;

	/**
	 * Konstruktor verstecken
	 */
	AbstractApproximation() {
		// nix zu tun
	}

	/**
	 * Zu approximierende Ganglinie festlegen
	 * 
	 * @param ganglinie
	 *            Eine Ganglinie
	 */
	void setGanglinie(Ganglinie ganglinie) {
		this.ganglinie = ganglinie;
	}

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<Stuetzstelle> getInterpolation(int anzahlIntervalle) {
		if (anzahlIntervalle < 0)
			Messages.get(GlLibMessages.Common_BadCount, anzahlIntervalle);

		SortedSet<Stuetzstelle> interpolation = new TreeSet<Stuetzstelle>();
		long start;
		long intervall;
		Stuetzstelle s;
		long zeitstempel;

		// Sonderfall: keine St&uuml;tzstellen vorhanden
		if (ganglinie.size() == 0) {
			return interpolation;
		}

		start = ganglinie.first().zeitstempel;
		intervall = ganglinie.last().zeitstempel - start / anzahlIntervalle;

		for (int i = 0; i <= anzahlIntervalle; i++) {
			zeitstempel = start + i * intervall;
			s = new Stuetzstelle(zeitstempel, getStuetzstelle(zeitstempel).wert);
			interpolation.add(s);
			zeitstempel += intervall;
		}

		return interpolation;
	}

	/**
	 * Bestimmt, falls vorhanden, die St&uuml;tzstellen direkt vor und nach
	 * einem bestimmten Zeitstempel
	 * 
	 * @param zeitstempel
	 *            Ein Zeitstempel
	 * @return Feld mit einer oder zwei St&uuml;tzstellen. Besteht das Feld nur
	 *         aus einem Element, fiel der Zeitstempel auf eine existierende
	 *         St&uuml;tzstelle. Andernfalls ist das erste Element die
	 *         St&uuml;tzstelle vor dem Zeitstempel und das zweite Element die
	 *         St&uuml;tzstelle nach dem Zeitstempel.
	 */
	protected Stuetzstelle[] getNaechsteStuetzstellen(long zeitstempel) {
		Stuetzstelle sp = new Stuetzstelle(zeitstempel, null);
		Stuetzstelle s1; // St&uuml;tzstelle vor Zeitstempel
		Stuetzstelle s2; // St&uuml;tzstelle am oder nach Zeitstempel
		Stuetzstelle result[] = new Stuetzstelle[0];

		// Sonderfall: keine St&uuml;tzstellen oder Zeitstempel au&szlig;erhalb
		// Ganglinie
		if (!ganglinie.contains(zeitstempel)) {
			return result;
		}

		s2 = ganglinie.tailSet(sp).first();
		if (s2.getZeitstempel() == zeitstempel) {
			// Zeitstempel f&auml;llt genau auf eine St&uuml;tzstelle
			result = new Stuetzstelle[1];
			result[0] = s2;
		} else {
			// Zeitstempel liegt zwischen zwei St&uuml;tzstellen
			result = new Stuetzstelle[2];
			s1 = ganglinie.headSet(sp).last();
			result[0] = s1;
			result[1] = s2;
		}

		return result;
	}

}
