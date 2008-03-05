/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * Contact Information:
 * BitCtrl Systems GmbH
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.dav;

import de.bsvrz.sys.funclib.bitctrl.util.dav.Umrechung;

/**
 * Für Messquerschnitte angepasste Stützstelle, die die Werte QKfz, QLkw, VLkw
 * und VPkw für den Zeitstempel enthält.
 * <p>
 * <strong>Abkürzungen:</strong>
 * <ul>
 * <li><em>Qx</em> - Verkehrsstärken [Fahrzeuge / h]</li>
 * <li><em>Vx</em> - Mittlere Geschwindigkeiten [km/h]</li>
 * <li><em>QB</em> - Bemessungsverkehrsstärke [PKW-Einheiten / Stunde]</li>
 * </ul>
 * </p>
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class Messwerte {

	/** Konstante für einen undefinierten Wert ({@value}). */
	public static final double UNDEFINIERT = -2147483.648;

	/** Wert für die Verkehrsstärke der Lkw. */
	private final Double qLkw;

	/** Wert für die Verkehrsstärke der Kfz. */
	private final Double qKfz;

	/** Wert für die Geschwindigkeit der Pkw. */
	private final Double vPkw;

	/** Wert für die Geschwindigkeit der Lkw. */
	private final Double vLkw;

	/** Parameter für die Berechnung von QB. */
	private final float k1;

	/** Parameter für die Berechnung von QB. */
	private final float k2;

	/**
	 * Kreiert einen undefinierten Messwert.
	 */
	public Messwerte() {
		this.qKfz = null;
		this.qLkw = null;
		this.vPkw = null;
		this.vLkw = null;
		this.k1 = 2.0f;
		this.k2 = 0.01f;
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
	 */
	public Messwerte(final Double qKfz, final Double qLkw, final Double vPkw,
			final Double vLkw) {
		this(qKfz, qLkw, vPkw, vLkw, 2.0f, 0.01f);
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
	 *            Parameter für die Berechnung von QB
	 * @param k2
	 *            Parameter für die Berechnung von QB
	 */
	Messwerte(final Double qKfz, final Double qLkw, final Double vPkw,
			final Double vLkw, final float k1, final float k2) {
		if (qKfz != null && !qKfz.equals(UNDEFINIERT)) {
			this.qKfz = qKfz;
		} else {
			this.qKfz = null;
		}
		if (qLkw != null && !qLkw.equals(UNDEFINIERT)) {
			this.qLkw = qLkw;
		} else {
			this.qLkw = null;
		}

		if (vPkw != null && !vPkw.equals(UNDEFINIERT)) {
			this.vPkw = vPkw;
		} else {
			this.vPkw = null;
		}

		if (vLkw != null && !vLkw.equals(UNDEFINIERT)) {
			this.vLkw = vLkw;
		} else {
			this.vLkw = null;
		}

		this.k1 = k1;
		this.k2 = k2;
	}

	/**
	 * Zwei Stützstellen sind identisch, wenn beide den selben Zeitstempel und
	 * die selben Werte haben.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
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
	 * Gibt den Wert für QB zurück. Diese Property ist read-only, da sie aus den
	 * in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert für QB
	 */
	public Double getQB() {
		return Umrechung.getQB(qLkw, qKfz, vPkw, vLkw, k1, k2);
	}

	/**
	 * Gibt den Wert für QKfz zurück.
	 * 
	 * @return Wert für QKfz
	 */
	public Double getQKfz() {
		return qKfz;
	}

	/**
	 * Gibt den Wert für QLkw zurück.
	 * 
	 * @return Wert für QLkw
	 */
	public Double getQLkw() {
		return qLkw;
	}

	/**
	 * Gibt den Wert für QPkw zurück. Diese Property ist read-only, da sie aus
	 * den in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert für QPkw
	 */
	public Double getQPkw() {
		return Umrechung.getQPkw(qKfz, qLkw);
	}

	/**
	 * Gibt den Wert für VKfz zurück. Diese Property ist read-only, da sie aus
	 * den in der Ganglinie gesicherten Werten berechnet wird.
	 * 
	 * @return Wert für VKfz
	 */
	public Double getVKfz() {
		return Umrechung.getVKfz(qLkw, qKfz, vPkw, vLkw);
	}

	/**
	 * Gibt den Wert für VLkw zurück.
	 * 
	 * @return Wert für VLkw
	 */
	public Double getVLkw() {
		return vLkw;
	}

	/**
	 * Gibt den Wert für VPkw zurück.
	 * 
	 * @return Wert für VPkw
	 */
	public Double getVPkw() {
		return vPkw;
	}

	/**
	 * Gibt ein Tupel (Zeitstempel, QKfz, QLkw, VPkw, VLkw) zurück.
	 * <p>
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[QKfz=" + qKfz + ", QPkw="
				+ getQPkw() + ", QLkw=" + qLkw + ", VKfz=" + getVKfz()
				+ ", VPkw=" + vPkw + ", VLkw=" + vLkw + ", QB=" + getQB() + "]";
	}

}
