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
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.event.EventListenerList;

import de.bsvrz.iav.gllib.gllib.events.GanglinienEvent;
import de.bsvrz.iav.gllib.gllib.events.GanglinienListener;
import de.bsvrz.sys.funclib.bitctrl.util.Intervall;

/**
 * Repr&auml;sentiert eine allgemeine Ganglinie, bestehend aus einer sortierten
 * Menge von St&uuml;tzstellen und der Angabe eines Interpolationsverfahren.
 * Wird kein Approximationsverfahren festgelegt, wird ein
 * {@link de.bsvrz.iav.gllib.gllib.BSpline B-Spline} mit Standardordnung
 * angenommen.
 * <p>
 * Da Ganglinien verschiedene Approximationsverfahren verwenden können, wird für
 * neue Ganglinien, die bei den Operationen (z. B. Addition) entstehen, das
 * Standardverfahren festgelegt (B-Spline mit Ordnung 5).
 * 
 * @author BitrCtrl, Schumann
 * @version $Id$
 */
public class Ganglinie {

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

		p1 = new Polyline(g1);
		p2 = new Polyline(g2);
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

		p1 = new Polyline(g1);
		p2 = new Polyline(g2);
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

		p1 = new Polyline(g1);
		p2 = new Polyline(g2);
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

