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

package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Testet relevante Funktionen der Klasse.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class TestIntervall {

	/**
	 * Testet pro forma die Getter-Methoden.
	 */
	@Test
	public void testGetter() {
		Intervall a;

		a = new Intervall(3, 6);
		assertEquals(3L, a.getStart());
		assertEquals(6L, a.getEnde());
	}

	/**
	 * Wenn Start und Ende vertauscht sind, also Start gr&ouml;&szlig;er als
	 * Ende, dann muss eine Exception geworfen werden.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testKonstruktor() {
		new Intervall(8, 2);
	}

}
