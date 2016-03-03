/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2011-2015 BitCtrl Systems GmbH
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

package de.bsvrz.iav.gllib.gllib.modell.db;

import java.io.Serializable;

/**
 * Korrespondiert mit DAV-Attributliste <code>atl.stützstelle</code>.
 *
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 */
public class DbStuetzstelle implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Zeitstempel.
	 */
	private long zeit;

	/**
	 * QLkw.
	 */
	private double qLkw;

	/**
	 * QKfz.
	 */
	private double qKfz;

	/**
	 * VPkw.
	 */
	private double vPkw;

	/**
	 * VLkw.
	 */
	private double vLkw;

	/**
	 * Standardkonstruktor.
	 */
	public DbStuetzstelle() {
		//
	}

	/**
	 * Konstruktor.
	 *
	 * @param zeit
	 *            Zeitstempel.
	 * @param qLkw
	 *            QLkw.
	 * @param qKfz
	 *            QKfz.
	 * @param vPkw
	 *            VPkw.
	 * @param vLkw
	 *            VLkw.
	 */
	public DbStuetzstelle(final long zeit, final double qLkw, final double qKfz,
			final double vPkw, final double vLkw) {
		this.zeit = zeit;
		this.qLkw = qLkw;
		this.qKfz = qKfz;
		this.vPkw = vPkw;
		this.vLkw = vLkw;
	}

	/**
	 * Erfragt den Zeitstempel dieser Stuetzstelle.
	 *
	 * @return der Zeitstempel dieser Stuetzstelle.
	 */
	public long getZeit() {
		return zeit;
	}

	/**
	 * Erfragt QLkw.
	 *
	 * @return QLkw.
	 */
	public double getqLkw() {
		return qLkw;
	}

	/**
	 * Erfragt QKfz.
	 *
	 * @return QKfz.
	 */
	public double getqKfz() {
		return qKfz;
	}

	/**
	 * Erfragt VPkw.
	 *
	 * @return VPkw.
	 */
	public double getvPkw() {
		return vPkw;
	}

	/**
	 * Erfragt VLkw.
	 *
	 * @return VLkw.
	 */
	public double getvLkw() {
		return vLkw;
	}

	@Override
	public String toString() {
		String txt = getClass().getSimpleName();

		txt += "["; //$NON-NLS-1$
		txt += "zeit=" + zeit; //$NON-NLS-1$
		txt += ", qLkw=" + qLkw; //$NON-NLS-1$
		txt += ", qKfz=" + qKfz; //$NON-NLS-1$
		txt += ", vPkw=" + vPkw; //$NON-NLS-1$
		txt += ", vLkw=" + vLkw; //$NON-NLS-1$
		txt += "]"; //$NON-NLS-1$

		return txt;
	}

}
