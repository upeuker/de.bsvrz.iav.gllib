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

package de.bsvrz.iav.gllib.gllib.events;

import java.util.EventObject;

import de.bsvrz.iav.gllib.gllib.Ganglinie;

/**
 * Ein Ereignis mit Bezug auf eine Ganglinie. Die Quelle aller
 * Ganglinien-Ereignisse ist die betroffene Ganglinie.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GanglinienEvent extends EventObject {

	/** F&uuml;r Serialisierung (und die Warnung zu unterdr&uuml;cken). */
	private static final long serialVersionUID = 1L;

	/**
	 * Erzeugt ein Ereignis mit Bezug zu der angegebenen Ganglinie.
	 * 
	 * @param g
	 *            Die Ganglinie f&uuml;r die das Ereignis gilt
	 */
	public GanglinienEvent(Ganglinie g) {
		super(g);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.util.EventObject#toString()
	 */
	@Override
	public String toString() {
		return "GanglinienEvent=";
	}

}
