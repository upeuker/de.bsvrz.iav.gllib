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

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import com.bitctrl.util.Interval;

import de.bsvrz.dav.daf.main.config.ConfigurationChangeException;
import de.bsvrz.sys.funclib.bitctrl.daf.DavTools;
import de.bsvrz.sys.funclib.bitctrl.modell.AnmeldeException;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensendeException;
import de.bsvrz.sys.funclib.bitctrl.modell.DefaultObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.KonfigurationsDatum;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.att.Zeitstempel;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.attribute.AtlVerkehrlicheGueltigkeit;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.attribute.AttEreignisTypPrioritaet;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.konfigurationsdaten.KdEreignisEigenschaften;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.konfigurationsdaten.KdEreignisTypEigenschaften;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.objekte.Ereignis;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.parameter.PdEreignisParameter;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.parameter.PdEreignisTypParameter;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.parameter.PdGanglinienModellAutomatischesLernenEreignis;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttAbstandsMass;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttAnzahlSekunden0Bis;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttAnzahlSekunden1Bis;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttGanglinienTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttGanglinienVerfahren;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttGanzzahl1Bis;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttWichtungsFaktor;
import de.bsvrz.sys.funclib.bitctrl.modell.tmsystemkalenderglobal.objekte.Kalender;
import de.bsvrz.sys.funclib.dynobj.DynObjektException;

