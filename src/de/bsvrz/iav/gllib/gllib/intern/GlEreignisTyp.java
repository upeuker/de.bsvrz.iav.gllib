/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.1 Ganglinienprognose
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

package de.bsvrz.iav.gllib.gllib.intern;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTypParameter;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTypParameterImpl;

/**
 * Erweitert ein Ereignistyp um dessen Priorit&auml;t.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GlEreignisTyp extends EreignisTyp implements EreignisTypParameter {

	/** Die Priori&auml;t des Ereignistyps. */
	private EreignisTypParameter parameter;

	/**
	 * Ruft den Superkonstruktor auf.
	 * 
	 * @param obj
	 *            ein Systemobjekt, welches ein Ereignistyp sein muss.
	 */
	public GlEreignisTyp(SystemObject obj) {
		super(obj);
		parameter = new EreignisTypParameterImpl();
	}

	/**
	 * {@inheritDoc}
	 */
	public long getPrioritaet() {
		return parameter.getPrioritaet();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPrioritaet(long prioritaet) {
		parameter.setPrioritaet(prioritaet);
	}

	/**
	 * Setzt den inneren Zustand anhand des angegebenen Datums.
	 * 
	 * @param daten
	 *            ein g&uuml;ltiges Datum.
	 */
	public void setDaten(Data daten) {
		parameter.setDaten(daten);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[name="
				+ getSystemObject().getName() + ", pid="
				+ getSystemObject().getPid() + ", prioritaet="
				+ parameter.getPrioritaet() + "]";
	}

}
