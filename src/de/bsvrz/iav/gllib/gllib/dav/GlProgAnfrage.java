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

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.bitctrl.util.Intervall;

/**
 * Repr&auml;sentiert eine einzelne Anfrage einer Anfragenachricht an die
 * Ganglinienprognose.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GlProgAnfrage {

	/** Messquerschnitt f&uuml;r den eine Ganglinie angefragt wird. */
	private MessQuerschnittAllgemein messQuerschnitt;

	/** Der Zeitraum in f&uuml;r den die Ganglinie bestimmt werden soll. */
	private Intervall prognoseZeitraum;

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
	public GlProgAnfrage() {
		ereignisTypen = new HashSet<EreignisTyp>();
	}

	/**
	 * Generiert eine einmalige Anfrage. Die Option "zyklische Anfrage" wird auf
	 * {@code false} gesetzt und die davon abh&auml;ngigen Parameter mit
	 * Defaultwerten belegt.
	 * 
	 * @param mq
	 *            der Messquerschnitt f&uuml;r den eine Ganglinie angefragt
	 *            wird.
	 * @param prognoseZeitraum
	 *            der Zeitraum der Prognose.
	 * @param nurLangfristigeAuswahl
	 *            Nur Auswahlverfahren der langfristigen Prognose benutzen?
	 */
	public GlProgAnfrage(MessQuerschnittAllgemein mq,
			Intervall prognoseZeitraum, boolean nurLangfristigeAuswahl) {
		this(mq, prognoseZeitraum, nurLangfristigeAuswahl, false, 1, 0, 1);
	}

	/**
	 * Generiert eine Anfrage.
	 * 
	 * @param mq
	 *            der Messquerschnitt f&uuml;r den eine Ganglinie angefragt
	 *            wird.
	 * @param prognoseZeitraum
	 *            der Zeitraum der Prognose.
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
	public GlProgAnfrage(MessQuerschnittAllgemein mq,
			Intervall prognoseZeitraum, boolean nurLangfristigeAuswahl,
			boolean zyklischePrognose, long pruefIntervall, double schwelle,
			long sendeIntervall) {
		this();

		if (mq == null) {
			throw new NullPointerException(
					"Der Messquerschnitt darf nicht null sein.");
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
	 * @return das ausgef&uuml;llte Datum.
	 */
	public Data getDaten(Data daten) {
		Array feld;
		int i;

		daten.getReferenceValue("Messquerschnitt").setSystemObject(
				messQuerschnitt.getSystemObject());
		daten.getTimeValue("ZeitpunktPrognoseBeginn").setMillis(
				prognoseZeitraum.getStart());
		daten.getTimeValue("ZeitpunktPrognoseEnde").setMillis(
				prognoseZeitraum.getEnde());
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
	 * Gibt die Menge der ausgeschlossenen Ereignistypen zur&uuml;ck.
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
	public Intervall getPrognoseZeitraum() {
		return prognoseZeitraum;
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
	 * Sollen nur Auswahlverfahren der langfristigen Prognose benutzt werden?
	 * 
	 * @return {@code true}, wenn dies der Fall ist, sonst {@code false}
	 */
	public boolean isNurLangfristigeAuswahl() {
		return nurLangfristigeAuswahl;
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
	 * Entfernt einen Ereignistyp aus der Filterliste.
	 * 
	 * @param typ
	 *            ein Ereignistyp der bei der Ganglinienprognose ignoriert
	 *            werden soll.
	 * @return {@code true}, wenn der Typ enthalten war und {@code false},
	 *         wenn er bereits enthalten war.
	 */
	public boolean removeEreignisTyp(EreignisTyp typ) {
		return ereignisTypen.remove(typ);
	}

	/**
	 * &Uuml;bernimmt die Informationen aus dem Datum als inneren Zustand.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param daten
	 *            ein Datum, welches <strong>eine</strong> Anfrage darstellt.
	 */
	public void setDaten(Data daten) {
		Array feld;
		long start, ende;

		messQuerschnitt = (MessQuerschnittAllgemein) ObjektFactory.getInstanz()
				.getModellobjekt(
						daten.getReferenceValue("Messquerschnitt")
								.getSystemObject());
		pruefIntervall = daten.getScaledValue("Überprüfungsintervall")
				.longValue();
		schwelle = daten.getScaledValue("Aktualisierungsschwelle").floatValue();
		sendeIntervall = daten.getScaledValue("Aktualisierungsintervall")
				.longValue();

		start = daten.getTimeValue("ZeitpunktPrognoseBeginn").getMillis();
		ende = daten.getTimeValue("ZeitpunktPrognoseEnde").getMillis();
		if (start < ende) {
			prognoseZeitraum = new Intervall(start, ende);
		} // Wenn Intervall ungültig, dann bleibt es null

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
		String s = getClass().getSimpleName() + "[";

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
