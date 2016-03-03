/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2011-2015 BitCtrl Systems GmbH
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.bitctrl.util.Interval;

import de.bsvrz.iav.gllib.gllib.Approximation;
import de.bsvrz.iav.gllib.gllib.BSpline;
import de.bsvrz.iav.gllib.gllib.CubicSpline;
import de.bsvrz.iav.gllib.gllib.Ganglinie;
import de.bsvrz.iav.gllib.gllib.Polyline;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.iav.gllib.gllib.modell.db.DbGanglinie;
import de.bsvrz.iav.gllib.gllib.modell.db.DbStuetzstelle;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;

/**
 * Für Messquerschnitte angepasste Ganglinie. Die vier Verkehrswerte QKfz, QLkw,
 * VPkw und VLkw werden als Gruppe pro Zeitstempel gesichert. Aus diesen Werten
 * lassen sich die drei davon abhängigen Größe QPkw, VKfz und QB berechnen.
 *
 * @author BitCtrl Systems GmbH, Falko Schumann
 */
public class GanglinieMQ extends Ganglinie<Messwerte> {

	/**
	 * Die Attributgruppe, in der historische Ganglinien gesichert werden:
	 * {@value} .
	 */
	public static final String ATG_GANGLINIE = "atg.ganglinie"; //$NON-NLS-1$

	/** Datenkatalogkonstante für die unbestimmte Approximation: {@value} . */
	public static final int APPROX_UNBESTIMMT = 0;

	/** Datenkatalogkonstante für einen B-Spline: {@value} . */
	public static final int APPROX_BSPLINE = 1;

	/** Datenkatalogkonstante für einen Cubic-Spline: {@value} . */
	public static final int APPROX_CUBICSPLINE = 2;

	/** Datenkatalogkonstante für eine Polylinie: {@value} . */
	public static final int APPROX_POLYLINE = 3;

	/**
	 * Standardordung der Approximation. Nur für B-Spline relevant: {@value} .
	 */
	public static final byte APPROX_STANDARD_ORDNUNG = 5;

	/** Datenkatalogkonstante für eine absolute Ganglinie: {@value} . */
	public static final int TYP_ABSOLUT = 0;

	/**
	 * Datenkatalogkonstante für eine relative additive Ganglinie: {@value} .
	 */
	public static final int TYP_ADDITIV = 1;

	/**
	 * Datenkatalogkonstante für eine relative multiplikative Ganglinie: * * * *
	 * * {@value} .
	 */
	public static final int TYP_MULTIPLIKATIV = 2;

	/** Die Eigenschaft {@code serialVersionUID}. */
	private static final long serialVersionUID = 0;

	/** Der Messquerschnitt, zu dem die Ganglinie gehört. */
	private MessQuerschnittAllgemein messQuerschnitt;

	/** Parameter für die Berechnung von QB, Standard ist 2.0. */
	private float k1 = 2.0f;

	/** Parameter für die Berechnung von QB, Standard ist 0.01. */
	private float k2 = 0.01f;

	/**
	 * Zeitpunkt der letzten Verschmelzung, Standard ist der Zeitstempel der
	 * Instanzierung.
	 */
	private long letzteVerschmelzung = System.currentTimeMillis();

	/** Anzahl der Verschmelzung mit anderen Ganglinien, Standard ist 1. */
	private long anzahlVerschmelzungen = 1;

	/**
	 * Identifier für das mit der Ganglinie verknüpfte Ereignis, Standard ist
	 * null.
	 */
	private EreignisTyp ereignisTyp = null;

	/**
	 * Flag, ob die Ganglinie eine Referenzganglinie darstellt, Standard ist
	 * false.
	 */
	private boolean referenz = false;

	/** Typ der Ganglinie, Standard ist {@link #TYP_ABSOLUT}. */
	private int typ = TYP_ABSOLUT;

	/** Art der Approximation, Standard ist {@link #APPROX_BSPLINE}. */
	private int approximationDaK = APPROX_BSPLINE;

	/** Ordnung des B-Spline, Standard ist {@link #APPROX_STANDARD_ORDNUNG}. */
	private int bSplineOrdnung = APPROX_STANDARD_ORDNUNG;

	/** Das Intervall für das die Ganglinie prognostiziert wird. */
	private Interval prognoseZeitraum;

	/** Cache der Einzelganglinie. */
	private Ganglinie<Double> gQKfz;

	/** Cache der Einzelganglinie. */
	private Ganglinie<Double> gQLkw;

