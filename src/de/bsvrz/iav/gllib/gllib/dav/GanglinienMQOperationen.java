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

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.bitctrl.util.Interval;
import com.bitctrl.util.Timestamp;

import de.bsvrz.iav.gllib.gllib.Ganglinie;
import de.bsvrz.iav.gllib.gllib.GanglinienOperationen;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;

/**
 * Helferklasse mit den Operationen auf Messquerschnittsganglinien.
 * <p>
 * <em>Hinweis:</em> Die Ergebnisganglinie bekommt den Messquerschnitt und die
 * Approximation der Ganglinie, die als erster Parameter einer Methode �bergeben
 * wird. Alle anderen Eigenschaften, einer Messquerschnittsganglinie, bleiben
 * bei der Ergebnisganglinie uninitialisiert.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 * @todo Parameter defLueckenSchliessen wieder entfernen, au�er beim
 *       Verschmelzen
 */
public final class GanglinienMQOperationen {

	/**
	 * Konvertiert eine Ganglinie in einen lesbaren Text. Dazu werden die
	 * St�tzstelle in einer einfachen Texttabelle ausgegeben. Enth�lt die
	 * Ganglinie keine St�tzstellen wird eine kurze Notiz ausgegeben.
	 * 
	 * @param g
	 *            eine Ganglinie.
	 * @return ein String der jede St�tzstelle auf eine eigene Zeile schreibt.
	 */
	public static String formatierterText(final GanglinieMQ g) {
		String txt;

		txt = "Messquerschnitt: " + g.getMessQuerschnitt();
		txt += "\nEreignistyp: " + g.getEreignisTyp();
		txt += "\nTyp: ";
		switch (g.getTyp()) {
		case GanglinieMQ.TYP_ABSOLUT:
			txt += "absolute Ganglinie";
			break;
		case GanglinieMQ.TYP_ADDITIV:
			txt += "relative Ganglinie (additiv)";
			break;
		case GanglinieMQ.TYP_MULTIPLIKATIV:
			txt += "relative Ganglinie (multiplikativ)";
			break;
		default:
			txt += "fehlerhafte Angabe";
			break;
		}
		txt += "\nAnzahl Verschmelzungen: " + g.getAnzahlVerschmelzungen();
		txt += "\nLetzte Verschmelzung: "
				+ Timestamp.absoluteTime(g.getLetzteVerschmelzung());
		txt += "\nReferenzganglinie: " + g.isReferenz();
		txt += "\nIntervall: " + g.getIntervall();
		txt += "\nApproximation: ";
		switch (g.getApproximationDaK()) {
		case GanglinieMQ.APPROX_BSPLINE:
			txt += "B-Spline, Ordnung " + g.getBSplineOrdnung();
			break;
		case GanglinieMQ.APPROX_CUBICSPLINE:
			txt += "Cubic-Spline";
			break;
		case GanglinieMQ.APPROX_POLYLINE:
			txt += "Polyline";
			break;
		case GanglinieMQ.APPROX_UNBESTIMMT:
			txt += "unbestimmt";
			break;
		default:
			txt += "fehlerhafte Angabe";
			break;
		}

		if (g.size() == 0) {
			txt += "\nKeine St�tzstellen vorhanden.";
		} else {
			txt += "\nZeitpunkt\t\tQKfz\tQLkw\tVPkw\tVLkw\n";
			for (final Stuetzstelle<Messwerte> s : g.getStuetzstellen()) {
				txt += Timestamp.absoluteTime(s.getZeitstempel()) + "\t";
				txt += s.getWert().getQKfz() + "\t";
				txt += s.getWert().getQLkw() + "\t";
				txt += s.getWert().getVPkw() + "\t";
				txt += s.getWert().getVLkw() + "\n";
			}
		}

		return txt;
	}

