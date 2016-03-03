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

package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bitctrl.i18n.Messages;

/**
 * Prüft ob alle Nachrichten, die verwendet werden auch in der Property-Datei
 * enthalten sind.
 *
 * @author BitCtrl Systems GmbH, Schumann
 */
@SuppressWarnings("nls")
public class TestMessages {

	/**
	 * Testet ob alle Nachrichten-Konstanten eine Nachricht liefern.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void messages() {
		for (final GlLibMsg e : GlLibMsg.values()) {
			assertEquals(Messages.get(e), e.toString());
		}
	}

	/**
	 * Testet ob alle Nachrichten-Konstanten als Namenspräfix, den angegebenen
	 * Log-Level besitzen.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void messagesLevelKorrekt() {
		for (final GlLibMsg e : GlLibMsg.values()) {
			String prefix, level;

			if (e.getLogLevel() == null) {
				continue;
			}

			level = e.getLogLevel().getName().toLowerCase();
			if (level.equals("severe")) {
				// Umbenennung im Datenverteilerfunktionen rückgängig machen
				level = "error";
			}
			prefix = e.name().substring(0, level.length()).toLowerCase();

			assertEquals(level, prefix);
		}
	}

}
