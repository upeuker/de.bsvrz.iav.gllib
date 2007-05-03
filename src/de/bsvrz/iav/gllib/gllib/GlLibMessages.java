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

package de.bsvrz.iav.gllib.gllib;

import java.util.ResourceBundle;

import de.bsvrz.sys.funclib.bitctrl.i18n.MessageHandler;

/**
 * Versorgt das Package {@link de.bsvrz.iav.gllib.gllib}, samt Subpackages, mit
 * lokalisierten Meldungen.
 * 
 * @author BitCtrl, Schumann
 * @version $Id: IavMessage.java 160 2007-02-23 15:09:31Z Schumann $
 */
public enum GlLibMessages implements MessageHandler {

	/** Das Wort "Ganglinie". */
	Ganglinie,

	/** Das Wort "St&uuml;tzstelle". */
	Node,

	/** Anzahl muss >0 sein. Argumente: Anzahl */
	BadCount,

	/** Anfang muss vor Ende des Intervalls liegen. Argumente: Anfang, Ende */
	BadIntervall,

	/** B-Spline-Ordnung muss > 0 sein. Argumente: Ordnung */
	BadBSplineDegree;

	/** Name des Ressource-Bundles. */
	private static final String BUNDLE_NAME = GlLibMessages.class
			.getCanonicalName();

	/** Das Ressource-Bundle. */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.sys.funclib.bitctrl.i18n.MessageHandler#getResourceBundle()
	 */
	public ResourceBundle getResourceBundle() {
		return RESOURCE_BUNDLE;
	}

}
