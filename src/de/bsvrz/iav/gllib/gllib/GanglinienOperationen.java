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
import com.bitctrl.util.Timestamp;

/**
 * Helferklasse mit den Operationen auf Ganglinien, deren Stützstellen
 * Gleitkommazahlen sind.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public final class GanglinienOperationen {

	/**
	 * Kapselt die Informationen die als Ergebnis aus dem Pattern-Matching
	 * hervorgehen.
	 * 
	 * @param <T>
	 *            der Typ der Ganglinie im Ergebnis.
	 */
	public static final class PatternMatchingErgebnis<T extends Ganglinie<?>>
			implements Comparable<PatternMatchingErgebnis<T>> {

		/** Die Ergebnisganglinie. */
		private final T ganglinie;

		/** Der Index der ausgewählten Ganglinie in der Vergleichsliste. */
		private final int index;

		/** Der Abstand der Ergebnisganglinie zur Referenzganglinie. */
		private final int abstand;

		/** Der Offset um den die Ergebnisganglinie verschoben wurde. */
		private final long offset;

		/**
		 * Erzeugt ein Pattern-Matching-Ergebnis.
		 * 
		 * @param ganglinie
		 *            die Ergebnisganglinie.
		 * @param index
		 *            der Index der ausgewählten Ganglinie in der
		 *            Vergleichsliste.
		 * @param abstand
		 *            der Abstand der Ergebnisganglinie zur Referenzganglinie.
		 * @param offset
		 *            der Offset um den die Ergebnisganglinie verschoben wurde.
		 */
		public PatternMatchingErgebnis(final T ganglinie, final int index,
				final int abstand, final long offset) {
			this.ganglinie = ganglinie;
			this.index = index;
			this.abstand = abstand;
			this.offset = offset;
		}

		/**
		 * Gibt die Ergebnisganglinie zurück.
		 * 
		 * @return die Ergebnisganglinie.
		 */
		public T getGanglinie() {
			return (T) ganglinie.clone();
		}

		/**
		 * Gibt den Index der ausgewählten Ganglinie in der Vergleichsliste
		 * zurück.
		 * 
		 * @return der Index.
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * Gibt den Abstand der Ergebnisganglinie zur Referenzganglinie zurück.
		 * 
		 * @return der Abstand.
		 */
		public int getAbstand() {
			return abstand;
		}

		/**
		 * 
		 * Gibt der Offset um den die Ergebnisganglinie verschoben wurde zurück.
		 * 
		 * @return der Offset der Ergebnisganglinie.
		 */
		public long getOffset() {
			return offset;
		}

		/**
		 * {@inheritDoc}
		 */
		public int compareTo(final PatternMatchingErgebnis<T> o) {
			if (abstand < o.abstand) {
				return -1;
			} else if (abstand > o.abstand) {
				return 1;
			}
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			} else if (obj instanceof PatternMatchingErgebnis) {
				PatternMatchingErgebnis<?> pme;

				pme = (PatternMatchingErgebnis<?>) obj;
				return ganglinie.equals(pme.ganglinie) && index == pme.index
						&& abstand == pme.abstand && offset == pme.offset;
			}

			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			String s;

			s = getClass() + "[";
			s += "index=" + index;
			s += ", abstand=" + abstand;
			s += ", offset=" + Timestamp.relativeTime(offset);
			s += ", ganglinie=" + ganglinie;
			s += "]";

			return s;
		}

	}

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
	 * @return Die "Summe" der beiden Ganglinien
	 */
	public static Ganglinie<Double> addiere(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2) {
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

		for (final long z : zeitstempel) {
			if (g1.isValid(z) && g2.isValid(z)) {
				// irgendwas + irgendwas = irgendwas
				g.put(z, p1.get(z).getWert() + p2.get(z).getWert());
			} else if (g1.isValid(z) && p2.getStuetzstellen().size() > 0) {
				Stuetzstelle<Double> erste, letzte;

				erste = p2.getStuetzstellen().get(0);
				letzte = p2.getStuetzstellen().get(
						p2.getStuetzstellen().size() - 1);

				if (z < erste.getZeitstempel()) {
					// irgendwas + undefiniert = irgendwas, am Anfang
					g.put(z, p1.get(z).getWert() + erste.getWert());
				} else if (z > letzte.getZeitstempel()) {
					// irgendwas + undefiniert = irgendwas, am Ende
					g.put(z, p1.get(z).getWert() + letzte.getWert());
				} else {
					// irgendwas + undefiniert = undefiniert, in der Mitte
					g.put(z, null);
				}
			} else if (g2.isValid(z) && p1.getStuetzstellen().size() > 0) {
				Stuetzstelle<Double> erste, letzte;

				erste = p1.getStuetzstellen().get(0);
				letzte = p1.getStuetzstellen().get(
						p1.getStuetzstellen().size() - 1);

				if (z < erste.getZeitstempel()) {
					// undefiniert + irgendwas = irgendwas, am Anfang
					g.put(z, erste.getWert() + p2.get(z).getWert());
				} else if (z > letzte.getZeitstempel()) {
					// undefiniert + irgendwas = irgendwas, am Ende
					g.put(z, letzte.getWert() + p2.get(z).getWert());
				} else {
					// undefiniert + irgendwas = undefiniert, in der Mitte
					g.put(z, null);
				}
			} else {
				// undefiniert + irgendwas = undefiniert
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
		final Ganglinie<Double> p1, p2;
		final Queue<Long> zeitstempel;
		final double fehler;

		p1 = g1.clone();
		p1.setApproximation(new Polyline());
		p2 = g2.clone();
		p2.setApproximation(new Polyline());

		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);
		fehler = fehler(p1, p2, zeitstempel);
		if (fehler < Double.POSITIVE_INFINITY) {
			return (int) Math.round(fehler);
		}
		return Integer.MAX_VALUE;
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
	 * @return Das "Produkt" der beiden Ganglinien
	 */
	public static Ganglinie<Double> dividiere(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2) {
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

		for (final long z : zeitstempel) {
			Double a, b, c;

			if (g1.isValid(z) && g2.isValid(z)) {
				// irgendwas / irgendwas = irgendwas
				a = p1.get(z).getWert();
				b = p2.get(z).getWert();
			} else if (g1.isValid(z) && p2.getStuetzstellen().size() > 0) {
				Stuetzstelle<Double> erste, letzte;

				erste = p2.getStuetzstellen().get(0);
				letzte = p2.getStuetzstellen().get(
						p2.getStuetzstellen().size() - 1);

				if (z < erste.getZeitstempel()) {
					// irgendwas / undefiniert = irgendwas, am Anfang
					a = p1.get(z).getWert();
					b = erste.getWert();
				} else if (z > letzte.getZeitstempel()) {
					// irgendwas / undefiniert = irgendwas, am Ende
					a = p1.get(z).getWert();
					b = letzte.getWert();
				} else {
					// irgendwas / undefiniert = undefiniert, in der Mitte
					a = b = null;
				}
			} else if (g2.isValid(z) && p1.getStuetzstellen().size() > 0) {
				Stuetzstelle<Double> erste, letzte;

				erste = p1.getStuetzstellen().get(0);
				letzte = p1.getStuetzstellen().get(
						p1.getStuetzstellen().size() - 1);

				if (z < erste.getZeitstempel()) {
					// undefiniert / irgendwas = irgendwas, am Anfang
					a = erste.getWert();
					b = p2.get(z).getWert();
				} else if (z > letzte.getZeitstempel()) {
					// undefiniert + irgendwas = irgendwas, am Ende
					a = letzte.getWert();
					b = p2.get(z).getWert();
				} else {
					// undefiniert + irgendwas = undefiniert, in der Mitte
					a = b = null;
				}
			} else {
				// undefiniert + irgendwas = undefiniert
				a = b = null;
			}

			if (a != null && b != null) {
				c = a / b;
				if (Double.isNaN(c) || Double.isInfinite(c)) {
					g.put(z, null);
				} else {
					g.put(z, c);
				}
			} else {
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

		return (int) Math.round(fehler(g1, g2, zeitstempel));
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
	 * @return Das "Produkt" der beiden Ganglinien
	 */
	public static Ganglinie<Double> multipliziere(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2) {
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

		for (final long z : zeitstempel) {
			if (g1.isValid(z) && g2.isValid(z)) {
				// irgendwas * irgendwas = irgendwas
				g.put(z, p1.get(z).getWert() * p2.get(z).getWert());
			} else if (g1.isValid(z) && p2.getStuetzstellen().size() > 0) {
				Stuetzstelle<Double> erste, letzte;

				erste = p2.getStuetzstellen().get(0);
				letzte = p2.getStuetzstellen().get(
						p2.getStuetzstellen().size() - 1);

				if (z < erste.getZeitstempel()) {
					// irgendwas * undefiniert = irgendwas, am Anfang
					g.put(z, p1.get(z).getWert() * erste.getWert());
				} else if (z > letzte.getZeitstempel()) {
					// irgendwas * undefiniert = irgendwas, am Ende
					g.put(z, p1.get(z).getWert() * letzte.getWert());
				} else {
					// irgendwas * undefiniert = undefiniert, in der Mitte
					g.put(z, null);
				}
			} else if (g2.isValid(z) && p1.getStuetzstellen().size() > 0) {
				Stuetzstelle<Double> erste, letzte;

				erste = p1.getStuetzstellen().get(0);
				letzte = p1.getStuetzstellen().get(
						p1.getStuetzstellen().size() - 1);

				if (z < erste.getZeitstempel()) {
					// undefiniert * irgendwas = irgendwas, am Anfang
					g.put(z, erste.getWert() * p2.get(z).getWert());
				} else if (z > letzte.getZeitstempel()) {
					// undefiniert * irgendwas = irgendwas, am Ende
					g.put(z, letzte.getWert() * p2.get(z).getWert());
				} else {
					// undefiniert * irgendwas = undefiniert, in der Mitte
					g.put(z, null);
				}
			} else {
				// undefiniert * irgendwas = undefiniert
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
	 * Referenzganglinie aus. Das Ergebnis ist die Ganglinie aus der Menge mit
	 * dem geringsten Abstand zur Referenzganglinie.
	 * <p>
	 * Zusätzlich zu der Liste von Vergleichsganglinien wird jede dieser
	 * Ganglinien im angegebenen Offset in {@code intervall} Schritten
	 * verschoben. Jede dieser so entstanden Ganglinien wird ebenfalls mit der
	 * Referenzganglinie verglichen.
	 * <p>
	 * Ergebnis ist die vorgegebene oder erzeugte Ganglinie mit dem geringsten
	 * Abstand. Der Index im Ergebnis ist der Index in der Ganglinienliste, aus
	 * der die Ergebnisganglinie hervorgegangen ist.
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
	 * @param schrittweite
	 *            das Intervall, in dem die Ganglinien innerhalb des Offsets
	 *            verschoben werden.
	 * @return das Ergebnis des Pattern-Matching.
	 */
	public static PatternMatchingErgebnis<Ganglinie<Double>> patternMatching(
			final Ganglinie<Double> referenz,
			final List<Ganglinie<Double>> liste, final long offsetVor,
			final long offsetNach, final long schrittweite) {
		if (referenz == null) {
			throw new IllegalArgumentException(
					"Referenzganglinie darf nicht null sein.");
		}
		if (liste.isEmpty()) {
			throw new IllegalArgumentException(
					"Die Vergleichsliste darf nicht leer sein.");
		}
		if (offsetNach < 0 || offsetVor < 0) {
			throw new IllegalArgumentException(
					"Der Offset darf nicht kleiner als 0 sein.");
		}
		if (schrittweite <= 0) {
			throw new IllegalArgumentException(
					"Die Schrittweite muss größer als 0 sein.");
		}

		final SortedSet<PatternMatchingErgebnis<Ganglinie<Double>>> ergebnisse;

		ergebnisse = new TreeSet<PatternMatchingErgebnis<Ganglinie<Double>>>();
		for (int i = 0; i < liste.size(); i++) {
			for (long offset = -offsetVor; offset <= offsetNach; offset += schrittweite) {
				int abstand;
				final Ganglinie<Double> g;

				g = liste.get(i).clone();
				verschiebe(g, offset);
				abstand = basisabstand(referenz, g);
				ergebnisse.add(new PatternMatchingErgebnis<Ganglinie<Double>>(
						g.clone(), i, abstand, offset));
			}
		}

		return ergebnisse.first();
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
	 * @return Die "Differenz" der beiden Ganglinien
	 */
	public static Ganglinie<Double> subtrahiere(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2) {
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

		for (final long z : zeitstempel) {
			if (g1.isValid(z) && g2.isValid(z)) {
				// irgendwas - irgendwas = irgendwas
				g.put(z, p1.get(z).getWert() - p2.get(z).getWert());
			} else if (g1.isValid(z) && p2.getStuetzstellen().size() > 0) {
				Stuetzstelle<Double> erste, letzte;

				erste = p2.getStuetzstellen().get(0);
				letzte = p2.getStuetzstellen().get(
						p2.getStuetzstellen().size() - 1);

				if (z < erste.getZeitstempel()) {
					// irgendwas - undefiniert = irgendwas, am Anfang
					g.put(z, p1.get(z).getWert() - erste.getWert());
				} else if (z > letzte.getZeitstempel()) {
					// irgendwas - undefiniert = irgendwas, am Ende
					g.put(z, p1.get(z).getWert() - letzte.getWert());
				} else {
					// irgendwas + undefiniert = undefiniert, in der Mitte
					g.put(z, null);
				}
			} else if (g2.isValid(z) && p1.getStuetzstellen().size() > 0) {
				Stuetzstelle<Double> erste, letzte;

				erste = p1.getStuetzstellen().get(0);
				letzte = p1.getStuetzstellen().get(
						p1.getStuetzstellen().size() - 1);

				if (z < erste.getZeitstempel()) {
					// undefiniert - irgendwas = irgendwas, am Anfang
					g.put(z, erste.getWert() - p2.get(z).getWert());
				} else if (z > letzte.getZeitstempel()) {
					// undefiniert - irgendwas = irgendwas, am Ende
					g.put(z, letzte.getWert() - p2.get(z).getWert());
				} else {
					// undefiniert - irgendwas = undefiniert, in der Mitte
					g.put(z, null);
				}
			} else {
				// undefiniert - irgendwas = undefiniert
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
	 * <p>
	 * Wird der maximale Abstand der beiden Ganglinien überschritten, ist der
	 * Bereich zwischen den Ganglinien undefiniert.
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 * @param maxAbstand
	 *            der maximale Abstand der beiden Ganglinien.
	 * @return Konkatenation der beiden Ganglinien
	 */
	public static Ganglinie<Double> verbinde(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2, final long maxAbstand) {
		if (g1.getIntervall().intersect(g2.getIntervall())) {
			throw new IllegalArgumentException(
					"Die zu verbindenden Ganglinien dürfen sicht überschneiden.");
		}

		final Ganglinie<Double> g;

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

		if (g1.lastKey() < g2.firstKey()) {
			// g1 liegt vor g2

			final long abstand;

			abstand = g2.firstKey() - g1.lastKey();
			if (abstand > maxAbstand) {
				g.put(g1.lastKey() + abstand / 2, null);
			}
		} else {
			// g2 liegt vor g1

			final long abstand;

			abstand = g1.firstKey() - g2.lastKey();
			if (abstand > maxAbstand) {
				g.put(g2.lastKey() + abstand / 2, null);
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
	 * @return das Ergebnis der Verschmelzung.
	 */
	public static Ganglinie<Double> verschmelze(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2, final long gewicht) {
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
			} else {
				// undefiniert # irgendwas = irgendwas
				if (g1.isValid(z)) {
					g.put(z, p1.get(z).getWert());
				} else if (g2.isValid(z)) {
					g.put(z, p2.get(z).getWert());
				} else {
					// beide Operanden sind undefiniert
					g.put(z, null);
				}
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
	 * Bestimmt den prozentualen Fehler (Abstand) zweier Ganglinien anhand
	 * gegebener Messpunkte.
	 * 
	 * @param g1
	 *            die erste Ganglinie.
	 * @param g2
	 *            die zweite Ganglinie.
	 * @param zeitstempel
	 *            die "Messpunkte"
	 * @return der prozentuale Fehler.
	 */
	private static double fehler(final Ganglinie<Double> g1,
			final Ganglinie<Double> g2, final Queue<Long> zeitstempel) {
		double fehler, summe;
		int undefinierte;

		// Quadratischen Fehler bestimmen
		summe = 0;
		undefinierte = 0;
		for (final long z : zeitstempel) {
			if (g1.getStuetzstelle(z).getWert() != null
					&& g2.getStuetzstelle(z).getWert() != null) {
				double x;

				x = g2.getStuetzstelle(z).getWert()
						- g1.getStuetzstelle(z).getWert();
				summe += x * x;
			} else {
				// Undefinierte Stützstellen werden nicht berücksichtigt
				undefinierte++;
			}
		}

		if (zeitstempel.size() - undefinierte == 0) {
			// Die beiden Ganglinienintevalle überschneiden sich nicht.
			return Double.POSITIVE_INFINITY;
		}
		fehler = Math.sqrt(summe / (zeitstempel.size() - undefinierte));

		// Prozentualen Fehler bestimmen
		summe = 0;
		for (final long z : zeitstempel) {
			if (g1.getStuetzstelle(z).getWert() != null
					&& g2.getStuetzstelle(z).getWert() != null) {
				double x;

				x = (g1.getStuetzstelle(z).getWert() + g2.getStuetzstelle(z).getWert()) / 2;
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
