/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact Information:
 * BitCtrl Systems GmbH
 * Wei�enfelser Stra�e 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
public class GanglinienOperationen {

	/**
	 * Konstruktor verstecken.
	 */
	protected GanglinienOperationen() {
		// nichts
	}

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
		SortedSet<Long> zeitstempel;

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

			z = zeitstempel.first();
			zeitstempel.remove(z);

			if (p1.get(z).getWert() == null || p2.get(z).getWert() == null) {
				g.setStuetzstelle(z, null);
			} else {
				g.setStuetzstelle(z, p1.get(z).getWert() + p2.get(z).getWert());
			}
		}

		return g;
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
		SortedSet<Long> zeitstempel;

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

			z = zeitstempel.first();
			zeitstempel.remove(z);

			if (p1.get(z).getWert() == null || p2.get(z).getWert() == null) {
				g.setStuetzstelle(z, null);
			} else {
				g.setStuetzstelle(z, p1.get(z).getWert() - p2.get(z).getWert());
			}
		}

		return g;
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
		SortedSet<Long> zeitstempel;

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

			z = zeitstempel.first();
			zeitstempel.remove(z);

			if (p1.get(z).getWert() == null || p2.get(z).getWert() == null) {
				g.setStuetzstelle(z, null);
			} else {
				g.setStuetzstelle(z, p1.get(z).getWert() * p2.get(z).getWert());
			}
		}

		return g;
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
		SortedSet<Long> zeitstempel;

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

			z = zeitstempel.first();
			zeitstempel.remove(z);

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

		// St�tzstellen an den beiden Schnittpunkten erg�nzen, falls n�tig
		if (!g.existsStuetzstelle(i.start)) {
			g.setStuetzstelle(p.get(i.start));
		}
		if (!g.existsStuetzstelle(i.ende)) {
			g.setStuetzstelle(p.get(i.ende));
		}

		// St�tzstellen au�erhalb des Intervalls entfernen
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
			if (g.existsStuetzstelle(t)) {
				Double d1, d2;

				d1 = g.getStuetzstelle(t).getWert();
				d2 = g2.getStuetzstelle(t).getWert();
				if (d1 != null && d2 != null) {
					g.setStuetzstelle(t, (d1 + d2) / 2);
				} else {
					g.setStuetzstelle(t, null);
				}
			} else {
				g.setStuetzstelle(g2.getStuetzstelle(t));
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
	 * @return Abstand nach dem Basisabstandsverfahren
	 */
	public static double basisabstand(Ganglinie g1, Ganglinie g2) {
		Polyline p1, p2;
		SortedSet<Long> zeitstempel;
		double fehler, summe, x;

		p1 = new Polyline();
		p1.setStuetzstellen(g1.getStuetzstellen());
		p1.initialisiere();
		p2 = new Polyline();
		p2.setStuetzstellen(g2.getStuetzstellen());
		p2.initialisiere();
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);

		// Quadratischen Fehler bestimmen
		summe = 0;
		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.first();
			zeitstempel.remove(z);

			x = p2.get(z).getWert() - p1.get(z).getWert();
			summe += x * x;
		}
		fehler = Math.sqrt(summe / zeitstempel.size());

		// Prozentualen Fehler bestimmen
		summe = 0;
		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.first();
			zeitstempel.remove(z);

			x = (p1.get(z).getWert() + p2.get(z).getWert()) / 2;
			summe += x * x;
		}
		fehler = (fehler * 100) / (Math.sqrt(summe / zeitstempel.size()));

		return fehler;
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
	public static double komplexerAbstand(Ganglinie g1, Ganglinie g2,
			int intervalle) {
		Polyline p1, p2;
		SortedSet<Long> zeitstempel;
		double fehler, summe, x;
		long start, ende, breite;

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
		breite = Math.round((double) (ende - start) / intervalle);

		// Haltepunkte bestimmen
		zeitstempel = new TreeSet<Long>();
		for (int i = 0; i < intervalle; i++) {
			zeitstempel.add(start + i * breite);
		}
		zeitstempel.add(ende);

		// Quadratischen Fehler bestimmen
		summe = 0;
		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.first();
			zeitstempel.remove(z);

			x = p2.get(z).getWert() - p1.get(z).getWert();
			summe += x * x;
		}
		fehler = Math.sqrt(summe / zeitstempel.size());

		// Prozentualen Fehler bestimmen
		summe = 0;
		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.first();
			zeitstempel.remove(z);

			x = (p1.get(z).getWert() + p2.get(z).getWert()) / 2;
			summe += x * x;
		}
		fehler = (fehler * 100) / (Math.sqrt(summe / zeitstempel.size()));

		return fehler;
	}

	/**
	 * F�hrt das Pattern-Matching einer Menge von Ganglinien mit einer
	 * Referenzganglinie aus. Ergebnis ist die Ganglinie aus der Menge mit dem
	 * geringsten Abstand zur Referenzganglinie.
	 * 
	 * @param referenz
	 *            Die Referenzganglinie
	 * @param menge
	 *            Eine Menge von zu vergleichenden Ganglinien
	 * @param offset
	 *            Offset, in dem die Ganglinien vor un zur&uuml;ck verschoben
	 *            werden
	 * @param intervall
	 *            Intervall, in dem die Ganglinien innerhalb des Offsets
	 *            verschoben werden
	 * @return Die Ganglinie mit dem kleinsten Abstand
	 */
	public static Ganglinie patternMatching(Ganglinie referenz,
			Collection<Ganglinie> menge, long offset, long intervall) {
		HashMap<Ganglinie, Double> fehler;
		Ganglinie erg;

		fehler = new HashMap<Ganglinie, Double>();

		// Abst�nde der Ganglinien bestimmen
		for (Ganglinie g : menge) {
			double abstand;
			int tests;

			abstand = 0;
			tests = 0;
			for (long i = -offset; i <= offset; i += intervall) {
				abstand += basisabstand(referenz, g);
				tests++;
			}
			fehler.put(g, abstand / tests);
		}

		// Ganglinie mit dem kleinsten Abstand bestimmen
		erg = null;
		for (Entry<Ganglinie, Double> e : fehler.entrySet()) {
			if (erg == null) {
				erg = e.getKey();
			} else {
				if (e.getValue() < fehler.get(erg)) {
					erg = e.getKey();
				}
			}
		}
		return erg;
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
	private static SortedSet<Long> vervollstaendigeStuetzstellen(Ganglinie g1,
			Ganglinie g2) {
		SortedSet<Long> zeitstempel;

		zeitstempel = new TreeSet<Long>();
		zeitstempel.addAll(g1.stuetzstellen.keySet());
		zeitstempel.addAll(g2.stuetzstellen.keySet());

		return zeitstempel;
	}

}
