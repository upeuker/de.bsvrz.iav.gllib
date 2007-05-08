package de.bsvrz.iav.gllib.gllib.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.SortedSet;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.bsvrz.iav.gllib.gllib.Ganglinie;
import de.bsvrz.iav.gllib.gllib.Polyline;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;

public class GanglinienPanel extends JPanel implements ChangeListener {

	private final Ganglinie ganglinie;

	private int intervalle = 20;

	private boolean verbinden = false;

	public GanglinienPanel(Ganglinie ganglinie) {
		setBackground(Color.white);
		this.ganglinie = ganglinie;
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

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof JCheckBox) {
			JCheckBox cb;

			cb = (JCheckBox) e.getSource();
			verbinden = cb.isSelected();
		} else if (e.getSource() instanceof JSlider) {
			JSlider sl;

			sl = (JSlider) e.getSource();
			intervalle = sl.getValue();
		} else {
			return;
		}

		System.out.println(ganglinie.getIntervall().breite + ", " + intervalle
				+ ", " + verbinden);

		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Stützstellen zeichnen
		g.setColor(Color.red);
		for (Stuetzstelle s : ganglinie.getStuetzstellen()) {
			g.fillOval((int) s.zeitstempel - 5, s.wert - 5, 10, 10);
		}

		// Polyline zeichnen
		Polyline polyline;
		Stuetzstelle s0;
		polyline = new Polyline(ganglinie);
		g.setColor(Color.blue);
		SortedSet<Stuetzstelle> stuetzstellen;
		stuetzstellen = polyline.interpoliere(intervalle);
		s0 = stuetzstellen.first();
		for (Stuetzstelle s : stuetzstellen) {
			if (verbinden) {
				if (s0.equals(s)) {
					continue;
				}

				g.drawLine((int) s0.zeitstempel, s0.wert, (int) s.zeitstempel,
						s.wert);
				if (s.equals(stuetzstellen.last())) {
					g.drawLine((int) s.zeitstempel, s.wert, (int) ganglinie
							.getStuetzstellen().last().zeitstempel, ganglinie
							.getStuetzstellen().last().wert);
				}

				s0 = s;

			} else {
				g.fillOval((int) s.zeitstempel - 1, s.wert - 1, 2, 2);
			}
		}
	}

}
