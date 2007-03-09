package de.bsvrz.iav.gllib.gllib;

import de.bsvrz.sys.funclib.i18n.Messages;


/**
 * Repr&auml;sentiert eine allgemeine St&uuml;tzstelle f&uuml;r Ganglinien bestehend aus
 * Zeitstempel und Wert. Die St&uuml;tzstellen k&ouml;nnen nach den Zeitstempeln sortiert
 * werden. Ist der Wert einer St&uuml;tzstelle <em>undefiniert</em>
 * (<code>null</code>), so ist auch das Intervall bis zur vorherigen und
 * n&auml;chsten St&uuml;tzstelle <em>undefiniert</em>.
 * 
 * @author BitCtrl, Schumann
 * @version $Id: Stuetzstelle.java 160 2007-02-23 15:09:31Z Schumann $
 */
public class Stuetzstelle implements Comparable<Stuetzstelle> {

	private long zeitstempel;
	private Integer wert;
	
	
	/**
	 * Initialisierung. F&uuml;r den Wert wird <code>null</code> angenommen.
	 * 
	 * @param zeitstempel Zeitstempel
	 */
	public Stuetzstelle (long zeitstempel) {
		setZeitstempel(zeitstempel);
		wert = null;
	}
	
	/**
	 * Zuweisungskonstruktor
	 * 
	 * @param zeitstempel Zeitstempel
	 * @param wert Wert
	 */
	public Stuetzstelle (long zeitstempel, Integer wert) {
		setZeitstempel(zeitstempel);
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
	 * Setzt den Zeitstempel der St&uuml;tzstelle auf den Wert im Parameter
	 * 
	 * @param zeitstempel Zeitstempel
	 */
	public void setZeitstempel(long zeitstempel) {
		assert zeitstempel >= 0 :
			Messages.get(GlLibMessages.Common_BadTimestamp, zeitstempel);
		
		this.zeitstempel = zeitstempel;
	}

	/** 
	 * Gibt den Wert der St&uuml;tzstelle zur&uuml;ck
	 * 
	 * @return Wert oder <code>null</code> f&uuml;r "undefiniert"
	 */
	public Integer getWert() {
		return wert;
	}

	/** 
	 * Setzt den Wert der St&uuml;tzstelle auf den Wert im Parameter
	 * 
	 * @param wert Wert oder <code>null</code> f&uuml;r "undefiniert"
	 */
	public void setWert(Integer wert) {
		this.wert = wert;
	}

	/**
	 * Eine St&uuml;tzstelle ist kleiner bzw gr&ouml;&szlig;er, wenn der Zeitstempel kleiner
	 * bzw gr&ouml;&szlig;er ist
	 * 
	 * @param stuetzstelle Eine St&uuml;tzstelle zum Vergleichen
	 * @return -1, 0 oder +1, wenn der Zeitstempel dieser St&uuml;tzstelle kleiner,
	 * gleich oder gr&ouml;&szlig;er als die St&uuml;tzstelle im Parameter ist
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Stuetzstelle stuetzstelle) {
		if (this.zeitstempel < stuetzstelle.zeitstempel) {
			return -1;
		} else if (this.zeitstempel > stuetzstelle.zeitstempel) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Zwei St&uuml;tzstellen sind identisch, wenn Zeitstempel und Wert identisch
	 * sind
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj){
		Stuetzstelle s;
		
		try {
			s = (Stuetzstelle) obj;
		} catch (ClassCastException ex) {
			return false;
		}
		
		return (this.getZeitstempel() == s.getZeitstempel()
				&& this.getWert() == s.getWert());
	}
	
	/**
	 * Gibt ein Tupel (Zeitstempel, Wert) zur&uuml;ck
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + zeitstempel + ", " + wert +")";  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}
	
}
