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

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.bsvrz.iav.gllib.gllib.GanglinienOperationen;
import de.bsvrz.sys.funclib.bitctrl.util.Intervall;

/**
 * Helferklasse mit den Operationen auf Messquerschnittsganglinien.
 * <p>
 * <em>Hinweis:</em> Die Ergebnisganglinie bekommt den Messquerschnitt und die
 * Approximation der Ganglinie, die als erster Parameter einer Methode übergeben
 * wird. Alle anderen Eigenschaften, einer Messquerschnittsganglinie, bleiben
 * bei der Ergebnisganglinie uninitialisiert.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GanglinienMQOperationen {

	/**
	 * Addiert zwei Ganglinien, indem die Werte der vervollst&auml;ndigten
	 * St&uuml;tzstellenmenge addiert werden. Die beiden Ganglinien werden dabei
	 * nicht ver&auml;ndert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Die "Summe" der beiden Ganglinien
	 */
	public static GanglinieMQ addiere(GanglinieMQ g1, GanglinieMQ g2) {
		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien müssen zum gleichen Messquerschnitt gehören.";

		GanglinieMQ g;

		g = new GanglinieMQ();
		g.setMessQuerschnitt(g1.getMessQuerschnitt());
		g.setApproximation(g1.getApproximation());

		g.qKfz = GanglinienOperationen.addiere(g1.qKfz, g2.qKfz);
		g.qLkw = GanglinienOperationen.addiere(g1.qLkw, g2.qLkw);
		g.vPkw = GanglinienOperationen.addiere(g1.vPkw, g2.vPkw);
		g.vLkw = GanglinienOperationen.addiere(g1.vLkw, g2.vLkw);

		return g;
	}

	/**
	 * Schneidet ein Intervall aus einer GanglinieMQ heraus. Existieren keine
	 * St&uuml;tzstellen in den Intervallgrenzen, werden an diesen Stellen
	 * mittels Approximation durch Polyline St&uuml;tzstellen hinzugef&uuml;gt.
	 * <p>
	 * <em>Hinweis:</em> Das Intervall wird aus der Ganglinie im Parameter
	 * ausgeschnitten.
	 * 
	 * @param g
	 *            Eine Ganglinie
	 * @param i
	 *            Auszuschneidendes Intervall
	 * @return Der Intervallausschnitt
	 */
	public static GanglinieMQ auschneiden(GanglinieMQ g, Intervall i) {
		g.qKfz = GanglinienOperationen.auschneiden(g.qKfz, i);
		g.qLkw = GanglinienOperationen.auschneiden(g.qLkw, i);
		g.vPkw = GanglinienOperationen.auschneiden(g.vPkw, i);
		g.vLkw = GanglinienOperationen.auschneiden(g.vLkw, i);

		return g;
	}

	/**
	 * Berechnet den Abstand zweier Ganglinien mit Hilfe des
	 * Basisabstandsverfahren.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Abstand nach dem Basisabstandsverfahren
	 */
	public static int basisabstand(GanglinieMQ g1, GanglinieMQ g2) {
		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien müssen zum gleichen Messquerschnitt gehören.";

		int fehlerQKfz, fehlerQLkw, fehlerVPkw, fehlerVLkw;

		fehlerQKfz = GanglinienOperationen.basisabstand(g1.qKfz, g2.qKfz);
		fehlerQLkw = GanglinienOperationen.basisabstand(g1.qLkw, g2.qLkw);
		fehlerVPkw = GanglinienOperationen.basisabstand(g1.vPkw, g2.vPkw);
		fehlerVLkw = GanglinienOperationen.basisabstand(g1.vLkw, g2.vLkw);

		return (fehlerQKfz + fehlerQLkw + fehlerVLkw + fehlerVPkw) / 4;
	}

	/**
	 * Division zweier Ganglinien, indem die Werte der vervollst&auml;ndigten
	 * St&uuml;tzstellenmenge dividiert werden. Die beiden Ganglinien werden
	 * dabei nicht ver&auml;ndert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Das "Produkt" der beiden Ganglinien
	 */
	public static GanglinieMQ dividiere(GanglinieMQ g1, GanglinieMQ g2) {
		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien müssen zum gleichen Messquerschnitt gehören.";

		GanglinieMQ g;

		g = new GanglinieMQ();
		g.setMessQuerschnitt(g1.getMessQuerschnitt());
		g.setApproximation(g1.getApproximation());

		g.qKfz = GanglinienOperationen.dividiere(g1.qKfz, g2.qKfz);
		g.qLkw = GanglinienOperationen.dividiere(g1.qLkw, g2.qLkw);
		g.vPkw = GanglinienOperationen.dividiere(g1.vPkw, g2.vPkw);
		g.vLkw = GanglinienOperationen.dividiere(g1.vLkw, g2.vLkw);

		return g;
	}

	/**
	 * Berechnet den Abstand zweier Ganglinien mit Hilfe des komplexen
	 * Abstandsverfahren. Es werden jeweils die Werte an den Intervallgrenzen
	 * verglichen.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @param intervalle
	 *            Anzahl der zu vergleichenden Intervalle
	 * @return Abstand nach dem komplexen Abstandsverfahren
	 */
	public static int komplexerAbstand(GanglinieMQ g1, GanglinieMQ g2,
			int intervalle) {
		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien müssen zum gleichen Messquerschnitt gehören.";

		int fehlerQKfz, fehlerQLkw, fehlerVPkw, fehlerVLkw;

		fehlerQKfz = GanglinienOperationen.komplexerAbstand(g1.qKfz, g2.qKfz,
				intervalle);
		fehlerQLkw = GanglinienOperationen.komplexerAbstand(g1.qLkw, g2.qLkw,
				intervalle);
		fehlerVPkw = GanglinienOperationen.komplexerAbstand(g1.vPkw, g2.vPkw,
				intervalle);
		fehlerVLkw = GanglinienOperationen.komplexerAbstand(g1.vLkw, g2.vLkw,
				intervalle);

		return (fehlerQKfz + fehlerQLkw + fehlerVLkw + fehlerVPkw) / 4;
	}

	/**
	 * Berechnet den Abstand zweier Ganglinien mit Hilfe des komplexen
	 * Abstandsverfahren. Es werden jeweils die Werte an den Intervallgrenzen
	 * verglichen.
	 * 
	 * @param g1
	 *            Erste Ganglinie.
	 * @param g2
	 *            Zweite Ganglinie.
	 * @param intervallBreite
	 *            die Breite der zu vergleichenden Intervalle.
	 * @return Abstand nach dem komplexen Abstandsverfahren
	 */
	public static int komplexerAbstand(GanglinieMQ g1, GanglinieMQ g2,
			long intervallBreite) {
		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien müssen zum gleichen Messquerschnitt gehören.";

		int fehlerQKfz, fehlerQLkw, fehlerVPkw, fehlerVLkw;

		fehlerQKfz = GanglinienOperationen.komplexerAbstand(g1.qKfz, g2.qKfz,
				intervallBreite);
		fehlerQLkw = GanglinienOperationen.komplexerAbstand(g1.qLkw, g2.qLkw,
				intervallBreite);
		fehlerVPkw = GanglinienOperationen.komplexerAbstand(g1.vPkw, g2.vPkw,
				intervallBreite);
		fehlerVLkw = GanglinienOperationen.komplexerAbstand(g1.vLkw, g2.vLkw,
				intervallBreite);

		return (fehlerQKfz + fehlerQLkw + fehlerVLkw + fehlerVPkw) / 4;
	}

	/**
	 * Multiplikation zweier Ganglinien, indem die Werte der
	 * vervollst&auml;ndigten St&uuml;tzstellenmenge multipliziert werden. Die
	 * beiden Ganglinien werden dabei nicht ver&auml;ndert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Das "Produkt" der beiden Ganglinien
	 */
	public static GanglinieMQ multipliziere(GanglinieMQ g1, GanglinieMQ g2) {
		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien müssen zum gleichen Messquerschnitt gehören.";

		GanglinieMQ g;

		g = new GanglinieMQ();
		g.setMessQuerschnitt(g1.getMessQuerschnitt());
		g.setApproximation(g1.getApproximation());

		g.qKfz = GanglinienOperationen.multipliziere(g1.qKfz, g2.qKfz);
		g.qLkw = GanglinienOperationen.multipliziere(g1.qLkw, g2.qLkw);
		g.vPkw = GanglinienOperationen.multipliziere(g1.vPkw, g2.vPkw);
		g.vLkw = GanglinienOperationen.multipliziere(g1.vLkw, g2.vLkw);

		return g;
	}

	/**
	 * Führt das Pattern-Matching einer Menge von Ganglinien mit einer
	 * Referenzganglinie aus. Ergebnis ist die Ganglinie aus der Menge mit dem
	 * geringsten Abstand zur Referenzganglinie.
	 * 
	 * @param referenz
	 *            die Referenzganglinie.
	 * @param liste
	 *            die Liste von zu vergleichenden Ganglinien.
	 * @param offsetVor
	 *            der Offset, in dem die Ganglinien nach vorn verschoben werden
	 *            kann.
	 * @param offsetNach
	 *            der Offset, in dem die Ganglinien nach hinten verschoben
	 *            werden kann.
	 * @param intervall
	 *            das Intervall, in dem die Ganglinien innerhalb des Offsets
	 *            verschoben werden.
	 * @return der Index der Ganglinie mit dem kleinsten Abstand.
	 */
	public static int patternMatching(GanglinieMQ referenz,
			List<GanglinieMQ> liste, long offsetVor, long offsetNach,
			long intervall) {
		HashMap<Integer, Double> fehler;
		int index;
		long start, ende; // Start und Ende des Pattern-Matching-Intervalls

		fehler = new HashMap<Integer, Double>();
		start = referenz.getIntervall().getStart() - offsetVor;
		ende = referenz.getIntervall().getEnde() + offsetNach;

		// Abstände der Ganglinien bestimmen
		for (int i = 0; i < liste.size(); i++) {
			GanglinieMQ g, ref;
			double abstand;
			int tests;

			ref = referenz.clone();
			GanglinienMQOperationen.verschiebe(ref, -offsetVor);
			g = liste.get(i);
			abstand = 0;
			tests = 0;
			for (long j = start; j <= ende; j += intervall) {
				GanglinienMQOperationen.verschiebe(ref, intervall);
				abstand += basisabstand(ref, g);
				tests++;
			}
			fehler.put(i, abstand / tests);
		}

		// Ganglinie mit dem kleinsten Abstand bestimmen
		index = -1;
		for (Entry<Integer, Double> e : fehler.entrySet()) {
			if (index == -1) {
				index = e.getKey();
			} else {
				if (e.getValue() < fehler.get(index)) {
					index = e.getKey();
				}
			}
		}
		return index;
	}

	/**
	 * Subtraktion zweier Ganglinien, indem die Werte der vervollst&auml;ndigten
	 * St&uuml;tzstellenmenge subtrahiert werden. Die beiden Ganglinien werden
	 * dabei nicht ver&auml;ndert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Die "Differenz" der beiden Ganglinien
	 */
	public static GanglinieMQ subtrahiere(GanglinieMQ g1, GanglinieMQ g2) {
		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien müssen zum gleichen Messquerschnitt gehören.";

		GanglinieMQ g;

		g = new GanglinieMQ();
		g.setMessQuerschnitt(g1.getMessQuerschnitt());
		g.setApproximation(g1.getApproximation());

		g.qKfz = GanglinienOperationen.subtrahiere(g1.qKfz, g2.qKfz);
		g.qLkw = GanglinienOperationen.subtrahiere(g1.qLkw, g2.qLkw);
		g.vPkw = GanglinienOperationen.subtrahiere(g1.vPkw, g2.vPkw);
		g.vLkw = GanglinienOperationen.subtrahiere(g1.vLkw, g2.vLkw);

		return g;
	}

	/**
	 * Verbindet zwei Ganglinien durch Konkatenation. Es werden die
	 * St&uuml;tzstellen beider Ganglinien zu einer neuen Ganglinien
	 * zusammengefasst. Dies ist nur m&ouml;glich, wenn sich die
	 * St&uuml;tzstellenmengen nicht &uuml;berschneiden. Ber&uuml;hren sich die
	 * beiden Ganglinien wird im Ber&uuml;hrungspunkt er Mittelwert der beiden
	 * St&uuml;tzstellen gebildet.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Konkatenation der beiden Ganglinien
	 */
	public static GanglinieMQ verbinde(GanglinieMQ g1, GanglinieMQ g2) {
		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien müssen zum gleichen Messquerschnitt gehören.";

		GanglinieMQ g;

		g = new GanglinieMQ();
		g.setMessQuerschnitt(g1.getMessQuerschnitt());
		g.setApproximation(g1.getApproximation());

		g.qKfz = GanglinienOperationen.verbinde(g1.qKfz, g2.qKfz);
		g.qLkw = GanglinienOperationen.verbinde(g1.qLkw, g2.qLkw);
		g.vPkw = GanglinienOperationen.verbinde(g1.vPkw, g2.vPkw);
		g.vLkw = GanglinienOperationen.verbinde(g1.vLkw, g2.vLkw);

		return g;
	}

	/**
	 * Verschiebt eine GanglinieMQ auf der Zeitachse.
	 * <p>
	 * <em>Hinweis:</em> Es wird die Ganglinie im Parameter verschoben.
	 * 
	 * @param g
	 *            Zu verschiebende Ganglinie
	 * @param offset
	 *            Offset um den die GanglinieMQ verschoben werden soll
	 * @return Die verschobene Ganglinie
	 */
	public static GanglinieMQ verschiebe(GanglinieMQ g, long offset) {
		g.qKfz = GanglinienOperationen.verschiebe(g.qKfz, offset);
		g.qLkw = GanglinienOperationen.verschiebe(g.qLkw, offset);
		g.vPkw = GanglinienOperationen.verschiebe(g.vPkw, offset);
		g.vLkw = GanglinienOperationen.verschiebe(g.vLkw, offset);

		return g;
	}

	/**
	 * Verschmilzt eine Ganglinie mit einer anderen. Dabei wird das gewichtete
	 * arithmetische Mittel der vervollst&auml;ndigten St&uuml;tzstellen
	 * gebildet. Die zweite Ganglinie hat immer das Gewicht 1. Die beiden
	 * Ganglinien werden dabei nicht ver&auml;ndert.
	 * <p>
	 * Die Anzahl der Verschmelzungen und der Zeitpunkt der letzten
	 * Verschmelzungen werden aktualisiert.
	 * 
	 * @param ganglinie
	 *            die Ganglinie mit der verschmolzen wird. Sie hat immer das
	 *            Gewicht 1.
	 * @param historGl
	 *            die historische Ganglinie die verschmolzen wird.
	 * @param gewicht
	 *            das Gewicht der zweiten Ganglinie.
	 * @return das Ergebnis der Verschmelzung.
	 */
	public static GanglinieMQ verschmelze(GanglinieMQ ganglinie,
			GanglinieMQ historGl, long gewicht) {
		assert ganglinie.getMessQuerschnitt().equals(
				historGl.getMessQuerschnitt()) : "Die Ganglinien müssen zum gleichen Messquerschnitt gehören.";

		GanglinieMQ g;

		g = new GanglinieMQ();
		g.setMessQuerschnitt(ganglinie.getMessQuerschnitt());
		g.setApproximation(ganglinie.getApproximation());

		g.qKfz = GanglinienOperationen.verschmelze(ganglinie.qKfz,
				historGl.qKfz, gewicht);
		g.qLkw = GanglinienOperationen.verschmelze(ganglinie.qLkw,
				historGl.qLkw, gewicht);
		g.vPkw = GanglinienOperationen.verschmelze(ganglinie.vPkw,
				historGl.vPkw, gewicht);
		g.vLkw = GanglinienOperationen.verschmelze(ganglinie.vLkw,
				historGl.vLkw, gewicht);

		g.setAnzahlVerschmelzungen(historGl.getAnzahlVerschmelzungen() + 1);
		g.setLetzteVerschmelzung(System.currentTimeMillis());

		return g;
	}

	/**
	 * Konstruktor verstecken.
	 */
	protected GanglinienMQOperationen() {
		// nichts
	}

}
