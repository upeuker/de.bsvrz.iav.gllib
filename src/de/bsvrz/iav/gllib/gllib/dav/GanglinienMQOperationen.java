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

import com.bitctrl.util.Interval;

import de.bsvrz.iav.gllib.gllib.Ganglinie;
import de.bsvrz.iav.gllib.gllib.GanglinienOperationen;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;

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
public final class GanglinienMQOperationen {

	/**
	 * Addiert zwei Ganglinien, indem die Werte der vervollständigten
	 * Stützstellenmenge addiert werden. Die beiden Ganglinien werden dabei
	 * nicht verändert. Die Metainformationen der ersten Ganglinien werden in
	 * die Ergebnisganglinie kopiert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Die "Summe" der beiden Ganglinien
	 */
	public static GanglinieMQ addiere(GanglinieMQ g1, GanglinieMQ g2) {
		final Ganglinie<Double> gQKfz;
		final Ganglinie<Double> gQLkw;
		final Ganglinie<Double> gVPkw;
		final Ganglinie<Double> gVLkw;

		gQKfz = GanglinienOperationen.addiere(g1.getGanglinieQKfz(), g2
				.getGanglinieQKfz());
		gQLkw = GanglinienOperationen.addiere(g1.getGanglinieQLkw(), g2
				.getGanglinieQLkw());
		gVPkw = GanglinienOperationen.addiere(g1.getGanglinieVPkw(), g2
				.getGanglinieVPkw());
		gVLkw = GanglinienOperationen.addiere(g1.getGanglinieVLkw(), g2
				.getGanglinieVLkw());

		return kopiereMetaDaten(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw), g1);
	}

	/**
	 * Schneidet ein Intervall aus einer GanglinieMQ heraus. Existieren keine
	 * Stützstellen in den Intervallgrenzen, werden an diesen Stellen mittels
	 * Approximation durch Polyline Stützstellen hinzugefügt.
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
	public static GanglinieMQ auschneiden(GanglinieMQ g, Interval i) {
		final Ganglinie<Double> gQKfz;
		final Ganglinie<Double> gQLkw;
		final Ganglinie<Double> gVPkw;
		final Ganglinie<Double> gVLkw;

		gQKfz = GanglinienOperationen.auschneiden(g.getGanglinieQKfz(), i);
		gQLkw = GanglinienOperationen.auschneiden(g.getGanglinieQLkw(), i);
		gVPkw = GanglinienOperationen.auschneiden(g.getGanglinieVPkw(), i);
		gVLkw = GanglinienOperationen.auschneiden(g.getGanglinieVLkw(), i);

		g.clear();
		g.putAll(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw));
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
		int fehlerQKfz, fehlerQLkw, fehlerVPkw, fehlerVLkw;

		fehlerQKfz = GanglinienOperationen.basisabstand(g1.getGanglinieQKfz(),
				g2.getGanglinieQKfz());
		fehlerQLkw = GanglinienOperationen.basisabstand(g1.getGanglinieQLkw(),
				g2.getGanglinieQLkw());
		fehlerVPkw = GanglinienOperationen.basisabstand(g1.getGanglinieVPkw(),
				g2.getGanglinieVPkw());
		fehlerVLkw = GanglinienOperationen.basisabstand(g1.getGanglinieVLkw(),
				g2.getGanglinieVLkw());

		return (fehlerQKfz + fehlerQLkw + fehlerVLkw + fehlerVPkw) / 4;
	}

	/**
	 * Division zweier Ganglinien, indem die Werte der vervollständigten
	 * Stützstellenmenge dividiert werden. Die beiden Ganglinien werden dabei
	 * nicht verändert. Die Metainformationen der ersten Ganglinien werden in
	 * die Ergebnisganglinie kopiert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Das "Produkt" der beiden Ganglinien
	 */
	public static GanglinieMQ dividiere(GanglinieMQ g1, GanglinieMQ g2) {
		final Ganglinie<Double> gQKfz;
		final Ganglinie<Double> gQLkw;
		final Ganglinie<Double> gVPkw;
		final Ganglinie<Double> gVLkw;

		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien müssen zum gleichen Messquerschnitt gehören.";

		gQKfz = GanglinienOperationen.dividiere(g1.getGanglinieQKfz(), g2
				.getGanglinieQKfz());
		gQLkw = GanglinienOperationen.dividiere(g1.getGanglinieQLkw(), g2
				.getGanglinieQLkw());
		gVPkw = GanglinienOperationen.dividiere(g1.getGanglinieVPkw(), g2
				.getGanglinieVPkw());
		gVLkw = GanglinienOperationen.dividiere(g1.getGanglinieVLkw(), g2
				.getGanglinieVLkw());

		return kopiereMetaDaten(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw), g1);
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

		fehlerQKfz = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieQKfz(), g2.getGanglinieQKfz(), intervalle);
		fehlerQLkw = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieQLkw(), g2.getGanglinieQLkw(), intervalle);
		fehlerVPkw = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieVPkw(), g2.getGanglinieVPkw(), intervalle);
		fehlerVLkw = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieVLkw(), g2.getGanglinieVLkw(), intervalle);

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
	 * @return Abstand nach dem komplexen Abstandsverfahren in Prozent.
	 */
	public static int komplexerAbstand(GanglinieMQ g1, GanglinieMQ g2,
			long intervallBreite) {
		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien müssen zum gleichen Messquerschnitt gehören.";

		int fehlerQKfz, fehlerQLkw, fehlerVPkw, fehlerVLkw;

		fehlerQKfz = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieQKfz(), g2.getGanglinieQKfz(), intervallBreite);
		fehlerQLkw = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieQLkw(), g2.getGanglinieQLkw(), intervallBreite);
		fehlerVPkw = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieQPkw(), g2.getGanglinieVPkw(), intervallBreite);
		fehlerVLkw = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieQLkw(), g2.getGanglinieVLkw(), intervallBreite);

		return (fehlerQKfz + fehlerQLkw + fehlerVLkw + fehlerVPkw) / 4;
	}

	/**
	 * Kopiert die Metainformationen einer Ganglinie auf eine andere.
	 * <p>
	 * <em>Hinweis:</em> Es wird die Ganglinie im Parameter verändert.
	 * 
	 * @param ziel
	 *            das Ziel der Metadaten.
	 * @param quelle
	 *            die Quelle der Metadaten.
	 * @return die neue Ganglinie.
	 */
	public static GanglinieMQ kopiereMetaDaten(GanglinieMQ ziel,
			GanglinieMQ quelle) {
		ziel.setAnzahlVerschmelzungen(quelle.getAnzahlVerschmelzungen());
		ziel.setApproximationDaK(quelle.getApproximationDaK());
		ziel.setBSplineOrdnung(quelle.getBSplineOrdnung());
		ziel.setEreignisTyp(quelle.getEreignisTyp());
		ziel.setK1(quelle.getK1());
		ziel.setK2(quelle.getK2());
		ziel.setLetzteVerschmelzung(quelle.getLetzteVerschmelzung());
		ziel.setMessQuerschnitt(quelle.getMessQuerschnitt());
		ziel.setPrognoseZeitraum(quelle.getPrognoseIntervall());
		ziel.setReferenz(quelle.isReferenz());
		ziel.setTyp(quelle.getTyp());

		return ziel;
	}

	/**
	 * Multiplikation zweier Ganglinien, indem die Werte der vervollständigten
	 * Stützstellenmenge multipliziert werden. Die beiden Ganglinien werden
	 * dabei nicht verändert. Die Metainformationen der ersten Ganglinien werden
	 * in die Ergebnisganglinie kopiert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Das "Produkt" der beiden Ganglinien
	 */
	public static GanglinieMQ multipliziere(GanglinieMQ g1, GanglinieMQ g2) {
		final Ganglinie<Double> gQKfz;
		final Ganglinie<Double> gQLkw;
		final Ganglinie<Double> gVPkw;
		final Ganglinie<Double> gVLkw;

		gQKfz = GanglinienOperationen.multipliziere(g1.getGanglinieQKfz(), g2
				.getGanglinieQKfz());
		gQLkw = GanglinienOperationen.multipliziere(g1.getGanglinieQLkw(), g2
				.getGanglinieQLkw());
		gVPkw = GanglinienOperationen.multipliziere(g1.getGanglinieVPkw(), g2
				.getGanglinieVPkw());
		gVLkw = GanglinienOperationen.multipliziere(g1.getGanglinieVLkw(), g2
				.getGanglinieVLkw());

		return kopiereMetaDaten(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw), g1);
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
		ende = referenz.getIntervall().getEnd() + offsetNach;

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
	 * Subtraktion zweier Ganglinien, indem die Werte der vervollständigten
	 * Stützstellenmenge subtrahiert werden. Die beiden Ganglinien werden dabei
	 * nicht verändert. Die Metainformationen der ersten Ganglinien werden in
	 * die Ergebnisganglinie kopiert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Die "Differenz" der beiden Ganglinien
	 */
	public static GanglinieMQ subtrahiere(GanglinieMQ g1, GanglinieMQ g2) {
		final Ganglinie<Double> gQKfz;
		final Ganglinie<Double> gQLkw;
		final Ganglinie<Double> gVPkw;
		final Ganglinie<Double> gVLkw;

		gQKfz = GanglinienOperationen.subtrahiere(g1.getGanglinieQKfz(), g2
				.getGanglinieQKfz());
		gQLkw = GanglinienOperationen.subtrahiere(g1.getGanglinieQLkw(), g2
				.getGanglinieQLkw());
		gVPkw = GanglinienOperationen.subtrahiere(g1.getGanglinieVPkw(), g2
				.getGanglinieVPkw());
		gVLkw = GanglinienOperationen.subtrahiere(g1.getGanglinieVLkw(), g2
				.getGanglinieVLkw());

		return kopiereMetaDaten(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw), g1);
	}

	/**
	 * Verbindet zwei Ganglinien durch Konkatenation. Es werden die Stützstellen
	 * beider Ganglinien zu einer neuen Ganglinien zusammengefasst. Dies ist nur
	 * möglich, wenn sich die Stützstellenmengen nicht überschneiden. Berühren
	 * sich die beiden Ganglinien wird im Berührungspunkt er Mittelwert der
	 * beiden Stützstellen gebildet. Die Metainformationen der ersten Ganglinien
	 * werden in die Ergebnisganglinie kopiert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Konkatenation der beiden Ganglinien
	 */
	public static GanglinieMQ verbinde(GanglinieMQ g1, GanglinieMQ g2) {
		final Ganglinie<Double> gQKfz, gQLkw, gVPkw, gVLkw;

		gQKfz = GanglinienOperationen.verbinde(g1.getGanglinieQKfz(), g2
				.getGanglinieQKfz());
		gQLkw = GanglinienOperationen.verbinde(g1.getGanglinieQLkw(), g2
				.getGanglinieQLkw());
		gVPkw = GanglinienOperationen.verbinde(g1.getGanglinieVPkw(), g2
				.getGanglinieVPkw());
		gVLkw = GanglinienOperationen.verbinde(g1.getGanglinieVLkw(), g2
				.getGanglinieVLkw());

		return kopiereMetaDaten(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw), g1);
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
		Ganglinie<Double> gQKfz, gQLkw, gVPkw, gVLkw;

		gQKfz = GanglinienOperationen.verschiebe(g.getGanglinieQKfz(), offset);
		gQLkw = GanglinienOperationen.verschiebe(g.getGanglinieQLkw(), offset);
		gVPkw = GanglinienOperationen.verschiebe(g.getGanglinieVPkw(), offset);
		gVLkw = GanglinienOperationen.verschiebe(g.getGanglinieVLkw(), offset);

		g.clear();
		g.putAll(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw));
		return g;
	}

	/**
	 * Verschmilzt eine Ganglinie mit einer anderen. Dabei wird das gewichtete
	 * arithmetische Mittel der vervollständigten Stützstellen gebildet. Die
	 * zweite Ganglinie hat immer das Gewicht 1. Die beiden Ganglinien werden
	 * dabei nicht verändert.
	 * <p>
	 * Die Anzahl der Verschmelzungen und der Zeitpunkt der letzten
	 * Verschmelzungen werden aktualisiert.
	 * <p>
	 * <em>Hinweis:</em> die historische Ganglinie im Parameter wird geändert.
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
		final long zeitstempel;
		final Ganglinie<Double> gQKfz, gQLkw, gVPkw, gVLkw;

		gQKfz = GanglinienOperationen.verschmelze(ganglinie.getGanglinieQKfz(),
				historGl.getGanglinieQKfz(), gewicht);
		gQLkw = GanglinienOperationen.verschmelze(ganglinie.getGanglinieQLkw(),
				historGl.getGanglinieQLkw(), gewicht);
		gVPkw = GanglinienOperationen.verschmelze(ganglinie.getGanglinieVPkw(),
				historGl.getGanglinieVPkw(), gewicht);
		gVLkw = GanglinienOperationen.verschmelze(ganglinie.getGanglinieVLkw(),
				historGl.getGanglinieVLkw(), gewicht);

		historGl.clear();
		historGl.putAll(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw));
		historGl
				.setAnzahlVerschmelzungen(historGl.getAnzahlVerschmelzungen() + 1);
		if (ObjektFactory.getInstanz().getVerbindung() != null) {
			zeitstempel = ObjektFactory.getInstanz().getVerbindung().getTime();
		} else {
			zeitstempel = System.currentTimeMillis();
		}
		historGl.setLetzteVerschmelzung(zeitstempel);

		return historGl;
	}

	/**
	 * Erzeugt aus den vier Ganglinien eine Messquerschnittsganglinie.
	 * 
	 * @param gQKfz
	 *            die Ganglinie für QKfz.
	 * @param gQLkw
	 *            die Ganglinie für QLkw.
	 * @param gVPkw
	 *            die Ganglinie für VPkw.
	 * @param gVLkw
	 *            die Ganglinie für VLkw.
	 * @return die zusammengeführte Ganglinie.
	 */
	public static GanglinieMQ zusammenfuehren(Ganglinie<Double> gQKfz,
			Ganglinie<Double> gQLkw, Ganglinie<Double> gVPkw,
			Ganglinie<Double> gVLkw) {
		final GanglinieMQ g;

		assert gQKfz.size() == gQLkw.size() && gQLkw.size() == gVPkw.size()
				&& gVPkw.size() == gVLkw.size() : "Die berechneten Stützstellenlisten müssen gleich groß sein.";
		g = new GanglinieMQ();
		for (Long t : gQKfz.keySet()) {
			assert gQKfz.containsKey(t) && gQLkw.containsKey(t)
					&& gVPkw.containsKey(t) && gVLkw.containsKey(t) : "Die Stützstellen mit dem selben Index, müssen den selben Zeitstempel besitzen.";

			g.put(t, new Messwerte(gQKfz.get(t), gQLkw.get(t), gVPkw.get(t),
					gVLkw.get(t)));
		}

		return g;
	}

	/**
	 * Konstruktor verstecken.
	 */
	private GanglinienMQOperationen() {
		// nichts
	}

}
