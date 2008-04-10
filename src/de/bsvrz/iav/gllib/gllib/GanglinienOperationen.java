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

import com.bitctrl.util.Interval;

/**
 * Helferklasse mit den Operationen auf Ganglinien, deren Stützstellen
 * Gleitkommazahlen sind.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 * @todo Parameter defLueckenSchliessen wieder entfernen, außer beim
 *       Verschmelzen
 */
public final class GanglinienOperationen {

	/**
	 * Konvertiert eine Ganglinie in einen lesbaren Text. Dazu wird zwischen
	 * jeder Stützstelle einfach ein Zeilenbruch eingefügt. Enthält die
	 * Ganglinie keine Stützstellen wird eine kurze Notiz ausgegeben.
	 * 
	 * @param g
	 *            eine Ganglinie.
	 * @return ein String der jede Stützstelle auf eine eigene Zeile schreibt.
	 */
	public static String formatierterText(final Ganglinie<?> g) {
		String txt;

		txt = "Intervall: " + g.getIntervall();
		txt += "\nApproximation: " + g.getApproximation();
		for (final Stuetzstelle<?> s : g.getStuetzstellen()) {
			txt += "\n" + s;
		}

		if (g.size() == 0) {
			txt += "\nKeine Stützstellen vorhanden.";
		}

		return txt;
	}

