package de.bsvrz.iav.gllib.gllib.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.SortedSet;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.bsvrz.iav.gllib.gllib.BSpline;
import de.bsvrz.iav.gllib.gllib.CubicSpline;
import de.bsvrz.iav.gllib.gllib.Ganglinie;
import de.bsvrz.iav.gllib.gllib.Polyline;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;

public class GanglinienPanel extends JPanel {

	private final Ganglinie ganglinie;

	private int intervalle = 20;

	private boolean verbinden = false;

	private boolean polyline = true;

	private boolean cubicspline = true;

	private boolean bspline = false;

	public GanglinienPanel(Ganglinie ganglinie) {
		setBackground(Color.white);
		this.ganglinie = ganglinie;
	}

	public boolean isBspline() {
		return bspline;
	}

	public void setBspline(boolean bspline) {
		this.bspline = bspline;
	}

	public boolean isCubicspline() {
		return cubicspline;
	}

	public void setCubicspline(boolean cubicspline) {
		this.cubicspline = cubicspline;
	}

	public boolean isPolyline() {
		return polyline;
	}

	public void setPolyline(boolean polyline) {
		this.polyline = polyline;
	}

	public int getIntervalle() {
		return intervalle;
	}

	public void setIntervalle(int intervalle) {
		this.intervalle = intervalle;
		repaint();
	}

	public boolean isVerbinden() {
		return verbinden;
	}

	public void setVerbinden(boolean verbinden) {
		this.verbinden = verbinden;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// St�tzstellen zeichnen
		g.setColor(Color.red);
		for (Stuetzstelle s : ganglinie.getStuetzstellen()) {
			g.fillOval((int) s.zeitstempel - 5, s.wert - 5, 10, 10);
		}

		// Approximationen zeichnen
		if (polyline) {
			zeichne(g, new Polyline(ganglinie).interpoliere(intervalle),
					Color.blue);
		}
		if (cubicspline) {
			zeichne(g, new CubicSpline(ganglinie).interpoliere(intervalle),
					Color.green);
		}
		if (bspline) {
			zeichne(g, new BSpline(ganglinie).interpoliere(intervalle),
					Color.yellow);
		}

	}

	private void zeichne(Graphics g, SortedSet<Stuetzstelle> stuetzstellen,
			Color farbe) {
		Stuetzstelle s0;

		g.setColor(farbe);
		s0 = stuetzstellen.first();
		for (Stuetzstelle s : stuetzstellen) {
			if (verbinden) {
				if (s0.equals(s)) {
					continue;
				}

				g.drawLine((int) s0.zeitstempel, s0.wert, (int) s.zeitstempel,
						s.wert);
				if (s.equals(stuetzstellen.last())) {
					g
							.drawLine(
									(int) s.zeitstempel,
									s.wert,
									(int) ganglinie.getStuetzstellen()
											.get(
													ganglinie
															.getStuetzstellen()
															.size() - 1).zeitstempel,
									ganglinie.getStuetzstellen()
											.get(
													ganglinie
															.getStuetzstellen()
															.size() - 1).wert);
				}

				s0 = s;

			} else {
				g.fillOval((int) s.zeitstempel - 1, s.wert - 1, 2, 2);
			}
		}
	}

}