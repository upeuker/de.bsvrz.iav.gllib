package de.bsvrz.iav.gllib.gllib;

/**
 * Repr&auml;sentiert eine allgemeine St&uuml;tzstelle f&uuml;r Ganglinien
 * bestehend aus Zeitstempel und Wert. Die St&uuml;tzstellen k&ouml;nnen nach
 * den Zeitstempeln sortiert werden. Ist der Wert einer St&uuml;tzstelle
 * <em>undefiniert</em> ({@code null}), so ist auch das Intervall bis zur
 * vorherigen und n&auml;chsten St&uuml;tzstelle <em>undefiniert</em>.
 * 
 * @author BitCtrl, Schumann
 * @version $Id: Stuetzstelle.java 160 2007-02-23 15:09:31Z Schumann $
 */
public class Stuetzstelle implements Comparable<Stuetzstelle> {

	/** Der Messwert */
	public final Integer wert;

	/** Zeitpunkt des Messwerts */
	public final long zeitstempel;

	/**
	 * Initialisierung. F&uuml;r den Wert wird <code>null</code>
	 * (=undefiniert) angenommen.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel
	 */
	public Stuetzstelle(long zeitstempel) {
		this(zeitstempel, null);
	}

	/**
	 * Zuweisungskonstruktor
	 * 
	 * @param zeitstempel
	 *            Zeitstempel
	 * @param wert
	 *            Wert oder {@code null} f&uuml;r "undefiniert"
	 */
	public Stuetzstelle(long zeitstempel, Integer wert) {
		this.zeitstempel = zeitstempel;
		this.wert = wert;
	}

	/**
	 * Gibt den Zeitstempel der St&uuml;tzstelle zur&uuml;ck
	 * 
	 * @return Zeitstempel
	 */
	public long getZeitstempel() {
		return zeitstempel;
	}

	/**
	 * Gibt den Wert der St&uuml;tzstelle zur&uuml;ck
	 * 
	 * @return Wert oder {@code null} f&uuml;r "undefiniert"
	 */
	public Integer getWert() {
		return wert;
	}

	/**
	 * Eine St&uuml;tzstelle ist kleiner bzw gr&ouml;&szlig;er, wenn der
	 * Zeitstempel kleiner bzw gr&ouml;&szlig;er ist
	 * 
	 * @param stuetzstelle
	 *            Eine St&uuml;tzstelle zum Vergleichen
	 * @return -1, 0 oder +1, wenn der Zeitstempel dieser St&uuml;tzstelle
	 *         kleiner, gleich oder gr&ouml;&szlig;er als die St&uuml;tzstelle
	 *         im Parameter ist
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Stuetzstelle stuetzstelle) {
		if (zeitstempel < stuetzstelle.zeitstempel) {
			return -1;
		} else if (zeitstempel > stuetzstelle.zeitstempel) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Zwei St&uuml;tzstellen sind identisch, wenn Zeitstempel und Wert
	 * identisch sind
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Stuetzstelle) {
			Stuetzstelle s;
			s = (Stuetzstelle) obj;
			return (zeitstempel == s.zeitstempel && wert == s.wert);
		}

		return false;
	}

	/**
	 * Gibt ein Tupel (Zeitstempel, Wert) zur&uuml;ck
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Stützstelle(" + zeitstempel + " => " + wert + ")";
	}

}
