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
package de.bsvrz.iav.gllib.gllib.speicher;

import java.sql.Types;

import org.hibernate.dialect.DerbyDialect;

/**
 * Notwendig zum Lesen und Schreiben von Blobs via Hibernate.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class GlDerbyDialect extends DerbyDialect {

	/**
	 * Standardkonstruktor.
	 */
	public GlDerbyDialect() {
		registerColumnType(Types.BLOB, "blob"); //$NON-NLS-1$
		registerColumnType(Types.CLOB, "clob"); //$NON-NLS-1$
	}

}