	/** Cache der Einzelganglinie. */
	private Ganglinie<Double> gVPkw;

	/** Cache der Einzelganglinie. */
	private Ganglinie<Double> gVLkw;

	/**
	 * Standardkonstruktor.
	 */
	public GanglinieMQ() {
	}

	/**
	 * Konvertiert dieses Objekt in ein Objekt vom Typ {@link DbGanglinie}, das
	 * in der Datenbank gespeichert werden kann.
	 *
	 * @return ein Objekt vom Typ {@link DbGanglinie}, das in der Datenbank
	 *         gespeichert werden kann.
	 */
	public final DbGanglinie convertToDbDatum() {
		final DbGanglinie dbGanglinie = new DbGanglinie();

		final List<Stuetzstelle<Messwerte>> stuetzstellenOrignial = getStuetzstellen();
		final List<DbStuetzstelle> dbStuetzstellen = new ArrayList<DbStuetzstelle>();

		for (int i = 0; i < stuetzstellenOrignial.size(); i++) {
			final Stuetzstelle<Messwerte> stuetzstelleOrignial = stuetzstellenOrignial
					.get(i);

			if (stuetzstelleOrignial != null) {
				DbStuetzstelle dbStuetzstelle;
				if (stuetzstelleOrignial.getWert() != null) {
					dbStuetzstelle = new DbStuetzstelle(
							stuetzstelleOrignial.getZeitstempel(),
							stuetzstelleOrignial.getWert().getQLkwNichtNull(),
							stuetzstelleOrignial.getWert().getQKfzNichtNull(),
							stuetzstelleOrignial.getWert().getVPkwNichtNull(),
							stuetzstelleOrignial.getWert().getVLkwNichtNull());
				} else {
					dbStuetzstelle = new DbStuetzstelle(
							stuetzstelleOrignial.getZeitstempel(),
							Messwerte.UNDEFINIERT, Messwerte.UNDEFINIERT,
							Messwerte.UNDEFINIERT, Messwerte.UNDEFINIERT);
				}

				dbStuetzstellen.add(dbStuetzstelle);
			}
		}

		dbGanglinie.setAnzahlVerschmelzungen(getAnzahlVerschmelzungen());
		dbGanglinie.setEreignisTypId(getEreignisTyp().getId());
		dbGanglinie.setLetzteVerschmelzung(getLetzteVerschmelzung());
		dbGanglinie.setMqPid(getMessQuerschnitt().getPid());
		dbGanglinie.setOrdnung(getBSplineOrdnung());
		dbGanglinie.setTyp(getTyp());
		dbGanglinie.setApproximationsverfahren(getApproximationDaK());
		dbGanglinie.setReferenzGanglinie(isReferenz());
		dbGanglinie.setStuetzstellen(dbStuetzstellen);

		return dbGanglinie;
	}

	/**
	 * Kopiert die St&uuml;tzstellen, das Approximationsverfahren und alle
	 * anderen Eigenschaften bis auf {@code approximationAktuell}. Der Wert für
	 * {@code approximationAktuell} wird auf false gesetzt.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public GanglinieMQ clone() {
		GanglinieMQ g;

		g = new GanglinieMQ();
		g.putAll(this);
		g.setAnzahlVerschmelzungen(anzahlVerschmelzungen);
		g.setApproximationDaK(approximationDaK);
		g.setBSplineOrdnung(bSplineOrdnung);
		g.setEreignisTyp(ereignisTyp);
		g.setK1(k1);
		g.setK2(k2);
		g.setLetzteVerschmelzung(letzteVerschmelzung);
		g.setMessQuerschnitt(messQuerschnitt);
		g.setPrognoseZeitraum(prognoseZeitraum);
		g.setReferenz(referenz);
		g.setTyp(typ);

		return g;
	}

	/**
	 * Gibt die Approximation zur Datenverteilerapproximation zurück.
	 *
	 * @return die Approximation.
	 * @see #approximationDaK
	 */
	private Approximation<Double> erzeugeApproximation() {
		switch (approximationDaK) {
		case APPROX_BSPLINE:
			return new BSpline(bSplineOrdnung);
		case APPROX_CUBICSPLINE:
			return new CubicSpline();
		case APPROX_POLYLINE:
			return new Polyline();
		default:
			return new BSpline((byte) 5);
		}
	}

