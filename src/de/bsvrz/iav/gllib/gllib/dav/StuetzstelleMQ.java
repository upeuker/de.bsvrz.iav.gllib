/**
 * 
 */
package de.bsvrz.iav.gllib.gllib.dav;

import de.bsvrz.iav.gllib.gllib.Stuetzstelle;

/**
 * F&uuml;r Messquerschnitte angepasste St&uuml;tzstelle, die die Werte QKfz, QLkw, VLkw
 * und VPkw f&uuml;r den Zeitstempel enth&auml;lt.
 * <p>
 * <strong>Abk&uuml;rzungen:</strong>
 * <ul>
 * <li><em>Qx</em> - Verkehrsst&auml;rken [Fahrzeuge / h]</li>
 * <li><em>Vx</em> - Mittlere Geschwindigkeiten [km/h]</li>
 * <li><em>QB</em> - Bemessungsverkehrsst&auml;rke [PKW-Einheiten / Stunde]</li>
 * </ul>
 * </p>
 * 
 * @author BitCtrl, Schumann
 * @version $Id: StuetzstelleMQ.java 160 2007-02-23 15:09:31Z Schumann $
 */
public class StuetzstelleMQ extends Stuetzstelle {

	private Integer qLkw = -1;
	private Integer qKfz = -1;
	private Integer vPkw = -1;
	private Integer vLkw = -1;
	
	
	/**
	 * Initialisierung. F&uuml;r die Verkehrswerte wird <code>null</code> angenommen.
	 * 
	 * @param zeitstempel Zeitstempel
	 */
	public StuetzstelleMQ(long zeitstempel) {
		super(zeitstempel, null);
		qKfz = null;
		qLkw = null;
		vPkw = null;
		vLkw = null;
	}
	
	/**
	 * Zuweisungskonstruktor
	 * 
	 * @param zeitstempel Zeitstempel
	 * @param qKfz Kfz/h
	 * @param qLkw Lkw/h
	 * @param vPkw Geschwindigkeit Pkw
	 * @param vLkw Geschwindigkeit Lkw
	 */
	public StuetzstelleMQ(long zeitstempel, Integer qKfz, Integer qLkw,
			Integer vPkw, Integer vLkw) {
		super(zeitstempel, null);
		this.qKfz = qKfz;
		this.qLkw = qLkw;
		this.vPkw = vPkw;
		this.vLkw = vLkw;
	}
	
	/**
	 * Gibt den Wert f&uuml;r QLkw zur&uuml;ck
	 * 
	 * @return Wert f&uuml;r QLkw
	 */
	public Integer getQLkw() {
		return qLkw;
	}

	/**
	 * Setzt den Wert f&uuml;r QLkw
	 * 
	 * @param qLkw Wert f&uuml;r QLkw
	 */
	public void setQLkw(Integer qLkw) {
		this.qLkw = qLkw;
	}

	/**
	 * Gibt den Wert f&uuml;r QKfz zur&uuml;ck
	 * 
	 * @return Wert f&uuml;r QKfz
	 */
	public Integer getQKfz() {
		return qKfz;
	}

	/**
	 * Setzt den Wert f&uuml;r QKfz
	 * 
	 * @param qKfz Wert f&uuml;r QKfz
	 */
	public void setQKfz(Integer qKfz) {
		this.qKfz = qKfz;
	}

	/**
	 * Gibt den Wert f&uuml;r VPkw zur&uuml;ck
	 * 
	 * @return Wert f&uuml;r VPkw
	 */
	public Integer getVPkw() {
		return vPkw;
	}

	/**
	 * Setzt den Wert f&uuml;r VPkw
	 * 
	 * @param vPkw Wert f&uuml;r VPkw
	 */
	public void setVPkw(Integer vPkw) {
		this.vPkw = vPkw;
	}

	/**
	 * Gibt den Wert f&uuml;r VLkw zur&uuml;ck
	 * 
	 * @return Wert f&uuml;r VLkw
	 */
	public Integer getVLkw() {
		return vLkw;
	}

	/**
	 * Setzt den Wert f&uuml;r VLkw
	 * 
	 * @param vLkw Wert f&uuml;r VLkw
	 */
	public void setVLkw(Integer vLkw) {
		this.vLkw = vLkw;
	}

		
	/**
	 * Gibt den Wert f&uuml;r QPkw zur&uuml;ck. Diese Property ist read-only, da sie aus
	 * den in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert f&uuml;r QPkw 
	 */
	public Integer getQPkw(){
		return 0;
	}
	
	/**
	 * Gibt den Wert f&uuml;r VKfz zur&uuml;ck. Diese Property ist read-only, da sie aus
	 * den in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert f&uuml;r VKfz
	 */
	public Integer getVKfz(){
		return 0;
	}
	
	/**
	 * Gibt den Wert f&uuml;r QB zur&uuml;ck. Diese Property ist read-only, da sie aus den
	 * in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert f&uuml;r QB
	 */
	public Integer getQB(){
		return 0;
	}
	
	/**
	 * Gibt ein Tupel (Zeitstempel, QKfz, QLkw, VPkw, VLkw) zur&uuml;ck
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + getZeitstempel() + ", " + qKfz //$NON-NLS-1$ //$NON-NLS-2$
		 + ", " + qLkw //$NON-NLS-1$
		 + ", " + vPkw //$NON-NLS-1$
		 + ", " + vLkw+")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
