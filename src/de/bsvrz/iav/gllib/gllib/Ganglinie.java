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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.event.EventListenerList;

import de.bsvrz.iav.gllib.gllib.events.GanglinienEvent;
import de.bsvrz.iav.gllib.gllib.events.GanglinienListener;
import de.bsvrz.iav.gllib.gllib.util.Intervall;
import de.bsvrz.iav.gllib.gllib.util.SortierteListe;
import de.bsvrz.iav.gllib.gllib.util.UndefiniertException;
import de.bsvrz.sys.funclib.bitctrl.i18n.Messages;

/**
 * Repr&auml;sentiert eine allgemeine Ganglinie, bestehend aus einer sortierten
 * Menge von St&uuml;tzstellen und der Angabe eines Interpolationsverfahren.
 * Wird kein Approximationsverfahren festgelegt, wird ein
 * {@link BSpline B-Spline} mit Standardordnung angenommen.
 * <p>
 * Da Ganglinien verschiedene Approximationsverfahren verwenden können, wird für
 * neue Ganglinien, die bei den Operationen (z. B. Addition) entstehen, das
 * Standardverfahren festgelegt (B-Spline mit Ordnung 5).
 * 
 * @author BitrCtrl, Schumann
 * @version $Id$
 */
public class Ganglinie implements Approximation {

	/** Liste aller Listener. */
	private final EventListenerList listeners;

	/** Speicher der St&uuml;tzstellen. */
	private final SortierteListe<Stuetzstelle> stuetzstellen;

	/** Verfahren zur Berechnung der Punkte zwischen den St&uuml;tzstellen. */
	private Approximation approximation = new BSpline(this, (short) 5);

	// /**
	// * Vervollst&auml;ndigt die St&uuml;tzstellenmengen zweier Ganglinien.
	// Dabei
	// * werden fehlende Stu&uuml;tzstellen mittels Approximation durch eine
	// * Polylinie erg&auml;nzt.
	// * <p>
	// * <em>Hinweis:</em> Die beiden Parameter der Methode werden modifiziert!
	// *
	// * @param g1
	// * Erste Ganglinie
	// * @param g2
	// * Zweite Ganglinie
	// */
	// public static void vervollstaendigeStuetzstellen(Ganglinie g1, Ganglinie
	// g2) {
	// Polyline p1, p2;
	//
	// p1 = new Polyline(g1);
	// p2 = new Polyline(g2);
	//
	// for (Stuetzstelle s : g1.getStuetzstellen()) {
	// if (!g2.existsStuetzstelle(s.zeitstempel)) {
	// try {
	// g2.set(p2.get(s.zeitstempel));
	// } catch (UndefiniertException e) {
	// g2.set(s.zeitstempel, null);
	// }
	// }
	// }
	//
	// for (Stuetzstelle s : g2.getStuetzstellen()) {
	// if (!g1.existsStuetzstelle(s.zeitstempel)) {
	// try {
	// g1.set(p1.get(s.zeitstempel));
	// } catch (UndefiniertException e) {
	// g1.set(s.zeitstempel, null);
	// }
	// }
	// }
	// }

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
		for (Stuetzstelle s : g1.stuetzstellen) {
			zeitstempel.add(s.zeitstempel);
		}
		for (Stuetzstelle s : g2.stuetzstellen) {
			zeitstempel.add(s.zeitstempel);
		}

		return zeitstempel;
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

		p1 = new Polyline(g1);
		p2 = new Polyline(g2);
		g = new Ganglinie();
		zeitstempel = vervollstaendigeStuetzstellen(g1, g2);

		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.first();
			zeitstempel.remove(z);

