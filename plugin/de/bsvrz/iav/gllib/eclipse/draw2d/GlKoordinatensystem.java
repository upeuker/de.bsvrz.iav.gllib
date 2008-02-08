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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.bsvrz.iav.gllib.gllib.CubicSpline;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;

/**
 * Eine Draw2D-Figur zum Anzeigen eines Koordinatensystems zur Visualierung von
 * Ganglinien.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class GlKoordinatensystem extends GlFigure {

	/**
	 * TODO Löschen.
	 * 
	 * @param args
	 *            keine Argumente.
	 */
	public static void main(String[] args) {
		// final LightweightSystem lws;
		final Display d;
		final Shell shell;
		final FigureCanvas canvas;
		final ScalableLayeredPane pane;
		final GlKoordinatensystem kos;
		final GlGanglinieMQ g;
		GanglinieMQ gmq;
		Calendar cal;
		GlSkalierung skal;

		d = Display.getDefault();
		shell = new Shell(d);
		shell.setSize(1024, 786);
		shell.setLayout(new FillLayout());

		// lws = new LightweightSystem(shell);
		// lws.setContents(new Koordinatensystem());

		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		// cal.setTimeInMillis(0);

		gmq = new GanglinieMQ();
		gmq.setStuetzstelle(cal.getTimeInMillis(), new Messwerte(0.0, 0.0,
				80.0, 78.0));
		cal.add(Calendar.HOUR_OF_DAY, 3);
		gmq.setStuetzstelle(cal.getTimeInMillis(), new Messwerte(3000.0,
				1200.0, 100.0, 84.0));
		cal.add(Calendar.HOUR_OF_DAY, 1);
		gmq.setStuetzstelle(cal.getTimeInMillis(), new Messwerte(2000.0, null,
				150.0, null));
		cal.add(Calendar.HOUR_OF_DAY, 2);
		gmq.setStuetzstelle(cal.getTimeInMillis(), new Messwerte(4000.0,
				2250.0, 180.0, 80.0));
		cal.add(Calendar.HOUR_OF_DAY, 3);
		gmq.setStuetzstelle(cal.getTimeInMillis(), new Messwerte(1000.0, 354.0,
				130.0, 82.0));

		gmq.setApproximation(new CubicSpline());
		// gmq.setApproximation(new Polyline());

		canvas = new FigureCanvas(shell, SWT.DOUBLE_BUFFERED);
		// canvas.setBackground(ColorConstants.white);
		g = new GlGanglinieMQ();
		g.setGanglinie(gmq);
		skal = new GlSkalierung();
		// skal.setMinZeit(0);
		// skal.setMaxZeit(MILLIS_PER_TAG);
		kos = new GlKoordinatensystem();
		kos.setSkalierung(skal);
		kos.add(g);
		pane = new ScalableLayeredPane();
		pane.add(kos);
		canvas.setContents(pane);
		// pane.setScale(0.1);

		shell.open();

		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}

	}

	/** Die aktuelle Mausposition. */
	private Point mausPos;

	/**
	 * Initialisiert das Koordinatensystem.
	 */
	public GlKoordinatensystem() {
		setSkalierung(new GlSkalierung());

		addMouseMotionListener(new MouseMotionListener.Stub() {

			@Override
			public void mouseExited(MouseEvent me) {
				mausPos = me.getLocation();
				repaint();
			}

			@Override
			public void mouseMoved(MouseEvent me) {
				mausPos = me.getLocation();
				repaint();
			}

		});
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.eclipse.draw2d.GlFigure#getGroesse()
	 */
	@Override
	protected Dimension getGroesse() {
		Dimension dim;

		dim = super.getGroesse();
		dim.width += OFFSET.width;
		return dim;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Figure#paintChildren(org.eclipse.draw2d.Graphics)
	 */
	@Override
	protected void paintChildren(Graphics graphics) {
		for (Object o : getChildren()) {
			if (o instanceof GlFigure) {
				((GlFigure) o).setSkalierung(getSkalierung());
			}
		}
		super.paintChildren(graphics);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	@Override
	protected void paintFigure(Graphics g) {
		int schrittweite, zeitbereich;
		Dimension basis;

		basis = getGroesse();
		g.setForegroundColor(ColorConstants.darkGray);

		// Koordinatensystem
		g.setBackgroundColor(ColorConstants.white);
		g.fillRectangle(OFFSET.width, 0, basis.width - 2 * OFFSET.width,
				basis.height - OFFSET.height);
		g.drawRectangle(OFFSET.width, 0, basis.width - 2 * OFFSET.width,
				basis.height - OFFSET.height);

		// Zeitachse
		zeitbereich = (int) ((getSkalierung().getMaxZeit() - getSkalierung()
				.getMinZeit()) / 1000);
		schrittweite = 60 * 60;
		for (int i = 0; i < zeitbereich; i += schrittweite) {
			int x, y;
			Calendar cal;
			DateFormat format;

			x = (int) (OFFSET.width + getSkalierung().getZoomZeit() * i);
			y = basis.height - OFFSET.height;

			cal = Calendar.getInstance();
			cal.setTimeInMillis(getSkalierung().getMinZeit() + i * 1000);
			format = DateFormat.getTimeInstance(DateFormat.SHORT);
			g.drawText(format.format(cal.getTime()), x + 2, y + 1);

			if (cal.get(Calendar.HOUR_OF_DAY) == 0) {
				format = DateFormat.getDateInstance(DateFormat.SHORT);
				g.drawText(format.format(cal.getTime()), x + 2, y + 15);
				g.drawLine(x, y, x, y + 30);
			} else {
				g.drawLine(x, y, x, y + 14);
			}
		}

		// Verkehrsstärke
		schrittweite = (getSkalierung().getMaxQKfz() - getSkalierung()
				.getMinQKfz()) / 10;
		for (int i = getSkalierung().getMinQKfz(); i <= getSkalierung()
				.getMaxQKfz(); i += schrittweite) {
			int x, y;

			x = 0;
			y = (int) (basis.height - OFFSET.height - getSkalierung()
					.getZoomQKfz()
					* i);
			g.drawLine(x, y, x + 60, y);
			g.drawText(String.valueOf(i) + " Kfz/h", x + 2, y + 1);
		}

		// Geschwindigkeit
		schrittweite = (getSkalierung().getMaxVKfz() - getSkalierung()
				.getMinVKfz()) / 10;
		for (int i = getSkalierung().getMinVKfz(); i <= getSkalierung()
				.getMaxVKfz(); i += schrittweite) {
			int x, y;

			x = basis.width - OFFSET.width;
			y = (int) (basis.height - OFFSET.height - getSkalierung()
					.getZoomVKfz()
					* i);
			g.drawLine(x, y, x + 50, y);
			g.drawText(String.valueOf(i) + " km/h", x + 2, y + 1);
		}

		// Legende
		g.setForegroundColor(FARBE_QKFZ);
		g.drawText("QKfz in Kfz/h", OFFSET.width + 10, 5);
		g.setForegroundColor(FARBE_QLKW);
		g.drawText("QLkw in Kfz/h", OFFSET.width + 10, 20);
		g.setForegroundColor(FARBE_VPKW);
		g.drawText("VPkw in km/h", OFFSET.width + 10, 35);
		g.setForegroundColor(FARBE_VLKW);
		g.drawText("VLkw in km/h", OFFSET.width + 10, 50);

		// Mauszeiger
		g.setForegroundColor(ColorConstants.black);
		if (mausPos != null) {
			if (OFFSET.width <= mausPos.x && mausPos.x <= basis.width
					&& 0 <= mausPos.y
					&& mausPos.y <= basis.height - OFFSET.height) {
				g.drawLine(mausPos.x, 0, mausPos.x, basis.height
						- OFFSET.height);
				g.drawLine(OFFSET.width, mausPos.y, basis.width, mausPos.y);

				String txt;
				long t;
				Calendar cal;
				DateFormat format;
				int qKfz, vKfz;

				// Zeit
				t = getSkalierung().getMinZeit()
						+ (long) ((mausPos.x - OFFSET.width) * 1000 / getSkalierung()
								.getZoomZeit());
				cal = Calendar.getInstance();
				cal.setTimeInMillis(t);
				format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
						DateFormat.SHORT);
				txt = "Zeit: " + format.format(cal.getTime()) + " [" + t + "]";

				// Verkehrstärke
				qKfz = (int) (getSkalierung().getMinQKfz() + (basis.height
						- OFFSET.height - mausPos.y)
						/ getSkalierung().getZoomQKfz());
				txt += ", QKfz: " + qKfz + " Kfz/h";

				// Geschwindigkeit
				vKfz = (int) (getSkalierung().getMinVKfz() + (basis.height
						- OFFSET.height - mausPos.y)
						/ getSkalierung().getZoomVKfz());
				txt += ", VKfz: " + vKfz + " km/h";

				g.drawText(txt, OFFSET.width + 100, 5);
			}
		}
	}

}
