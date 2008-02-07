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

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Beschreibt den Wertebereich und desses Skalierung von Gangliniengrößen.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class GlSkalierung {

	/** Die minimal anzuzeigende Zeit. */
	private long minZeit;

	/** Die maximal anzuzeigende Zeit. */
	private long maxZeit;

	/** Der Skalierungsfaktor der Zeit. */
	private double zoomZeit;

	/** Die minimal anzuzeigende Geschwindigkeit. */
	private int minVKfz;

	/** Die maximal anzuzeigende Geschwindigkeit. */
	private int maxVKfz;

	/** Der Skalierungsfaktor der Geschwindigkeit. */
	private double zoomVKfz;

	/** Die minimal anzuzeigende Verkehrsstärke. */
	private int minQKfz;

	/** Die maximal anzuzeigende Verkehrsstärke. */
	private int maxQKfz;

	/** Der Skalierungsfaktor der Verkehrsstärke. */
	private double zoomQKfz;

	/**
	 * Initialisiert die Skalierung mit Standardwerten.
	 */
	public GlSkalierung() {
		Calendar cal;

		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		minZeit = cal.getTimeInMillis();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		maxZeit = cal.getTimeInMillis();

		zoomZeit = 0.01;
		minVKfz = 0; // Standard 0
		maxVKfz = 255; // Standard 255
		zoomVKfz = 2.5;
		minQKfz = 0; // Standard 0
		maxQKfz = 10000; // Maximal 100.000.000
		zoomQKfz = 0.07;
	}

	/**
	 * Gibt den Maximalwert von QKfz zurück.
	 * 
	 * @return der Maximalwert von QKfz.
	 */
	public int getMaxQKfz() {
		return maxQKfz;
	}

	/**
	 * Gibt den Maximalwert von VKfz zurück.
	 * 
	 * @return der Maximalwert von VKfz.
	 */
	public int getMaxVKfz() {
		return maxVKfz;
	}

	/**
	 * Gibt den Maximalwert der Zeitachse zurück.
	 * 
	 * @return der Maximalwert der Zeitachse.
	 */
	public long getMaxZeit() {
		return maxZeit;
	}

	/**
	 * Gibt den Minimalwert von QKfz zurück.
	 * 
	 * @return der Minimalwert von QKfz.
	 */
	public int getMinQKfz() {
		return minQKfz;
	}

	/**
	 * Gibt den Minimalwert von VKfz zurück.
	 * 
	 * @return der Minimalwert von VKfz.
	 */
	public int getMinVKfz() {
		return minVKfz;
	}

	/**
	 * Gibt den Minimalwert der Zeitachse zurück.
	 * 
	 * @return der Minimalwert der Zeitachse.
	 */
	public long getMinZeit() {
		return minZeit;
	}

	/**
	 * Gibt den Skalierungsfaktor von QKfz zurück.
	 * 
	 * @return der Skalierungsfaktor von QKfz.
	 */
	public double getZoomQKfz() {
		return zoomQKfz;
	}

	/**
	 * Gibt den Skalierungsfaktor von VKfz zurück.
	 * 
	 * @return der Skalierungsfaktor von VKfz.
	 */
	public double getZoomVKfz() {
		return zoomVKfz;
	}

	/**
	 * Gibt den Skalierungsfaktor der Zeitachse zurück.
	 * 
	 * @return der Skalierungsfaktor der Zeitachse.
	 */
	public double getZoomZeit() {
		return zoomZeit;
	}

	/**
	 * Legt den Maximalwert von QKfz fest.
	 * 
	 * @param maxQKfz
	 *            der Maximalwert von QKfz.
	 */
	public void setMaxQKfz(int maxQKfz) {
		this.maxQKfz = maxQKfz;
	}

	/**
	 * Legt den Maximalwert von VKfz fest.
	 * 
	 * @param maxVKfz
	 *            der Maximalwert von VKfz.
	 */
	public void setMaxVKfz(int maxVKfz) {
		this.maxVKfz = maxVKfz;
	}

	/**
	 * Legt den Maximalwert der Zeitachse fest.
	 * 
	 * @param maxZeit
	 *            der Maximalwert der Zeitachse.
	 */
	public void setMaxZeit(long maxZeit) {
		this.maxZeit = maxZeit;
	}

	/**
	 * Legt den Minimalwert von QKfz fest.
	 * 
	 * @param minQKfz
	 *            der Minimalwert von QKfz.
	 */
	public void setMinQKfz(int minQKfz) {
		this.minQKfz = minQKfz;
	}

	/**
	 * Legt den Minimalwert von VKfz fest.
	 * 
	 * @param minVKfz
	 *            der Minimalwert von VKfz.
	 */
	public void setMinVKfz(int minVKfz) {
		this.minVKfz = minVKfz;
	}

	/**
	 * Legt den Minimalwert de Zeitachse fest.
	 * 
	 * @param minZeit
	 *            der Minimalwert der Zeitachse.
	 */
	public void setMinZeit(long minZeit) {
		this.minZeit = minZeit;
	}

	/**
	 * Legt den Skalierungsfaktor von QKfz fest.
	 * 
	 * @param skalierungQKfz
	 *            der Skalierungsfaktor von QKfz.
	 */
	public void setZoomQKfz(double skalierungQKfz) {
		this.zoomQKfz = skalierungQKfz;
	}

	/**
	 * Legt den Skalierungsfaktor von VKfz fest.
	 * 
	 * @param skalierungVKfz
	 *            der Skalierungsfaktor von VKfz.
	 */
	public void setZoomVKfz(double skalierungVKfz) {
		this.zoomVKfz = skalierungVKfz;
	}

	/**
	 * Legt den Skalierungsfaktor der Zeitachse fest.
	 * 
	 * @param skalierungZeit
	 *            der Skalierungsfaktor der Zeitachse.
	 */
	public void setZoomZeit(double skalierungZeit) {
		this.zoomZeit = skalierungZeit;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s;
		Calendar cal;
		DateFormat format;

		cal = Calendar.getInstance();
		format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT);

		s = getClass().getName() + "[";
		cal.setTimeInMillis(minZeit);
		s += "minZeit=" + format.format(cal.getTime());
		cal.setTimeInMillis(maxZeit);
		s += ", maxZeit=" + format.format(cal.getTime());
		s += ", zoomZeit=" + zoomZeit;
		s += ", minQKfz=" + minQKfz;
		s += ", maxQKfz=" + maxQKfz;
		s += ", zoomQKfz=" + zoomQKfz;
		s += ", minVKfz=" + minVKfz;
		s += ", maxVKfz=" + maxVKfz;
		s += ", zoomVKfz=" + zoomVKfz;
		s += "]";

		return s;
	}

}
