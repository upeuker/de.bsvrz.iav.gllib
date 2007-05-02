package de.bsvrz.iav.gllib.gllib;

import de.bsvrz.sys.funclib.bitctrl.i18n.Messages;

/**
 * Repr&auml;sentiert ein Intervall f&uuml;r <code>long</code>-Werte. Wird
 * f&uuml;r Zeitintervalle genutzt, die mit Zeitstempeln arbeiten.
 * 
 * @author BitCtrl, Schumann
 * @version $Id: Intervall.java 160 2007-02-23 15:09:31Z Schumann $
 */
public class Intervall {

	/** Startzeitpunkt des Intervall */
	public final long start;

	/** Endzeitpunkt des Intervall */
	public final long ende;

	/**
	 * Konstruiert das Intervall mit dem angegebenen Grenzen
	 * 
	 * @param start
	 *            Start des Intervalls
	 * @param ende
	 *            Ende des Intervalls
	 */
	public Intervall(long start, long ende) {
		if (start > ende) {
			throw new IllegalArgumentException(Messages.get(
					GlLibMessages.Common_BadIntervall, start, ende));
		}

		this.start = start;
		this.ende = ende;
	}

	/**
	 * Gibt den Anfang des Intervalls zur&uuml;ck
	 * 
	 * @return Zeitstempel
	 */
	public long getStart() {
		return start;
	}

	/**
	 * Gibt das Ende des Intervalls zur&uuml;ck
	 * 
	 * @return Zeitstempel
	 */
	public long getEnde() {
		return ende;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + start + ", " + ende + "]";
	}

}
