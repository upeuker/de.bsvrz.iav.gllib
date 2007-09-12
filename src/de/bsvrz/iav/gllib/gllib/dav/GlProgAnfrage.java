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

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.MessQuerschnitt;

/**
 * Repr&auml;sentiert eine einzelne Anfrage einer Anfragenachricht an die
 * Ganglinienprognose.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GlProgAnfrage {

	/** Messquerschnitt f&uuml;r den eine Ganglinie angefragt wird. */
	private MessQuerschnitt mq;

	/** Zeitpunkt des Beginns des Prognoseintervalls. */
	private long prognoseBeginn;

	/** Zeitpunkt des Endes des Prognoseintervalls. */
	private long prognoseEnde;

	/** Nur Auswahlverfahren der langfristigen Prognose benutzen? */
	private boolean nurLangfristigeAuswahl;

	/** Diese Ereignistypen werden bei der Ganglinienauswahl ignoriert. */
	private final Set<EreignisTyp> ereignisTypen;

	/** Soll eine zyklische Prognose erstellt werden? */
	private boolean zyklischePrognose;

	/** Sp&auml;testens nach dieser Zeit in Sekunden Prognose pr&uuml;fen. */
	private long pruefIntervall;

	/** Maximale &Auml;nderung in Prozent zwischen zwei zyklischen Prognosen. */
	private double schwelle;

	/** Sp&auml;testens nach dieser Zeit in Sekunden Prognose publizieren. */
	private long sendeIntervall;

	/**
	 * Konstruktor f&uuml;r Vererbung.
	 */
	protected GlProgAnfrage() {
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
	public GlProgAnfrage(MessQuerschnitt mq, long prognoseBeginn,
			long prognoseEnde, boolean nurLangfristigeAuswahl,
			boolean zyklischePrognose, long pruefIntervall, double schwelle,
			long sendeIntervall) {
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
	public GlProgAnfrage(MessQuerschnitt mq, long prognoseBeginn,
			long prognoseEnde, boolean nurLangfristigeAuswahl) {
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
	public MessQuerschnitt getMq() {
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
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param daten
	 *            ein Datum, welches eine (leere) Anfrage darstellt.
	 * @return das ausgef&uuml;llte Datum.
	 */
	public Data getDaten(Data daten) {
		Array feld;
		int i;

		daten.getReferenceValue("Messquerschnitt").setSystemObject(mq.getSystemObject());
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
	 * &Uuml;bernimmt die Informationen aus dem Datum als inneren Zustand.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param daten
	 *            ein Datum, welches eine Anfrage darstellt.
	 */
	public void setDaten(Data daten) {
		Array feld;

		mq = (MessQuerschnitt) ObjektFactory.getModellobjekt(daten
				.getReferenceValue("Messquerschnitt").getSystemObject());
		prognoseBeginn = daten.getTimeValue("ZeitpunktPrognoseBeginn")
				.getMillis();
		prognoseEnde = daten.getTimeValue("ZeitpunktPrognoseEnde").getMillis();
		pruefIntervall = daten.getScaledValue("Überprüfungsintervall")
				.longValue();
		schwelle = daten.getScaledValue("Aktualisierungsschwelle").floatValue();
		sendeIntervall = daten.getScaledValue("Aktualisierungsintervall")
				.longValue();

		if (daten.getUnscaledValue("NurLangfristigeAuswahl").getText().equals(
				"Ja")) {
			nurLangfristigeAuswahl = true;
		} else {
			nurLangfristigeAuswahl = false;
		}

		if (daten.getUnscaledValue("ZyklischePrognose").getText().equals("Ja")) {
			zyklischePrognose = true;
		} else {
			zyklischePrognose = false;
		}

		ereignisTypen.clear();
		feld = daten.getArray("EreignisTyp");
		for (int i = 0; i < feld.getLength(); i++) {
			ereignisTypen.add(new EreignisTyp(feld.getItem(i)
					.asReferenceValue().getSystemObject()));
		}
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
