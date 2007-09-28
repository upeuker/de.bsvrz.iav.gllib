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
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.intern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.modell.ModellObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.SystemObjekt;
import de.bsvrz.sys.funclib.bitctrl.modell.SystemObjektTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.Ereignis;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.KalenderModellTypen;

/**
 * Fabrikmethode f&uuml;r gekapselte Systemobjekte aus dem Verkehrsmodell. Jedes
 * gekapselte Objekt wird als Singleton behandelt und zwischengespeichert.
 * <p>
 * <em>Hinweis:</em> Diese Klasse ist nicht Teil der öffentlichen API und
 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 */
public final class GanglinienobjektFactory implements ModellObjektFactory {

	/** Globaler Cache f&uuml;r Systemobjekte des Verkehrmodells. */
	private static Map<SystemObject, SystemObjekt> objekte;

	/**
	 * {@inheritDoc}
	 */
	public SystemObjekt getInstanz(SystemObject objekt) {
		if (objekt == null) {
			return null;
		}

		if (objekte == null) {
			objekte = new HashMap<SystemObject, SystemObjekt>();
		}

		// Gesuchtes Objekt im Cache?
		if (objekte.containsKey(objekt)) {
			return objekte.get(objekt);
		}

		// Objekt neu anlegen
		SystemObjekt obj = null;
		if (objekt.isOfType(KalenderModellTypen.EREIGNISTYP.getPid())) {
			obj = new GlEreignisTyp(objekt);
		} else if (objekt.isOfType(KalenderModellTypen.EREIGNIS.getPid())) {
			obj = new Ereignis(objekt);
		}

		if (obj != null) {
			// Nur konkrete Objekte dürfen in den Cache
			objekte.put(objekt, obj);
		}

		return obj;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<SystemObjekt> getInstanzen() {
		return new ArrayList<SystemObjekt>(objekte.values());
	}

	/**
	 * {@inheritDoc}
	 */
	public SystemObjektTyp[] getTypen() {
		return KalenderModellTypen.values();
	}
}
