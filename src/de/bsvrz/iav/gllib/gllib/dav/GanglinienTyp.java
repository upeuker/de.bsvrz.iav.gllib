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

import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttGanglinienTyp;

/**
 * Kapselt die im Datenverteiler vorhandenen Ganglinientypen als Enum, um
 * leichter mit Ihnen programmieren zu können.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public enum GanglinienTyp {

	/** Die Ganglinie respräsentiert absolute Werte. */
	Absolut(AttGanglinienTyp.ZUSTAND_0_ABSOLUT),

	/**
	 * Die Ganglinie respräsentiert relative Werte die auf eine absolute
	 * Ganglinie aufaddiert werden.
	 */
	Additiv(AttGanglinienTyp.ZUSTAND_1_ADDITIV),

	/**
	 * Die Ganglinie respräsentiert relative Werte die mit einer absoluten
	 * Ganglinie multipliziert werden.
	 */
	Multiplikativ(AttGanglinienTyp.ZUSTAND_2_MULTIPLIKATIV);

	/**
	 * Bestimmt zu einem Datenverteilerzustand den äquivalenten Wert des Enums.
	 * 
	 * @param typ
	 *            ein Ganglinientyp.
	 * @return der entsprechende Enum-Wert.
	 * @throws NullPointerException
	 *             wenn der Parameter <code>null</code> ist.
	 * @throws IllegalArgumentException
	 *             wenn zu dem Parameter kein Enum-Wert existiert.
	 */
	public static GanglinienTyp valueOf(final AttGanglinienTyp typ) {
		if (typ == null) {
			throw new NullPointerException("Parameter darf nicht null sein");
		}

		if (AttGanglinienTyp.ZUSTAND_0_ABSOLUT.equals(typ)) {
			return Absolut;
		}
		if (AttGanglinienTyp.ZUSTAND_1_ADDITIV.equals(typ)) {
			return Additiv;
		}
		if (AttGanglinienTyp.ZUSTAND_2_MULTIPLIKATIV.equals(typ)) {
			return Multiplikativ;
		}

		throw new IllegalArgumentException("Unbekannter Ganglinientyp: " + typ);
	}

	private final AttGanglinienTyp typ;

	private GanglinienTyp(final AttGanglinienTyp typ) {
		this.typ = typ;
	}

	/**
	 * Gibt den zu dem Enum-Wert äquivalente Datenverteilerzustand zurück.
	 * 
	 * @return das Datenverteilerattribut zu dem Enum-Wert.
	 */
	public AttGanglinienTyp getTyp() {
		return typ;
	}

	/**
	 * Gibt den Namen des entsprechenden Datenverteilerzustands im
	 * Datenverteiler zurück.
	 */
	@Override
	public String toString() {
		return typ.toString();
	}

}
