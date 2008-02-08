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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;

import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;

/**
 * Repräsentiert eine Ganglinie als Draw2D-Figure.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class GlGanglinie extends GlFigure {

	/** Die Eigenschaft {@code FARBE_QKFZ}. */
	private static final Color FARBE_QKFZ = ColorConstants.red;

	/** Die Eigenschaft {@code FARBE_QLKW}. */
	private static final Color FARBE_QLKW = ColorConstants.orange;

	/** Die Eigenschaft {@code FARBE_VPKW}. */
	private static final Color FARBE_VPKW = ColorConstants.green;

	/** Die Eigenschaft {@code FARBE_VLKW}. */
	private static final Color FARBE_VLKW = ColorConstants.yellow;

	/** Die Eigenschaft {@code ganglinie}. */
	private GanglinieMQ ganglinie;

	/** Die Eigenschaft {@code approximationQKfz}. */
	private Polyline approximationQKfz;

	/** Die Eigenschaft {@code stuetzstellenQKfz}. */
	private PointList stuetzstellenQKfz;

	/** Die Eigenschaft {@code approximationQLkw}. */
	private Polyline approximationQLkw;

	/** Die Eigenschaft {@code stuetzstellenQLkw}. */
	private PointList stuetzstellenQLkw;

	/** Die Eigenschaft {@code approximationVPkw}. */
	private Polyline approximationVPkw;

	/** Die Eigenschaft {@code stuetzstellenVPkw}. */
	private PointList stuetzstellenVPkw;

	/** Die Eigenschaft {@code approximationVLkw}. */
	private Polyline approximationVLkw;

	/** Die Eigenschaft {@code stuetzstellenVLkw}. */
	private PointList stuetzstellenVLkw;

	/**
	 * Gibt die Ganglinie der Figur zurück.
	 * 
	 * @return die Ganglinie.
	 */
	public GanglinieMQ getGanglinie() {
		return ganglinie;
	}

	/**
	 * Legt die zu zeichnende Ganglinie fest.
	 * 
	 * @param ganglinie
	 *            eine Ganglinie.
	 */
	public void setGanglinie(GanglinieMQ ganglinie) {
		this.ganglinie = ganglinie;
		updateCache();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.eclipse.draw2d.GlFigure#setSkalierung(de.bsvrz.iav.gllib.eclipse.draw2d.GlSkalierung)
	 */
	@Override
	public void setSkalierung(GlSkalierung skalierung) {
		boolean update;

		update = getSkalierung() == null || !getSkalierung().equals(skalierung);
		super.setSkalierung(skalierung);
		if (update) {
			System.out.println("Update Cache.");
			updateCache();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	@Override
	protected void paintFigure(Graphics graphics) {
		if (approximationQKfz.getPoints() != null) {
			graphics.setForegroundColor(FARBE_QKFZ);
			graphics.setBackgroundColor(FARBE_QKFZ);
			graphics.drawPolyline(approximationQKfz.getPoints());
			for (int i = 0; i < stuetzstellenQKfz.size(); ++i) {
				Point p;

				p = stuetzstellenQKfz.getPoint(i);
				graphics.fillOval(p.x - 5, p.y - 5, 10, 10);
			}
		}
		if (approximationQLkw.getPoints() != null) {
			graphics.setForegroundColor(FARBE_QLKW);
			graphics.setBackgroundColor(FARBE_QLKW);
			graphics.drawPolyline(approximationQLkw.getPoints());
			for (int i = 0; i < stuetzstellenQLkw.size(); ++i) {
				Point p;

				p = stuetzstellenQLkw.getPoint(i);
				graphics.fillOval(p.x - 5, p.y - 5, 10, 10);
			}
		}

		if (approximationVPkw.getPoints() != null) {
			graphics.setForegroundColor(FARBE_VPKW);
			graphics.setBackgroundColor(FARBE_VPKW);
			graphics.drawPolyline(approximationVPkw.getPoints());
			for (int i = 0; i < stuetzstellenVPkw.size(); ++i) {
				Point p;

				p = stuetzstellenVPkw.getPoint(i);
				graphics.fillOval(p.x - 5, p.y - 5, 10, 10);
			}
		}

		if (approximationVLkw.getPoints() != null) {
			graphics.setForegroundColor(FARBE_VLKW);
			graphics.setBackgroundColor(FARBE_VLKW);
			graphics.drawPolyline(approximationVLkw.getPoints());
			for (int i = 0; i < stuetzstellenVLkw.size(); ++i) {
				Point p;

				p = stuetzstellenVLkw.getPoint(i);
				graphics.fillOval(p.x - 5, p.y - 5, 10, 10);
			}
		}
	}

	/**
	 * Bestimmt aus der Stützstelle einen graphischen Punkt.
	 * 
	 * @param zeitstempel
	 *            der Zeitstempel der Stützstelle.
	 * @param wert
	 *            der QKfz-Wert der Stützstelle.
	 * 
	 * @return der dazugehörige Punkt auf der Zeichenfläche.
	 */
	private Point getQKfz(long zeitstempel, double wert) {
		int x, y, t;
		Dimension basis;

		basis = getGroesse();
		t = (int) ((zeitstempel - getSkalierung().getMinZeit()) / 1000);
		x = (int) (t * getSkalierung().getZoomZeit());
		y = (int) (basis.height - wert * getSkalierung().getZoomQKfz());
		return new Point(x, y);
	}

	/**
	 * Bestimmt aus der Stützstelle einen graphischen Punkt.
	 * 
	 * @param zeitstempel
	 *            der Zeitstempel der Stützstelle.
	 * @param wert
	 *            der VKfz-Wert der Stützstelle.
	 * 
	 * @return der dazugehörige Punkt auf der Zeichenfläche.
	 */
	private Point getVKfz(long zeitstempel, double wert) {
		int x, y, t;
		Dimension basis;

		basis = getGroesse();
		t = (int) ((zeitstempel - getSkalierung().getMinZeit()) / 1000);
		x = (int) (t * getSkalierung().getZoomZeit());
		y = (int) (basis.height - wert * getSkalierung().getZoomVKfz());
		return new Point(x, y);
	}

	/**
	 * Aktualisiert den Cache der benötigten Punkte für das Zeichnen.
	 */
	private void updateCache() {
		approximationQKfz = new Polyline();
		stuetzstellenQKfz = new PointList();
		approximationQLkw = new Polyline();
		stuetzstellenQLkw = new PointList();
		approximationVPkw = new Polyline();
		stuetzstellenVPkw = new PointList();
		approximationVLkw = new Polyline();
		stuetzstellenVLkw = new PointList();

		if (ganglinie == null) {
			return;
		}

		if (!ganglinie.isApproximationAktuell()) {
			ganglinie.aktualisiereApproximation();
		}

		for (Stuetzstelle<Messwerte> s : getGanglinie().getStuetzstellen()) {
			if (s.getWert().getQKfz() != null) {
				stuetzstellenQKfz.addPoint(getQKfz(s.getZeitstempel(), s
						.getWert().getQKfz()));
			}
			if (s.getWert().getQLkw() != null) {
				stuetzstellenQLkw.addPoint(getQKfz(s.getZeitstempel(), s
						.getWert().getQLkw()));
			}
			if (s.getWert().getVPkw() != null) {
				stuetzstellenVPkw.addPoint(getVKfz(s.getZeitstempel(), s
						.getWert().getVPkw()));
			}
			if (s.getWert().getVLkw() != null) {
				stuetzstellenVLkw.addPoint(getVKfz(s.getZeitstempel(), s
						.getWert().getVLkw()));
			}
		}

		for (Stuetzstelle<Double> s : getGanglinie().getGanglinieQKfz()
				.getApproximation().interpoliere(15 * 60 * 1000)) {
			approximationQKfz
					.addPoint(getQKfz(s.getZeitstempel(), s.getWert()));
		}
		for (Stuetzstelle<Double> s : getGanglinie().getGanglinieQLkw()
				.getApproximation().interpoliere(15 * 60 * 1000)) {
			approximationQLkw
					.addPoint(getQKfz(s.getZeitstempel(), s.getWert()));
		}
		for (Stuetzstelle<Double> s : getGanglinie().getGanglinieVPkw()
				.getApproximation().interpoliere(15 * 60 * 1000)) {
			approximationVPkw
					.addPoint(getVKfz(s.getZeitstempel(), s.getWert()));
		}
		for (Stuetzstelle<Double> s : getGanglinie().getGanglinieVLkw()
				.getApproximation().interpoliere(15 * 60 * 1000)) {
			approximationVLkw
					.addPoint(getVKfz(s.getZeitstempel(), s.getWert()));
		}
	}

}