/**
 * Liest aus einer Datenbank die notwendigen Daten zum Anlegen von Ereignissen
 * aus und kann diese anlegen.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class EreignisFactory {

	/** Enthält die Spaltennamen der Tabelle. */
	public enum Ereignisse {

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		EREIGNISTYP,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		TAG,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		STARTZEIT,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		ENDZEIT
	}

	/** Enthält die Spaltennamen der Tabelle. */
	public enum Ereignistypen {

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		EREIGNISTYP,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		PRIORITAET,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		AUSSCHLUSS,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		BEZUG,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		TYP,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		VERGLEICHSSCHRITTWEITE,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		MAX_ABSTAND,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		MAX_MATCHINGFEHLER,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		MATCHINGSCHRITTWEITE,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		MATCHINGINTERVALL_VOR,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		MATCHINGINTERVALL_NACH,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		MAX_WICHTUNGSFAKTOR,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		DARSTELLUNGSVERFAHREN,

		/**
		 * Spaltenname {@link #name()} und Spaltenposition {@link #ordinal()}+1.
		 */
		MAX_GANGLINIEN;

	}

	/** Der Standardpräfix ({@value} ) für die PID eines neuen Ereignistyps. */
	public static final String PRAEFIX_PID = "ereignisTyp.";

	/** Der Logger der Klassse. */
	private final Logger log;

	/** Die Verbindung zur Datenbank mit den Testdaten. */
	private final Connection connection;

	/**
	 * Intitialisiert den Datenbankzugriff. Der JDBC-Treiber für HSQLDB wird per
	 * Standard geladen, andere müssen explizit vorher geladen werden.
	 * 
	 * @param url
	 *            die URL der Datenbank in JDBC-Notation.
	 * @param benutzer
	 *            der Benutzername für der Datenbank.
	 * @param kennwort
	 *            das Kennwort des Benutzer.
	 */
	public EreignisFactory(final String url, final String benutzer,
			final String kennwort) {
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
	}

	/**
	 * Legt die Ereignisse für den Test an.
	 * <p>
	 * Die Tabelle mit den Ereignistypen muss folgenden Aufbau besitzen:
	 * 
	 * <pre>
	 * CREATE TABLE ereignisse (
	 *     ereignistyp VARCHAR(50) NOT NULL,
	 *     tag INTEGER NOT NULL,
	 *     startzeit INTEGER DEFAULT 0 NOT NULL,
	 *     endzeit INTEGER DEFAULT 24 NOT NULL,
	 * 
	 *     FOREIGN KEY (ereignistyp) REFERENCES ereignistypen(ereignistyp)
	 * );
	 * </pre>
	 * 
	 * @return die Liste der angelegten Ereignisse.
	 * @throws AnmeldeException
	 *             bei einem Fehler beim Anmelden zu sendender Daten.
	 * @throws DatensendeException
	 *             bei einem Fehler beim Daten senden.
	 * @throws ConfigurationChangeException
	 *             bei einem Fehler beim Ändern der Konfiguration.
	 * @throws SQLException
	 *             bei einem Fehler beim Datenbankzugriff.
	 * @throws DynObjektException
	 */
	public List<Ereignis> anlegenEreignisse()
			throws ConfigurationChangeException, DatensendeException,
			AnmeldeException, SQLException, DynObjektException {
		final ObjektFactory factory;
		final Kalender kalender;
		final Statement stat;
		final ResultSet rs;
		final String sql;
		final List<Ereignis> ereignisse;

		log.info("Erzeuge die Ereignisse für den Test ...");

		factory = DefaultObjektFactory.getInstanz();
		kalender = (Kalender) factory.getModellobjekt(factory.getDav()
				.getLocalConfigurationAuthority());
		stat = connection.createStatement();

		ereignisse = new ArrayList<Ereignis>();

		sql = "SELECT * FROM ereignisse";
		rs = stat.executeQuery(sql);
		while (rs.next()) {
			final EreignisTyp ereignisTyp;
			final Ereignis ereignis;
			final int tag;
			final long start, ende;
			final Interval intervall;
			final Calendar calendar;
			String pid;

			// Ereignistyp bestimmen
			pid = DavTools.generierePID(
					rs.getString(Ereignisse.EREIGNISTYP.name()), PRAEFIX_PID);
			ereignisTyp = (EreignisTyp) factory.getModellobjekt(pid);
			assert ereignisTyp != null : "Der Ereignistyp " + pid
					+ " muss existieren.";

			// Ereignis anlegen
			tag = rs.getInt(Ereignisse.TAG.name());
			pid = DavTools.generierePID(
					rs.getString(Ereignistypen.EREIGNISTYP.name()) + "Tag"
							+ tag, PRAEFIX_PID);
			calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, tag);
			calendar.set(Calendar.HOUR_OF_DAY,
					rs.getInt(Ereignisse.STARTZEIT.name()));
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			start = calendar.getTimeInMillis();
			calendar.set(Calendar.HOUR_OF_DAY,
					rs.getInt(Ereignisse.ENDZEIT.name()));
			ende = calendar.getTimeInMillis();
			intervall = new Interval(start, ende);
			ereignis = anlegenEreignis(kalender, pid, ereignisTyp.getName(),
					"", ereignisTyp, intervall, "");
			ereignisse.add(ereignis);
			log.info("Ereignis " + ereignis + " angelegt.");
		}
		stat.close();
		return ereignisse;
	}

	private Ereignis anlegenEreignis(final Kalender kalender, final String pid,
			final String name, final String beschreibung,
			final EreignisTyp typ, final Interval intervall, final String quelle)
			throws DatensendeException, DynObjektException, AnmeldeException {

		final ObjektFactory factory = DefaultObjektFactory.getInstanz();

		final KdEreignisEigenschaften.Daten eigenschaften = new KdEreignisEigenschaften.Daten(
				new KdEreignisEigenschaften(null, factory),
				KdEreignisEigenschaften.Aspekte.Eigenschaften);
		eigenschaften.setEreignisTypReferenz(typ);
		eigenschaften.setEreignisbeschreibung(beschreibung);

		final Ereignis erg = factory.createDynamischesObjekt(Ereignis.class,
				name, pid, new KonfigurationsDatum[] { eigenschaften });

		kalender.getEreignisse().add(erg);

		final PdEreignisParameter param = erg.getPdEreignisParameter();
		param.anmeldenSender();

		final PdEreignisParameter.Daten datum = param.createDatum();
		datum.setSystemKalenderEintragReferenz(null);
		datum.getVerkehrlicheGueltigkeit()
				.add(new AtlVerkehrlicheGueltigkeit());
		datum.setBeginnZeitlicheGueltigkeit(new Zeitstempel(intervall
				.getStart()));
		datum.setEndeZeitlicheGueltigkeit(new Zeitstempel(intervall.getEnd()));
		datum.setQuelle(quelle);
		param.sendeDatum(datum);

		return erg;
	}

	/**
	 * Legt die Ereignistypen für den Test an. Die Ereignistypen werden aus der
	 * Datenbank aus Tabelle <em>ereignistypen</em> gelesen.
	 * <p>
	 * Die Tabelle mit den Ereignistypen muss folgenden Aufbau besitzen:
	 * 
	 * <pre>
	 * CREATE TABLE ereignistypen (
	 *     ereignistyp VARCHAR(50) NOT NULL,
	 *     prioritaet INTEGER NOT NULL,
	 *     ausschluss VARCHAR(50),
	 *     bezug VARCHAR(50),
	 *     typ INTEGER DEFAULT 0 NOT NULL,
	 *     vergleichsschrittweite BIGINT DEFAULT 60000 NOT NULL,
	 *     max_abstand INTEGER DEFAULT 100 NOT NULL,
	 *     max_matchingfehler INTEGER DEFAULT 15 NOT NULL,
	 *     matchingschrittweite BIGINT DEFAULT 60000 NOT NULL,
	 *     matchingintervall_vor BIGINT DEFAULT 21600000 NOT NULL,
	 *     matchingintervall_nach BIGINT DEFAULT 21600000 NOT NULL,
	 *     max_wichtungsfaktor INTEGER DEFAULT 3 NOT NULL,
	 *     darstellungsverfahren INTEGER DEFAULT 1 NOT NULL,
	 *     max_ganglinien INTEGER DEFAULT 10 NOT NULL,
	 * 
	 *     PRIMARY KEY (ereignistyp),
	 *     FOREIGN KEY (ereignistyp) REFERENCES ereignistypen(ereignistyp),
	 *     FOREIGN KEY (ausschluss) REFERENCES ereignistypen(ereignistyp)
	 * );
	 * </pre>
	 * 
	 * @return die Liste der angelegten Ereignistypen.
	 * @throws AnmeldeException
	 *             bei einem Fehler beim Anmelden zu sendender Daten.
	 * @throws DatensendeException
	 *             bei einem Fehler beim Daten senden.
	 * @throws ConfigurationChangeException
	 *             bei einem Fehler beim Ändern der Konfiguration.
	 * @throws SQLException
	 *             bei einem Fehler beim Datenbankzugriff.
	 * @throws DynObjektException
	 */
	public List<EreignisTyp> anlegenEreignistypen()
			throws ConfigurationChangeException, DatensendeException,
			AnmeldeException, SQLException, DynObjektException {
		final ObjektFactory factory;
		final Kalender kalender;
		final Statement stat;
		final ResultSet rs;
		final String sql;
		final List<EreignisTyp> ereignisTypen;

		log.info("Erzeuge die Ereignistypen für den Test ...");

		factory = DefaultObjektFactory.getInstanz();
		kalender = (Kalender) factory.getModellobjekt(factory.getDav()
				.getLocalConfigurationAuthority());

		stat = connection.createStatement();

		ereignisTypen = new ArrayList<EreignisTyp>();

		sql = "SELECT * FROM ereignistypen";
		rs = stat.executeQuery(sql);
		while (rs.next()) {
			final String name;
			final EreignisTyp ereignisTyp, auschluss, bezug;
			final PdGanglinienModellAutomatischesLernenEreignis param;
			final PdGanglinienModellAutomatischesLernenEreignis.Daten datum;
			String pid;

			name = rs.getString(Ereignistypen.EREIGNISTYP.name());

			// Ereignistyp anlegen
			pid = DavTools
					.generierePID(
							rs.getString(Ereignistypen.EREIGNISTYP.name()),
							PRAEFIX_PID);
			if (factory.getModellobjekt(pid) != null) {
				log.severe("Der Ereignistyp " + pid
						+ " darf für den Test noch nicht existieren.");
				System.exit(-1);
			}

			final KdEreignisTypEigenschaften.Daten typEigenschaften = new KdEreignisTypEigenschaften.Daten(
					new KdEreignisTypEigenschaften(null, factory),
					KdEreignisTypEigenschaften.Aspekte.Eigenschaften);

			ereignisTyp = factory.createDynamischesObjekt(EreignisTyp.class,
					name, pid, new KonfigurationsDatum[] { typEigenschaften });

			final PdEreignisTypParameter typParam = ereignisTyp
					.getPdEreignisTypParameter();
			typParam.anmeldenSender();
			final PdEreignisTypParameter.Daten typDatum = typParam
					.createDatum();
			typDatum.setEreignisTypPrioritaet(new AttEreignisTypPrioritaet(
					(long) rs.getInt(Ereignistypen.PRIORITAET.name())));
			typParam.sendeDatum(typDatum);

			ereignisTypen.add(ereignisTyp);

			// Lernparameter für Ereignistyp setzen
			param = ereignisTyp
					.getPdGanglinienModellAutomatischesLernenEreignis();
			param.anmeldenSender();
			datum = param.createDatum();
			datum.setAlgDarstellungsverfahren(new AttGanglinienVerfahren(
					(byte) rs.getInt(Ereignistypen.DARSTELLUNGSVERFAHREN.name())));
			datum.setAlgGanglinienTyp(new AttGanglinienTyp((byte) rs
					.getInt(Ereignistypen.TYP.name())));
			datum.setAlgMatchingIntervallNach(new AttAnzahlSekunden0Bis(rs
					.getLong(Ereignistypen.MATCHINGINTERVALL_NACH.name())));
			datum.setAlgMatchingIntervallVor(new AttAnzahlSekunden0Bis(rs
					.getLong(Ereignistypen.MATCHINGINTERVALL_VOR.name())));
			datum.setAlgMatchingSchrittweite(new AttAnzahlSekunden1Bis(rs
					.getLong(Ereignistypen.MATCHINGSCHRITTWEITE.name())));
			datum.setAlgMaxAbstand(new AttAbstandsMass((byte) rs
					.getInt(Ereignistypen.MAX_ABSTAND.name())));
			datum.setAlgMaxGanglinien(new AttGanzzahl1Bis((long) rs
					.getInt(Ereignistypen.MAX_GANGLINIEN.name())));
			datum.setAlgMaxMatchingFehler(new AttAbstandsMass((byte) rs
					.getInt(Ereignistypen.MAX_MATCHINGFEHLER.name())));
			datum.setAlgMaxWichtungsfaktor(new AttWichtungsFaktor((short) rs
					.getInt(Ereignistypen.MAX_WICHTUNGSFAKTOR.name())));
			datum.setAlgVergleichsSchrittweite(new AttAnzahlSekunden1Bis(rs
					.getLong(Ereignistypen.VERGLEICHSSCHRITTWEITE.name())));

			pid = rs.getString(Ereignistypen.AUSSCHLUSS.name());
			if (!rs.wasNull()) {
				pid = DavTools.generierePID(pid, PRAEFIX_PID);
				auschluss = (EreignisTyp) factory.getModellobjekt(pid);
				assertNotNull(auschluss);
				datum.getAlgAusschlussliste().add(auschluss);
			}

			pid = rs.getString(Ereignistypen.BEZUG.name());
			if (!rs.wasNull()) {
				pid = DavTools.generierePID(pid, PRAEFIX_PID);
				bezug = (EreignisTyp) factory.getModellobjekt(pid);
				assertNotNull(bezug);
				datum.getAlgBezugsereignistypen().add(bezug);
			}

			kalender.getEreignisTypen().add(ereignisTyp);
			param.sendeDatum(datum);
			log.info("Ereignistyp " + ereignisTyp + " angelegt.");
		}
		stat.close();

		return ereignisTypen;
	}

	/**
	 * Entfernt alle angelegten Ereignise und Ereignistypen wieder. Die
	 * entsprechende Objekte werden aus den Tabellen entnommen.
	 * 
	 * @throws ConfigurationChangeException
	 *             wenn das Löschen der angelegten Ereignisse und Ereignistypen
	 *             fehlschlug.
	 * @throws SQLException
	 *             bei einem Datenbankfehler.
	 * @see #anlegenEreignisse()
	 * @see #anlegenEreignistypen()
	 */
	public void aufraeumen() throws ConfigurationChangeException, SQLException {
		final ObjektFactory factory;
		final Kalender kalender;
		final Statement stat;
		ResultSet rs;
		String sql;

		factory = DefaultObjektFactory.getInstanz();
		kalender = (Kalender) factory.getModellobjekt(factory.getDav()
				.getLocalConfigurationAuthority());

		stat = connection.createStatement();

		log.info("Lösche die für den Test angelegten Ereignisse ...");
		sql = "SELECT * FROM ereignisse";
		rs = stat.executeQuery(sql);
		while (rs.next()) {
			final Ereignis ereignis;
			final String pid;
			final int tag;

			// Ereignistyp bestimmen
			tag = rs.getInt(Ereignisse.TAG.name());
			pid = DavTools.generierePID(
					rs.getString(Ereignisse.EREIGNISTYP.name()) + "Tag" + tag,
					PRAEFIX_PID);
			ereignis = (Ereignis) factory.getModellobjekt(pid);
			if (ereignis != null) {
				kalender.getEreignisse().remove(ereignis);
			}
		}

		log.info("Lösche die für den Test angelegten Ereignistypen ...");
		sql = "SELECT * FROM ereignistypen";
		rs = stat.executeQuery(sql);
		while (rs.next()) {
			final EreignisTyp ereignisTyp;
			final String pid;

			// Ereignistyp bestimmen
			pid = DavTools
					.generierePID(
							rs.getString(Ereignistypen.EREIGNISTYP.name()),
							PRAEFIX_PID);
			ereignisTyp = (EreignisTyp) factory.getModellobjekt(pid);
			if (ereignisTyp != null) {
				kalender.getEreignisTypen().remove(ereignisTyp);
			}
		}
	}

}
