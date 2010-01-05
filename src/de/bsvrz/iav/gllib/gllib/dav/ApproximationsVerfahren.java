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

import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttGanglinienVerfahren;

/**
 * Kapselt die im Datenverteiler vorhandenen Approximationsverfahren als Enum,
 * um leichter mit Ihnen programmieren zu können.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public enum ApproximationsVerfahren {

	/** Das Approximationsverharen wurde nicht festgelegt. */
	Unbestimmt(AttGanglinienVerfahren.ZUSTAND_0_UNBESTIMMT),

	/** Das Approximationsverfahren B-Spline. */
	BSpline(
			AttGanglinienVerfahren.ZUSTAND_1_B_SPLINE_APPROXIMATION_BELIEBIGER_ORDNUNG),

	/** Das Approximationsverfahren Cubic-Spline. */
	CubicSpline(AttGanglinienVerfahren.ZUSTAND_2_CUBIC_SPLINE_INTERPOLATION),

	/** Das Approximationsverfahren Polyline. */
	Polyline(
			AttGanglinienVerfahren.ZUSTAND_3_POLYLINE_VERFAHREN_LINEARE_INTERPOLATION_);

	/**
	 * Bestimmt zu einem Datenverteilerzustand den äquivalenten Wert des Enums.
	 * 
	 * @param verfahren
	 *            ein Ganglinienverfahren.
	 * @return der entsprechende Enum-Wert.
	 * @throws NullPointerException
	 *             wenn der Parameter <code>null</code> ist.
	 * @throws IllegalArgumentException
	 *             wenn zu dem Parameter kein Enum-Wert existiert.
	 */
	public static ApproximationsVerfahren valueOf(
			final AttGanglinienVerfahren verfahren) {
		if (verfahren == null) {
			throw new NullPointerException("Parameter darf nicht null sein");
		}

		if (AttGanglinienVerfahren.ZUSTAND_0_UNBESTIMMT.equals(verfahren)) {
			return Unbestimmt;
		}
		if (AttGanglinienVerfahren.ZUSTAND_1_B_SPLINE_APPROXIMATION_BELIEBIGER_ORDNUNG
				.equals(verfahren)) {
			return BSpline;
		}
		if (AttGanglinienVerfahren.ZUSTAND_2_CUBIC_SPLINE_INTERPOLATION
				.equals(verfahren)) {
			return CubicSpline;
		}
		if (AttGanglinienVerfahren.ZUSTAND_3_POLYLINE_VERFAHREN_LINEARE_INTERPOLATION_
				.equals(verfahren)) {
			return Polyline;
		}

		throw new IllegalArgumentException(
				"Unbekanntes Approximationsverfahren: " + verfahren);
	}

	private final AttGanglinienVerfahren verfahren;

	private ApproximationsVerfahren(final AttGanglinienVerfahren verfahren) {
		this.verfahren = verfahren;
	}

	/**
	 * Gibt den zu dem Enum-Wert äquivalente Datenverteilerzustand zurück.
	 * 
	 * @return das Datenverteilerattribut zu dem Enum-Wert.
	 */
	public AttGanglinienVerfahren getVerfahren() {
		return verfahren;
	}

	/**
	 * Gibt den Namen des entsprechenden Datenverteilerzustands im
	 * Datenverteiler zurück.
	 */
	@Override
	public String toString() {
		return verfahren.toString();
	}

}
