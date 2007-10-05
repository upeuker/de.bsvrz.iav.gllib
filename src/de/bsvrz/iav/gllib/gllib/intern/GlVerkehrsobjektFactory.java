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
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.intern;

import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.modell.SystemObjekt;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.VerkehrsModellTypen;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.VerkehrsobjektFactory;

/**
 * Fabrikmethode f&uuml;r gekapselte Systemobjekte aus dem Verkehrsmodell. Jedes
 * gekapselte Objekt wird als Singleton behandelt und zwischengespeichert.
 * <p>
 * <em>Hinweis:</em> Diese Klasse ist nicht Teil der ˆffentlichen API und
 * sollte nicht auﬂerhalb der Ganglinie-API verwendet werden.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class GlVerkehrsobjektFactory extends VerkehrsobjektFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SystemObjekt getModellobjekt(SystemObject objekt) {
		if (objekt == null) {
			throw new IllegalArgumentException("Argument darf nicht null sein.");
		}

		SystemObjekt so = null;
		if (objekt.isOfType(VerkehrsModellTypen.MESSQUERSCHNITTALLGEMEIN
				.getPid())) {
			so = new GlMessQuerschnittAllgemein(objekt);
		} else {
			so = super.getModellobjekt(objekt);
		}

		return so;
	}

}
