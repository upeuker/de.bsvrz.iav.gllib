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

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.HashSet;
import java.util.Set;

import com.bitctrl.Constants;
import com.bitctrl.util.Interval;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;

/**
 * Repräsentiert eine einzelne Anfrage einer Anfragenachricht an die
 * Ganglinienprognose.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GlProgAnfrage {

	/** Messquerschnitt für den eine Ganglinie angefragt wird. */
	private MessQuerschnittAllgemein messQuerschnitt;

	/** Der Zeitraum in für den die Ganglinie bestimmt werden soll. */
	private Interval prognoseZeitraum;

	/** Nur Auswahlverfahren der langfristigen Prognose benutzen? */
	private boolean nurLangfristigeAuswahl;

	/** Diese Ereignistypen werden bei der Ganglinienauswahl ignoriert. */
	private final Set<EreignisTyp> ereignisTypen;

	/** Soll eine zyklische Prognose erstellt werden? */
	private boolean zyklischePrognose;

	/** Spätestens nach dieser Zeit die Prognose prüfen. */
	private long pruefIntervall;

	/** Maximale &Auml;nderung in Prozent zwischen zwei zyklischen Prognosen. */
	private int schwelle;

	/** Spätestens nach dieser Zeit eine Prognose publizieren. */
	private long sendeIntervall;

	/**
	 * Standardkonstruktor.
	 */
	public GlProgAnfrage() {
		ereignisTypen = new HashSet<EreignisTyp>();
	}

	/**
	 * Generiert eine einmalige Anfrage. Die Option "zyklische Anfrage" wird auf
	 * {@code false} gesetzt und die davon abhängigen Parameter mit
	 * Defaultwerten belegt.
	 * 
	 * @param mq
	 *            der Messquerschnitt für den eine Ganglinie angefragt wird.
	 * @param prognoseZeitraum
	 *            der Zeitraum der Prognose.
	 * @param nurLangfristigeAuswahl
	 *            Nur Auswahlverfahren der langfristigen Prognose benutzen?
	 */
	public GlProgAnfrage(final MessQuerschnittAllgemein mq,
			final Interval prognoseZeitraum,
			final boolean nurLangfristigeAuswahl) {
		this(mq, prognoseZeitraum, nurLangfristigeAuswahl, false,
				Constants.MILLIS_PER_SECOND, 0, Constants.MILLIS_PER_SECOND);
	}

	/**
	 * Generiert eine Anfrage.
	 * 
	 * @param mq
	 *            der Messquerschnitt für den eine Ganglinie angefragt wird.
	 * @param prognoseZeitraum
	 *            der Zeitraum der Prognose.
	 * @param nurLangfristigeAuswahl
	 *            Nur Auswahlverfahren der langfristigen Prognose benutzen?
	 * @param zyklischePrognose
	 *            Soll eine zyklische Prognose erstellt werden?
	 * @param pruefIntervall
	 *            Spätestens nach dieser Zeit die Prognose prüfen.
	 * @param schwelle
	 *            Maximale &Auml;nderung in Prozent zwischen zwei zyklischen
	 *            Prognosen.
	 * @param sendeIntervall
	 *            Spätestens nach dieser Zeit eine Prognose publizieren.
	 */
	public GlProgAnfrage(final MessQuerschnittAllgemein mq,
			final Interval prognoseZeitraum,
			final boolean nurLangfristigeAuswahl,
			final boolean zyklischePrognose, final long pruefIntervall,
			final int schwelle, final long sendeIntervall) {
		this();

		this.messQuerschnitt = mq;
		this.prognoseZeitraum = prognoseZeitraum;
		this.nurLangfristigeAuswahl = nurLangfristigeAuswahl;
		this.zyklischePrognose = zyklischePrognose;
		this.pruefIntervall = pruefIntervall;
		this.schwelle = schwelle;
		this.sendeIntervall = sendeIntervall;
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
	 * @return das ausgefüllte Datum.
	 */
	public Data getDaten(final Data daten) {
		Array feld;
		int i;

		daten.getReferenceValue("Messquerschnitt").setSystemObject(
				messQuerschnitt.getSystemObject());
		daten.getTimeValue("ZeitpunktPrognoseBeginn").setMillis(
				prognoseZeitraum.getStart());
		daten.getTimeValue("ZeitpunktPrognoseEnde").setMillis(
				prognoseZeitraum.getEnd());
		daten.getScaledValue("Überprüfungsintervall").set(
				pruefIntervall / Constants.MILLIS_PER_SECOND);
		daten.getScaledValue("Aktualisierungsschwelle").set(schwelle);
		daten.getScaledValue("Aktualisierungsintervall").set(
				sendeIntervall / Constants.MILLIS_PER_SECOND);

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
		for (final EreignisTyp et : ereignisTypen) {
			feld.getItem(i++).asReferenceValue().setSystemObject(
					et.getSystemObject());
		}

		return daten;
	}

	/**
	 * Gibt die Menge der ausgeschlossenen Ereignistypen zurück.
	 * 
	 * @return Ereignistypeniterator
	 */
	public Set<EreignisTyp> getEreignisTypen() {
		return new HashSet<EreignisTyp>(ereignisTypen);
	}

	/**
	 * Gibt den Messquerschnitt für den eine Ganglinie angefragt wird zurück.
	 * 
	 * @return Ein Messquerschnitt
	 */
	public MessQuerschnittAllgemein getMessQuerschnitt() {
		return messQuerschnitt;
	}

	/**
	 * Gibt den Prognosezeitraum zurück.
	 * 
	 * @return der Zeitraum für den die Ganglinie bestimmt wird oder
	 *         {@code null}, wenn kein Intervall gesetzt ist bzw. das Intervall
	 *         ungültig ist. Bei einem ungültigen Intervall liegt der
	 *         Startzeitpunkt <em>hinter</em> dem Endzeitpunkt.
	 */
	public Interval getPrognoseZeitraum() {
		return prognoseZeitraum;
	}

	/**
	 * Spätestens nach dieser Zeit wird die Prognose geprüft.
	 * 
	 * @return das Prüfintervall
	 * @see #isZyklischePrognose()
	 */
	public long getPruefIntervall() {
		return pruefIntervall;
	}

	/**
	 * Maximale &Auml;nderung in Prozent zwischen zwei zyklischen Prognosen.
	 * Wird dieser Schwellwert überschritten, wird eine neue Prognose
	 * publiziert.
	 * 
	 * @return Schwellwert in Prozent.
	 * @see #isZyklischePrognose()
	 */
	public int getSchwelle() {
		return schwelle;
	}

	/**
	 * Spätestens nach dieser Zeit wird eine Prognose publiziert. Die Ganglinie
	 * wird nach dieser Zeit auch publiziert, wenn sie sich nicht geändert hat.
	 * 
	 * @return der Zyklus des Publizierens.
	 * @see #isZyklischePrognose()
	 */
	public long getSendeIntervall() {
		return sendeIntervall;
	}

	/**
	 * Sollen nur Auswahlverfahren der langfristigen Prognose benutzt werden?
	 * 
	 * @return {@code true}, wenn dies der Fall ist, sonst {@code false}.
	 */
	public boolean isNurLangfristigeAuswahl() {
		return nurLangfristigeAuswahl;
	}

	/**
	 * Gibt zurück, ob es sich um eine zyklische oder einmalige Prognose
	 * handelt.
	 * 
	 * @return {@code true}, wenn die Prognose zyklisch wiederholt wird und
	 *         {@code false}, wenn die Prognose nur einmal durchgeführt wird
	 * @see #getPruefIntervall()
	 * @see #getSchwelle()
	 * @see #getSendeIntervall()
	 */
	public boolean isZyklischePrognose() {
		return zyklischePrognose;
	}

	/**
	 * Entfernt einen Ereignistyp aus der Filterliste.
	 * 
	 * @param typ
	 *            ein Ereignistyp der bei der Ganglinienprognose ignoriert
	 *            werden soll.
	 * @return {@code true}, wenn der Typ enthalten war und {@code false},
	 *         wenn er bereits enthalten war.
	 */
	public boolean removeEreignisTyp(final EreignisTyp typ) {
		return ereignisTypen.remove(typ);
	}

	/**
	 * übernimmt die Informationen aus dem Datum als inneren Zustand.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param daten
	 *            ein Datum, welches <strong>eine</strong> Anfrage darstellt.
	 */
	public void setDaten(final Data daten) {
		Array feld;
		long start, ende;

		messQuerschnitt = (MessQuerschnittAllgemein) ObjektFactory.getInstanz().getModellobjekt(
				daten.getReferenceValue("Messquerschnitt").getSystemObject());
		pruefIntervall = daten.getScaledValue("Überprüfungsintervall").longValue()
				* Constants.MILLIS_PER_SECOND;
		schwelle = daten.getScaledValue("Aktualisierungsschwelle").intValue();
		sendeIntervall = daten.getScaledValue("Aktualisierungsintervall").longValue()
				* Constants.MILLIS_PER_SECOND;

		start = daten.getTimeValue("ZeitpunktPrognoseBeginn").getMillis();
		ende = daten.getTimeValue("ZeitpunktPrognoseEnde").getMillis();
		if (start <= ende) {
			prognoseZeitraum = new Interval(start, ende);
		} // Wenn Intervall ungültig, dann bleibt es null

		if (daten.getUnscaledValue("NurLangfristigeAuswahl").intValue() == 0) {
			nurLangfristigeAuswahl = false;
		} else {
			nurLangfristigeAuswahl = true;
		}

		if (daten.getUnscaledValue("ZyklischePrognose").intValue() == 0) {
			zyklischePrognose = false;
		} else {
			zyklischePrognose = true;
		}

		ereignisTypen.clear();
		feld = daten.getArray("EreignisTyp");
		for (int i = 0; i < feld.getLength(); i++) {
			ereignisTypen.add(new EreignisTyp(
					feld.getItem(i).asReferenceValue().getSystemObject()));
		}
	}

	/**
	 * Ändert den Messquerschnitt für den angefragt wird.
	 * 
	 * @param messQuerschnitt
	 *            ein Messquerschnitt.
	 */
	public void setMessQuerschnitt(
			final MessQuerschnittAllgemein messQuerschnitt) {
		this.messQuerschnitt = messQuerschnitt;
	}

	/**
	 * Ändert den Prognosezeitraum.
	 * 
	 * @param prognoseZeitraum
	 *            der neue Prognosezeitraum.
	 */
	public void setPrognoseZeitraum(final Interval prognoseZeitraum) {
		this.prognoseZeitraum = prognoseZeitraum;
	}

	/**
	 * Setzt das Flag für die langfristigen Auswahlmethoden.
	 * 
	 * @param nurLangfristigeAuswahl
	 *            {@code true}, wenn nur langfristige Auswahlmethoden verwendet
	 *            werden sollen.
	 */
	public void setNurLangfristigeAuswahl(final boolean nurLangfristigeAuswahl) {
		this.nurLangfristigeAuswahl = nurLangfristigeAuswahl;
	}

	/**
	 * Setzt Flag für eine zyklische Prognose.
	 * 
	 * @param zyklischePrognose
	 *            {@code false}, wenn die Prognose einmalig ausgeführt werden
	 *            soll und {@code true}, wenn die Prognose zyklisch ausgeführt
	 *            werdens soll.
	 */
	public void setZyklischePrognose(final boolean zyklischePrognose) {
		this.zyklischePrognose = zyklischePrognose;
	}

	/**
	 * Legt das Intervall fest in dem die Prognose überprüft werden soll.
	 * 
	 * @param pruefIntervall
	 *            das Prüfintervall.
	 */
	public void setPruefIntervall(final long pruefIntervall) {
		this.pruefIntervall = pruefIntervall;
	}

	/**
	 * Legt die Änderungsschwelle fest, bei der eine neue Prognoseganglinie
	 * gesendet werden soll.
	 * 
	 * @param schwelle
	 *            die Schwelle.
	 */
	public void setSchwelle(final int schwelle) {
		this.schwelle = schwelle;
	}

	/**
	 * Legt das Intervall fest, in dem mindestens eine Prognoseganglinie
	 * versendet werden soll.
	 * 
	 * @param sendeIntervall
	 *            das Intervall.
	 */
	public void setSendeIntervall(final long sendeIntervall) {
		this.sendeIntervall = sendeIntervall;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String s = getClass().getName() + "[";

		s += "messQuerschnitt=" + messQuerschnitt;
		s += ", prognoseZeitraum=" + prognoseZeitraum;
		s += ", nurLangfristigeAuswahl=" + nurLangfristigeAuswahl;
		s += ", ereignisTypen=" + ereignisTypen;
		s += ", zyklischePrognose=" + zyklischePrognose;
		s += ", pruefIntervall=" + pruefIntervall;
		s += ", schwelle=" + schwelle;
		s += ", sendeIntervall=" + sendeIntervall;

		return s + "]";
	}
}
