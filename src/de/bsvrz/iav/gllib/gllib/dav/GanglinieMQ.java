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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.bitctrl.util.Interval;

import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.iav.gllib.gllib.Approximation;
import de.bsvrz.iav.gllib.gllib.BSpline;
import de.bsvrz.iav.gllib.gllib.CubicSpline;
import de.bsvrz.iav.gllib.gllib.Ganglinie;
import de.bsvrz.iav.gllib.gllib.Polyline;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttGanglinienTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttGanglinienVerfahren;
import de.bsvrz.sys.funclib.bitctrl.modell.tmverkehrglobal.objekte.MessQuerschnittAllgemein;

/**
 * Für Messquerschnitte angepasste Ganglinie. Die vier Verkehrswerte QKfz, QLkw,
 * VPkw und VLkw werden als Gruppe pro Zeitstempel gesichert. Aus diesen Werten
 * lassen sich die drei davon abhängigen Größe QPkw, VKfz und QB berechnen.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class GanglinieMQ extends Ganglinie<Messwerte> {

	private static final long serialVersionUID = 0;

	/** Der Messquerschnitt, zu dem die Ganglinie gehört. */
	private MessQuerschnittAllgemein messQuerschnitt;

	/** Parameter für die Berechnung von QB. */
	private float k1 = 2.0f;

	/** Parameter für die Berechnung von QB. */
	private float k2 = 0.01f;

	/**
	 * Zeitpunkt der letzten Verschmelzung, Standard ist der Zeitstempel der
	 * Instanzierung.
	 */
	private long letzteVerschmelzung = System.currentTimeMillis();

	/** Anzahl der Verschmelzung mit anderen Ganglinien. */
	private long anzahlVerschmelzungen = 1;

	/**
	 * Identifier für das mit der Ganglinie verknüpfte Ereignis.
	 */
	private EreignisTyp ereignisTyp;

	private SystemObject ereignisTypObj;

	/**
	 * Flag, ob die Ganglinie eine Referenzganglinie darstellt.
	 */
	private boolean referenz;

	/** Typ der Ganglinie. */
	private GanglinienTyp typ = GanglinienTyp.Absolut;

	/** Art der Approximation. */
	private ApproximationsVerfahren approximationsVerfahren = ApproximationsVerfahren.BSpline;

	/** Ordnung des B-Spline. */
	private int bSplineOrdnung = 5;

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
	 * Kopiert die Stützstellen, das Approximationsverfahren und alle anderen
	 * Eigenschaften bis auf {@code approximationAktuell}. Der Wert für
	 * {@code approximationAktuell} wird auf false gesetzt.
	 */
	@Override
	public GanglinieMQ clone() {
		GanglinieMQ g;

		g = new GanglinieMQ();
		g.putAll(this);
		g.setAnzahlVerschmelzungen(anzahlVerschmelzungen);
		g.setApproximationsVerfahren(approximationsVerfahren);
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
	 */
	private Approximation<Double> erzeugeApproximation() {
		if (AttGanglinienVerfahren.ZUSTAND_1_B_SPLINE_APPROXIMATION_BELIEBIGER_ORDNUNG
				.equals(approximationsVerfahren)) {
			return new BSpline(bSplineOrdnung);
		} else if (AttGanglinienVerfahren.ZUSTAND_2_CUBIC_SPLINE_INTERPOLATION
				.equals(approximationsVerfahren)) {
			return new CubicSpline();
		} else if (AttGanglinienVerfahren.ZUSTAND_3_POLYLINE_VERFAHREN_LINEARE_INTERPOLATION_
				.equals(approximationsVerfahren)) {
			return new Polyline();
		} else {
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
	 * @deprecated Die Approximation der einzelnen Größen kann mit
	 *             <code>getGanglinieXXX.getApproximation()</code> abgerufen
	 *             werden. Der Typ der Approximation kann mit
	 *             {@link #getApproximationsVerfahren()} erfragt werden.
	 */
	@Deprecated
	@Override
	public Approximation<Messwerte> getApproximation() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gibt die Art der Approximation als Datenkatalogkonstante zurück.
	 * 
	 * @return das Approximationsverfahren.
	 */
	public ApproximationsVerfahren getApproximationsVerfahren() {
		return approximationsVerfahren;
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
	 * @deprecated Die Intervalle müssen an den einzelnen Größen mit
	 *             <code>getGanglinieXXX.getIntervalle()</code> abgerufen
	 *             werden.
	 */
	@Deprecated
	@Override
	public List<Interval> getIntervalle() {
		throw new UnsupportedOperationException("Es müssen die Intervalle der "
				+ "einzelnen Ganglinien für Q, V und QB abgefragt werden.");
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
	 * @see #setPrognoseZeitraum(Interval)
	 */
	@Override
	public Stuetzstelle<Messwerte> getStuetzstelle(final long zeitstempel) {

		if (prognoseZeitraum != null && !prognoseZeitraum.contains(zeitstempel)) {
			// Zeitstempel liegt nicht innerhalb der Prognoseganglinie
			return new Stuetzstelle<Messwerte>(zeitstempel, new Messwerte(null,
					null, null, null, k1, k2));
		}

		if (!isApproximationAktuell()) {
			gQKfz = getGanglinieQKfz();
			gQLkw = getGanglinieQLkw();
			gVPkw = getGanglinieVPkw();
			gVLkw = getGanglinieVLkw();
			setApproximationAktuell(true);
		}

		return new Stuetzstelle<Messwerte>(zeitstempel, new Messwerte(gQKfz
				.getStuetzstelle(zeitstempel).getWert(), gQLkw.getStuetzstelle(
				zeitstempel).getWert(), gVPkw.getStuetzstelle(zeitstempel)
				.getWert(), gVLkw.getStuetzstelle(zeitstempel).getWert(), k1,
				k2));
	}

	/**
	 * @see #setPrognoseZeitraum(Interval)
	 */
	@Override
	public List<Stuetzstelle<Messwerte>> getStuetzstellen() {
		List<Stuetzstelle<Messwerte>> liste;

		liste = new ArrayList<Stuetzstelle<Messwerte>>();
		for (final long t : keySet()) {
			if (prognoseZeitraum != null && !prognoseZeitraum.contains(t)) {
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
			if (prognoseZeitraum != null && !prognoseZeitraum.contains(t)) {
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
	 * @see #setPrognoseZeitraum(Interval)
	 */
	public List<Stuetzstelle<Messwerte>> getStuetzstellen(
			final long schrittweite) {
		return getStuetzstellen(getPrognoseIntervall() == null ? getIntervall()
				: getPrognoseIntervall(), schrittweite);
	}

	/**
	 * @see #setPrognoseZeitraum(Interval)
	 */
	public List<Stuetzstelle<Messwerte>> getStuetzstellen(
			final Interval intervall, final long schrittweite) {
		List<Stuetzstelle<Messwerte>> liste;

		liste = new ArrayList<Stuetzstelle<Messwerte>>();
		for (long t = intervall.getStart(); t <= intervall.getEnd(); t += schrittweite) {
			if (prognoseZeitraum != null && !prognoseZeitraum.contains(t)) {
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
	 */
	public GanglinienTyp getTyp() {
		return typ;
	}

	/**
	 * Bestimmt, ob die Ganglinie absolut oder relativ ist. Für eine relative
	 * Ganglinie wird nicht zwischen additiv und multiplikativ unterschieden.
	 * 
	 * @return {@code true}, wenn die Ganglinie absolut ist.
	 */
	public boolean isAbsolut() {
		return AttGanglinienTyp.ZUSTAND_0_ABSOLUT.equals(typ);
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
	 * @deprecated Es muss die Methode <code>getGanglinieXXX.isValid()</code>
	 *             verwendet werden.
	 */
	@Override
	@Deprecated
	public boolean isValid(final Interval intervall) {
		throw new UnsupportedOperationException(
				"Es müssen die Methode isValid() an den "
						+ "einzelnen Ganglinien für Q, V und QB abgefragt werden.");
	}

	/**
	 * @deprecated Es muss die Methode <code>getGanglinieXXX.isValid()</code>
	 *             verwendet werden.
	 */
	@Override
	@Deprecated
	public boolean isValid(final long zeitstempel) {
		throw new UnsupportedOperationException(
				"Es müssen die Methode isValid() an den "
						+ "einzelnen Ganglinien für Q, V und QB abgefragt werden.");
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
	 * @deprecated der Typ der Ganglinie muss mit
	 *             {@link #setApproximationsVerfahren(AttGanglinienVerfahren)}
	 *             festgelegt werden.
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
	 * @param approximationsVerfahren
	 *            das Approximationsverfahren.
	 */
	public void setApproximationsVerfahren(
			final ApproximationsVerfahren approximationsVerfahren) {
		this.approximationsVerfahren = approximationsVerfahren;
	}

	/**
	 * Legt die Ordnung des B-Spline fest. Wird zur Approximation kein B-Spline
	 * benutzt, wird der Wert ignoriert.
	 * 
	 * @param bSplineOrdnung
	 *            die neue Ordnung des B-Spline.
	 */
	public void setBSplineOrdnung(final long bSplineOrdnung) {
		this.bSplineOrdnung = (int) bSplineOrdnung;
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
			neu.put(e.getKey(), new Messwerte(e.getValue().getQKfz(), e
					.getValue().getQLkw(), e.getValue().getVPkw(), e.getValue()
					.getVLkw(), k1, k2));
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
			neu.put(e.getKey(), new Messwerte(e.getValue().getQKfz(), e
					.getValue().getQLkw(), e.getValue().getVPkw(), e.getValue()
					.getVLkw(), k1, k2));
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
	public void setTyp(final GanglinienTyp typ) {
		this.typ = typ;
	}

	/**
	 * Gibt die Ganglinie als String Form einer Tabelle zurück.
	 * 
	 * <p>
	 * <em>Hinweis:</em> Der String wird bei großen Ganglinien entsprechend auch
	 * sehr groß!!
	 */
	@Override
	public String toString() {
		String result;

		result = getClass().getSimpleName() + "[\n";
		result += GanglinienMQOperationen.formatierterText(this);
		result += "]";

		return result;
	}

}
