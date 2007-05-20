/**
 * 
 */
package de.bsvrz.iav.gllib.gllib.dav;

/**
 * F&uuml;r Messquerschnitte angepasste St&uuml;tzstelle, die die Werte QKfz,
 * QLkw, VLkw und VPkw f&uuml;r den Zeitstempel enth&auml;lt.
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
 * @version $Id$
 */
public class StuetzstelleMQ {

	/** Zeitstempel der St&uuml;tzstelle. */
	private final long zeitstempel;

	/** Wert f&uuml;r die Verkehrsst&auml;rke der Lkw. */
	private final Integer qLkw;

	/** Wert f&uuml;r die Verkehrsst&auml;rke der Kfz. */
	private final Integer qKfz;

	/** Wert f&uuml;r die Geschwindigkeit der Pkw. */
	private final Integer vPkw;

	/** Wert f&uuml;r die Geschwindigkeit der Lkw. */
	private final Integer vLkw;

	/** Parameter f&uuml;r die Berechnung von QB. */
	private final float k1;

	/** Parameter f&uuml;r die Berechnung von QB. */
	private final float k2;

	/**
	 * Zuweisungskonstruktor
	 * 
	 * @param zeitstempel
	 *            Zeitstempel
	 * @param qKfz
	 *            Kfz/h
	 * @param qLkw
	 *            Lkw/h
	 * @param vPkw
	 *            Geschwindigkeit Pkw
	 * @param vLkw
	 *            Geschwindigkeit Lkw
	 * @param k1
	 *            Parameter f&uuml;r die Berechnung von QB
	 * @param k2
	 *            Parameter f&uuml;r die Berechnung von QB
	 */
	public StuetzstelleMQ(long zeitstempel, Integer qKfz, Integer qLkw,
			Integer vPkw, Integer vLkw, float k1, float k2) {
		this.zeitstempel = zeitstempel;
		this.qKfz = qKfz;
		this.qLkw = qLkw;
		this.vPkw = vPkw;
		this.vLkw = vLkw;
		this.k1 = k1;
		this.k2 = k2;
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
	 * Gibt den Wert f&uuml;r QKfz zur&uuml;ck
	 * 
	 * @return Wert f&uuml;r QKfz
	 */
	public Integer getQKfz() {
		return qKfz;
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
	 * Gibt den Wert f&uuml;r VLkw zur&uuml;ck
	 * 
	 * @return Wert f&uuml;r VLkw
	 */
	public Integer getVLkw() {
		return vLkw;
	}

	/**
	 * Gibt den Wert f&uuml;r QPkw zur&uuml;ck. Diese Property ist read-only, da
	 * sie aus den in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert f&uuml;r QPkw
	 */
	public Integer getQPkw() {
		return QPkw(qKfz, qLkw);
	}

	/**
	 * Gibt den Wert f&uuml;r VKfz zur&uuml;ck. Diese Property ist read-only, da
	 * sie aus den in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert f&uuml;r VKfz
	 */
	public Integer getVKfz() {
		return VKfz(qLkw, qKfz, vPkw, vLkw);
	}

	/**
	 * Gibt den Wert f&uuml;r QB zur&uuml;ck. Diese Property ist read-only, da
	 * sie aus den in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert f&uuml;r QB
	 */
	public Integer getQB() {
		return QB(qLkw, qKfz, vPkw, vLkw, k1, k2);
	}

	/**
	 * Gibt ein Tupel (Zeitstempel, QKfz, QLkw, VPkw, VLkw) zur&uuml;ck
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + zeitstempel + ", " + qKfz //$NON-NLS-1$ //$NON-NLS-2$
				+ ", " + qLkw //$NON-NLS-1$
				+ ", " + vPkw //$NON-NLS-1$
				+ ", " + vLkw + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Hilfsfunktion zum Bestimmen von QPkw
	 * 
	 * @param qKfz
	 *            QKfz
	 * @param qLkw
	 *            QLkw
	 * @return QPkw
	 */
	static Integer QPkw(Integer qKfz, Integer qLkw) {
		if (qKfz != null && qLkw != null) {
			return qKfz - qLkw;
		}

		return null;
	}

	/**
	 * Hilfsfunktion zum Bestimmen von VKfz
	 * 
	 * @param qLkw
	 *            QLkw
	 * @param qKfz
	 *            QKfz
	 * @param vPkw
	 *            VPkw
	 * @param vLkw
	 *            VLkw
	 * @return VKfz
	 */
	static Integer VKfz(Integer qLkw, Integer qKfz, Integer vPkw, Integer vLkw) {
		if (vPkw != null && qLkw != null && vLkw != null && qKfz != null
				&& qKfz > 0) {
			Integer qPkw;

			qPkw = QPkw(qKfz, qLkw);
			return Math.round((float) (qPkw * vPkw + qLkw * vLkw) / qKfz);
		}

		return null;
	}

	/**
	 * Hilfsfunktion zum Bestimmen von QB
	 * 
	 * @param qLkw
	 *            QLkw
	 * @param qKfz
	 *            QKfz
	 * @param vPkw
	 *            VPkw
	 * @param vLkw
	 *            VLkw
	 * @param k1
	 *            k1
	 * @param k2
	 *            k2
	 * @return QB
	 */
	static Integer QB(Integer qLkw, Integer qKfz, Integer vPkw, Integer vLkw,
			float k1, float k2) {
		if (vPkw != null && qLkw != null && vLkw != null && qKfz != null) {
			float fLGL;
			Integer qPkw;

			qPkw = QPkw(qKfz, qLkw);
			if (vPkw > vLkw) {
				fLGL = k1 + k2 * (vPkw - vLkw);
			} else {
				fLGL = k1;
			}

			return Math.round(qPkw + fLGL * qLkw);
		}

		return null;
	}

}
