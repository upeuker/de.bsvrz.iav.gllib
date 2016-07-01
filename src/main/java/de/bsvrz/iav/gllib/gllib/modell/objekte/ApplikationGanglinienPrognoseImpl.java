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

package de.bsvrz.iav.gllib.gllib.modell.objekte;

import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.iav.gllib.gllib.GlLibMsg;
import de.bsvrz.iav.gllib.gllib.modell.GanglinienModellTypen;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractSystemObjekt;
import de.bsvrz.sys.funclib.bitctrl.modell.SystemObjektTyp;

/**
 * Implementiert die Ganglinienprognose und das automatische Ganglinienlernen.
 *
 * @author BitCtrl Systems GmbH, Falko Schumann, Peuker
 */
public class ApplikationGanglinienPrognoseImpl extends AbstractSystemObjekt {

	/**
	 * Erzeugt die Ganglinienprognose aus einem Systemobjekt.
	 *
	 * @param obj
	 *            Ein Systemobjekt, welches die Ganglinienprognose sein muss, in
	 *            der Regel handelt es um die autarke Organisationseinheit.
	 */
	public ApplikationGanglinienPrognoseImpl(final SystemObject obj) {
		super(obj);

		if (!obj.isOfType(getTyp().getPid())) {
			throw new IllegalArgumentException(
					GlLibMsg.IllegalArgumentExceptionKeineGanglinienPrognose
							.toString());
		}
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public SystemObjektTyp getTyp() {
		return GanglinienModellTypen.APPLIKATION_GANGLINIEN_PROGNOSE;
	}

}
