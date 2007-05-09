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
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.EventListenerList;

import de.bsvrz.iav.gllib.gllib.events.GanglinienEvent;
import de.bsvrz.iav.gllib.gllib.events.GanglinienListener;
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
@SuppressWarnings("serial")
public class Ganglinie implements Approximation {

	/** Liste aller Listener. */
	private final EventListenerList listeners;

	/** Speicher der St&uuml;tzstellen. */
	private final SortedSet<Stuetzstelle> stuetzstellen;

	/** Verfahren zur Berechnung der Punkte zwischen den St&uuml;tzstellen. */
	private Approximation approximation = new BSpline(this);

	/**
	 * Konstruiert eine Ganglinie ohne St&uuml;tzstellen.
	 */
	public Ganglinie() {
		stuetzstellen = new TreeSet<Stuetzstelle>();
		listeners = new EventListenerList();
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
	 * Gibt die sortierte Liste der existierenden St&uuml;tzstellen
	 * zur&uuml;ck;.
	 * 
	 * @return Nach Zeitstempel sortiere St&uuml;tzstellenliste
	 */
	public List<Stuetzstelle> getStuetzstellen() {
		return new ArrayList<Stuetzstelle>(stuetzstellen);
	}

	/**
	 * Nimmt eine St&uuml;tzstelle in die Ganglinie auf. Existiert zu dem
	 * Zeitpunkt bereits eine, wird diese &uuml;berschrieben.
	 * 
	 * @param s
	 *            Die neue Stu&uuml;tzstelle
	 */
	public void set(Stuetzstelle s) {
		if (!stuetzstellen.tailSet(s).isEmpty()
				&& stuetzstellen.tailSet(s).first().getZeitstempel() == s
						.getZeitstempel()) {
			stuetzstellen.remove(s);
		}

		stuetzstellen.add(s);
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
	 * Pr&uuml;ft ob ein Zeitstempel im Definitionsbereich der Ganglinie liegt.
	 * 
	 * @param zeitstempel
	 *            zu pr&uuml;fender Zeitstempel
	 * @return <code>true</code>, wenn <code>zeitstempel</code> zwischen
	 *         den Zeitstempeln der ersten und letzten St&uuml;tzstelle liegt,
	 *         sonst <code>false</code>
	 */
	public boolean isValid(long zeitstempel) {
		if (stuetzstellen.size() == 0) {
			return false;
		}

		return stuetzstellen.first().zeitstempel <= zeitstempel
				&& zeitstempel <= stuetzstellen.last().zeitstempel;
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
	 * Gibt die real existierende St&uuml;tzstelle zu einem Zeitpunkt
	 * zur&uuml;ck. Ist zu dem angegebenen Zeitpunkt keine St&uuml;tzstelle
	 * gesichert, wird {@code null} zur&uuml;ckgegeben.
	 * 
	 * @param zeitstempel
	 *            Der Zeitstempel zu dem eine St&uuml;tzstelle gesucht wird
	 * @return Die gesuchte St&uuml;tzstelle oder {@code null}, wenn keine
	 *         existiert
	 * @throws IllegalArgumentException
	 *             Wenn der Zeitstempel nicht im G&uuml;ltigkeitsbereich der
	 *             Ganglinie liegt
	 */
	public Stuetzstelle getStuetzstelle(long zeitstempel) {
		if (!isValid(zeitstempel)) {
			throw new IllegalArgumentException(
					"Der Zeitstempel liegt nicht im Gültigkeitsbereich der Ganglinie.");
		}

		SortedSet<Stuetzstelle> kopf;

		kopf = stuetzstellen.tailSet(new Stuetzstelle(zeitstempel));

		if (!kopf.isEmpty() && kopf.first().zeitstempel == zeitstempel) {
			return kopf.first();
		}

		return null;
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
		return getStuetzstelle(zeitstempel) != null;
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

		if (kopf.isEmpty()) {
			return null;
		}

		return kopf.last();
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

		if (kopf.isEmpty()) {
			return null;
		}

		return kopf.first();
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
	 * Gibt die St&uuml;tzstelle zu einem bestimmten Zeitstempel zur&uuml;ck.
	 * Existiert die St&uuml;tzstelle wird diese zur&uuml;ckgegeben. Andernfalls
	 * wird der Wert zum Zeitstempel mit der eingestellten Approximation
	 * berechnet.
	 * <p>
	 * {@inheritDoc}
	 * 
	 * @param zeitstempel
	 *            Ein Zeitpunkt
	 * @return Die St&uuml;tzstelle zum Zeitpunkt
	 */
	public Stuetzstelle get(long zeitstempel) {
		if (isValid(zeitstempel)) {
			Stuetzstelle s;

			// Wenn echte Stützstelle vorhanden, diese benutzen
			s = new Stuetzstelle(zeitstempel);
			s = stuetzstellen.tailSet(s).first();
			if (s.zeitstempel == zeitstempel) {
				return s;
			}

			// Ansonsten genäherte Stützstelle verwenden
			return approximation.get(zeitstempel);
		}

		return null;
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
