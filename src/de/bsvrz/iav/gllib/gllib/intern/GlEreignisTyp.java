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

import java.util.List;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTypParameter;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTypParameterImpl;

/**
 * Erweitert ein Ereignistyp um dessen Priorit&auml;t.
 * <p>
 * <em>Hinweis:</em> Diese Klasse ist nicht Teil der ˆffentlichen API und
 * sollte nicht auﬂerhalb der Ganglinie-API verwendet werden.
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
	public long getPrioritaet() {
		return parameter.getPrioritaet();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPrioritaet(long prioritaet) {
		parameter.setPrioritaet(prioritaet);
	}

	/**
	 * Setzt den inneren Zustand anhand des angegebenen Datums.
	 * 
	 * @param daten
	 *            ein g&uuml;ltiges Datum.
	 */
	public void setDaten(Data daten) {
		if (daten.getName().equals("atg.ereignisTypParameter")) {
			if (parameter == null) {
				parameter = new EreignisTypParameterImpl();
			}
			parameter.setDaten(daten);
		} else if (daten.getName().equals(
				"atg.ganglinienModellAutomatischesLernenEreignis")) {
			if (lernParameter == null) {
				lernParameter = new GanglinienModellAutomatischesLernenEreignisParameterImpl();
			}
			lernParameter.setDaten(daten);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ "[name="
				+ getSystemObject().getName()
				+ ", pid="
				+ getSystemObject().getPid()
				+ ", prioritaet="
				+ (parameter != null ? parameter.getPrioritaet() : null)
				+ ", ganglinienTyp="
				+ (lernParameter != null ? lernParameter.getGanglinienTyp()
						: null)
				+ ", maxAbstand="
				+ (lernParameter != null ? lernParameter.getMaxAbstand() : null)
				+ ", maxMatchingFehler="
				+ (lernParameter != null ? lernParameter.getMaxMatchingFehler()
						: null) + "]";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#getAusschlussliste()
	 */
	public List<EreignisTyp> getAusschlussliste() {
		return lernParameter.getAusschlussliste();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#getBezugsereignistypen()
	 */
	public List<EreignisTyp> getBezugsereignistypen() {
		return lernParameter.getBezugsereignistypen();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#getDarstellungsverfahren()
	 */
	public int getDarstellungsverfahren() {
		return lernParameter.getDarstellungsverfahren();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#getGanglinienTyp()
	 */
	public int getGanglinienTyp() {
		return lernParameter.getGanglinienTyp();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#getMatchingIntervallNach()
	 */
	public long getMatchingIntervallNach() {
		return lernParameter.getMatchingIntervallNach();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#getMatchingIntervallVor()
	 */
	public long getMatchingIntervallVor() {
		return lernParameter.getMatchingIntervallVor();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#getMatchingSchrittweite()
	 */
	public long getMatchingSchrittweite() {
		return lernParameter.getMatchingSchrittweite();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#getMaxAbstand()
	 */
	public int getMaxAbstand() {
		return lernParameter.getMaxAbstand();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#getMaxGanglinien()
	 */
	public long getMaxGanglinien() {
		return lernParameter.getMaxGanglinien();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#getMaxMatchingFehler()
	 */
	public int getMaxMatchingFehler() {
		return lernParameter.getMaxMatchingFehler();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#getMaxWichtungsfaktor()
	 */
	public int getMaxWichtungsfaktor() {
		return lernParameter.getMaxWichtungsfaktor();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#getVergleichsSchrittweite()
	 */
	public long getVergleichsSchrittweite() {
		return lernParameter.getVergleichsSchrittweite();
	}

}
