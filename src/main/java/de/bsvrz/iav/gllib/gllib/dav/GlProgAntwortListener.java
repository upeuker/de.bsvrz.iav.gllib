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

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.EventListener;

/**
 * Schnittsstelle die anfragende Applikationen implementieren müssen, um über
 * das Eintreffen der Prognoseantwort informiert zu werden.
 *
 * @author BitCtrl Systems GmbH, Schumann
 */
public interface GlProgAntwortListener extends EventListener {

	/**
	 * Wird ausgelößt, wenn die Antwort auf eine Prognoseanfrage eingetroffen
	 * ist.
	 *
	 * @param e
	 *            Das Event, welches die Antwort darstellt.
	 */
	void antwortEingetroffen(GlProgAntwortEvent e);

}
