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
 * Wei�enfelser Stra�e 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.onlinedaten.OdVerkehrsDatenKurzZeitMq;
import de.bsvrz.sys.funclib.bitctrl.test.zufallsdaten.verkehr.VerkehrsDatenKurzZeitMqGenerator;

/**
 * Erzeugt f�r bestimmte Ereignistypen je eine Ganglinie.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public final class GanglinienFactory {

	/**
	 * Die Ereignistypen, f�r die Ganglinien erzeugt werden k�nnen.
	 */
	public enum Typ {

		/** Der Wochentag Montag. */
		Montag("ereignisTyp.montag"),

		/** Der Wochentag Dienstag. */
		Dienstag("ereignisTyp.dienstag"),

		/** Der Wochentag Mittwoch. */
		Mittwoch("ereignisTyp.mittwoch"),

		/** Der Wochentag Donnerstag. */
		Donnerstag("ereignisTyp.donnerstag"),

		/** Der Wochentag Freitag. */
		Freitag("ereignisTyp.freitag"),

		/** Der Wochentag Samstag. */
		Samstag("ereignisTyp.samstag"),

		/** Der Wochentag Sonntag. */
		Sonntag("ereignisTyp.sonntag");

		/** Die Eigenschaft {@code pid}. */
		private String pid;

		/**
		 * Initialisiert das Objekt.
		 * 
		 * @param pid
		 *            die PID des Ereignistyps.
		 */
		private Typ(String pid) {
			this.pid = pid;
		}

		/**
		 * Gibt die PID des Ereignistyps zur�ck.
		 * 
		 * @return die PID.
		 */
		public String getPid() {
			return pid;
		}

	}

	/** Die Eigenschaft {@code singleton}. */
	private static GanglinienFactory singleton;

	/**
	 * Gibt das Singleton der Factory zur�ck.
	 * 
	 * @return die Ganglinienfactory.
	 */
	public static GanglinienFactory getInstance() {
		if (singleton == null) {
			singleton = new GanglinienFactory();
		}
		return singleton;
	}

	/**
	 * Konstruktor verstecken.
	 */
	private GanglinienFactory() {
		// nix
	}

	/**
	 * Erzeugt eine Ganglinien mit zuf�lligen St�tzstellen.
	 * <p>
	 * <em>Hinweis:</em> Die Ganglinie wird keinem Ereignistyp zugeordnet.
	 * Dieser muss nachgetragen werden, bevor die Ganglinien an den
	 * Datenverteiler gesendet werden kann.
	 * 
	 * @param mq
	 *            der Messquerschnitt der Ganglinie.
	 * @param abstand
	 *            der gew�nschte Abstand St�tzstellen in Millisekunden.
	 * @return die generierte Ganglinie.
	 */
	public GanglinieMQ erzeugeGanglinie(MessQuerschnittAllgemein mq,
			long abstand) {
		GanglinieMQ g;
		long t;
		VerkehrsDatenKurzZeitMqGenerator generator;

		g = new GanglinieMQ();
		g.setMessQuerschnitt(mq);
		g.setLetzteVerschmelzung(System.currentTimeMillis());

		t = 0;
		generator = new VerkehrsDatenKurzZeitMqGenerator();
		while (t <= 24 * 60 * 60 * 1000) {
			OdVerkehrsDatenKurzZeitMq.Daten datum;
			Messwerte messwerte;
			Number qKfz, qLkw, vPkw, vLkw;

			datum = generator.generiere();
			qKfz = datum.getWert(OdVerkehrsDatenKurzZeitMq.Daten.Werte.QKfz
					.name());
			qLkw = datum.getWert(OdVerkehrsDatenKurzZeitMq.Daten.Werte.QLkw
					.name());
			vPkw = datum.getWert(OdVerkehrsDatenKurzZeitMq.Daten.Werte.VPkw
					.name());
			vLkw = datum.getWert(OdVerkehrsDatenKurzZeitMq.Daten.Werte.VLkw
					.name());
			messwerte = new Messwerte(qKfz != null ? qKfz.doubleValue() : null,
					qLkw != null ? qLkw.doubleValue() : null,
					vPkw != null ? vPkw.doubleValue() : null,
					vLkw != null ? vLkw.doubleValue() : null);
			g.setStuetzstelle(new Stuetzstelle<Messwerte>(t, messwerte));

			t += abstand;
		}

		return g;
	}

	/**
	 * Erzeugt eine neue Ganglinie f�r den angegebenen Ereignistyp.
	 * 
	 * @todo Die verschiedenen Ganglinien generieren.
	 * @param typ
	 *            ein Ereignistyp.
	 * @param mq
	 *            der Messquerschnitt, f�r den die Ganglinie sein soll.
	 * @return die Ganglinie.
	 * @todo Implementieren.
	 * @deprecated Muss noch implementiert werden!!
	 */
	@Deprecated
	public GanglinieMQ erzeugeGanglinie(Typ typ, MessQuerschnittAllgemein mq) {
		GanglinieMQ g;
		switch (typ) {
		case Montag:
			g = erzeugeMontag();
			break;
		case Dienstag:
			g = erzeugeMontag();
			break;
		case Mittwoch:
			g = erzeugeMontag();
			break;
		case Donnerstag:
			g = erzeugeMontag();
			break;
		case Freitag:
			g = erzeugeMontag();
			break;
		case Samstag:
			g = erzeugeMontag();
			break;
		case Sonntag:
			g = erzeugeMontag();
			break;
		default:
			throw new IllegalStateException("Die Factory kann f�r den Typ '"
					+ typ + "' keine Ganglinie erzeugen.");
		}

		g.setMessQuerschnitt(mq);
		return g;
	}

	/**
	 * Erzeugt eine Ganglinie f�r den Ereignistyp Montag.
	 * 
	 * @todo Implementieren
	 * @return die Ganglinie;
	 */
	private GanglinieMQ erzeugeMontag() {
		GanglinieMQ g;

		g = new GanglinieMQ();

		return g;
	}

}
