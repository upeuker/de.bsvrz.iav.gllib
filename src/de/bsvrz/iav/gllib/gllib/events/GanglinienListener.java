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
 * Wei�enfelser Stra�e 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.events;

import java.util.EventListener;

/**
 * Listener der auf die &Auml;nderung einer Ganglinie reagieren will.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public interface GanglinienListener extends EventListener {

	/**
	 * Wird aufgerufen, wenn sich eine Ganglinie ge&auml;ndert hat. Die Quelle
	 * des Ereignisses ist die ge&auml;nderte Ganglinie.
	 * 
	 * @param e
	 *            Das eingetretene Ereignis
	 */
	void ganglinieAktualisiert(GanglinienEvent e);

}