		p1 = new Polyline(g1);
		p2 = new Polyline(g2);
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
				g.setStuetzstelle(z, Math.round((float) p1.get(z).getWert()
						/ p2.get(z).getWert()));
			}
		}

		return g;
	}

	/**
	 * Verschiebt eine Ganglinie auf der Zeitachse.
	 * 
	 * @param g
	 *            Zu verschiebende Ganglinie
	 * @param offset
	 *            Offset um den die Ganglinie verschoben werden soll
	 * @return Die verschobene Ganglinie
	 */
	public static Ganglinie verschiebe(Ganglinie g, long offset) {
		Ganglinie v;

		v = new Ganglinie();
		for (long t : g.stuetzstellen.keySet()) {
			v.setStuetzstelle(t + offset, g.getStuetzstelle(t).getWert());
		}

		return v;
	}

	/**
	 * Schneidet ein Intervall aus einer Ganglinie heraus. Existieren keine
	 * St&uuml;tzstellen in den Intervallgrenzen, werden an diesen Stellen
	 * mittels Approximation durch Polyline St&uuml;tzstellen hinzugef&uuml;gt.
	 * 
	 * @param g
	 *            Eine Ganglinie
	 * @param i
	 *            Auszuschneidendes Intervall
	 * @return Der Intervallausschnitt
	 */
	public static Ganglinie auschneiden(Ganglinie g, Intervall i) {
		Ganglinie a;
		Polyline p;
		SortedMap<Long, Integer> teilintervall;

		teilintervall = g.stuetzstellen.subMap(i.start, i.ende + 1);
		a = new Ganglinie(teilintervall);
		p = new Polyline(g);

		if (!a.existsStuetzstelle(i.start)) {
			a.setStuetzstelle(p.get(i.start));
		}

		if (!a.existsStuetzstelle(i.ende)) {
			a.setStuetzstelle(p.get(i.ende));
		}

		return a;

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

				g.setStuetzstelle(t, Math.round((float) (g.getStuetzstelle(t)
						.getWert() + g2.getStuetzstelle(t).getWert()) / 2));
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

		p1 = new Polyline(g1);
		p2 = new Polyline(g2);
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

		p1 = new Polyline(g1);
		p2 = new Polyline(g2);

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
		breite = Math.round((float) (ende - start) / intervalle);

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
	 * Führt das Pattern-Matching einer Menge von Ganglinien mit einer
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

		// Abstände der Ganglinien bestimmen
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

	/** Speicher der St&uuml;tzstellen. */
	protected SortedMap<Long, Integer> stuetzstellen;

	/** Verfahren zur Berechnung der Punkte zwischen den St&uuml;tzstellen. */
	private Approximation approximation;

	/** Liste aller Listener. */
	private final EventListenerList listeners;

	/**
	 * Konstruiert eine Ganglinie ohne St&uuml;tzstellen.
	 */
	public Ganglinie() {
		stuetzstellen = new TreeMap<Long, Integer>();
		listeners = new EventListenerList();
		setStandardApproximation();
	}

	/**
	 * Kopierkonstruktor. Es werden die St&uuml;tzstellen aus der
	 * <em>Collection</em> &uuml;bernommen.
	 * 
	 * @param stuetzstellen
	 *            Die St&uuml;tzstellen der Ganglinie.
	 */
	public Ganglinie(Collection<Stuetzstelle> stuetzstellen) {
		this();
		for (Stuetzstelle s : stuetzstellen) {
			this.stuetzstellen.put(s.getZeitstempel(), s.getWert());
		}
		fireGanglinienAktualisierung();
	}

	/**
	 * Kopierkonstruktor. Es werden die St&uuml;tzstellen aus der <em>Map</em>
	 * &uuml;bernommen, wobei die Schl&uuml;ssel als Zeitstempel interpretiert
	 * werden (Zeitstempel -> Wert).
	 * 
	 * @param stuetzstellen
	 *            Die St&uuml;tzstellen der Ganglinie.
	 */
	public Ganglinie(Map<Long, Integer> stuetzstellen) {
		this();
		for (long t : stuetzstellen.keySet()) {
			this.stuetzstellen.put(t, stuetzstellen.get(t));
		}
		fireGanglinienAktualisierung();
	}

	/**
	 * Gibt die Anzahl der St&uuml;tzstellen der Ganglinie zur&uuml;ck.
	 * 
	 * @return St&uuml;tzstellenanzahl
	 */
	public int anzahlStuetzstellen() {
		return stuetzstellen.size();
	}

	/**
	 * Pr&uuml;ft ob zu einem Zeitstempel eine reale St&uuml;tzstelle existiert.
	 * 
	 * @param zeitstempel
	 *            Ein Zeitstempel
	 * @return {@code true}, wenn die Ganglinie eine St&uuml;tzstelle zum
	 *         Zeitpunkt speichert und {@code false}, wenn zu dem Zeitpunkt die
	 *         St&uuml;tzstelle berechnet werden muss
	 */
	public boolean existsStuetzstelle(long zeitstempel) {
		return stuetzstellen.containsKey(zeitstempel);
	}

	/**
	 * Pr&uuml;ft ob ein Zeitstempel im Definitionsbereich der Ganglinie liegt.
	 * 
	 * @param zeitstempel
	 *            zu pr&uuml;fender Zeitstempel
	 * @return <code>true</code>, wenn <code>zeitstempel</code> im
	 *         definierten Bereich der Ganglinie liegt
	 * @see #getIntervalle()
	 */
	public boolean isValid(long zeitstempel) {
		boolean ok;

		ok = false;
		for (Intervall i : getIntervalle()) {
			if (i.isEnthalten(zeitstempel)) {
				ok = true;
				break;
			}
		}

		return ok;
	}

	/**
	 * Gibt die real existierende St&uuml;tzstelle zu einem Zeitpunkt
	 * zur&uuml;ck. Ist zu dem angegebenen Zeitpunkt keine St&uuml;tzstelle
	 * vorhanden, wird {@code null} zur&uuml;ckgegeben.
	 * 
	 * @param zeitstempel
	 *            Der Zeitstempel zu dem eine St&uuml;tzstelle gesucht wird
	 * @return Die gesuchte St&uuml;tzstelle oder {@code null}, wenn keine
	 *         existiert
	 */
	public Stuetzstelle getStuetzstelle(long zeitstempel) {
		if (stuetzstellen.containsKey(zeitstempel)) {
			return new Stuetzstelle(zeitstempel, stuetzstellen.get(zeitstempel));
		}

		return null;
	}

	/**
	 * Gibt ein sortiertes Feld der existierenden St&uuml;tzstellen
	 * zur&uuml;ck;.
	 * 
	 * @return Nach Zeitstempel sortiere St&uuml;tzstellen
	 */
	public List<? extends Stuetzstelle> getStuetzstellen() {
		List<Stuetzstelle> liste;

		liste = new ArrayList<Stuetzstelle>();
		for (long t : stuetzstellen.keySet()) {
			liste.add(getStuetzstelle(t));
		}

		return liste;
	}

	/**
	 * Nimmt eine St&uuml;tzstelle in die Ganglinie auf. Existiert zu dem
	 * Zeitpunkt bereits eine, wird diese &uuml;berschrieben.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der St&uuml;tzstelle
	 * @param wert
	 *            Wert der St&uuml;tzstelle
	 * @return {@code true}, wenn die St&uuml;tzstelle neu angelegt wurde und
	 *         {@code false}, wenn eine vorhandene St&uuml;tzstelle ersetzt
	 *         wurde.
	 */
	public boolean setStuetzstelle(long zeitstempel, Integer wert) {
		boolean neu;

		neu = stuetzstellen.put(zeitstempel, wert) == null;
		fireGanglinienAktualisierung();

		return neu;
	}

	/**
	 * Nimmt eine St&uuml;tzstelle in die Ganglinie auf. Existiert zu dem
	 * Zeitpunkt bereits eine, wird diese &uuml;berschrieben. Ruft intern
	 * {@link #setStuetzstelle(long, Integer)} auf.
	 * 
	 * @param s
	 *            Die neue Stu&uuml;tzstelle
	 * @return {@code true}, wenn die St&uuml;tzstelle neu angelegt wurde und
	 *         {@code false}, wenn eine vorhandene St&uuml;tzstelle ersetzt
	 *         wurde.
	 */
	public boolean setStuetzstelle(Stuetzstelle s) {
		return setStuetzstelle(s.getZeitstempel(), s.getWert());
	}

	/**
	 * Entfernt eine St&uuml;tzstelle.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der St&uuml;tzstelle, die entfernt werden soll
	 */
	public void remove(long zeitstempel) {
		stuetzstellen.remove(zeitstempel);
		fireGanglinienAktualisierung();
	}

	/**
	 * Gibt das Zeitintervall der Ganglinie zur&uuml;ck.
	 * 
	 * @return Ein {@link Intervall} oder {@code null}, wenn keine
	 *         St&uuml;tzstellen vorhanden sind
	 */
	public Intervall getIntervall() {
		if (stuetzstellen.size() == 0) {
			return null;
		}

		return new Intervall(stuetzstellen.firstKey(), stuetzstellen.lastKey());
	}

	/**
	 * Bestimmt die Intervalle in denen die Ganglinie definiert ist.
	 * 
	 * @return Liste von Intervallen
	 */
	public List<Intervall> getIntervalle() {
		List<Intervall> intervalle;
		Long start, ende;

		intervalle = new ArrayList<Intervall>();
		start = null;
		ende = null;

		for (long t : stuetzstellen.keySet()) {
			if (start == null && stuetzstellen.get(t) != null) {
				start = t;
			}

			if (stuetzstellen.get(t) == null) {
				// Definitionslücke gefunden
				if (start != null && ende != null) {
					intervalle.add(new Intervall(start, ende));
					start = null;
					ende = null;
				}
			} else {
				// Intervall verlängern
				if (stuetzstellen.get(t) != null) {
					ende = t;
				}
			}

			if (t == stuetzstellen.lastKey()) {
				intervalle.add(new Intervall(start, ende));
			}
		}

		return intervalle;
	}

	/**
	 * Die Ganglinie als Approximation zu&uuml;ck.
	 * 
	 * @return Approximation der Ganglinie
	 */
	public Approximation getApproximation() {
		assert approximation != null;

		return approximation;
	}

	/**
	 * Legt das Approximationsverfahren fest, mit dem die Werte zwischen den
	 * St&uuml;tzstellen bestimmt werden soll.
	 * 
	 * @param approximation
	 *            Klasse eines Approximationsverfahrens. Die Klasse m&uuml;ss
	 *            einen parameterlosen Konstruktor besitzen.
	 * @throws IllegalArgumentException
	 *             Wenn die Klassen keinen &ouml;ffentlichen parameterlosen
	 *             Konstruktor besitzt
	 */
	public void setApproximation(Class<? extends Approximation> approximation) {
		Constructor<? extends Approximation> konstruktor;

		try {
			konstruktor = approximation
					.getConstructor(new Class[] { Ganglinie.class });
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException();
		}

		try {
			this.approximation = konstruktor.newInstance(new Object[] { this });
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException();
		}

	}

	/**
	 * Registriert einen Listener.
	 * 
	 * @param listener
	 *            Der neue Listener
	 */
	public void addGanglinienListener(GanglinienListener listener) {
		listeners.add(GanglinienListener.class, listener);
	}

	/**
	 * Entfernt einen Listener wieder aus der Liste registrierter Listener.
	 * 
	 * @param listener
	 *            Listener der abgemeldet werden soll
	 */
	public void removeGanglinienListener(GanglinienListener listener) {
		listeners.remove(GanglinienListener.class, listener);
	}

	/**
	 * Informiert die angemeldeten Listener &uuml;ber die &Auml;nderung der
	 * Ganglinie.
	 */
	protected synchronized void fireGanglinienAktualisierung() {
		GanglinienEvent e = new GanglinienEvent(this);

		for (GanglinienListener l : listeners
				.getListeners(GanglinienListener.class)) {
			l.ganglinieAktualisiert(e);
		}
	}

	/**
	 * Legt die Standardapproximation fest.
	 */
	private void setStandardApproximation() {
		approximation = new BSpline(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Ganglinie) {
			Ganglinie g;

			g = (Ganglinie) o;
			return stuetzstellen.equals(g.stuetzstellen);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return stuetzstellen.toString();
	}

}
