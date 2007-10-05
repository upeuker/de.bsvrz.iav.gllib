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

package de.bsvrz.iav.gllib.gllib.intern;

import java.util.List;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTypParameter;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTypParameterImpl;

/**
 * Erweitert ein Ereignistyp um dessen Priorit&auml;t.
 * <p>
 * <em>Hinweis:</em> Diese Klasse ist nicht Teil der öffentlichen API und
 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GlEreignisTyp extends EreignisTyp implements EreignisTypParameter,
		GanglinienModellAutomatischesLernenEreignisParameter {

	/** Die Priori&auml;t des Ereignistyps. */
	private EreignisTypParameter parameter;

	/** Der Parameter f&uuml;r das Ganglinienlernen. */
	private GanglinienModellAutomatischesLernenEreignisParameterImpl lernParameter;

	/**
	 * Ruft den Superkonstruktor auf.
	 * 
	 * @param obj
	 *            ein Systemobjekt, welches ein Ereignistyp sein muss.
	 */
	public GlEreignisTyp(SystemObject obj) {
		super(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EreignisTyp> getAusschlussliste() {
		return lernParameter.getAusschlussliste();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EreignisTyp> getBezugsereignistypen() {
		return lernParameter.getBezugsereignistypen();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getDarstellungsverfahren() {
		return lernParameter.getDarstellungsverfahren();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getGanglinienTyp() {
		return lernParameter.getGanglinienTyp();
	}

	/**
	 * {@inheritDoc}
	 */
	public long getMatchingIntervallNach() {
		return lernParameter.getMatchingIntervallNach();
	}

	/**
	 * {@inheritDoc}
	 */
	public long getMatchingIntervallVor() {
		return lernParameter.getMatchingIntervallVor();
	}

	/**
	 * {@inheritDoc}
	 */
	public long getMatchingSchrittweite() {
		return lernParameter.getMatchingSchrittweite();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMaxAbstand() {
		return lernParameter.getMaxAbstand();
	}

	/**
	 * {@inheritDoc}
	 */
	public long getMaxGanglinien() {
		return lernParameter.getMaxGanglinien();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMaxMatchingFehler() {
		return lernParameter.getMaxMatchingFehler();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMaxWichtungsfaktor() {
		return lernParameter.getMaxWichtungsfaktor();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see EreignisTypParameter#getPrioritaet()
	 */
	public long getPrioritaet() {
		return parameter.getPrioritaet();
	}

	/**
	 * {@inheritDoc}
	 */
	public long getVergleichsSchrittweite() {
		return lernParameter.getVergleichsSchrittweite();
	}

	/**
	 * Setzt den inneren Zustand anhand des angegebenen Datums.
	 * 
	 * @param daten
	 *            ein g&uuml;ltiges Datum.
	 */
	public void setDaten(Data daten) {
		if (daten.getName().equals(EreignisTypParameter.ATG_PARAMETER)) {
			if (parameter == null) {
				parameter = new EreignisTypParameterImpl();
			}
			parameter.setDaten(daten);
		} else if (daten
				.getName()
				.equals(
						GanglinienModellAutomatischesLernenEreignisParameter.ATG_PARAMETER)) {
			if (lernParameter == null) {
				lernParameter = new GanglinienModellAutomatischesLernenEreignisParameterImpl();
			}
			lernParameter.setDaten(daten);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPrioritaet(long prioritaet) {
		parameter.setPrioritaet(prioritaet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String s;

		s = getClass().getSimpleName() + "[name=" + getSystemObject().getName()
				+ ", pid=" + getSystemObject().getPid() + ", prioritaet=";
		if (parameter != null) {
			s += parameter.getPrioritaet();
		} else {
			s += null;
		}
		s += ", ganglinienTyp=";
		if (lernParameter != null) {
			s += lernParameter.getGanglinienTyp();
		} else {
			s += null;
		}
		s += ", maxAbstand=";
		if (lernParameter != null) {
			s += lernParameter.getMaxAbstand();
		} else {
			s += null;
		}
		s += ", maxMatchingFehler=";
		if (lernParameter != null) {
			s += lernParameter.getMaxMatchingFehler();
		} else {
			s += null;
		}
		s += "]";
		return s;
	}
}
