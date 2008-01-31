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

package de.bsvrz.iav.gllib.gllib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import de.bsvrz.sys.funclib.bitctrl.util.Intervall;

/**
 * Helferklasse mit den Operationen auf Ganglinien, deren St&uuml;tzstellen
 * Gleitkommazahlen sind.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public final class GanglinienOperationen {

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
	public static Ganglinie addiere(Ganglinie g1, Ganglinie g2) {
		Ganglinie g;
		Polyline p1, p2;
		Queue<Long> zeitstempel;

		if (!(g1.getApproximation() == null && g2.getApproximation() == null)) {
			if (!g1.getApproximation().getClass().equals(
					g2.getApproximation().getClass())) {
				throw new IllegalArgumentException(
						"Die Addition kann nicht durchgeführt werden, da die beiden Ganglinien unterschiedle Approximationsverfahren verwenden.");
			}
		}

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();
		g = new Ganglinie();
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);

		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.poll();

			if (p1.get(z).getWert() == null || p2.get(z).getWert() == null) {
				g.setStuetzstelle(z, null);
			} else {
				g.setStuetzstelle(z, p1.get(z).getWert() + p2.get(z).getWert());
			}
		}

		return g;
	}

	/**
	 * Schneidet ein Intervall aus einer Ganglinie heraus. Existieren keine
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
	public static Ganglinie auschneiden(Ganglinie g, Intervall i) {
		Polyline p;

		p = new Polyline();
		p.setStuetzstellen(g.getStuetzstellen());
		p.initialisiere();

		// Stützstellen an den beiden Schnittpunkten ergänzen, falls nötig
		if (!g.existsStuetzstelle(i.start)) {
			g.setStuetzstelle(p.get(i.start));
		}
		if (!g.existsStuetzstelle(i.ende)) {
			g.setStuetzstelle(p.get(i.ende));
		}

		// Stützstellen außerhalb des Intervalls entfernen
		Iterator<Entry<Long, Double>> iterator;
		iterator = g.stuetzstellen.entrySet().iterator();
		while (iterator.hasNext()) {
			long t;

			t = iterator.next().getKey();
			if (!i.isEnthalten(t)) {
				iterator.remove();
			}
		}

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
	 * @return Abstand nach dem Basisabstandsverfahren in Prozent.
	 */
	public static int basisabstand(Ganglinie g1, Ganglinie g2) {
		Polyline p1, p2;
		Queue<Long> zeitstempel;
		double fehler, summe;
		int undefinierte;

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();

		// Quadratischen Fehler bestimmen
		summe = 0;
		undefinierte = 0;
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);
		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.poll();
			if (p1.get(z).getWert() != null && p2.get(z).getWert() != null) {
				double x;

				x = p2.get(z).getWert() - p1.get(z).getWert();
				summe += x * x;
			} else {
				// Undefinierte Stützstellen werden nicht berücksichtigt
				undefinierte++;
			}
		}
		fehler = Math.sqrt(summe / (zeitstempel.size() - undefinierte));

		// Prozentualen Fehler bestimmen
		summe = 0;
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);
		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.poll();
			if (p1.get(z).getWert() != null && p2.get(z).getWert() != null) {
				double x;

				x = (p1.get(z).getWert() + p2.get(z).getWert()) / 2;
				summe += x * x;
			}
		}
		fehler = (fehler * 100)
				/ (Math.sqrt(summe / (zeitstempel.size() - undefinierte)));

		return (int) Math.round(fehler);
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
	public static Ganglinie dividiere(Ganglinie g1, Ganglinie g2) {
		Ganglinie g;
		Polyline p1, p2;
		Queue<Long> zeitstempel;

		if (!(g1.getApproximation() == null && g2.getApproximation() == null)) {
			if (!g1.getApproximation().getClass().equals(
					g2.getApproximation().getClass())) {
				throw new IllegalArgumentException(
						"Die Division kann nicht durchgeführt werden, da die beiden Ganglinien unterschiedle Approximationsverfahren verwenden.");
			}
		}

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();
		g = new Ganglinie();
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);

		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.poll();

			if (p1.get(z).getWert() == null || p2.get(z).getWert() == null
					|| p2.get(z).getWert() == 0) {
				// Einer der Werte ist undefiniert oder der Divisor ist 0
				g.setStuetzstelle(z, null);
			} else {
				g.setStuetzstelle(z, p1.get(z).getWert() / p2.get(z).getWert());
			}
		}

		return g;
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
	 * @param intervalle
	 *            die Anzahl der zu vergleichenden Intervalle.
	 * @return Abstand nach dem komplexen Abstandsverfahren in Prozent.
	 */
	public static int komplexerAbstand(Ganglinie g1, Ganglinie g2,
			int intervalle) {
		long start, ende, breite;

		// Zu betrachtendes Intervall und Intervallbreite bestimmen
		if (g1.stuetzstellen.firstKey() < g2.stuetzstellen.firstKey()) {
			start = g2.stuetzstellen.firstKey();
		} else {
			start = g1.stuetzstellen.firstKey();
		}
		if (g1.stuetzstellen.lastKey() < g2.stuetzstellen.lastKey()) {
			ende = g1.stuetzstellen.lastKey();
		} else {
			ende = g2.stuetzstellen.lastKey();
		}
		breite = Math.round((double) (ende - start) / intervalle);

		return komplexerAbstand(g1, g2, breite);
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
	public static int komplexerAbstand(Ganglinie g1, Ganglinie g2,
			long intervallBreite) {
		Polyline p1, p2;
		List<Long> zeitstempel;
		double fehler, summe;
		long start, ende;
		int undefinierte;

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();

		// Zu betrachtendes Intervall und Intervallbreite bestimmen
		if (g1.stuetzstellen.firstKey() < g2.stuetzstellen.firstKey()) {
			start = g2.stuetzstellen.firstKey();
		} else {
			start = g1.stuetzstellen.firstKey();
		}
		if (g1.stuetzstellen.lastKey() < g2.stuetzstellen.lastKey()) {
			ende = g1.stuetzstellen.lastKey();
		} else {
			ende = g2.stuetzstellen.lastKey();
		}

		// Haltepunkte bestimmen
		zeitstempel = new ArrayList<Long>();
		for (long i = start; i < ende; i += intervallBreite) {
			zeitstempel.add(i);
		}
		zeitstempel.add(ende);

		// Quadratischen Fehler bestimmen
		summe = 0;
		undefinierte = 0;
		for (long z : zeitstempel) {
			if (p1.get(z).getWert() != null && p2.get(z).getWert() != null) {
				double x;

				x = p2.get(z).getWert() - p1.get(z).getWert();
				summe += x * x;
			} else {
				undefinierte++;
			}
		}
		fehler = Math.sqrt(summe / (zeitstempel.size() - undefinierte));

		// Prozentualen Fehler bestimmen
		summe = 0;
		undefinierte = 0;
		for (long z : zeitstempel) {
			if (p1.get(z).getWert() != null && p2.get(z).getWert() != null) {
				double x;

				x = (p1.get(z).getWert() + p2.get(z).getWert()) / 2;
				summe += x * x;
			} else {
				undefinierte++;
			}
		}
		fehler = (fehler * 100)
				/ (Math.sqrt(summe / (zeitstempel.size() - undefinierte)));

		return (int) Math.round(fehler);
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
	public static Ganglinie multipliziere(Ganglinie g1, Ganglinie g2) {
		Ganglinie g;
		Polyline p1, p2;
		Queue<Long> zeitstempel;

		if (!(g1.getApproximation() == null && g2.getApproximation() == null)) {
			if (!g1.getApproximation().getClass().equals(
					g2.getApproximation().getClass())) {
				throw new IllegalArgumentException(
						"Die Multiplikation kann nicht durchgeführt werden, da die beiden Ganglinien unterschiedle Approximationsverfahren verwenden.");
			}
		}

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();
		g = new Ganglinie();
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);

		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.poll();

			if (p1.get(z).getWert() == null || p2.get(z).getWert() == null) {
				g.setStuetzstelle(z, null);
			} else {
				g.setStuetzstelle(z, p1.get(z).getWert() * p2.get(z).getWert());
			}
		}

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
	public static int patternMatching(Ganglinie referenz,
			List<Ganglinie> liste, long offsetVor, long offsetNach,
			long intervall) {
		HashMap<Integer, Double> fehler;
		int index;
		long start, ende; // Start und Ende des Pattern-Matching-Intervalls

		fehler = new HashMap<Integer, Double>();
		start = referenz.getIntervall().getStart() - offsetVor;
		ende = referenz.getIntervall().getEnde() + offsetNach;

		// Abstände der Ganglinien bestimmen
		for (int i = 0; i < liste.size(); i++) {
			Ganglinie g, ref;
			double abstand;
			int tests;

			ref = referenz.clone();
			GanglinienOperationen.verschiebe(ref, -offsetVor);
			g = liste.get(i);
			abstand = 0;
			tests = 0;
			for (long j = start; j <= ende; j += intervall) {
				GanglinienOperationen.verschiebe(ref, intervall);
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
	public static Ganglinie subtrahiere(Ganglinie g1, Ganglinie g2) {
		Ganglinie g;
		Polyline p1, p2;
		Queue<Long> zeitstempel;

		if (!(g1.getApproximation() == null && g2.getApproximation() == null)) {
			if (!g1.getApproximation().getClass().equals(
					g2.getApproximation().getClass())) {
				throw new IllegalArgumentException(
						"Die Subtraktion kann nicht durchgeführt werden, da die beiden Ganglinien unterschiedle Approximationsverfahren verwenden.");
			}
		}

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();
		g = new Ganglinie();
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);

		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.poll();

			if (p1.get(z).getWert() == null || p2.get(z).getWert() == null) {
				g.setStuetzstelle(z, null);
			} else {
				g.setStuetzstelle(z, p1.get(z).getWert() - p2.get(z).getWert());
			}
		}

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
	public static Ganglinie verbinde(Ganglinie g1, Ganglinie g2) {
		if (g1.getIntervall().schneidet(g2.getIntervall())) {
			throw new IllegalArgumentException();
		}

		Ganglinie g;

		g = new Ganglinie(g1.stuetzstellen);
		for (long t : g2.stuetzstellen.keySet()) {
			if (g.stuetzstellen.containsKey(t)) {
				Double d1, d2;

				d1 = g.stuetzstellen.get(t);
				d2 = g2.stuetzstellen.get(t);
				if (d1 != null && d2 != null) {
					g.stuetzstellen.put(t, (d1 + d2) / 2);
				} else {
					g.stuetzstellen.put(t, null);
				}
			} else {
				g.stuetzstellen.put(t, g2.stuetzstellen.get(t));
			}
		}

		return g;
	}

	/**
	 * Verschiebt eine Ganglinie auf der Zeitachse.
	 * <p>
	 * <em>Hinweis:</em> Es wird die Ganglinie im Parameter verschoben.
	 * 
	 * @param g
	 *            Zu verschiebende Ganglinie
	 * @param offset
	 *            Offset um den die Ganglinie verschoben werden soll
	 * @return Die verschobene Ganglinie
	 */
	public static Ganglinie verschiebe(Ganglinie g, long offset) {
		SortedMap<Long, Double> stuetzstellen;

		stuetzstellen = new TreeMap<Long, Double>();
		for (long t : g.stuetzstellen.keySet()) {
			stuetzstellen.put(t + offset, g.stuetzstellen.get(t));
		}
		g.stuetzstellen = stuetzstellen;
		return g;
	}

	/**
	 * Verschmilzt eine Ganglinie mit einer anderen. Dabei wird das gewichtete
	 * arithmetische Mittel der vervollst&auml;ndigten St&uuml;tzstellen
	 * gebildet. Die zweite Ganglinie hat immer das Gewicht 1.
	 * 
	 * @param g1
	 *            die Ganglinie mit der verschmolzen wird. Sie hat immer das
	 *            Gewicht 1.
	 * @param g2
	 *            die Ganglinie die verschmolzen wird.
	 * @param gewicht
	 *            das Gewicht der zweiten Ganglinie.
	 * 
	 * @return das Ergebnis der Verschmelzung.
	 */
	public static Ganglinie verschmelze(Ganglinie g1, Ganglinie g2, long gewicht) {
		Ganglinie g;
		Polyline p1, p2;
		Queue<Long> zeitstempel;

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();
		g = new Ganglinie();
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);

		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.poll();

			if (p1.get(z).getWert() == null || p2.get(z).getWert() == null) {
				g.setStuetzstelle(z, null);
			} else {
				g.setStuetzstelle(z, (p1.get(z).getWert() + p2.get(z).getWert()
						* gewicht)
						/ (gewicht + 1));
			}
		}

		return g;
	}

	/**
	 * Bestimmt die vereinigte Menge der St&uuml;tzstellen beider Ganglinien.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Menge von St&uuml;tzstellenreferenzen in Form von Zeitstempeln
	 */
	private static LinkedList<Long> vervollstaendigeStuetzstellen(Ganglinie g1,
			Ganglinie g2) {
		SortedSet<Long> zeitstempel;

		zeitstempel = new TreeSet<Long>();
		zeitstempel.addAll(g1.stuetzstellen.keySet());
		zeitstempel.addAll(g2.stuetzstellen.keySet());

		return new LinkedList<Long>(zeitstempel);
	}

	/**
	 * Konstruktor verstecken.
	 */
	private GanglinienOperationen() {
		// nichts
	}

}
