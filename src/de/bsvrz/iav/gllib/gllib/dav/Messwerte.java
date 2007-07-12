/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact Information:
 * BitCtrl Systems GmbH
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.dav;

import de.bsvrz.sys.funclib.bitctrl.util.dav.Umrechung;

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
public class Messwerte {

	/** Wert f&uuml;r die Verkehrsst&auml;rke der Lkw. */
	private final Float qLkw;

	/** Wert f&uuml;r die Verkehrsst&auml;rke der Kfz. */
	private final Float qKfz;

	/** Wert f&uuml;r die Geschwindigkeit der Pkw. */
	private final Float vPkw;

	/** Wert f&uuml;r die Geschwindigkeit der Lkw. */
	private final Float vLkw;

	/** Parameter f&uuml;r die Berechnung von QB. */
	private final float k1;

	/** Parameter f&uuml;r die Berechnung von QB. */
	private final float k2;

	/**
	 * Zuweisungskonstruktor.
	 * 
	 * @param qKfz
	 *            Kfz/h
	 * @param qLkw
	 *            Lkw/h
	 * @param vPkw
	 *            Geschwindigkeit Pkw
	 * @param vLkw
	 *            Geschwindigkeit Lkw
	 */
	public Messwerte(Float qKfz, Float qLkw, Float vPkw, Float vLkw) {
		this.qKfz = qKfz;
		this.qLkw = qLkw;
		this.vPkw = vPkw;
		this.vLkw = vLkw;
		k1 = -1;
		k2 = -1;
	}
	
	/**
	 * Zuweisungskonstruktor.
	 * 
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
	Messwerte(Float qKfz, Float qLkw, Float vPkw, Float vLkw, float k1, float k2) {
		this.qKfz = qKfz;
		this.qLkw = qLkw;
		this.vPkw = vPkw;
		this.vLkw = vLkw;
		this.k1 = k1;
		this.k2 = k2;
	}

	/**
	 * Gibt den Wert f&uuml;r QLkw zur&uuml;ck.
	 * 
	 * @return Wert f&uuml;r QLkw
	 */
	public Float getQLkw() {
		return qLkw;
	}

	/**
	 * Gibt den Wert f&uuml;r QKfz zur&uuml;ck.
	 * 
	 * @return Wert f&uuml;r QKfz
	 */
	public Float getQKfz() {
		return qKfz;
	}

	/**
	 * Gibt den Wert f&uuml;r VPkw zur&uuml;ck.
	 * 
	 * @return Wert f&uuml;r VPkw
	 */
	public Float getVPkw() {
		return vPkw;
	}

	/**
	 * Gibt den Wert f&uuml;r VLkw zur&uuml;ck.
	 * 
	 * @return Wert f&uuml;r VLkw
	 */
	public Float getVLkw() {
		return vLkw;
	}

	/**
	 * Gibt den Wert f&uuml;r QPkw zur&uuml;ck. Diese Property ist read-only, da
	 * sie aus den in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert f&uuml;r QPkw
	 */
	public Float getQPkw() {
		return Umrechung.getQPkw(qKfz, qLkw);
	}

	/**
	 * Gibt den Wert f&uuml;r VKfz zur&uuml;ck. Diese Property ist read-only, da
	 * sie aus den in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert f&uuml;r VKfz
	 */
	public Float getVKfz() {
		return Umrechung.getVKfz(qLkw, qKfz, vPkw, vLkw);
	}

	/**
	 * Gibt den Wert f&uuml;r QB zur&uuml;ck. Diese Property ist read-only, da
	 * sie aus den in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert f&uuml;r QB
	 */
	public Float getQB() {
		return Umrechung.getQB(qLkw, qKfz, vPkw, vLkw, k1, k2);
	}

	/**
	 * Zwei St&uuml;tzstellen sind identisch, wenn beide den selben Zeitstempel
	 * und die selben Werte haben.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Messwerte) {
			Messwerte s;
			boolean gleich;

			s = (Messwerte) obj;

			if (qKfz != null) {
				gleich = qKfz.equals(s.qKfz);
			} else {
				gleich = qKfz == s.qKfz;
			}

			if (qLkw != null) {
				gleich &= qLkw.equals(s.qLkw);
			} else {
				gleich &= qLkw == s.qLkw;
			}

			if (vLkw != null) {
				gleich &= vLkw.equals(s.vLkw);
			} else {
				gleich &= vLkw == s.vLkw;
			}

			if (vPkw != null) {
				gleich &= vPkw.equals(s.vPkw);
			} else {
				gleich &= vPkw == s.vPkw;
			}

			return gleich;
		}

		return false;
	}

	/**
	 * Gibt ein Tupel (Zeitstempel, QKfz, QLkw, VPkw, VLkw) zur&uuml;ck.
	 * <p>
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QKfz=" + qKfz + ", QPkw=" + getQPkw() + ", QLkw=" + qLkw
				+ ", VKfz=" + getVKfz() + ", VPkw=" + vPkw + ", VLkw" + vLkw
				+ ", QB=" + getQB();
	}

}
