package de.iav.gllib.gllib;

import de.sys.funclib.i18n.Messages;


/**
 * Repr&auml;sentiert ein Intervall f&uuml;r <code>long</code>-Werte. Wird f&uuml;r
 * Zeitintervalle genutzt, die mit Zeitstempeln arbeiten.
 * 
 * @author BitCtrl, Schumann
 * @version $Id: Intervall.java 160 2007-02-23 15:09:31Z Schumann $
 */
public class Intervall {

	private long start;
	private long ende;
	
	
	/**
	 * Konstruiert das Intervall mit dem angegebenen Grenzen
	 * 
	 * @param start Start des Intervalls
	 * @param ende Ende des Intervalls
	 */
	public Intervall(long start, long ende){
		setStart(start);
		setEnde(ende);
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
	 * Legt den Anfang des Intervalls fest
	 * 
	 * @param start Zeitstempel
	 */
	public void setStart(long start) {
		check(start, ende);		
		this.start = start;
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
	 * Legt das Ende des Intervalls fest
	 *  
	 * @param ende Zeitstempel
	 */
	public void setEnde(long ende) {
		check(start, ende);
		this.ende = ende;
	}
	
	private void check(long s, long e) {
		if (s > e) {
			throw new IllegalArgumentException(
				Messages.get(GlLibMessages.Common_BadIntervall, s, e)
			);
		}
	}

}
