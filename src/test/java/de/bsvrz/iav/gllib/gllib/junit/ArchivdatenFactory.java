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

package de.bsvrz.iav.gllib.gllib.junit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import de.bsvrz.iav.gllib.gllib.junit.GanglinienFactory.Stuetzstellen;
import de.bsvrz.sys.funclib.bitctrl.modell.AnmeldeException;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensendeException;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.onlinedaten.OdVerkehrsDatenKurzZeitMq;

/**
 * Liest aus einer Datenbank die notwendigen Daten zum Anlegen von Archivdaten
 * aus und kann diese anlegen.
 *
 * @author BitCtrl Systems GmbH, Falko Schumann
 */
@SuppressWarnings("nls")
public class ArchivdatenFactory {

	/** Enthält die Spaltennamen der Tabelle. */
	public enum Archivdaten {

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		TAG,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		STUNDE,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		QKFZ,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		QLKW,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		VPKW,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		VLKW;

	}

	/** Der Logger der Klassse. */
	private final Logger log;

	/** Die Verbindung zur Datenbank mit den Testdaten. */
	private final Connection connection;

	/**
	 * Der Messquerschnitt dessen Archivdaten geschrieben werden sollen.
	 */
	private final MessQuerschnittAllgemein mq;

	/**
	 * Intitialisiert den Datenbankzugriff. Der JDBC-Treiber für HSQLDB wird per
	 * Standard geladen, andere müssen explizit vorher geladen werden.
	 *
	 * @param mq
	 *            der Messquerschnitt dessen Archivdaten geschrieben werden
	 *            sollen.
	 * @param url
	 *            die URL der Datenbank in JDBC-Notation.
	 * @param benutzer
	 *            der Benutzername für der Datenbank.
	 * @param kennwort
	 *            das Kennwort des Benutzer.
	 */
	public ArchivdatenFactory(final MessQuerschnittAllgemein mq,
			final String url, final String benutzer, final String kennwort) {
		log = Logger.getLogger(getClass().getName());

		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (final ClassNotFoundException ex) {
			throw new IllegalStateException("Datenbanktreiber nicht gefunden.");
		}

		try {
			connection = DriverManager.getConnection(url, benutzer, kennwort);
		} catch (final SQLException ex) {
			throw new IllegalStateException(
					"Kann keine Verbindung zur Datenbank aufbauen: "
							+ ex.getLocalizedMessage());
		}

		this.mq = mq;
	}

	/**
	 * Schreibt die benötigten Testdaten ins Archiv.
	 * <p>
	 * Die Tabelle mit den Ereignistypen muss folgenden Aufbau besitzen:
	 *
	 * <pre>
	 * CREATE TABLE archivdaten (
	 *     tag INTEGER NOT NULL,
	 *     stunde INTEGER NOT NULL,
	 *     qkfz REAL,
	 *     qlkw REAL,
	 *     vpkw REAL,
	 *     vlkw REAL
	 * );
	 * </pre>
	 *
	 * @return die Liste den den archivierten Daten.
	 * @throws SQLException
	 *             bei einem Datenbankfehler.
	 * @throws AnmeldeException
	 *             bei einem Fehler beim Anmelden zu sendender Daten.
	 * @throws DatensendeException
	 *             bei einem Fehler beim Daten senden.
	 */
	public List<OdVerkehrsDatenKurzZeitMq.Daten> anlegenArchivdaten()
			throws AnmeldeException, SQLException, DatensendeException {
		final List<OdVerkehrsDatenKurzZeitMq.Daten> archivdaten;
		final OdVerkehrsDatenKurzZeitMq onlinedaten;
		final Statement stat;
		final ResultSet rs;
		String sql;

		onlinedaten = mq.getOnlineDatensatz(OdVerkehrsDatenKurzZeitMq.class);
		onlinedaten.setQuelle(
				OdVerkehrsDatenKurzZeitMq.Aspekte.Analyse.getAspekt(), true);
		onlinedaten.anmeldenSender(
				OdVerkehrsDatenKurzZeitMq.Aspekte.Analyse.getAspekt());

		archivdaten = new ArrayList<OdVerkehrsDatenKurzZeitMq.Daten>();

		stat = connection.createStatement();
		sql = "SELECT * FROM archivdaten ORDER BY tag, stunde";
		rs = stat.executeQuery(sql);
		while (rs.next()) {
			final Calendar cal;
			final OdVerkehrsDatenKurzZeitMq.Daten datum;
			Double qKfz, qLkw, vPkw, vLkw;

			datum = onlinedaten.erzeugeDatum();

			// Zeitstempel bestimmen
			cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, rs.getInt(Archivdaten.TAG.name()));
			cal.set(Calendar.HOUR_OF_DAY, rs.getInt(Archivdaten.STUNDE.name()));
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			datum.setZeitstempel(cal.getTimeInMillis());

			// Messwerte aus Datenbank lesen
			qKfz = rs.getDouble(Stuetzstellen.QKFZ.name());
			if (rs.wasNull()) {
				qKfz = null;
			}
			datum.setWert(OdVerkehrsDatenKurzZeitMq.Daten.Werte.QKfz.name(),
					qKfz);
			qLkw = rs.getDouble(Stuetzstellen.QLKW.name());
			if (rs.wasNull()) {
				qLkw = null;
			}
			datum.setWert(OdVerkehrsDatenKurzZeitMq.Daten.Werte.QLkw.name(),
					qLkw);
			vPkw = rs.getDouble(Stuetzstellen.VPKW.name());
			if (rs.wasNull()) {
				vPkw = null;
			}
			datum.setWert(OdVerkehrsDatenKurzZeitMq.Daten.Werte.VPkw.name(),
					vPkw);
			vLkw = rs.getDouble(Stuetzstellen.VLKW.name());
			if (rs.wasNull()) {
				vLkw = null;
			}
			datum.setWert(OdVerkehrsDatenKurzZeitMq.Daten.Werte.VLkw.name(),
					vLkw);

			// Datensatz senden
			archivdaten.add(datum);
			log.info("Sende Archivdaten für "
					+ DateFormat.getDateTimeInstance().format(cal.getTime()));
			onlinedaten.sendeDaten(
					OdVerkehrsDatenKurzZeitMq.Aspekte.Analyse.getAspekt(),
					datum);
		}
		onlinedaten.abmeldenSender(
				OdVerkehrsDatenKurzZeitMq.Aspekte.Analyse.getAspekt());

		return archivdaten;
	}

}
