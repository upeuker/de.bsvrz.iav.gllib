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

import java.util.SortedSet;
import java.util.TreeSet;

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
public class Ganglinie extends TreeSet<Stuetzstelle> implements Approximation {

	/** Verfahren zur Berechnung der Punkte zwischen den St&uuml;tzstellen. */
	private Approximation approximation = new BSpline();

	/**
	 * Gibt das Zeitintervall der Ganglinie zur&uuml;ck.
	 * 
	 * @return Ein {@link Intervall} oder {@code null}, wenn keine
	 *         Stz&uuml;tzstellen vorhanden sind
	 */
	public Intervall getIntervall() {
		if (size() == 0) {
			return null;
		}

		return new Intervall(first().zeitstempel, last().zeitstempel);
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
	public boolean contains(long zeitstempel) {
		if (size() == 0) {
			return false;
		}

		return first().zeitstempel <= zeitstempel
				&& zeitstempel <= last().zeitstempel;
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
		clear();
		addAll(ganglinie);
	}

	/**
	 * Gibt die St&uuml;tzstelle zu einem bestimmten Zeitstempel zur&uuml;ck.
	 * Existiert die St&uuml;tzstelle wird diese zur&uuml;ckgegeben. Andernfalls
	 * wird der Wert zum Zeitstempel approximiert.
	 * <p>
	 * TODO: Approximation einbauen
	 * <p>
	 * {@inheritDoc}
	 * 
	 * @param zeitstempel
	 *            Ein Zeitpunkt
	 * @return Die St&uuml;tzstelle zum Zeitpunkt
	 */
	public Stuetzstelle getStuetzstelle(long zeitstempel) {
		if (contains(zeitstempel)) {
			Stuetzstelle s;

			// Wenn echte Stützstelle vorhanden, diese benutzen
			s = new Stuetzstelle(zeitstempel);
			s = tailSet(s).first();
			if (s.zeitstempel == zeitstempel) {
				return s;
			}

			// Ansonsten genäherte Stützstelle verwenden
			return approximation.getStuetzstelle(zeitstempel);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<Stuetzstelle> getInterpolation(int anzahlIntervalle) {
		return approximation.getInterpolation(anzahlIntervalle);
	}

	/**
	 * Gibt Zeilenweise die St&uuml;tzstellen zur&uuml;ck.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.util.AbstractCollection#toString()
	 */
	@SuppressWarnings("nls")
	@Override
	public String toString() {
		String result;

		result = Messages.get(GlLibMessages.Ganglinie) + " " + getIntervall()
				+ ":\n";
		for (Stuetzstelle s : this) {
			result += "\t" + s + "\n";
		}

		return result;
	}

}
