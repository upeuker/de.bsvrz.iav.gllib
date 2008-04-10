/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2007 BitCtrl Systems GmbH 
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

import static com.bitctrl.Constants.MILLIS_PER_HOUR;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;
import de.bsvrz.iav.gllib.gllib.modell.parameter.PdGanglinie;
import de.bsvrz.sys.funclib.bitctrl.daf.DavTools;
import de.bsvrz.sys.funclib.bitctrl.modell.AnmeldeException;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensendeException;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;

/**
 * Liest aus einer Datenbank die notwendigen Daten zum Anlegen von Ganglinien
 * aus und kann diese anlegen.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class GanglinienFactory {

	/** Enthält die Spaltennamen der Tabelle. */
	public enum Ganglinien {

		/** Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1. */
		ID,

		/** Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1. */
		EREIGNISTYP,

		/** Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1. */
		TYP,

		/** Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1. */
		APPROXIMATION,

		/** Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1. */
		REFERENZ;

	}

	/** Enthält die Spaltennamen der Tabelle. */
	public enum Stuetzstellen {

		/** Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1. */
		GL_ID,

		/** Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1. */
		TAG,

		/** Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1. */
		STUNDE,

		/** Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1. */
		QKFZ,

		/** Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1. */
		QLKW,

		/** Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1. */
		VPKW,

		/** Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1. */
		VLKW;

	}

	/**
	 * Kosntante für den ersten Tag, für den Ganglinien in der Datenbank
	 * gesichert sind.
	 */
	public static final int ERSTER_TAG = Integer.MIN_VALUE;

	/**
	 * Der Logger der Klassse.
	 */
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
	public GanglinienFactory(final MessQuerschnittAllgemein mq,
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
	 * Setzt die Ganglinien am Messquerschnitt. Die Ganglinien werden aus der
	 * Datenbank aus der Tabelle <em>ganglinien</em> und deren Stützstellen
	 * aus der Tabelle <em>stuetzstellen</em> gelesen. Die Ausgangsganglinie
	 * hat die Stützstellen des Tags {@link #ERSTER_TAG}.
	 * <p>
	 * Die Tabelle mit den Gangliniendefinition muss folgenden Aufbau besitzen:
	 * 
	 * <pre>
	 * CREATE TABLE ganglinien (
	 *     id INTEGER NOT NULL,
	 *     ereignistyp VARCHAR(50) NOT NULL,
	 *     typ INTEGER DEFAULT 0 NOT NULL,
	 *     approximation INTEGER DEFAULT 1 NOT NULL,
	 *     referenz BOOLEAN DEFAULT false NOT NULL,
	 * 
	 *     PRIMARY KEY (id),
	 *     FOREIGN KEY (ereignistyp) REFERENCES ereignistypen(ereignistyp)
	 * );
	 * </pre>
	 * 
	 * Die Tabelle mit den Stützstellen muss folgenden Aufbau besitzen:
	 * 
	 * <pre>
	 * CREATE TABLE stuetzstellen (
	 *     gl_id INTEGER NOT NULL,
	 *     tag INTEGER NOT NULL,
	 *     stunde INTEGER NOT NULL,
	 *     qkfz REAL,
	 *     qlkw REAL,
	 *     vpkw REAL,
	 *     vlkw REAL,
	 * 	
	 *     PRIMARY KEY (gl_id, tag, stunde),
	 *     FOREIGN KEY (gl_id) REFERENCES ganglinien(id)
	 * );
	 * </pre>
	 * 
	 * Der Tag ist eine relative Angabe in Bezug auf heute = 0.
	 * 
	 * @param tag
	 *            der Tag dessen Ganglinien verwendet werden sollen: heute 0,
	 *            gestern -1 usw.
	 * @return die Ganglinienliste die angelegt wurde.
	 * @throws SQLException
	 *             bei einem Datenbankfehler.
	 * @throws AnmeldeException
	 *             bei einem Fehler beim Anmelden zu sendender Daten.
	 * @throws DatensendeException
	 *             bei einem Fehler beim Daten senden.
	 * @see #getGanglinienTestdaten(int)
	 */
	public List<GanglinieMQ> anlegenGanglinien(final int tag)
			throws SQLException, AnmeldeException, DatensendeException {
		final PdGanglinie param;
		PdGanglinie.Daten datum;

		param = mq.getParameterDatensatz(PdGanglinie.class);
		param.anmeldenSender();
		datum = param.abrufenDatum();

		if (datum.isValid() && datum.size() > 0) {
			log.severe("Am Testmessquerschnitt " + mq
					+ " sind bereits Ganglinien vorhanden.");
			System.exit(-1);
		}

		datum = getGanglinienTestdaten(tag);
		param.sendeDaten(datum);

		log.info("Ganglinien gesendet.");
		return datum;
	}

	/**
	 * Liest den Sollwert des Ganglinienparameters aus der Datenbank und gibt
	 * ihn zurück.
	 * 
	 * @param tag
	 *            der gesuchte Tag: heute 0, gestern -1 usw.
	 * @return der Sollwert des Ganglinienparameters
	 * @throws SQLException
	 *             bei einem Datenbankfehler.
	 */
	public PdGanglinie.Daten getGanglinienTestdaten(final int tag)
			throws SQLException {
		final ObjektFactory factory;
		final Statement statGl;
		final ResultSet rsGl;
		final PdGanglinie.Daten datum;
		final PdGanglinie glParam;

		String sql;

		factory = ObjektFactory.getInstanz();
		glParam = mq.getParameterDatensatz(PdGanglinie.class);
		datum = glParam.erzeugeDatum();

		statGl = connection.createStatement();
		sql = "SELECT * FROM ganglinien";
		rsGl = statGl.executeQuery(sql);
		while (rsGl.next()) {
			final int id;
			final String pid;
			final EreignisTyp ereignisTyp;
			final Statement statSt;
			final ResultSet rsSt;
			final GanglinieMQ g;
			Integer ersterTag;

			id = rsGl.getInt(Ganglinien.ID.name());
			pid = DavTools.generierePID(rsGl.getString(Ganglinien.EREIGNISTYP
					.name()), EreignisTyp.PRAEFIX_PID);
			ereignisTyp = (EreignisTyp) factory.getModellobjekt(pid);
			assert ereignisTyp != null : "Der Ereignistyp " + pid
					+ " muss existieren.";

			g = new GanglinieMQ();
			g.setMessQuerschnitt(mq);
			g.setEreignisTyp(ereignisTyp);
			g.setTyp(rsGl.getInt(Ganglinien.TYP.name()));
			g.setApproximationDaK(rsGl.getInt(Ganglinien.APPROXIMATION.name()));
			g.setLetzteVerschmelzung(factory.getVerbindung().getTime());
			g.setReferenz(rsGl.getBoolean(Ganglinien.REFERENZ.name()));

			statSt = connection.createStatement();
			if (tag == ERSTER_TAG) {
				sql = "SELECT * FROM stuetzstellen WHERE gl_id=" + id
						+ " ORDER BY tag ASC";
			} else {
				sql = "SELECT * FROM stuetzstellen WHERE gl_id=" + id
						+ " AND tag=" + tag;
			}
			ersterTag = null;
			rsSt = statSt.executeQuery(sql);
			while (rsSt.next()) {
				final long t;
				Double qKfz, qLkw, vPkw, vLkw;

				if (ersterTag == null) {
					ersterTag = rsSt.getInt(Stuetzstellen.TAG.name());
				} else if (rsSt.getInt(Stuetzstellen.TAG.name()) > ersterTag) {
					break;
				}

				t = rsSt.getLong(Stuetzstellen.STUNDE.name()) * MILLIS_PER_HOUR;
				qKfz = rsSt.getDouble(Stuetzstellen.QKFZ.name());
				if (rsSt.wasNull()) {
					qKfz = null;
				}
				qLkw = rsSt.getDouble(Stuetzstellen.QLKW.name());
				if (rsSt.wasNull()) {
					qLkw = null;
				}
				vPkw = rsSt.getDouble(Stuetzstellen.VPKW.name());
				if (rsSt.wasNull()) {
					vPkw = null;
				}
				vLkw = rsSt.getDouble(Stuetzstellen.VLKW.name());
				if (rsSt.wasNull()) {
					vLkw = null;
				}

				g.put(t, new Messwerte(qKfz, qLkw, vPkw, vLkw));
			}
			statSt.close();
			datum.add(g);
		}
		statGl.close();

		if (tag == ERSTER_TAG) {
			log.fine("Ganglinien für den ersten Tag aus Datenbank gelesen.");
		} else {
			log.fine("Ganglinien für den Tag " + tag
					+ " aus Datenbank gelesen.");
		}
		return datum;
	}

	/**
	 * Entfernt alle angelegten Testdaten. Der Parameter mit den Ganglinien wird
	 * auf eine leere Liste von Ganglinien gesetzt.
	 * 
	 * @throws AnmeldeException
	 *             wenn das Anmelden zu Senden fehlschlug.
	 * @throws DatensendeException
	 *             wenn der Parameter mit den Ganglinien nicht gelöscht werden
	 *             konnte.
	 */
	public void aufraeumen() throws AnmeldeException, DatensendeException {
		final PdGanglinie param;
		final PdGanglinie.Daten datum;

		log.info("Lösche die Testganglinien am Messquerschnitt " + mq + " ...");
		param = mq.getParameterDatensatz(PdGanglinie.class);
		param.anmeldenSender();
		datum = param.erzeugeDatum();
		param.sendeDaten(datum);
		param.abmeldenSender();
	}

}
