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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Erstellt ein Backup der Gangliniendatenbank zur Laufzeit.
 *
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 */
public class Backup {

	/** Optionales Kommandozeilenargument für den Datenbankname. */
	private static final String ARG_DATENBANK_NAME = "-name="; //$NON-NLS-1$

	/** Defaultwert für den Datenbankname. */
	private static final String DEFAULT_DATENBANK_NAME = "gldb"; //$NON-NLS-1$

	/** Optionales Kommandozeilenargument für den Datenbanknutzer. */
	private static final String ARG_DATENBANK_NUTZER = "-nutzer="; //$NON-NLS-1$

	/** Defaultwert für den Datenbanknutzer. */
	private static final String DEFAULT_DATENBANK_NUTZER = "derby"; //$NON-NLS-1$

	/** Optionales Kommandozeilenargument für das Datenbankpasswort. */
	private static final String ARG_DATENBANK_PASSWORT = "-pass="; //$NON-NLS-1$

	/** Defaultwert für das Datenbankpasswort. */
	private static final String DEFAULT_DATENBANK_PASSWORT = "derby"; //$NON-NLS-1$

	/**
	 * Startet das Programm.
	 *
	 * @param args
	 *            Kommandozeilenargumente.
	 */
	public static void main(final String args[]) {
		try {
			final String backupverzeichnis = args[0];
			final String hostnameUndPort = args[1];
			String dbName = DEFAULT_DATENBANK_NAME;
			String dbNutzer = DEFAULT_DATENBANK_NUTZER;
			String dbPasswort = DEFAULT_DATENBANK_PASSWORT;
			for (int i = 2; i < args.length; i++) {
				if (args[i].startsWith(ARG_DATENBANK_NAME)) {
					dbName = args[i].substring(ARG_DATENBANK_NAME.length());
				}
				if (args[i].startsWith(ARG_DATENBANK_NUTZER)) {
					dbNutzer = args[i].substring(ARG_DATENBANK_NUTZER.length());
				}
				if (args[i].startsWith(ARG_DATENBANK_PASSWORT)) {
					dbPasswort = args[i]
							.substring(ARG_DATENBANK_PASSWORT.length());
				}
			}

			Class.forName("org.apache.derby.jdbc.ClientDriver"); //$NON-NLS-1$
			final Connection conn = DriverManager.getConnection(
					"jdbc:derby://" //$NON-NLS-1$
							+ hostnameUndPort + "/" + dbName, //$NON-NLS-1$
					dbNutzer, dbPasswort);

			backUpDatabase(conn, backupverzeichnis);
		} catch (final Exception ex) {
			System.out.println("Fehler:\n" + ex.toString() + "\n---"); //$NON-NLS-1$//$NON-NLS-2$
			System.out
					.println("Syntax: java -cp de.bsvrz.iav.gllib-runtime.jar " //$NON-NLS-1$
							+ Backup.class.getCanonicalName()
							+ " <Backup-Verzeichnis> <Hostname:Port> [" //$NON-NLS-1$
							+ ARG_DATENBANK_NAME + "<Datenbankname>] [" //$NON-NLS-1$
							+ ARG_DATENBANK_NUTZER + "Nutzername] [" //$NON-NLS-1$
							+ ARG_DATENBANK_PASSWORT + "Passwort]"); //$NON-NLS-1$
		}
	}

	/**
	 * Fuehrt ein komplettes Online-Backup der uebergebenen Datenbank in das
	 * uebergebene Zielverzeichnis durch. Im Zielverzeichnis steht danach eine
	 * Kopie des originalen Datenbankverzeichnisses.
	 *
	 * @param conn
	 *            Datenbankverbindung.
	 * @param backup
	 *            Backup-Zielverzeichnis.
	 * @throws SQLException
	 *             wird weitergereicht.
	 */
	private static void backUpDatabase(final Connection conn,
			final String backup) throws SQLException {
		final CallableStatement cs = conn
				.prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)"); //$NON-NLS-1$
		cs.setString(1, backup);
		cs.execute();
		cs.close();
		System.out.println("Gangliniendatenbank-Backup gesichert nach: " //$NON-NLS-1$
				+ backup);
	}

}