	/**
	 * Gibt die Anzahl der bisherigen Verschmelzungen beim automatischen Lernen
	 * zurück.
	 *
	 * @return Anzahl bisheriger Verschmelzungen
	 */
	public long getAnzahlVerschmelzungen() {
		return anzahlVerschmelzungen;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Die Approximation der einzelnen Größen kann mit
	 *             {@code getGanglinie*.getApproximation()} abgerufen werden.
	 *             Der Typ der Approximation kann mit
	 *             {@link #getApproximationDaK()} erfragt werden.
	 */
	@Deprecated
	@Override
	public Approximation<Messwerte> getApproximation() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gibt die Art der Approximation als Datenkatalogkonstante zurück.
	 *
	 * @return eine der Konstante {@link #APPROX_POLYLINE},
	 *         {@link #APPROX_CUBICSPLINE}, {@link #APPROX_BSPLINE} oder
	 *         {@link #APPROX_UNBESTIMMT}.
	 */
	public int getApproximationDaK() {
		return approximationDaK;
	}

	/**
	 * Gibt die Ordnung des B-Spline zurück. Wird zur Approximation kein
	 * B-Spline benutzt, wird der Wert ignoriert.
	 *
	 * @return die Ordnung des B-Spline.
	 */
	public int getBSplineOrdnung() {
		return bSplineOrdnung;
	}

	/**
	 * Gibt den Ereignistyp der Ganglinie zurück.
	 *
	 * @return der Ereignistyp.
	 */
	public EreignisTyp getEreignisTyp() {
		return ereignisTyp;
	}

	/**
	 * Gibt die Ganglinie für QB zurück.
	 *
	 * @return eine einfache mathematische Ganglinie mit den Stützstellen von
	 *         QB.
	 */
	public Ganglinie<Double> getGanglinieQB() {
		Ganglinie<Double> g;

		g = new Ganglinie<Double>();
		for (final Map.Entry<Long, Messwerte> e : entrySet()) {
			g.put(e.getKey(), e.getValue().getQB());
		}
		g.setApproximation(erzeugeApproximation());

		return g;
	}

	/**
	 * Gibt die Ganglinie für QKfz zurück.
	 *
	 * @return eine einfache mathematische Ganglinie mit den Stützstellen von
	 *         QKfz.
	 */
	public Ganglinie<Double> getGanglinieQKfz() {
		Ganglinie<Double> g;

		g = new Ganglinie<Double>();
		for (final Map.Entry<Long, Messwerte> e : entrySet()) {
			g.put(e.getKey(), e.getValue().getQKfz());
		}
		g.setApproximation(erzeugeApproximation());

		return g;
	}

	/**
	 * Gibt die Ganglinie für QLkw zurück.
	 *
	 * @return eine einfache mathematische Ganglinie mit den Stützstellen von
	 *         QLkw.
	 */
	public Ganglinie<Double> getGanglinieQLkw() {
		Ganglinie<Double> g;

		g = new Ganglinie<Double>();
		for (final Map.Entry<Long, Messwerte> e : entrySet()) {
			g.put(e.getKey(), e.getValue().getQLkw());
		}
		g.setApproximation(erzeugeApproximation());

		return g;
	}

	/**
	 * Gibt die Ganglinie für QPkw zurück.
	 *
	 * @return eine einfache mathematische Ganglinie mit den Stützstellen von
	 *         QPkw.
	 */
	public Ganglinie<Double> getGanglinieQPkw() {
		Ganglinie<Double> g;

		g = new Ganglinie<Double>();
		for (final Map.Entry<Long, Messwerte> e : entrySet()) {
			g.put(e.getKey(), e.getValue().getQPkw());
		}
		g.setApproximation(erzeugeApproximation());

		return g;
	}

	/**
	 * Gibt die Ganglinie für VKfz zurück.
	 *
	 * @return eine einfache mathematische Ganglinie mit den Stützstellen von
	 *         VKfz.
	 */
	public Ganglinie<Double> getGanglinieVKfz() {
		Ganglinie<Double> g;

		g = new Ganglinie<Double>();
		for (final Map.Entry<Long, Messwerte> e : entrySet()) {
			g.put(e.getKey(), e.getValue().getVKfz());
		}
		g.setApproximation(erzeugeApproximation());

		return g;

	}

	/**
	 * Gibt die Ganglinie für VLkw zurück.
	 *
	 * @return eine einfache mathematische Ganglinie mit den Stützstellen von
	 *         VLkw.
	 */
	public Ganglinie<Double> getGanglinieVLkw() {
		Ganglinie<Double> g;

		g = new Ganglinie<Double>();
		for (final Map.Entry<Long, Messwerte> e : entrySet()) {
			g.put(e.getKey(), e.getValue().getVLkw());
		}
		g.setApproximation(erzeugeApproximation());

		return g;
	}

	/**
	 * Gibt die Ganglinie für QPkw zurück.
	 *
	 * @return eine einfache mathematische Ganglinie mit den Stützstellen von
	 *         QPkw.
	 */
	public Ganglinie<Double> getGanglinieVPkw() {
		Ganglinie<Double> g;

		g = new Ganglinie<Double>();
		for (final Map.Entry<Long, Messwerte> e : entrySet()) {
			g.put(e.getKey(), e.getValue().getVPkw());
		}
		g.setApproximation(erzeugeApproximation());

		return g;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see #setPrognoseZeitraum(Interval)
	 */
	@Override
	public Interval getIntervall() {
		if (prognoseZeitraum != null) {
			return prognoseZeitraum;
		}

		if (isEmpty()) {
			return null;
		}
		return new Interval(firstKey(), lastKey());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Die Intervalle müssen an den einzelnen Größen mit
	 *             {@code getGanglinie*.getIntervalle()} abgerufen werden.
	 */
	@Deprecated
	@Override
	public List<Interval> getIntervalle() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gibt einen Parameter für die Berechnung von QB zurück.
	 *
	 * @return der Parameter.
	 */
	public float getK1() {
		return k1;
	}

	/**
	 * Gibt einen Parameter für die Berechnung von QB zurück.
	 *
	 * @return der Parameter
	 */
	public float getK2() {
		return k2;
	}

	/**
	 * Gibt den Zeitpunkt der letzten Verschmelzung als Zeitstempel zurück.
	 *
	 * @return Zeitstempel
	 */
	public long getLetzteVerschmelzung() {
		return letzteVerschmelzung;
	}

	/**
	 * Gibt den Messquerschnitt der Ganglinie zurück.
	 *
	 * @return ein Messquerschnitt.
	 */
	public MessQuerschnittAllgemein getMessQuerschnitt() {
		return messQuerschnitt;
	}

	/**
	 * Gibt das Prognoseintervall der Ganglinie zurück.
	 *
	 * @return das Prognoseintervall.
	 */
	public Interval getPrognoseIntervall() {
		return prognoseZeitraum;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see #setPrognoseZeitraum(Interval)
	 */
	@Override
	public Stuetzstelle<Messwerte> getStuetzstelle(final long zeitstempel) {

		if ((prognoseZeitraum != null)
				&& !prognoseZeitraum.contains(zeitstempel)) {
			// Zeitstempel liegt nicht innerhalb der Prognoseganglinie
			return new Stuetzstelle<Messwerte>(zeitstempel,
					new Messwerte(null, null, null, null, k1, k2));
		}

		if (!isApproximationAktuell()) {
			gQKfz = getGanglinieQKfz();
			gQLkw = getGanglinieQLkw();
			gVPkw = getGanglinieVPkw();
			gVLkw = getGanglinieVLkw();
			setApproximationAktuell(true);
		}

		return new Stuetzstelle<Messwerte>(zeitstempel,
				new Messwerte(gQKfz.getStuetzstelle(zeitstempel).getWert(),
						gQLkw.getStuetzstelle(zeitstempel).getWert(),
						gVPkw.getStuetzstelle(zeitstempel).getWert(),
						gVLkw.getStuetzstelle(zeitstempel).getWert(), k1, k2));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see #setPrognoseZeitraum(Interval)
	 */
	@Override
	public List<Stuetzstelle<Messwerte>> getStuetzstellen() {
		List<Stuetzstelle<Messwerte>> liste;

		liste = new ArrayList<Stuetzstelle<Messwerte>>();
		for (final long t : keySet()) {
			if ((prognoseZeitraum != null) && !prognoseZeitraum.contains(t)) {
				continue;
			}

			liste.add(new Stuetzstelle<Messwerte>(t, get(t)));
		}

		return liste;
	}

	/**
	 * Gibt berechnete Stützstellen im angegebenen Intervall in der angegebenen
	 * Schrittweite zurück.
	 *
	 * @see #setPrognoseZeitraum(Interval)
	 */
	@Override
	public List<Stuetzstelle<Messwerte>> getStuetzstellen(
			final Interval intervall) {
		SortedMap<Long, Messwerte> menge;
		List<Stuetzstelle<Messwerte>> liste;

		menge = subMap(intervall.getStart(), intervall.getEnd() + 1);
		liste = new ArrayList<Stuetzstelle<Messwerte>>();
		for (final long t : menge.keySet()) {
			if ((prognoseZeitraum != null) && !prognoseZeitraum.contains(t)) {
				continue;
			}

			liste.add(getStuetzstelle(t));
		}

		return liste;
	}

	/**
	 * Gibt berechnete Stützstellen im prognostizierten Intervall in der
	 * angegebenen Schrittweite zurück.
	 *
	 * @param schrittweite
	 *            die Schrittweite der Stützstellen für die Berechnung.
	 * @return die berechneten Stützstellen.
	 * @see #setPrognoseZeitraum(Interval)
	 */
	public List<Stuetzstelle<Messwerte>> getStuetzstellen(
			final long schrittweite) {
		return getStuetzstellen(getPrognoseIntervall() == null ? getIntervall()
				: getPrognoseIntervall(), schrittweite);
	}

	/**
	 * Gibt berechnete Stützstellen in einem bestimmten Intervall in der
	 * angegebenen Schrittweite zurück.
	 *
	 * @param intervall
	 *            das Intervall der Ganglinie für das die Stützstellen berechnet
	 *            werden sollen.
	 * @param schrittweite
	 *            die Schrittweite der Stützstellen für die Berechnung.
	 * @return die berechneten Stützstellen.
	 * @see #setPrognoseZeitraum(Interval)
	 */
	public List<Stuetzstelle<Messwerte>> getStuetzstellen(
			final Interval intervall, final long schrittweite) {
		List<Stuetzstelle<Messwerte>> liste;

		liste = new ArrayList<Stuetzstelle<Messwerte>>();
		for (long t = intervall.getStart(); t <= intervall
				.getEnd(); t += schrittweite) {
			if ((prognoseZeitraum != null) && !prognoseZeitraum.contains(t)) {
				continue;
			}

			liste.add(getStuetzstelle(t));
		}

		return liste;
	}

	/**
	 * Gibt den Ganglinientyp zurück.
	 *
	 * @return der Typ der Ganglinie.
	 * @see #TYP_ABSOLUT
	 * @see #TYP_ADDITIV
	 * @see #TYP_MULTIPLIKATIV
	 */
	public int getTyp() {
		return typ;
	}

	/**
	 * Bestimmt, ob die Ganglinie absolut oder relativ ist. Für eine relative
	 * Ganglinie wird nicht zwischen additiv und multiplikativ unterschieden.
	 *
	 * @return {@code true}, wenn die Ganglinie absolut ist.
	 */
	public boolean isAbsolut() {
		return typ == TYP_ABSOLUT;
	}

	/**
	 * Besitzt die Ganglinie die Auszeichnung als Referenz?
	 *
	 * @return <code>true</code>, wenn diese Ganglinie eine Referenzganglinie
	 *         ist, sonst <code>false</code>
	 */
	public boolean isReferenz() {
		return referenz;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Es muss die Methode {@code getGanglinie*.isValid()} verwendet
	 *             werden.
	 */
	@Override
	@Deprecated
	public boolean isValid(final Interval intervall) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Es muss die Methode {@code getGanglinie*.isValid()} verwendet
	 *             werden.
	 */
	@Override
	@Deprecated
	public boolean isValid(final long zeitstempel) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Legt die Anzahl der bisherigen Verschmelzungen fest.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 *
	 * @param anzahlVerschmelzungen
	 *            Anzahl der Verschmelzungen
	 */
	public void setAnzahlVerschmelzungen(final long anzahlVerschmelzungen) {
		this.anzahlVerschmelzungen = anzahlVerschmelzungen;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated der Typ der Ganglinie muss mit
	 *             {@link #setApproximationDaK(int)} festgelegt werden.
	 */
	@Deprecated
	@Override
	public void setApproximation(final Approximation<Messwerte> approximation) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Legt die zu verwendende Approximation mit Hilfe einer
	 * Datenkatalogkonstante fest.
	 *
	 * @param approximationDaK
	 *            eine der Konstante {@link #APPROX_POLYLINE},
	 *            {@link #APPROX_CUBICSPLINE}, {@link #APPROX_BSPLINE} oder
	 *            {@link #APPROX_UNBESTIMMT}.
	 */
	public void setApproximationDaK(final int approximationDaK) {
		if ((approximationDaK != APPROX_BSPLINE)
				&& (approximationDaK != APPROX_CUBICSPLINE)
				&& (approximationDaK != APPROX_POLYLINE)
				&& (approximationDaK != APPROX_UNBESTIMMT)) {
			this.approximationDaK = APPROX_UNBESTIMMT;
		} else {
			this.approximationDaK = approximationDaK;
		}
	}

	/**
	 * Legt die Ordnung des B-Spline fest. Wird zur Approximation kein B-Spline
	 * benutzt, wird der Wert ignoriert.
	 *
	 * @param bSplineOrdnung
	 *            die neue Ordnung des B-Spline.
	 */
	public void setBSplineOrdnung(final int bSplineOrdnung) {
		this.bSplineOrdnung = bSplineOrdnung;
	}

	/**
	 * Legt den Ereignistyp der Ganglinie fest.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 *
	 * @param ereignisTyp
	 *            PID des Ereignistyp
	 */
	public void setEreignisTyp(final EreignisTyp ereignisTyp) {
		this.ereignisTyp = ereignisTyp;
	}

	/**
	 * Legt den Parameter k1 für die Berechnung von QB fest.
	 *
	 * @param k1
	 *            der parameter k1
	 */
	public void setK1(final float k1) {
		final SortedMap<Long, Messwerte> neu;

		this.k1 = k1;

		neu = new TreeMap<Long, Messwerte>();
		for (final Map.Entry<Long, Messwerte> e : entrySet()) {
			neu.put(e.getKey(),
					new Messwerte(e.getValue().getQKfz(),
							e.getValue().getQLkw(), e.getValue().getVPkw(),
							e.getValue().getVLkw(), k1, k2));
		}
		clear();
		putAll(neu);
	}

	/**
	 * Legt den Parameter k2 für die Berechnung von QB fest.
	 *
	 * @param k2
	 *            der parameter k2
	 */
	public void setK2(final float k2) {
		final SortedMap<Long, Messwerte> neu;

		this.k2 = k2;

		neu = new TreeMap<Long, Messwerte>();
		for (final Map.Entry<Long, Messwerte> e : entrySet()) {
			neu.put(e.getKey(),
					new Messwerte(e.getValue().getQKfz(),
							e.getValue().getQLkw(), e.getValue().getVPkw(),
							e.getValue().getVLkw(), k1, k2));
		}
		clear();
		putAll(neu);
	}

	/**
	 * Legt die Anzahl der bisherigen Verschmelzungen fest.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 *
	 * @param letzteVerschmelzung
	 *            die neue Anzahl der Verschmelzungen.
	 */
	public void setLetzteVerschmelzung(final long letzteVerschmelzung) {
		this.letzteVerschmelzung = letzteVerschmelzung;
	}

	/**
	 * Legt den Messquerschnitt fest, auf den sich die Ganglinie bezieht.
	 *
	 * @param messQuerschnitt
	 *            ein Messquerschnitt.
	 */
	public void setMessQuerschnitt(
			final MessQuerschnittAllgemein messQuerschnitt) {
		this.messQuerschnitt = messQuerschnitt;
	}

	/**
	 * Legt das Prognoseintervall fest. Die getter-Methoden für Stützstellen
	 * liefern nur Stützstellen innerhalb dieses Intervalls. Ist das
	 * Prognoseintervall gleich {@code null}, dann werden alle vorhanden
	 * Stützstellen berücksichtigt.
	 *
	 * @param prognoseZeitraum
	 *            ein Intervall.
	 */
	public void setPrognoseZeitraum(final Interval prognoseZeitraum) {
		this.prognoseZeitraum = prognoseZeitraum;
	}

	/**
	 * Kennzeichnet die Ganglinie als Referenzganglinie.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 *
	 * @param referenz
	 *            <code>true</code>, wenn diese Ganglinie eine Referenzganglinie
	 *            sein soll, sonst <code>false</code>
	 */
	public void setReferenz(final boolean referenz) {
		this.referenz = referenz;
	}

	/**
	 * Legt den Ganglinientyp fest.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 *
	 * @param typ
	 *            der Typ der Ganglinie.
	 */
	public void setTyp(final int typ) {
		this.typ = typ;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String result;

		result = getClass().getSimpleName() + "[\n"; //$NON-NLS-1$
		result += GanglinienMQOperationen.formatierterText(this);
		result += "]"; //$NON-NLS-1$

		return result;
	}

}
