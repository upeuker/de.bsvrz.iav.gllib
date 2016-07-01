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

package de.bsvrz.iav.gllib.gllib;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Kleines Testprogramm, um die verschiedenen Approximationen zu visualisieren
 * und zu vergleichen. Es wird dazu ein Beispiel von fest vorgegebenen
 * Stützstellen verwendet.
 *
 * @author BitCtrl Systems GmbH, Falko Schumann
 */
@SuppressWarnings("nls")
public final class ApproximationAnzeige extends JFrame implements ItemListener {

	/** Die ID für die Serialisierung. */
	private static final long serialVersionUID = 1L;

	/**
	 * Startet das Programm.
	 *
	 * @param args
	 *            es werden keine Kommandozeilenargumente verwendet.
	 */
	public static void main(final String[] args) {
		new ApproximationAnzeige().setVisible(true);
	}

	/**
	 * Das Widget, auf dem die Approximationen gezeichnet werden.
	 */
	private class GanglinienComponent extends JComponent {

		/** Die ID für die Serialisierung. */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paint(final Graphics g) {
			final Graphics2D g2;

			super.paint(g);
			g2 = (Graphics2D) g;

			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, getWidth(), getHeight());

			g2.setStroke(new BasicStroke(3f));

			if (showPolyline.isSelected()) {
				Stuetzstelle<Double> von;

				g2.setColor(Color.RED);
				von = null;
				for (final Stuetzstelle<Double> s : polyline) {
					if (von != null) {
						g2.drawLine((int) von.getZeitstempel(),
								von.getWert().intValue(),
								(int) s.getZeitstempel(),
								s.getWert().intValue());
					}
					von = s;
				}
			}

			if (showCubicSpline.isSelected()) {
				Stuetzstelle<Double> von;

				g2.setColor(Color.GREEN);
				von = null;
				for (final Stuetzstelle<Double> s : cubicspline) {
					if (von != null) {
						g2.drawLine((int) von.getZeitstempel(),
								von.getWert().intValue(),
								(int) s.getZeitstempel(),
								s.getWert().intValue());
					}
					von = s;
				}
			}

			if (showBSpline.isSelected()) {
				Stuetzstelle<Double> von;

				g2.setColor(Color.BLUE);
				von = null;
				for (final Stuetzstelle<Double> s : bspline) {
					if (von != null) {
						g2.drawLine((int) von.getZeitstempel(),
								von.getWert().intValue(),
								(int) s.getZeitstempel(),
								s.getWert().intValue());
					}
					von = s;
				}
			}

			g2.setColor(Color.BLACK);
			for (final Stuetzstelle<Double> s : stuetzstellen) {
				g2.drawRect((int) s.getZeitstempel() - 5,
						s.getWert().intValue() - 5, 10, 10);
			}
		}

	}

	/** Die Liste der verwendeten Stützstellen. */
	private final List<Stuetzstelle<Double>> stuetzstellen;

	/** Die Interpolation der Polylinie. */
	private final SortedSet<Stuetzstelle<Double>> polyline;

	/** Die Interpolation des Cubic Splines. */
	private final SortedSet<Stuetzstelle<Double>> cubicspline;

	/** Die Interpolation des B-Splines. */
	private final SortedSet<Stuetzstelle<Double>> bspline;

	/** Das Widget auf dem die Approximationen gezeichnet werden. */
	private final GanglinienComponent ganglinienComponent;

	/** Die Auswahlbox gibt an, ob die Polylinie angezeigt werden soll. */
	private final JCheckBox showPolyline;

	/** Die Auswahlbox gibt an, ob der Cubic Spline angezeigt werden soll. */
	private final JCheckBox showCubicSpline;

	/** Die Auswahlbox gibt an, ob der B-Spline angezeigt werden soll. */
	private final JCheckBox showBSpline;

	/** Ein Widget zum Ändern der Ordnung des B-Splines. */
	private final JSpinner bSplineOrdnung;

	/**
	 * Initialisiert das Applikationsfenster und die Approximationen.
	 */
	private ApproximationAnzeige() {
		super("Vergleich der Approximationen");

		final JPanel contentPane, toolbar;
		final Polyline appPolyline;
		final CubicSpline appCubicSpline;
		final BSpline appBSpline;

		stuetzstellen = new ArrayList<Stuetzstelle<Double>>();
		stuetzstellen.add(new Stuetzstelle<Double>(100, 100.0));
		stuetzstellen.add(new Stuetzstelle<Double>(200, 400.0));
		stuetzstellen.add(new Stuetzstelle<Double>(300, 300.0));
		stuetzstellen.add(new Stuetzstelle<Double>(500, 500.0));
		stuetzstellen.add(new Stuetzstelle<Double>(700, 200.0));

		appPolyline = new Polyline();
		appPolyline.setStuetzstellen(stuetzstellen);
		appPolyline.initialisiere();
		polyline = appPolyline.interpoliere(10);

		appCubicSpline = new CubicSpline();
		appCubicSpline.setStuetzstellen(stuetzstellen);
		appCubicSpline.setFaktor(1);
		appCubicSpline.initialisiere();
		cubicspline = appCubicSpline.interpoliere(10);

		appBSpline = new BSpline();
		appBSpline.setStuetzstellen(stuetzstellen);
		appBSpline.setInterpolationsintervall(10);
		appBSpline.initialisiere();
		bspline = appBSpline.interpoliere(10);

		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		toolbar = new JPanel(new FlowLayout());

		showPolyline = new JCheckBox("Polylinie", false);
		showPolyline.addItemListener(this);
		toolbar.add(showPolyline);

		showCubicSpline = new JCheckBox("Cubic Spline", false);
		showCubicSpline.addItemListener(this);
		toolbar.add(showCubicSpline);

		showBSpline = new JCheckBox("B-Spline", false);
		showBSpline.addItemListener(this);
		toolbar.add(showBSpline);

		bSplineOrdnung = new JSpinner(
				new SpinnerNumberModel(5, 1, stuetzstellen.size(), 1));
		bSplineOrdnung.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent e) {
				appBSpline.setOrdnung(
						((SpinnerNumberModel) bSplineOrdnung.getModel())
								.getNumber().byteValue());
				appBSpline.initialisiere();
				bspline.clear();
				bspline.addAll(appBSpline.interpoliere(10));
				itemStateChanged(null);
			}
		});
		toolbar.add(bSplineOrdnung);

		ganglinienComponent = new GanglinienComponent();

		contentPane = new JPanel(new BorderLayout());
		contentPane.add(toolbar, BorderLayout.NORTH);
		contentPane.add(ganglinienComponent, BorderLayout.CENTER);
		setContentPane(contentPane);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void itemStateChanged(final ItemEvent e) {
		ganglinienComponent.repaint();
	}

}
