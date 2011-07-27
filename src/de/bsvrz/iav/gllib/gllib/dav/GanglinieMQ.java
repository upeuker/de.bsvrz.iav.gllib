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
 * Wei�enfelser Stra�e 67
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
 * F�r Messquerschnitte angepasste Ganglinie. Die vier Verkehrswerte QKfz, QLkw,
 * VPkw und VLkw werden als Gruppe pro Zeitstempel gesichert. Aus diesen Werten
 * lassen sich die drei davon abh�ngigen Gr��e QPkw, VKfz und QB berechnen.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class GanglinieMQ extends Ganglinie<Messwerte> {

	private static final long serialVersionUID = 0;

	/** Der Messquerschnitt, zu dem die Ganglinie geh�rt. */
	private MessQuerschnittAllgemein messQuerschnitt;

	/** Parameter f�r die Berechnung von QB. */
	private float k1 = 2.0f;

	/** Parameter f�r die Berechnung von QB. */
	private float k2 = 0.01f;

	/**
	 * Zeitpunkt der letzten Verschmelzung, Standard ist der Zeitstempel der
	 * Instanzierung.
	 */
	private long letzteVerschmelzung = System.currentTimeMillis();

	/** Anzahl der Verschmelzung mit anderen Ganglinien. */
	private long anzahlVerschmelzungen = 1;

	/**
	 * Identifier f�r das mit der Ganglinie verkn�pfte Ereignis.
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

	/** Das Intervall f�r das die Ganglinie prognostiziert wird. */
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
	 * Kopiert die St�tzstellen, das Approximationsverfahren und alle anderen
	 * Eigenschaften bis auf {@code approximationAktuell}. Der Wert f�r
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
	 * Gibt die Approximation zur Datenverteilerapproximation zur�ck.
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
	 * zur�ck.
	 * 
	 * @return Anzahl bisheriger Verschmelzungen
	 */
	public long getAnzahlVerschmelzungen() {
		return anzahlVerschmelzungen;
	}

	/**
	 * @deprecated Die Approximation der einzelnen Gr��en kann mit
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
	 * Gibt die Art der Approximation als Datenkatalogkonstante zur�ck.
	 * 
	 * @return das Approximationsverfahren.
	 */
	public ApproximationsVerfahren getApproximationsVerfahren() {
		return approximationsVerfahren;
	}

	/**
	 * Gibt die Ordnung des B-Spline zur�ck. Wird zur Approximation kein
	 * B-Spline benutzt, wird der Wert ignoriert.
	 * 
	 * @return die Ordnung des B-Spline.
	 */
	public int getBSplineOrdnung() {
		return bSplineOrdnung;
	}

	/**
	 * Gibt den Ereignistyp der Ganglinie zur�ck.
	 * 
	 * @return der Ereignistyp.
	 */
	public EreignisTyp getEreignisTyp() {
		return ereignisTyp;
	}

	/**
	 * Gibt die Ganglinie f�r QB zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
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
	 * Gibt die Ganglinie f�r QKfz zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
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
	 * Gibt die Ganglinie f�r QLkw zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
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
	 * Gibt die Ganglinie f�r QPkw zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
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
	 * Gibt die Ganglinie f�r VKfz zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
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
	 * Gibt die Ganglinie f�r VLkw zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
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
	 * Gibt die Ganglinie f�r QPkw zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
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
	 * @deprecated Die Intervalle m�ssen an den einzelnen Gr��en mit
	 *             <code>getGanglinieXXX.getIntervalle()</code> abgerufen
	 *             werden.
	 */
	@Deprecated
	@Override
	public List<Interval> getIntervalle() {
		throw new UnsupportedOperationException("Es m�ssen die Intervalle der "
				+ "einzelnen Ganglinien f�r Q, V und QB abgefragt werden.");
	}

	/**
	 * Gibt einen Parameter f�r die Berechnung von QB zur�ck.
	 * 
	 * @return der Parameter.
	 */
	public float getK1() {
		return k1;
	}

	/**
	 * Gibt einen Parameter f�r die Berechnung von QB zur�ck.
	 * 
	 * @return der Parameter
	 */
	public float getK2() {
		return k2;
	}

	/**
	 * Gibt den Zeitpunkt der letzten Verschmelzung als Zeitstempel zur�ck.
	 * 
	 * @return Zeitstempel
	 */
	public long getLetzteVerschmelzung() {
		return letzteVerschmelzung;
	}

	/**
	 * Gibt den Messquerschnitt der Ganglinie zur�ck.
	 * 
	 * @return ein Messquerschnitt.
	 */
	public MessQuerschnittAllgemein getMessQuerschnitt() {
		return messQuerschnitt;
	}

	/**
	 * Gibt das Prognoseintervall der Ganglinie zur�ck.
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
	 * Gibt berechnete St�tzstellen im angegebenen Intervall in der angegebenen
	 * Schrittweite zur�ck.
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
	 * Gibt berechnete St�tzstellen im prognostizierten Intervall in der
	 * angegebenen Schrittweite zur�ck.
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
	 * Gibt den Ganglinientyp zur�ck.
	 * 
	 * @return der Typ der Ganglinie.
	 */
	public GanglinienTyp getTyp() {
		return typ;
	}

	/**
	 * Bestimmt, ob die Ganglinie absolut oder relativ ist. F�r eine relative
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
				"Es m�ssen die Methode isValid() an den "
						+ "einzelnen Ganglinien f�r Q, V und QB abgefragt werden.");
	}

	/**
	 * @deprecated Es muss die Methode <code>getGanglinieXXX.isValid()</code>
	 *             verwendet werden.
	 */
	@Override
	@Deprecated
	public boolean isValid(final long zeitstempel) {
		throw new UnsupportedOperationException(
				"Es m�ssen die Methode isValid() an den "
						+ "einzelnen Ganglinien f�r Q, V und QB abgefragt werden.");
	}

	/**
	 * Legt die Anzahl der bisherigen Verschmelzungen fest.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der �ffentlichen API und
	 * sollte nicht au�erhalb der Ganglinie-API verwendet werden.
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
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der �ffentlichen API und
	 * sollte nicht au�erhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param ereignisTyp
	 *            PID des Ereignistyp
	 */
	public void setEreignisTyp(final EreignisTyp ereignisTyp) {
		this.ereignisTyp = ereignisTyp;
	}

	/**
	 * Legt den Parameter k1 f�r die Berechnung von QB fest.
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
	 * Legt den Parameter k2 f�r die Berechnung von QB fest.
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
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der �ffentlichen API und
	 * sollte nicht au�erhalb der Ganglinie-API verwendet werden.
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
	 * Legt das Prognoseintervall fest. Die getter-Methoden f�r St�tzstellen
	 * liefern nur St�tzstellen innerhalb dieses Intervalls. Ist das
	 * Prognoseintervall gleich {@code null}, dann werden alle vorhanden
	 * St�tzstellen ber�cksichtigt.
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
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der �ffentlichen API und
	 * sollte nicht au�erhalb der Ganglinie-API verwendet werden.
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
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der �ffentlichen API und
	 * sollte nicht au�erhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param typ
	 *            der Typ der Ganglinie.
	 */
	public void setTyp(final GanglinienTyp typ) {
		this.typ = typ;
	}

	/**
	 * Gibt die Ganglinie als String Form einer Tabelle zur�ck.
	 * 
	 * <p>
	 * <em>Hinweis:</em> Der String wird bei gro�en Ganglinien entsprechend auch
	 * sehr gro�!!
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