	/**
	 * Addiert zwei Ganglinien, indem die Werte der vervollständigten
	 * Stützstellenmenge addiert werden. Die beiden Ganglinien werden dabei
	 * nicht verändert.
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
	public static Ganglinie<Double> addiere(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2, final boolean defLueckenSchliessen) {
		Ganglinie<Double> g;
		Polyline p1, p2;
		Queue<Long> zeitstempel;

		if ((g1.getApproximation() != null && g2.getApproximation() != null)
				&& !g1.getApproximation().getClass().equals(
						g2.getApproximation().getClass())
				|| (g1.getApproximation() == null ^ g2.getApproximation() == null)) {
			throw new IllegalArgumentException(
					"Die Addition kann nicht durchgeführt werden, da die beiden Ganglinien unterschiedlische Approximationsverfahren verwenden.");
		}

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();
		g = new Ganglinie<Double>();
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);

		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.poll();

			if (g1.isValid(z) && g2.isValid(z)) {
				g.put(z, p1.get(z).getWert() + p2.get(z).getWert());
			} else if (defLueckenSchliessen) {
				// undefiniert # irgendwas = irgendwas
				if (g1.isValid(z)) {
					g.put(z, p1.get(z).getWert());
				} else if (g2.isValid(z)) {
					g.put(z, p2.get(z).getWert());
				} else {
					// beide Operanden sind undefiniert
					g.put(z, null);
				}
			} else {
				// undefiniert # irgendwas = undefiniert
				g.put(z, null);
			}
		}

		return g;
	}

	/**
	 * Schneidet ein Intervall aus einer Ganglinie heraus. Existieren keine
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
	public static Ganglinie<Double> auschneiden(final Ganglinie<Double> g,
			final Interval i) {
		Polyline p;

		p = new Polyline();
		p.setStuetzstellen(g.getStuetzstellen());
		p.initialisiere();

		// Stützstellen an den beiden Schnittpunkten ergänzen, falls nötig
		if (!g.containsKey(i.getStart())) {
			g.setStuetzstelle(p.get(i.getStart()));
		}
		if (!g.containsKey(i.getEnd())) {
			g.setStuetzstelle(p.get(i.getEnd()));
		}

		// Stützstellen außerhalb des Intervalls entfernen
		Iterator<Entry<Long, Double>> iterator;
		iterator = g.entrySet().iterator();
		while (iterator.hasNext()) {
			long t;

			t = iterator.next().getKey();
			if (!i.contains(t)) {
				iterator.remove();
			}
		}

		return g;
	}

	/**
	 * Berechnet den Abstand zweier Ganglinien mit Hilfe des
	 * Basisabstandsverfahren. Es wir der Abstand anhand der vervollständigten
	 * Stützstellenmenge bestimmt.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Abstand nach dem Basisabstandsverfahren in Prozent.
	 */
	public static int basisabstand(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2) {
		final Polyline p1, p2;
		final Queue<Long> zeitstempel;

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();

		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);
		return (int) Math.round(fehler(p1, p2, zeitstempel));
	}

	/**
	 * Division zweier Ganglinien, indem die Werte der vervollständigten
	 * Stützstellenmenge dividiert werden. Die beiden Ganglinien werden dabei
	 * nicht verändert.
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
	public static Ganglinie<Double> dividiere(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2, final boolean defLueckenSchliessen) {
		Ganglinie<Double> g;
		Polyline p1, p2;
		Queue<Long> zeitstempel;

		if ((g1.getApproximation() != null && g2.getApproximation() != null)
				&& !g1.getApproximation().getClass().equals(
						g2.getApproximation().getClass())
				|| (g1.getApproximation() == null ^ g2.getApproximation() == null)) {
			throw new IllegalArgumentException(
					"Die Division kann nicht durchgeführt werden, da die beiden Ganglinien unterschiedle Approximationsverfahren verwenden.");
		}

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();
		g = new Ganglinie<Double>();
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);

		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.poll();

			if (g1.isValid(z) && g2.isValid(z)) {
				double x;

				x = p1.get(z).getWert() / p2.get(z).getWert();
				if (x == Double.NaN || x == Double.NEGATIVE_INFINITY
						|| x == Double.POSITIVE_INFINITY) {
					g.put(z, null);
				} else {
					g.put(z, x);
				}
			} else if (defLueckenSchliessen) {
				// undefiniert # irgendwas = irgendwas
				if (g1.isValid(z)) {
					g.put(z, p1.get(z).getWert());
				} else if (g2.isValid(z)) {
					g.put(z, p2.get(z).getWert());
				} else {
					// beide Operanden sind undefiniert
					g.put(z, null);
				}
			} else {
				// undefiniert # irgendwas = undefiniert
				g.put(z, null);
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
	public static int komplexerAbstand(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2, final int intervalle) {
		long start, ende, breite;

		// Zu betrachtendes Intervall und Intervallbreite bestimmen
		if (g1.firstKey() < g2.firstKey()) {
			start = g2.firstKey();
		} else {
			start = g1.firstKey();
		}
		if (g1.lastKey() < g2.lastKey()) {
			ende = g1.lastKey();
		} else {
			ende = g2.lastKey();
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
	public static int komplexerAbstand(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2, final long intervallBreite) {
		// final Polyline p1, p2;
		final Queue<Long> zeitstempel;
		long start, ende;

		// p1 = new Polyline();
		// p1.setStuetzstellen(g1.getStuetzstellen());
		// p1.initialisiere();
		// p2 = new Polyline();
		// p2.setStuetzstellen(g2.getStuetzstellen());
		// p2.initialisiere();

		// Zu betrachtendes Intervall und Intervallbreite bestimmen
		if (g1.firstKey() < g2.firstKey()) {
			start = g2.firstKey();
		} else {
			start = g1.firstKey();
		}
		if (g1.lastKey() < g2.lastKey()) {
			ende = g1.lastKey();
		} else {
			ende = g2.lastKey();
		}

		if (start > ende) {
			// Die beiden Ganglinienintervalle überschneiden sich nicht.
			return Integer.MAX_VALUE;
		}

		// Haltepunkte bestimmen
		zeitstempel = new LinkedList<Long>();
		for (long i = start; i < ende; i += intervallBreite) {
			zeitstempel.add(i);
		}
		zeitstempel.add(ende);

		return (int) Math.round(fehler(g1.getApproximation(), g2
				.getApproximation(), zeitstempel));
	}

	/**
	 * Multiplikation zweier Ganglinien, indem die Werte der vervollständigten
	 * Stützstellenmenge multipliziert werden. Die beiden Ganglinien werden
	 * dabei nicht verändert.
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
	public static Ganglinie<Double> multipliziere(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2, final boolean defLueckenSchliessen) {
		Ganglinie<Double> g;
		Polyline p1, p2;
		Queue<Long> zeitstempel;

		if ((g1.getApproximation() != null && g2.getApproximation() != null)
				&& !g1.getApproximation().getClass().equals(
						g2.getApproximation().getClass())
				|| (g1.getApproximation() == null ^ g2.getApproximation() == null)) {
			throw new IllegalArgumentException(
					"Die Multiplikation kann nicht durchgeführt werden, da die beiden Ganglinien unterschiedle Approximationsverfahren verwenden.");
		}

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();
		g = new Ganglinie<Double>();
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);

		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.poll();

			if (g1.isValid(z) && g2.isValid(z)) {
				g.put(z, p1.get(z).getWert() * p2.get(z).getWert());
			} else if (defLueckenSchliessen) {
				// undefiniert # irgendwas = irgendwas
				if (g1.isValid(z)) {
					g.put(z, p1.get(z).getWert());
				} else if (g2.isValid(z)) {
					g.put(z, p2.get(z).getWert());
				} else {
					// beide Operanden sind undefiniert
					g.put(z, null);
				}
			} else {
				// undefiniert # irgendwas = undefiniert
				g.put(z, null);
			}
		}

		return g;
	}

	/**
	 * Normiert die Stützstellen einer Ganglinie. Mehrere hintereinander
	 * folgende undefinierte Stützstellen werden zusammengefasst. Der Abstand
	 * der Stützstellen wird auf ein definiertes Intervall normiert. Der
	 * Intervallzyklus beginnt mit dem Zeitstempel der ersten Stützstelle.
	 * <p>
	 * <em>Hinweis:</em> die Ganglinie im Parameter wird verändert.
	 * 
	 * @param g
	 *            eine Ganglinie.
	 * @param abstand
	 *            der gewünschte Stützstellenabstand.
	 * @return die normierte Ganglinie.
	 */
	public static Ganglinie<Double> normiere(final Ganglinie<Double> g,
			final long abstand) {
		final Polyline p;
		final SortedMap<Long, Double> stuetzstellen;
		final long halberAbstand;

		p = new Polyline();
		p.setStuetzstellen(g.getStuetzstellen());
		p.initialisiere();

		stuetzstellen = new TreeMap<Long, Double>();
		halberAbstand = abstand / 2;
		stuetzstellen.put(g.firstKey(), g.get(g.firstKey()));
		for (long i = g.firstKey() + halberAbstand; i < g.lastKey(); i += abstand) {
			final Interval intervall;

			intervall = new Interval(i - halberAbstand, i + halberAbstand);
			if (g.isValid(intervall)) {
				stuetzstellen.put(i, p.integral(intervall)
						/ intervall.getLength());
			} else {
				stuetzstellen.put(i, null);
			}
		}
		stuetzstellen.put(g.lastKey(), g.get(g.lastKey()));

		g.clear();
		g.putAll(stuetzstellen);
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
	public static int patternMatching(final Ganglinie<Double> referenz,
			final List<Ganglinie<Double>> liste, long offsetVor,
			final long offsetNach, final long intervall) {
		HashMap<Integer, Double> fehler;
		int index;
		long start, ende; // Start und Ende des Pattern-Matching-Intervalls

		fehler = new HashMap<Integer, Double>();
		start = referenz.getIntervall().getStart() - offsetVor;
		ende = referenz.getIntervall().getEnd() + offsetNach;

		// Abstände der Ganglinien bestimmen
		for (int i = 0; i < liste.size(); i++) {
			Ganglinie<Double> g, ref;
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
		for (final Entry<Integer, Double> e : fehler.entrySet()) {
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
	 * nicht verändert.
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
	public static Ganglinie<Double> subtrahiere(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2, final boolean defLueckenSchliessen) {
		Ganglinie<Double> g;
		Polyline p1, p2;
		Queue<Long> zeitstempel;

		if ((g1.getApproximation() != null && g2.getApproximation() != null)
				&& !g1.getApproximation().getClass().equals(
						g2.getApproximation().getClass())
				|| (g1.getApproximation() == null ^ g2.getApproximation() == null)) {
			throw new IllegalArgumentException(
					"Die Subtraktion kann nicht durchgeführt werden, da die beiden Ganglinien unterschiedle Approximationsverfahren verwenden.");
		}

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();
		g = new Ganglinie<Double>();
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);

		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.poll();

			if (g1.isValid(z) && g2.isValid(z)) {
				g.put(z, p1.get(z).getWert() - p2.get(z).getWert());
			} else if (defLueckenSchliessen) {
				// undefiniert # irgendwas = irgendwas
				if (g1.isValid(z)) {
					g.put(z, p1.get(z).getWert());
				} else if (g2.isValid(z)) {
					g.put(z, p2.get(z).getWert());
				} else {
					// beide Operanden sind undefiniert
					g.put(z, null);
				}
			} else {
				// undefiniert # irgendwas = undefiniert
				g.put(z, null);
			}
		}

		return g;
	}

	/**
	 * Verbindet zwei Ganglinien durch Konkatenation. Es werden die Stützstellen
	 * beider Ganglinien zu einer neuen Ganglinien zusammengefasst. Dies ist nur
	 * möglich, wenn sich die Stützstellenmengen nicht überschneiden. Berühren
	 * sich die beiden Ganglinien wird im Berührungspunkt er Mittelwert der
	 * beiden Stützstellen gebildet.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Konkatenation der beiden Ganglinien
	 */
	public static Ganglinie<Double> verbinde(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2) {
		if (g1.getIntervall().intersect(g2.getIntervall())) {
			throw new IllegalArgumentException();
		}

		Ganglinie<Double> g;

		g = new Ganglinie<Double>();
		g.putAll(g1);
		for (final long t : g2.keySet()) {
			if (g.containsKey(t)) {
				Double d1, d2;

				d1 = g.get(t);
				d2 = g2.get(t);
				if (d1 != null && d2 != null) {
					g.put(t, (d1 + d2) / 2);
				} else {
					g.put(t, null);
				}
			} else {
				g.put(t, g2.get(t));
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
	public static Ganglinie<Double> verschiebe(final Ganglinie<Double> g,
			final long offset) {
		SortedMap<Long, Double> stuetzstellen;

		stuetzstellen = new TreeMap<Long, Double>();
		for (final long t : g.keySet()) {
			stuetzstellen.put(t + offset, g.get(t));
		}
		g.clear();
		g.putAll(stuetzstellen);
		return g;
	}

	/**
	 * Verschiebt eine Ganglinie auf der Zeitachse um ein halbes
	 * Stützstellenintervall. Jede Stützstelle wird um den halben Abstand zur
	 * nächsten Stützstelle verschoben. Die letzte Stützstelle wird um den
	 * halben Abstand zur vorherigen Stützstelle verschoben. Gibt es nur eine
	 * Stützstelle, wird diese nicht verschoben.
	 * <p>
	 * <em>Hinweis:</em> Es wird die Ganglinie im Parameter verschoben.
	 * 
	 * @param g
	 *            Zu verschiebende Ganglinie
	 * @return Die verschobene Ganglinie
	 */
	public static Ganglinie<Double> verschiebeUmHalbesIntervall(
			final Ganglinie<Double> g) {
		SortedMap<Long, Double> stuetzstellen;
		long intervall, t0;

		if (g.size() < 2) {
			throw new IllegalArgumentException(
					"Die Ganglinie muss mindestens zwei Stützstellen besitzen.");
		}

		t0 = Long.MIN_VALUE;
		intervall = 0;
		stuetzstellen = new TreeMap<Long, Double>();
		for (final Long t : g.keySet()) {
			if (t0 == Long.MIN_VALUE) {
				t0 = t;
			} else {
				intervall = t - t0;
				stuetzstellen.put(t0 + intervall / 2, g.get(t0));
				t0 = t;
			}
		}
		stuetzstellen.put(g.lastKey() + intervall / 2, g.get(g.lastKey()));
		g.clear();
		g.putAll(stuetzstellen);
		return g;
	}

	/**
	 * Verschmilzt eine Ganglinie mit einer anderen. Dabei wird das gewichtete
	 * arithmetische Mittel der vervollständigten Stützstellen gebildet. Die
	 * zweite Ganglinie hat immer das Gewicht 1.
	 * 
	 * @param g1
	 *            die Ganglinie mit der verschmolzen wird. Sie hat immer das
	 *            Gewicht 1.
	 * @param g2
	 *            die Ganglinie die verschmolzen wird.
	 * @param gewicht
	 *            das Gewicht der zweiten Ganglinie.
	 * @param defLueckenSchliessen
	 *            wenn {@code true}, dann wird bei nur einem undefinierten
	 *            Operanden der definierte Operand als Ergebnis angenommen. Wenn
	 *            {@code false} dann ist das Ergebnis ebenfalls undefiniert.
	 * @return das Ergebnis der Verschmelzung.
	 */
	public static Ganglinie<Double> verschmelze(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2, final long gewicht,
			final boolean defLueckenSchliessen) {
		Ganglinie<Double> g;
		Polyline p1, p2;
		Queue<Long> zeitstempel;

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();
		g = new Ganglinie<Double>();
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);

		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.poll();

			if (g1.isValid(z) && g2.isValid(z)) {
				g.put(z, (p1.get(z).getWert() + p2.get(z).getWert() * gewicht)
						/ (gewicht + 1));
			} else if (defLueckenSchliessen) {
				// undefiniert # irgendwas = irgendwas
				if (g1.isValid(z)) {
					g.put(z, p1.get(z).getWert());
				} else if (g2.isValid(z)) {
					g.put(z, p2.get(z).getWert());
				} else {
					// beide Operanden sind undefiniert
					g.put(z, null);
				}
			} else {
				// undefiniert # irgendwas = undefiniert
				g.put(z, null);
			}
		}

		return g;
	}

	/**
	 * Konstruktor verstecken.
	 */
	private GanglinienOperationen() {
		// nichts
	}

	/**
	 * Bestimmt den prozentualen Fehler (Abstand) zweier Approximationen anhand
	 * gegebener Messpunkte.
	 * 
	 * @param approx1
	 *            die erste Approximation.
	 * @param approx2
	 *            die zweite Approximation.
	 * @param zeitstempel
	 *            die "Messpunkte"
	 * @return der prozentuale Fehler.
	 */
	private static double fehler(final Approximation<Double> approx1,
			final Approximation<Double> approx2, final Queue<Long> zeitstempel) {
		double fehler, summe;
		int undefinierte;

		// Quadratischen Fehler bestimmen
		summe = 0;
		undefinierte = 0;
		for (final long z : zeitstempel) {
			if (approx1.get(z).getWert() != null
					&& approx2.get(z).getWert() != null) {
				double x;

				x = approx2.get(z).getWert() - approx1.get(z).getWert();
				summe += x * x;
			} else {
				// Undefinierte Stützstellen werden nicht berücksichtigt
				undefinierte++;
			}
		}
		fehler = Math.sqrt(summe / (zeitstempel.size() - undefinierte));
		if (fehler == Double.NaN) {
			// Die beiden Ganglinienintevalle überschneiden sich nicht.
			return Double.POSITIVE_INFINITY;
		}

		// Prozentualen Fehler bestimmen
		summe = 0;
		for (final long z : zeitstempel) {
			if (approx1.get(z).getWert() != null
					&& approx2.get(z).getWert() != null) {
				double x;

				x = (approx1.get(z).getWert() + approx2.get(z).getWert()) / 2;
				summe += x * x;
			}
		}
		return (fehler * 100)
				/ (Math.sqrt(summe / (zeitstempel.size() - undefinierte)));
	}

	/**
	 * Bestimmt die vereinigte Menge der Stützstellen beider Ganglinien.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @return Menge von Stützstellenreferenzen in Form von Zeitstempeln
	 */
	private static LinkedList<Long> vervollstaendigeStuetzstellen(
			final Ganglinie<Double> g1, final Ganglinie<Double> g2) {
		SortedSet<Long> zeitstempel;

		zeitstempel = new TreeSet<Long>();
		zeitstempel.addAll(g1.keySet());
		zeitstempel.addAll(g2.keySet());

		return new LinkedList<Long>(zeitstempel);
	}

}
