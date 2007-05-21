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
	 * Zuweisungskonstruktor.
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
	 * Gibt den Wert f&uuml;r QLkw zur&uuml;ck.
	 * 
	 * @return Wert f&uuml;r QLkw
	 */
	public Integer getQLkw() {
		return qLkw;
	}

	/**
	 * Gibt den Wert f&uuml;r QKfz zur&uuml;ck.
	 * 
	 * @return Wert f&uuml;r QKfz
	 */
	public Integer getQKfz() {
		return qKfz;
	}

	/**
	 * Gibt den Wert f&uuml;r VPkw zur&uuml;ck.
	 * 
	 * @return Wert f&uuml;r VPkw
	 */
	public Integer getVPkw() {
		return vPkw;
	}

	/**
	 * Gibt den Wert f&uuml;r VLkw zur&uuml;ck.
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
		return Umrechung.getQPkw(qKfz, qLkw);
	}

	/**
	 * Gibt den Wert f&uuml;r VKfz zur&uuml;ck. Diese Property ist read-only, da
	 * sie aus den in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert f&uuml;r VKfz
	 */
	public Integer getVKfz() {
		return Umrechung.getVKfz(qLkw, qKfz, vPkw, vLkw);
	}

	/**
	 * Gibt den Wert f&uuml;r QB zur&uuml;ck. Diese Property ist read-only, da
	 * sie aus den in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert f&uuml;r QB
	 */
	public Integer getQB() {
		return Umrechung.getQB(qLkw, qKfz, vPkw, vLkw, k1, k2);
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
		return "(" + zeitstempel + ", " + qKfz //$NON-NLS-1$ //$NON-NLS-2$
				+ ", " + qLkw //$NON-NLS-1$
				+ ", " + vPkw //$NON-NLS-1$
				+ ", " + vLkw + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
