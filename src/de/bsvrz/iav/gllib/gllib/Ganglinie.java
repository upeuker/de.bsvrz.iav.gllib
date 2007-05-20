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
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

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
 * 
 * @author BitrCtrl, Schumann
 * @version $Id$
 */
public class Ganglinie implements Approximation {

	/** Liste aller Listener. */
	private final EventListenerList listeners;

	/** Speicher der St&uuml;tzstellen. */
	private final SortierteListe<Stuetzstelle> stuetzstellen;

	/**
	 * Verfahren zur Berechnung der Punkte zwischen den St&uuml;tzstellen.
	 * <p>
	 * TODO: Auf B-Spline ändern
	 */
	private Approximation approximation = new Polyline(this);

	/**
	 * Vervollst&auml;ndigt die St&uuml;tzstellenmengen zweier Ganglinien. Dabei
	 * werden fehlende Stu&uuml;tzstellen mittels Approximation durch eine
	 * Polylinie erg&auml;nzt.
	 * <p>
	 * Die beiden Parameter der Methode werden modifiziert!
	 * 
	 * @param g1
	 *            Erste Ganglinie
	 * @param g2
	 *            Zweite Ganglinie
	 */
	public static void vervollstaendigeStuetzstellen(Ganglinie g1, Ganglinie g2) {
		Polyline p1, p2;

		p1 = new Polyline(g1);
		p2 = new Polyline(g2);

		for (Stuetzstelle s : g1.getStuetzstellen()) {
			if (!g2.existsStuetzstelle(s.zeitstempel)) {
				try {
					g2.set(p2.get(s.zeitstempel));
				} catch (UndefiniertException e) {
					// TODO Auto-generated catch block
					System.err.println(e.getLocalizedMessage());
				}
			}
		}

		for (Stuetzstelle s : g2.getStuetzstellen()) {
			if (!g1.existsStuetzstelle(s.zeitstempel)) {
				try {
					g1.set(p1.get(s.zeitstempel));
				} catch (UndefiniertException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
		Ganglinie g, gx1, gx2;

		gx1 = new Ganglinie(g1);
		gx2 = new Ganglinie(g2);
		vervollstaendigeStuetzstellen(gx1, gx2);
		g = new Ganglinie();

		assert gx1.anzahlStuetzstellen() == gx2.anzahlStuetzstellen();

		for (int i = 0; i < gx1.anzahlStuetzstellen(); i++) {
			// TODO
		}

		return g;
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
