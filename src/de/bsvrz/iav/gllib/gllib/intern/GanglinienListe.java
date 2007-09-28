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
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.intern;

import java.util.ArrayList;
import java.util.Collection;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.MessQuerschnitt;

/**
 * Hilfsklasse zum Bearbeiten aller Ganglinien eines Messquerschnitts. Alle
 * Methoden stellen sicher, dass sich die enthaltenen Ganglinien auf den
 * Messquerschnitt aus dem Konstruktor der Liste beziehen. Ist dies einmal nicht
 * der Fall, wird der Messquerschnitt der Ganglinie entsprechend korrigiert.
 * <p>
 * <em>Hinweis:</em> Diese Klasse sollte nur intern verwendet werden. Um eine
 * Ganglinie f&uuml;r einen Messquerschnitt zu bestimmen, kann die {#link
 * de.bsvrz.iav.gllib.gllib.dav.Ganglinienprognose Ganglinienprognose} verwendet
 * werden.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GanglinienListe extends ArrayList<GanglinieMQ> {

	/** UID der Serialisierung. */
	private static final long serialVersionUID = 1L;

	/** Merkt sichden Messquerschnitt zu dem die Ganglinien geh&ouml;ren. */
	private final MessQuerschnitt messQuerschnitt;

	/**
	 * Konstruiert die Ganglinienliste.
	 * 
	 * @param messQuerschnitt
	 *            der Messquerschnitt dessen Ganglinien verwaltet werden.
	 */
	public GanglinienListe(MessQuerschnitt messQuerschnitt) {
		this.messQuerschnitt = messQuerschnitt;
	}

	/**
	 * Gibt den Messquerschnitt zur&uuml;ck, auf den sich alle Ganglinien in der
	 * Liste beziehen.
	 * 
	 * @return ein Messquerschnitt.
	 */
	public MessQuerschnitt getMessQuerschnitt() {
		return messQuerschnitt;
	}

	/**
	 * Liest alle Ganglinien aus einem Datum. Eventuell vorhandenen Ganglinien
	 * der Liste werden dabei &uuml;berschrieben.
	 * 
	 * @param daten
	 *            ein Datum, welches die Ganglinieliste eines Messquerschnitts
	 *            sein muss.
	 */
	public void setDaten(Data daten) {
		Array feld;

		feld = daten.getArray("Ganglinie");
		clear();
		for (int i = 0; i < feld.getLength(); i++) {
			GanglinieMQ g;

			g = new GanglinieMQ();
			g.setMessQuerschnitt(messQuerschnitt);
			g.setDatenVonGanglinie(feld.getItem(i));
			add(g);
		}
	}

	/**
	 * Baut aus den Informationen der Ganglinienliste einen Datensatz. Das
	 * Ergebnis wird im Parameter abgelegt!
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der ˆffentlichen API und
	 * sollte nicht auﬂerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param daten
	 *            ein Datum, das eine Ganglinienliste sein muss.
	 */
	public void getDaten(Data daten) {
		Array feld;
		int i;

		feld = daten.getArray("Ganglinie");
		feld.setLength(size());
		i = 0;
		for (GanglinieMQ g : this) {
			g.getDatenFuerGanglinie(feld.getItem(i++));
		}
	}

	/**
	 * Passt falls notwendig den Messquerschnitt an.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	@Override
	public boolean add(GanglinieMQ g) {
		if (!g.getMessQuerschnitt().equals(messQuerschnitt)) {
			g.setMessQuerschnitt(messQuerschnitt);
		}
		return super.add(g);
	}

	/**
	 * Passt falls notwendig den Messquerschnitt an.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.util.ArrayList#add(int, java.lang.Object)
	 */
	@Override
	public void add(int index, GanglinieMQ g) {
		if (!g.getMessQuerschnitt().equals(messQuerschnitt)) {
			g.setMessQuerschnitt(messQuerschnitt);
		}
		super.add(index, g);
	}

	/**
	 * Passt falls notwendig den Messquerschnitt an.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.util.ArrayList#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends GanglinieMQ> c) {
		for (GanglinieMQ g : c) {
			if (!g.getMessQuerschnitt().equals(messQuerschnitt)) {
				g.setMessQuerschnitt(messQuerschnitt);
			}
		}
		return super.addAll(c);
	}

	/**
	 * Passt falls notwendig den Messquerschnitt an.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.util.ArrayList#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int index, Collection<? extends GanglinieMQ> c) {
		for (GanglinieMQ g : c) {
			if (!g.getMessQuerschnitt().equals(messQuerschnitt)) {
				g.setMessQuerschnitt(messQuerschnitt);
			}
		}
		return super.addAll(index, c);
	}

}