	/**
	 * Addiert zwei Ganglinien, indem die Werte der vervollst�ndigten
	 * St�tzstellenmenge addiert werden. Die beiden Ganglinien werden dabei
	 * nicht ver�ndert. Die Metainformationen der ersten Ganglinien werden in
	 * die Ergebnisganglinie kopiert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @param defLueckenSchliessen
	 *            wenn {@code true}, dann wird bei nur einem undefinierten
	 *            Operanden der definierte Operand als Ergebnis angenommen. Wenn
	 *            {@code false} dann ist das Ergebnis ebenfalls undefiniert.
	 * @return Die "Summe" der beiden Ganglinien
	 */
	public static GanglinieMQ addiere(final GanglinieMQ g1,
			final GanglinieMQ g2, final boolean defLueckenSchliessen) {
		final Ganglinie<Double> gQKfz;
		final Ganglinie<Double> gQLkw;
		final Ganglinie<Double> gVPkw;
		final Ganglinie<Double> gVLkw;

		gQKfz = GanglinienOperationen.addiere(g1.getGanglinieQKfz(), g2
				.getGanglinieQKfz(), defLueckenSchliessen);
		gQLkw = GanglinienOperationen.addiere(g1.getGanglinieQLkw(), g2
				.getGanglinieQLkw(), defLueckenSchliessen);
		gVPkw = GanglinienOperationen.addiere(g1.getGanglinieVPkw(), g2
				.getGanglinieVPkw(), defLueckenSchliessen);
		gVLkw = GanglinienOperationen.addiere(g1.getGanglinieVLkw(), g2
				.getGanglinieVLkw(), defLueckenSchliessen);

		return kopiereMetaDaten(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw), g1);
	}

	/**
	 * Schneidet ein Intervall aus einer GanglinieMQ heraus. Existieren keine
	 * St�tzstellen in den Intervallgrenzen, werden an diesen Stellen mittels
	 * Approximation durch Polyline St�tzstellen hinzugef�gt.
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
	public static GanglinieMQ auschneiden(final GanglinieMQ g, final Interval i) {
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
	public static int basisabstand(final GanglinieMQ g1, final GanglinieMQ g2) {
		double fehlerQKfz, fehlerQLkw, fehlerVPkw, fehlerVLkw;

		fehlerQKfz = GanglinienOperationen.basisabstand(g1.getGanglinieQKfz(),
				g2.getGanglinieQKfz());
		fehlerQLkw = GanglinienOperationen.basisabstand(g1.getGanglinieQLkw(),
				g2.getGanglinieQLkw());
		fehlerVPkw = GanglinienOperationen.basisabstand(g1.getGanglinieVPkw(),
				g2.getGanglinieVPkw());
		fehlerVLkw = GanglinienOperationen.basisabstand(g1.getGanglinieVLkw(),
				g2.getGanglinieVLkw());

		return (int) ((fehlerQKfz + fehlerQLkw + fehlerVLkw + fehlerVPkw) / 4);
	}

	/**
	 * Division zweier Ganglinien, indem die Werte der vervollst�ndigten
	 * St�tzstellenmenge dividiert werden. Die beiden Ganglinien werden dabei
	 * nicht ver�ndert. Die Metainformationen der ersten Ganglinien werden in
	 * die Ergebnisganglinie kopiert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @param defLueckenSchliessen
	 *            wenn {@code true}, dann wird bei nur einem undefinierten
	 *            Operanden der definierte Operand als Ergebnis angenommen. Wenn
	 *            {@code false} dann ist das Ergebnis ebenfalls undefiniert.
	 * @return Das "Produkt" der beiden Ganglinien
	 */
	public static GanglinieMQ dividiere(final GanglinieMQ g1,
			final GanglinieMQ g2, final boolean defLueckenSchliessen) {
		final Ganglinie<Double> gQKfz;
		final Ganglinie<Double> gQLkw;
		final Ganglinie<Double> gVPkw;
		final Ganglinie<Double> gVLkw;

		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien m�ssen zum gleichen Messquerschnitt geh�ren.";

		gQKfz = GanglinienOperationen.dividiere(g1.getGanglinieQKfz(), g2
				.getGanglinieQKfz(), defLueckenSchliessen);
		gQLkw = GanglinienOperationen.dividiere(g1.getGanglinieQLkw(), g2
				.getGanglinieQLkw(), defLueckenSchliessen);
		gVPkw = GanglinienOperationen.dividiere(g1.getGanglinieVPkw(), g2
				.getGanglinieVPkw(), defLueckenSchliessen);
		gVLkw = GanglinienOperationen.dividiere(g1.getGanglinieVLkw(), g2
				.getGanglinieVLkw(), defLueckenSchliessen);

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
	public static int komplexerAbstand(final GanglinieMQ g1,
			final GanglinieMQ g2, final int intervalle) {
		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien m�ssen zum gleichen Messquerschnitt geh�ren.";

		double fehlerQKfz, fehlerQLkw, fehlerVPkw, fehlerVLkw;

		fehlerQKfz = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieQKfz(), g2.getGanglinieQKfz(), intervalle);
		fehlerQLkw = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieQLkw(), g2.getGanglinieQLkw(), intervalle);
		fehlerVPkw = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieVPkw(), g2.getGanglinieVPkw(), intervalle);
		fehlerVLkw = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieVLkw(), g2.getGanglinieVLkw(), intervalle);

		return (int) ((fehlerQKfz + fehlerQLkw + fehlerVLkw + fehlerVPkw) / 4);
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
	public static int komplexerAbstand(final GanglinieMQ g1,
			final GanglinieMQ g2, final long intervallBreite) {
		assert g1.getMessQuerschnitt().equals(g2.getMessQuerschnitt()) : "Die Ganglinien m�ssen zum gleichen Messquerschnitt geh�ren.";

		double fehlerQKfz, fehlerQLkw, fehlerVPkw, fehlerVLkw;

		fehlerQKfz = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieQKfz(), g2.getGanglinieQKfz(), intervallBreite);
		fehlerQLkw = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieQLkw(), g2.getGanglinieQLkw(), intervallBreite);
		fehlerVPkw = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieQPkw(), g2.getGanglinieVPkw(), intervallBreite);
		fehlerVLkw = GanglinienOperationen.komplexerAbstand(g1
				.getGanglinieQLkw(), g2.getGanglinieVLkw(), intervallBreite);

		return (int) ((fehlerQKfz + fehlerQLkw + fehlerVLkw + fehlerVPkw) / 4);
	}

	/**
	 * Kopiert die Metainformationen einer Ganglinie auf eine andere.
	 * <p>
	 * <em>Hinweis:</em> Es wird die Ganglinie im Parameter ver�ndert.
	 * 
	 * @param ziel
	 *            das Ziel der Metadaten.
	 * @param quelle
	 *            die Quelle der Metadaten.
	 * @return die neue Ganglinie.
	 */
	public static GanglinieMQ kopiereMetaDaten(final GanglinieMQ ziel,
			final GanglinieMQ quelle) {
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
	 * Multiplikation zweier Ganglinien, indem die Werte der vervollst�ndigten
	 * St�tzstellenmenge multipliziert werden. Die beiden Ganglinien werden
	 * dabei nicht ver�ndert. Die Metainformationen der ersten Ganglinien werden
	 * in die Ergebnisganglinie kopiert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @param defLueckenSchliessen
	 *            wenn {@code true}, dann wird bei nur einem undefinierten
	 *            Operanden der definierte Operand als Ergebnis angenommen. Wenn
	 *            {@code false} dann ist das Ergebnis ebenfalls undefiniert.
	 * @return Das "Produkt" der beiden Ganglinien
	 */
	public static GanglinieMQ multipliziere(final GanglinieMQ g1,
			final GanglinieMQ g2, final boolean defLueckenSchliessen) {
		final Ganglinie<Double> gQKfz;
		final Ganglinie<Double> gQLkw;
		final Ganglinie<Double> gVPkw;
		final Ganglinie<Double> gVLkw;

		gQKfz = GanglinienOperationen.multipliziere(g1.getGanglinieQKfz(), g2
				.getGanglinieQKfz(), defLueckenSchliessen);
		gQLkw = GanglinienOperationen.multipliziere(g1.getGanglinieQLkw(), g2
				.getGanglinieQLkw(), defLueckenSchliessen);
		gVPkw = GanglinienOperationen.multipliziere(g1.getGanglinieVPkw(), g2
				.getGanglinieVPkw(), defLueckenSchliessen);
		gVLkw = GanglinienOperationen.multipliziere(g1.getGanglinieVLkw(), g2
				.getGanglinieVLkw(), defLueckenSchliessen);

		return kopiereMetaDaten(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw), g1);
	}

	/**
	 * Normiert die St�tzstellen einer Ganglinie. Mehrere hintereinander
	 * folgende undefinierte St�tzstellen werden zusammengefasst. Der Abstand
	 * der St�tzstellen wird auf ein definiertes Intervall normiert. Der
	 * Intervallzyklus beginnt mit dem Zeitstempel der ersten St�tzstelle.
	 * <p>
	 * <em>Hinweis:</em> die Ganglinie im Parameter wird ver�ndert.
	 * 
	 * @param g
	 *            eine Ganglinie.
	 * @param abstand
	 *            der gew�nschte St�tzstellenabstand.
	 * @return die normierte Ganglinie.
	 */
	public static GanglinieMQ normiere(final GanglinieMQ g, final long abstand) {
		Ganglinie<Double> gQKfz, gQLkw, gVPkw, gVLkw;

		gQKfz = GanglinienOperationen.normiere(g.getGanglinieQKfz(), abstand);
		gQLkw = GanglinienOperationen.normiere(g.getGanglinieQLkw(), abstand);
		gVPkw = GanglinienOperationen.normiere(g.getGanglinieVPkw(), abstand);
		gVLkw = GanglinienOperationen.normiere(g.getGanglinieVLkw(), abstand);

		g.clear();
		g.putAll(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw));
		return g;
	}

	/**
	 * F�hrt das Pattern-Matching einer Menge von Ganglinien mit einer
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
	 * @param maxFehler
	 *            der maximal erlaubte Fehler.
	 * @return der Index der Ganglinie mit dem kleinsten Abstand oder {@code -1},
	 *         wenn der kleinste Abstand gr��er als {@code maxFehler} ist.
	 */
	public static int patternMatching(final GanglinieMQ referenz,
			final List<GanglinieMQ> liste, long offsetVor,
			final long offsetNach, final long intervall, final int maxFehler) {
		HashMap<Integer, Double> fehler;
		int index;
		long start, ende; // Start und Ende des Pattern-Matching-Intervalls

		fehler = new HashMap<Integer, Double>();
		start = referenz.getIntervall().getStart() - offsetVor;
		ende = referenz.getIntervall().getEnd() + offsetNach;

		// Abst�nde der Ganglinien bestimmen
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
		for (final Entry<Integer, Double> e : fehler.entrySet()) {
			if (index == -1) {
				index = e.getKey();
			} else {
				if (e.getValue() < fehler.get(index)) {
					index = e.getKey();
				}
			}
		}
		if (fehler.get(index) > maxFehler) {
			return -1;
		}
		return index;
	}

	/**
	 * Subtraktion zweier Ganglinien, indem die Werte der vervollst�ndigten
	 * St�tzstellenmenge subtrahiert werden. Die beiden Ganglinien werden dabei
	 * nicht ver�ndert. Die Metainformationen der ersten Ganglinien werden in
	 * die Ergebnisganglinie kopiert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @param defLueckenSchliessen
	 *            wenn {@code true}, dann wird bei nur einem undefinierten
	 *            Operanden der definierte Operand als Ergebnis angenommen. Wenn
	 *            {@code false} dann ist das Ergebnis ebenfalls undefiniert.
	 * @return Die "Differenz" der beiden Ganglinien
	 */
	public static GanglinieMQ subtrahiere(final GanglinieMQ g1,
			final GanglinieMQ g2, final boolean defLueckenSchliessen) {
		final Ganglinie<Double> gQKfz;
		final Ganglinie<Double> gQLkw;
		final Ganglinie<Double> gVPkw;
		final Ganglinie<Double> gVLkw;

		gQKfz = GanglinienOperationen.subtrahiere(g1.getGanglinieQKfz(), g2
				.getGanglinieQKfz(), defLueckenSchliessen);
		gQLkw = GanglinienOperationen.subtrahiere(g1.getGanglinieQLkw(), g2
				.getGanglinieQLkw(), defLueckenSchliessen);
		gVPkw = GanglinienOperationen.subtrahiere(g1.getGanglinieVPkw(), g2
				.getGanglinieVPkw(), defLueckenSchliessen);
		gVLkw = GanglinienOperationen.subtrahiere(g1.getGanglinieVLkw(), g2
				.getGanglinieVLkw(), defLueckenSchliessen);

		return kopiereMetaDaten(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw), g1);
	}

	/**
	 * Verbindet zwei Ganglinien durch Konkatenation. Es werden die St�tzstellen
	 * beider Ganglinien zu einer neuen Ganglinien zusammengefasst. Dies ist nur
	 * m�glich, wenn sich die St�tzstellenmengen nicht �berschneiden. Ber�hren
	 * sich die beiden Ganglinien wird im Ber�hrungspunkt er Mittelwert der
	 * beiden St�tzstellen gebildet. Die Metainformationen der ersten Ganglinien
	 * werden in die Ergebnisganglinie kopiert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Konkatenation der beiden Ganglinien
	 */
	public static GanglinieMQ verbinde(final GanglinieMQ g1,
			final GanglinieMQ g2) {
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
	public static GanglinieMQ verschiebe(final GanglinieMQ g, final long offset) {
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
	 * Verschiebt eine Ganglinie auf der Zeitachse um ein halbes
	 * St�tzstellenintervall. Jede St�tzstelle wird um den halben Abstand zur
	 * n�chsten St�tzstelle verschoben. Die letzte St�tzstelle wird um den
	 * halben Abstand zur vorherigen St�tzstelle verschoben. Gibt es nur eine
	 * St�tzstelle, wird diese nicht verschoben.
	 * <p>
	 * <em>Hinweis:</em> Es wird die Ganglinie im Parameter verschoben.
	 * 
	 * @param g
	 *            Zu verschiebende Ganglinie
	 * @return Die verschobene Ganglinie
	 */
	public static GanglinieMQ verschiebeUmHalbesIntervall(final GanglinieMQ g) {
		Ganglinie<Double> gQKfz, gQLkw, gVPkw, gVLkw;

		gQKfz = GanglinienOperationen.verschiebeUmHalbesIntervall(g
				.getGanglinieQKfz());
		gQLkw = GanglinienOperationen.verschiebeUmHalbesIntervall(g
				.getGanglinieQLkw());
		gVPkw = GanglinienOperationen.verschiebeUmHalbesIntervall(g
				.getGanglinieVPkw());
		gVLkw = GanglinienOperationen.verschiebeUmHalbesIntervall(g
				.getGanglinieVLkw());

		g.clear();
		g.putAll(zusammenfuehren(gQKfz, gQLkw, gVPkw, gVLkw));
		return g;
	}

	/**
	 * Verschmilzt eine Ganglinie mit einer anderen. Dabei wird das gewichtete
	 * arithmetische Mittel der vervollst�ndigten St�tzstellen gebildet. Die
	 * zweite Ganglinie hat immer das Gewicht 1. Die beiden Ganglinien werden
	 * dabei nicht ver�ndert.
	 * <p>
	 * Die Anzahl der Verschmelzungen und der Zeitpunkt der letzten
	 * Verschmelzungen werden aktualisiert.
	 * <p>
	 * <em>Hinweis:</em> die historische Ganglinie im Parameter wird ge�ndert.
	 * 
	 * @param ganglinie
	 *            die Ganglinie mit der verschmolzen wird. Sie hat immer das
	 *            Gewicht 1.
	 * @param historGl
	 *            die historische Ganglinie die verschmolzen wird.
	 * @param gewicht
	 *            das Gewicht der zweiten Ganglinie.
	 * @param defLueckenSchliessen
	 *            wenn {@code true}, dann wird bei nur einem undefinierten
	 *            Operanden der definierte Operand als Ergebnis angenommen. Wenn
	 *            {@code false} dann ist das Ergebnis ebenfalls undefiniert.
	 * @return das Ergebnis der Verschmelzung.
	 */
	public static GanglinieMQ verschmelze(final GanglinieMQ ganglinie,
			final GanglinieMQ historGl, final long gewicht,
			final boolean defLueckenSchliessen) {
		final long zeitstempel;
		final Ganglinie<Double> gQKfz, gQLkw, gVPkw, gVLkw;

		gQKfz = GanglinienOperationen.verschmelze(ganglinie.getGanglinieQKfz(),
				historGl.getGanglinieQKfz(), gewicht, defLueckenSchliessen);
		gQLkw = GanglinienOperationen.verschmelze(ganglinie.getGanglinieQLkw(),
				historGl.getGanglinieQLkw(), gewicht, defLueckenSchliessen);
		gVPkw = GanglinienOperationen.verschmelze(ganglinie.getGanglinieVPkw(),
				historGl.getGanglinieVPkw(), gewicht, defLueckenSchliessen);
		gVLkw = GanglinienOperationen.verschmelze(ganglinie.getGanglinieVLkw(),
				historGl.getGanglinieVLkw(), gewicht, defLueckenSchliessen);

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
	 *            die Ganglinie f�r QKfz.
	 * @param gQLkw
	 *            die Ganglinie f�r QLkw.
	 * @param gVPkw
	 *            die Ganglinie f�r VPkw.
	 * @param gVLkw
	 *            die Ganglinie f�r VLkw.
	 * @return die zusammengef�hrte Ganglinie.
	 */
	public static GanglinieMQ zusammenfuehren(final Ganglinie<Double> gQKfz,
			final Ganglinie<Double> gQLkw, final Ganglinie<Double> gVPkw,
			final Ganglinie<Double> gVLkw) {
		final GanglinieMQ g;

		assert gQKfz.size() == gQLkw.size() && gQLkw.size() == gVPkw.size()
				&& gVPkw.size() == gVLkw.size() : "Die berechneten St�tzstellenlisten m�ssen gleich gro� sein.";
		g = new GanglinieMQ();
		for (final Long t : gQKfz.keySet()) {
			assert gQKfz.containsKey(t) && gQLkw.containsKey(t)
					&& gVPkw.containsKey(t) && gVLkw.containsKey(t) : "Die St�tzstellen mit dem selben Index, m�ssen den selben Zeitstempel besitzen.";

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
