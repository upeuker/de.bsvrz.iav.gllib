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

package de.bsvrz.iav.gllib.eclipse.draw2d;

import static de.bsvrz.sys.funclib.bitctrl.util.Konstanten.MILLIS_PER_TAG;

import java.util.Calendar;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;

/**
 * Basisklasse der Draw2D-Figuren der Ganglinienbibliothek.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public abstract class GlFigure extends Figure {

	/**
	 * Der Offset um den die Ganglinien in der Figur des Koordinatensystems
	 * verschoben sind, damit Platz für Achsen, Beschriftung und mehr bleibt.
	 */
	public static final Dimension OFFSET = new Dimension(60, 30);

	/** Die Eigenschaft {@code FARBE_QKFZ}. */
	public static final Color FARBE_QKFZ = ColorConstants.red;

	/** Die Eigenschaft {@code FARBE_QLKW}. */
	public static final Color FARBE_QLKW = ColorConstants.orange;

	/** Die Eigenschaft {@code FARBE_VPKW}. */
	public static final Color FARBE_VPKW = ColorConstants.blue;

	/** Die Eigenschaft {@code FARBE_VLKW}. */
	public static final Color FARBE_VLKW = ColorConstants.green;

	/** Die Eigenschaft {@code skalierung}. */
	private GlSkalierung skalierung;

	/**
	 * Initialisiert die Skalierung der Figur.
	 */
	public GlFigure() {
		setSkalierung(new GlSkalierung());
	}

	/**
	 * Gibt die Skalierung des Koordinatensystems zurück.
	 * 
	 * @return die Skalierung.
	 */
	public GlSkalierung getSkalierung() {
		return skalierung;
	}

	/**
	 * Legt die Skalierung des Koordinatensystems fest.
	 * 
	 * @param skalierung
	 *            die Skalierung.
	 */
	public void setSkalierung(GlSkalierung skalierung) {
		this.skalierung = skalierung.clone();
		updateGroesse();
	}

	/**
	 * Berechnet die benötigte Größe der Figur, um sie komplett anzuzeigen.
	 * 
	 * @return die Figurgröße.
	 */
	protected Dimension getGroesse() {
		int zeit, v, q, hoehe;

		zeit = (int) (OFFSET.width + getSkalierung().getZoomZeit()
				* (getSkalierung().getMaxZeit() - getSkalierung().getMinZeit())
				/ 1000);
		v = (int) (getSkalierung().getZoomVKfz() * (getSkalierung()
				.getMaxVKfz() - getSkalierung().getMinVKfz()));
		q = (int) (getSkalierung().getZoomQKfz() * (getSkalierung()
				.getMaxQKfz() - getSkalierung().getMinQKfz()));

		hoehe = OFFSET.height + Math.max(v, q);
		return new Dimension(zeit, hoehe);
	}

	/**
	 * Legt die Größe der Figur anhand der Skalierung fest.
	 */
	protected void updateGroesse() {
		Dimension dim;
		Calendar cal;

		// Zeitachse immer mit voller Stunde beginnen
		cal = Calendar.getInstance();
		cal.setTimeInMillis(getSkalierung().getMinZeit());
		if (cal.get(Calendar.MINUTE) != 0 || cal.get(Calendar.SECOND) != 0
				|| cal.get(Calendar.MILLISECOND) != 0) {
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			getSkalierung().setMinZeit(cal.getTimeInMillis());
		}

		// Mindestens einen Tag anzeigen
		if (getSkalierung().getMaxZeit() - getSkalierung().getMinZeit() < MILLIS_PER_TAG) {
			getSkalierung().setMaxZeit(
					getSkalierung().getMinZeit() + MILLIS_PER_TAG);
		}

		dim = getGroesse();
		setSize(dim);
		setPreferredSize(dim);
		setMinimumSize(dim);
		setMaximumSize(dim);
	}

}
