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

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.HashSet;
import java.util.Set;

import stauma.dav.clientside.Data;
import stauma.dav.clientside.Data.Array;
import stauma.dav.configuration.interfaces.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTyp;

/**
 * Repr&auml;sentiert eine einzelne Anfrage einer Anfragenachricht an die
 * Ganglinienprognose.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class Anfrage {

	/** Messquerschnitt f&uuml;r den eine Ganglinie angefragt wird. */
	protected SystemObject mq;

	/** Zeitpunkt des Beginns des Prognoseintervalls. */
	protected long prognoseBeginn;

	/** Zeitpunkt des Endes des Prognoseintervalls. */
	protected long prognoseEnde;

	/** Nur Auswahlverfahren der langfristigen Prognose benutzen? */
	protected boolean nurLangfristigeAuswahl;

	/** Diese Ereignistypen werden bei der Ganglinienauswahl ignoriert. */
	protected final Set<EreignisTyp> ereignisTypen;

	/** Soll eine zyklische Prognose erstellt werden? */
	protected boolean zyklischePrognose;

	/** Sp&auml;testens nach dieser Zeit in Sekunden Prognose pr&uuml;fen. */
	protected long pruefIntervall;

	/** Maximale &Auml;nderung in Prozent zwischen zwei zyklischen Prognosen. */
	protected double schwelle;

	/** Sp&auml;testens nach dieser Zeit in Sekunden Prognose publizieren. */
	protected long sendeIntervall;

	/**
	 * Konstruktor f&uuml;r Vererbung.
	 */
	protected Anfrage() {
		ereignisTypen = new HashSet<EreignisTyp>();
	}

	/**
	 * Generiert eine Anfrage.
	 * 
	 * @param mq
	 *            der Messquerschnitt f&uuml;r den eine Ganglinie angefragt
	 *            wird.
	 * @param prognoseBeginn
	 *            der Zeitpunkt des Beginns des Prognoseintervalls.
	 * @param prognoseEnde
	 *            der Zeitpunkt des Endes des Prognoseintervalls.
	 * @param nurLangfristigeAuswahl
	 *            Nur Auswahlverfahren der langfristigen Prognose benutzen?
	 * @param zyklischePrognose
	 *            Soll eine zyklische Prognose erstellt werden?
	 * @param pruefIntervall
	 *            Sp&auml;testens nach dieser Zeit in Sekunden Prognose
	 *            pr&uuml;fen.
	 * @param schwelle
	 *            Maximale &Auml;nderung in Prozent zwischen zwei zyklischen
	 *            Prognosen.
	 * @param sendeIntervall
	 *            Sp&auml;testens nach dieser Zeit in Sekunden Prognose
	 *            publizieren.
	 */
	public Anfrage(SystemObject mq, long prognoseBeginn, long prognoseEnde,
			boolean nurLangfristigeAuswahl, boolean zyklischePrognose,
			long pruefIntervall, double schwelle, long sendeIntervall) {
		this();

		if (mq == null) {
			throw new NullPointerException(
					"Der Messquerschnitt darf nicht null sein.");
		}
		if (prognoseBeginn > prognoseEnde) {
			throw new IllegalArgumentException(
					"Der Prognosebeginn darf nicht vor dem Prognoseende liegen.");
		}
		if (prognoseBeginn <= 0 || prognoseEnde <= 0) {
			throw new IllegalArgumentException(
					"Prognosebeginn und -ende müssen größer 0 sein.");
		}
		if (pruefIntervall <= 0) {
			throw new IllegalArgumentException(
					"Das Überprüfungsintervall muss größer 0 sein.");
		}
		if (schwelle < 0) {
			throw new IllegalArgumentException(
					"Die Aktualisierungsschwelle muss positiv und kleiner 100 sein.");
		}
		if (sendeIntervall <= 0) {
			throw new IllegalArgumentException(
					"Das Aktualisierungsintervall muss größer 0 sein.");
		}

		this.mq = mq;
		this.prognoseBeginn = prognoseBeginn;
		this.prognoseEnde = prognoseEnde;
		this.nurLangfristigeAuswahl = nurLangfristigeAuswahl;
		this.zyklischePrognose = zyklischePrognose;
		this.pruefIntervall = pruefIntervall;
		this.schwelle = schwelle;
		this.sendeIntervall = sendeIntervall;
	}

	/**
	 * Generiert eine einmalige Anfrage. Die Option "zyklische Anfrage" wird auf
	 * {@code false} gesetzt und die davon abh&auml;ngigen Parameter mit
	 * Defaultwerten belegt.
	 * 
	 * @param mq
	 *            der Messquerschnitt f&uuml;r den eine Ganglinie angefragt
	 *            wird.
	 * @param prognoseBeginn
	 *            der Zeitpunkt des Beginns des Prognoseintervalls.
	 * @param prognoseEnde
	 *            der Zeitpunkt des Endes des Prognoseintervalls.
	 * @param nurLangfristigeAuswahl
	 *            Nur Auswahlverfahren der langfristigen Prognose benutzen?
	 */
	public Anfrage(SystemObject mq, long prognoseBeginn, long prognoseEnde,
			boolean nurLangfristigeAuswahl) {
		this(mq, prognoseBeginn, prognoseEnde, nurLangfristigeAuswahl, false,
				1, 0, 1);
	}

	/**
	 * Gibt die Anzahl der ausgeschlossenen Ereignistypen dieser Anfrage
	 * zur&uuml;ck.
	 * 
	 * @return Anzahl der Ereignistypen
	 */
	public int getAnzahlEreignisTypen() {
		return ereignisTypen.size();
	}

	/**
	 * F&uuml;gt einen Ereignistyp der Filterliste hinzu.
	 * 
	 * @param typ
	 *            ein Ereignistyp
	 * @return {@code true}, wenn der Typ hinzugef&uuml;gt wurde und
	 *         {@code false}, wenn er bereits enthalten war.
	 */
	public boolean addEreignisTyp(EreignisTyp typ) {
		return ereignisTypen.add(typ);
	}

	/**
	 * Entfernt einen Ereignistyp aus der Filterliste.
	 * 
	 * @param typ
	 *            ein Ereignistyp
	 * @return {@code true}, wenn der Typ enthalten war und {@code false},
	 *         wenn er bereits enthalten war.
	 */
	public boolean removeEreignisTyp(EreignisTyp typ) {
		return ereignisTypen.remove(typ);
	}

	/**
	 * Gibt einen Iterator &uuml;ber die ausgeschlossenen Ereignistypen
	 * zur&uuml;ck.
	 * 
	 * @return Ereignistypeniterator
	 */
	public Set<EreignisTyp> getEreignisTypen() {
		return new HashSet<EreignisTyp>(ereignisTypen);
	}

	/**
	 * Gibt den Messquerschnitt f&uuml;r den eine Ganglinie angefragt wird
	 * zur&uuml;ck.
	 * 
	 * @return Ein Messquerschnitt
	 */
	public SystemObject getMq() {
		return mq;
	}

	/**
	 * Sollen nur Auswahlverfahren der langfristigen Prognose benutzt werden?
	 * 
	 * @return {@code true}, wenn dies der Fall ist, sonst {@code false}
	 */
	public boolean isNurLangfristigeAuswahl() {
		return nurLangfristigeAuswahl;
	}

	/**
	 * Gibt den Zeitpunkt des Beginns des Prognoseintervalls zur&uuml;ck.
	 * 
	 * @return Zeitpunkt in Millisekunden
	 */
	public long getPrognoseBeginn() {
		return prognoseBeginn;
	}

	/**
	 * Gibt den Zeitpunkt des Endes des Prognoseintervalls zur&uuml;ck.
	 * 
	 * @return Zeitpunkt in Millisekunden
	 */
	public long getPrognoseEnde() {
		return prognoseEnde;
	}

	/**
	 * Sp&auml;testens nach dieser Zeit in Sekunden wird die Prognose
	 * gepr&uuml;ft.
	 * <p>
	 * TODO: Was wird nach dieser Zeit geprüft?
	 * 
	 * @return Pr&uuml;fintervall in Sekunden
	 * @see #isZyklischePrognose()
	 */
	public long getPruefIntervall() {
		return pruefIntervall;
	}

	/**
	 * Maximale &Auml;nderung in Prozent zwischen zwei zyklischen Prognosen.
	 * Wird dieser Schwellwert &uuml;berschritten, wird eine neue Prognose
	 * publiziert.
	 * 
	 * @return Schwellwert in Prozent
	 * @see #isZyklischePrognose()
	 */
	public double getSchwelle() {
		return schwelle;
	}

	/**
	 * Sp&auml;testens nach dieser Zeit in Sekunden wird eine Prognose
	 * publiziert. Die Ganglinie wird nach dieser Zeit auch publiziert, wenn sie
	 * sich nicht ge&auml;ndert hat.
	 * 
	 * @return Zyklus des Publizierens in Sekunden
	 * @see #isZyklischePrognose()
	 */
	public long getSendeIntervall() {
		return sendeIntervall;
	}

	/**
	 * Gibt zur&uuml;ck, ob es sich um eine zyklische oder einmalige Prognose
	 * handelt.
	 * 
	 * @return {@code true}, wenn die Prognose zyklisch wiederholt wird und
	 *         {@code false}, wenn die Prognose nur einmal durchgef&uuml;hrt
	 *         wird
	 * @see #getPruefIntervall()
	 * @see #getSchwelle()
	 * @see #getSendeIntervall()
	 */
	public boolean isZyklischePrognose() {
		return zyklischePrognose;
	}

	/**
	 * Baut aus den Informationen der Anfrage ein Datum.
	 * <p>
	 * Hinweis: Das Ergebnis wird auch im Parameter abgelegt!
	 * 
	 * @param daten
	 *            ein Datum, welches eine (leere) Anfrage darstellt.
	 * @return das ausgef&uuml;llte Datum.
	 */
	protected Data getDaten(Data daten) {
		Array feld;
		int i;

		daten.getReferenceValue("Messquerschnitt").setSystemObject(mq);
		daten.getTimeValue("ZeitpunktPrognoseBeginn").setMillis(prognoseBeginn);
		daten.getTimeValue("ZeitpunktPrognoseEnde").setMillis(prognoseEnde);
		daten.getScaledValue("Überprüfungsintervall").set(pruefIntervall);
		daten.getScaledValue("Aktualisierungsschwelle").set(schwelle);
		daten.getScaledValue("Aktualisierungsintervall").set(sendeIntervall);

		if (nurLangfristigeAuswahl) {
			daten.getUnscaledValue("NurLangfristigeAuswahl").setText("Ja");
		} else {
			daten.getUnscaledValue("NurLangfristigeAuswahl").setText("Nein");
		}

		if (zyklischePrognose) {
			daten.getUnscaledValue("ZyklischePrognose").setText("Ja");
		} else {
			daten.getUnscaledValue("ZyklischePrognose").setText("Nein");
		}

		feld = daten.getArray("EreignisTyp");
		feld.setLength(ereignisTypen.size());
		i = 0;
		for (EreignisTyp et : ereignisTypen) {
			feld.getItem(i++).asReferenceValue().setSystemObject(
					et.getSystemObject());
		}

		return daten;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if (isZyklischePrognose()) {
			return "Zyklische Anfrage für " + mq + " von " + prognoseBeginn
					+ " bis " + prognoseEnde + " alle " + getSendeIntervall()
					+ " Sekunden.";
		}

		return "Einmalige Anfrage für " + mq + " von " + prognoseBeginn
				+ " bis " + prognoseEnde;
	}

}