			try {
				g.set(z, p1.get(z).wert + p2.get(z).wert);
			} catch (UndefiniertException e) {
				g.set(z, null);
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

			try {
				g.set(z, p1.get(z).wert - p2.get(z).wert);
			} catch (UndefiniertException e) {
				g.set(z, null);
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

			try {
				g.set(z, p1.get(z).wert * p2.get(z).wert);
			} catch (UndefiniertException e) {
				g.set(z, null);
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

			try {
				g.set(z, Math.round((float) p1.get(z).wert / p2.get(z).wert));
			} catch (UndefiniertException e) {
				g.set(z, null);
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
		for (Stuetzstelle s : g.stuetzstellen) {
			v.set(s.zeitstempel + offset, s.wert);
		}

		return v;
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

		g = new Ganglinie(g1);
		for (Stuetzstelle s : g2.stuetzstellen) {
			if (g.existsStuetzstelle(s.zeitstempel)) {
				try {
					g.set(s.zeitstempel, Math.round((float) (g
							.get(s.zeitstempel).wert + s.wert) / 2));
				} catch (UndefiniertException e) {
					g.set(s.zeitstempel, null);
				}
			} else {
				g.set(s);
			}
		}

		return g;
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
		SortedSet<Stuetzstelle> teilintervall;

		teilintervall = g.stuetzstellen.subSet(new Stuetzstelle(i.start),
				new Stuetzstelle(i.ende + 1));
		a = new Ganglinie(teilintervall);
		p = new Polyline(a);

		if (!a.existsStuetzstelle(i.start)) {
			try {
				a.set(p.get(i.start));
			} catch (UndefiniertException e) {
				// Nichts zu tun, da der Bereich vor der Ganglinie a priori
				// undefiniert ist
			}
		}

		if (!a.existsStuetzstelle(i.ende)) {
			try {
				a.set(p.get(i.ende));
			} catch (UndefiniertException e) {
				// Nichts zu tun, da der Bereich hinter der Ganglinie a priori
				// undefiniert ist
			}
		}

		return a;

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

			try {
				x = p2.get(z).wert - p1.get(z).wert;
				summe += x * x;
			} catch (UndefiniertException e) {
				// Nicht zu tun, da undefinierte Bereiche nicht bewertet werden
				// können
			}
		}
		fehler = Math.sqrt(summe / zeitstempel.size());

		// Prozentualen Fehler bestimmen
		summe = 0;
		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.first();
			zeitstempel.remove(z);

			try {
				x = (p1.get(z).wert + p2.get(z).wert) / 2;
				summe += x * x;
			} catch (UndefiniertException e) {
				// Nicht zu tun, da undefinierte Bereiche nicht bewertet werden
				// können
			}
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
		if (g1.stuetzstellen.first().zeitstempel < g2.stuetzstellen.first().zeitstempel) {
			start = g2.stuetzstellen.first().zeitstempel;
		} else {
			start = g1.stuetzstellen.first().zeitstempel;
		}
		if (g1.stuetzstellen.last().zeitstempel < g2.stuetzstellen.last().zeitstempel) {
			ende = g1.stuetzstellen.last().zeitstempel;
		} else {
			ende = g2.stuetzstellen.last().zeitstempel;
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

			try {
				x = p2.get(z).wert - p1.get(z).wert;
				summe += x * x;
			} catch (UndefiniertException e) {
				// Nicht zu tun, da undefinierte Bereiche nicht bewertet werden
				// können
			}
		}
		fehler = Math.sqrt(summe / zeitstempel.size());

		// Prozentualen Fehler bestimmen
		summe = 0;
		while (!zeitstempel.isEmpty()) {
			long z;

			z = zeitstempel.first();
			zeitstempel.remove(z);

			try {
				x = (p1.get(z).wert + p2.get(z).wert) / 2;
				summe += x * x;
			} catch (UndefiniertException e) {
				// Nicht zu tun, da undefinierte Bereiche nicht bewertet werden
				// können
			}
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
	 * Konstruiert eine Ganglinie ohne St&uuml;tzstellen.
	 */
	public Ganglinie() {
		stuetzstellen = new SortierteListe<Stuetzstelle>();
		listeners = new EventListenerList();
	}

	/**
	 * Kopierkonstruktor. Es werden die St&uuml;tzstellen und die Art der
	 * Approximation &uuml;bernommen.
	 * 
	 * @param ganglinie
	 *            Die zu kopierende Ganglinie
	 */
	public Ganglinie(Ganglinie ganglinie) {
		this();
		for (Stuetzstelle s : ganglinie.stuetzstellen) {
			stuetzstellen.add(s);
		}
		setApproximation(ganglinie.approximation.getClass());
		fireGanglinienAktualisierung();
	}

	/**
	 * Kopierkonstruktor. Es werden die St&uuml;tzstellen aus der
	 * <em>Collection</em> &uuml;bernommen.
	 * 
	 * @param stuetzstellen
	 *            Die St&uuml;tzstellen der Ganglinie
	 */
	public Ganglinie(Collection<Stuetzstelle> stuetzstellen) {
		this();
		for (Stuetzstelle s : stuetzstellen) {
			this.stuetzstellen.add(s);
		}
		fireGanglinienAktualisierung();
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
		return stuetzstellen.contains(new Stuetzstelle(zeitstempel));
	}

	/**
	 * Gibt die sortierte Liste der existierenden St&uuml;tzstellen
	 * zur&uuml;ck;.
	 * 
	 * @return Nach Zeitstempel sortiere St&uuml;tzstellenliste
	 */
	public List<Stuetzstelle> getStuetzstellen() {
		return new ArrayList<Stuetzstelle>(stuetzstellen);
	}

	/**
	 * Gibt die real existierende St&uuml;tzstelle zu einem Zeitpunkt
	 * zur&uuml;ck. Ist zu dem angegebenen Zeitpunkt keine St&uuml;tzstelle
	 * gesichert, wird {@code null} zur&uuml;ckgegeben.
	 * 
	 * @param zeitstempel
	 *            Der Zeitstempel zu dem eine St&uuml;tzstelle gesucht wird
	 * @return Die gesuchte St&uuml;tzstelle oder {@code null}, wenn keine
	 *         existiert
	 * @throws UndefiniertException
	 *             Wenn der Zeitstempel nicht im G&uuml;ltigkeitsbereich der
	 *             Ganglinie liegt
	 */
	public Stuetzstelle getStuetzstelle(long zeitstempel)
			throws UndefiniertException {
		if (!isValid(zeitstempel)) {
			throw new UndefiniertException(
					"Der Zeitstempel liegt nicht im Gültigkeitsbereich der Ganglinie.");
		}

		if (existsStuetzstelle(zeitstempel)) {
			return stuetzstellen.get(stuetzstellen.indexOf(new Stuetzstelle(
					zeitstempel)));
		}

		return null;
	}

	/**
	 * Gibt die St&uuml;tzstelle mit dem angegebenen Index zur&uuml;ck.
	 * 
	 * @param index
	 *            Index der gesuchten St&uuml;tzstelle
	 * @return Die gesuchte St&uuml;tzstelle
	 */
	public Stuetzstelle getStuetzstelle(int index) {
		return stuetzstellen.get(index);
	}

	/**
	 * Nimmt eine St&uuml;tzstelle in die Ganglinie auf. Existiert zu dem
	 * Zeitpunkt bereits eine, wird diese &uuml;berschrieben.
	 * 
	 * @param s
	 *            Die neue Stu&uuml;tzstelle
	 */
	public void set(Stuetzstelle s) {
		if (stuetzstellen.contains(s)) {
			stuetzstellen.remove(s);
		}

		stuetzstellen.add(s);
		fireGanglinienAktualisierung();
	}

	/**
	 * &Uuml;bernimmt alle St&uuml;tzstellen aus der <em>Collection</em>. Die
	 * vorhandenen St&uuml;tzstellen werden zuvor gel&ouml;scht.
	 * 
	 * @param menge
	 *            Die neuen St&uuml;tzstellen der Ganglinie
	 */
	public void set(Collection<Stuetzstelle> menge) {
		stuetzstellen.clear();

		for (Stuetzstelle s : menge) {
			stuetzstellen.add(s);
		}
		fireGanglinienAktualisierung();
	}

	/**
	 * Nimmt eine St&uuml;tzstelle in die Ganglinie auf. Existiert zu dem
	 * Zeitpunkt bereits eine, wird diese &uuml;berschrieben.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der St&uuml;tzstelle
	 * @param wert
	 *            Wert der St&uuml;tzstelle
	 */
	public void set(long zeitstempel, Integer wert) {
		set(new Stuetzstelle(zeitstempel, wert));
	}

	/**
	 * Entfernt eine St&uuml;tzstelle.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der St&uuml;tzstelle, die entfernt werden soll
	 */
	public void remove(long zeitstempel) {
		remove(new Stuetzstelle(zeitstempel));
	}

	/**
	 * Entfernt eine St&uuml;tzstelle.
	 * 
	 * @param stuetzstelle
	 *            Die St&uuml;tzstelle, die entfernt werden soll
	 */
	public void remove(Stuetzstelle stuetzstelle) {
		stuetzstellen.remove(stuetzstelle);
		fireGanglinienAktualisierung();
	}

	/**
	 * Gibt das Zeitintervall der Ganglinie zur&uuml;ck.
	 * 
	 * @return Ein {@link Intervall} oder {@code null}, wenn keine
	 *         Stz&uuml;tzstellen vorhanden sind
	 */
	public Intervall getIntervall() {
		if (stuetzstellen.size() == 0) {
			return null;
		}

		return new Intervall(stuetzstellen.first().zeitstempel, stuetzstellen
				.last().zeitstempel);
	}

	/**
	 * Bestimmt die Intervalle in denen die Ganglinie definiert ist.
	 * 
	 * @return Liste von Intervallen
	 */
	public List<Intervall> getIntervalle() {
		List<Intervall> intervalle;

		intervalle = new ArrayList<Intervall>();

		if (stuetzstellen.size() > 0) {
			Stuetzstelle s0, s1; // Intervallbeginn

			s0 = null;
			s1 = null;
			for (Stuetzstelle s : stuetzstellen) {
				if (s0 == null && s.wert != null) {
					s0 = s;
				}
				if (s.wert == null) {
					if (s0 != null && s1 != null) {
						long start, ende;

						start = s0.zeitstempel;
						ende = s1.zeitstempel;
						intervalle.add(new Intervall(start, ende));
						s0 = null;
						s1 = null;
					}
				} else if (s.equals(stuetzstellen.last())) {
					if (s0 != null && s1 != null) {
						long start, ende;

						start = s0.zeitstempel;
						ende = s.zeitstempel;
						intervalle.add(new Intervall(start, ende));
					}
				} else {
					if (s.wert != null) {
						s1 = s;
					}
				}
			}
		}

		return intervalle;
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
	 * Gibt, falls vorhanden, die n&auml;chste St&uuml;tzstelle vor dem
	 * Zeitstempel zur&uuml;ck.
	 * 
	 * @param zeitstempel
	 *            Ein Zeitstempel
	 * @return St&uuml;tzstelle oder {@code null}, falls keine existiert
	 */
	public Stuetzstelle naechsteStuetzstelleDavor(long zeitstempel) {
		Stuetzstelle s;
		SortedSet<Stuetzstelle> kopf;

		s = new Stuetzstelle(zeitstempel);
		kopf = stuetzstellen.headSet(s);

		if (!kopf.isEmpty()) {
			return kopf.last();
		}

		return null;
	}

	/**
	 * Gibt, falls vorhanden, die n&auml;chste St&uuml;tzstelle nach dem
	 * Zeitstempel zur&uuml;ck.
	 * 
	 * @param zeitstempel
	 *            Ein Zeitstempel
	 * @return St&uuml;tzstelle oder {@code null}, falls keine existiert
	 */
	public Stuetzstelle naechsteStuetzstelleDanach(long zeitstempel) {
		Stuetzstelle s;
		SortedSet<Stuetzstelle> kopf;

		s = new Stuetzstelle(zeitstempel + 1); // +1 wegen >= von tailSet()
		kopf = stuetzstellen.tailSet(s);

		if (!kopf.isEmpty()) {
			return kopf.first();
		}

		return null;
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
		try {
			this.approximation = approximation.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getLocalizedMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getLocalizedMessage());
		}

		this.approximation.setGanglinie(this);
	}

	/**
	 * Die Ganglinie als Approximation zu&uuml;ck.
	 * 
	 * @return Approximation der Ganglinie
	 */
	public Approximation getApproximation() {
		return approximation;
	}

	/**
	 * Ersetzt die aktuellen St&uuml;tzstellen mit denen der &uuml;bergebenen
	 * Ganglinie.
	 * 
	 * {@inheritDoc}
	 */
	public void setGanglinie(Ganglinie ganglinie) {
		stuetzstellen.clear();
		stuetzstellen.addAll(ganglinie.stuetzstellen);
		fireGanglinienAktualisierung();
	}

	/**
	 * Es wird zur Bestimmung der St&uuml;tzstelle die aktuelle Approximation
	 * verwendet.
	 * <p>
	 * {@inheritDoc}
	 */
	public Stuetzstelle get(long zeitstempel) throws UndefiniertException {
		return approximation.get(zeitstempel);
	}

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<Stuetzstelle> interpoliere(long anzahlIntervalle) {
		return approximation.interpoliere(anzahlIntervalle);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings("nls")
	@Override
	public String toString() {
		String result;
		Iterator<Stuetzstelle> iterator;

		result = Messages.get(GlLibMessages.Ganglinie) + " " + getIntervall()
				+ ": ";
		iterator = stuetzstellen.iterator();
		while (iterator.hasNext()) {
			result += iterator.next();
			if (iterator.hasNext()) {
				result += ", ";
			}
		}

		return result;
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

}
