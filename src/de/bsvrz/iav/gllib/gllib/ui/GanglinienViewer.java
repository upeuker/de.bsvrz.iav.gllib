package de.bsvrz.iav.gllib.gllib.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.bsvrz.iav.gllib.gllib.Ganglinie;

public class GanglinienViewer extends JFrame implements ChangeListener {

	private GanglinienPanel ganglinie;

	private JLabel intervalle;

	public static void main(String argv[]) {
		Ganglinie g;

		g = new Ganglinie();
		g.set(0, 0);
		g.set(300, 300);
		g.set(400, 200);
		g.set(600, 400);
		g.set(900, 100);

		new GanglinienViewer(g);
	}

	public GanglinienViewer(Ganglinie g) {
		setTitle("Ganglinienviewer");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		setSize(1000, 500);

		// Zeichenfl�che
		ganglinie = new GanglinienPanel(g);

		// Approximation
		JCheckBox polyline;
		JCheckBox cubicspline;
		JCheckBox bspline;
		polyline = new JCheckBox("Polyline");
		polyline.addChangeListener(this);
		cubicspline = new JCheckBox("Cubic Spline");
		cubicspline.addChangeListener(this);
		bspline = new JCheckBox("B-Spline");
		bspline.addChangeListener(this);

		// Punkte verbinden
		JCheckBox checkbox;
		checkbox = new JCheckBox("Punkte verbinden");
		checkbox.addChangeListener(this);

		// Anzahl Intervalle
		int maxIntervalle;
		JSlider slider;
		maxIntervalle = (int) g.getIntervall().breite;
		slider = new JSlider(1, maxIntervalle, maxIntervalle / 2);
		ganglinie.setIntervalle(maxIntervalle / 2);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing((maxIntervalle / 4) - 1);
		slider.setMinorTickSpacing((maxIntervalle / 8));
		slider.addChangeListener(this);
		intervalle = new JLabel(String.valueOf(slider.getValue()));
		slider.addChangeListener(this);

		// Optionen
		JPanel optionen;
		optionen = new JPanel(new FlowLayout());
		optionen.add(checkbox);
		optionen.add(slider);
		optionen.add(intervalle);

		// ContentPane
		Container content;
		content = new JPanel(new BorderLayout());
		content.add(optionen, BorderLayout.NORTH);
		content.add(ganglinie, BorderLayout.CENTER);
		setContentPane(content);

		setVisible(true);
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof JCheckBox) {
			JCheckBox cb;

			cb = (JCheckBox) e.getSource();
			ganglinie.setVerbinden(cb.isSelected());
		} else if (e.getSource() instanceof JSlider) {
			JSlider sl;

			sl = (JSlider) e.getSource();
			ganglinie.setIntervalle(sl.getValue());
			intervalle.setText(String.valueOf(sl.getValue()));
		} else {
			return;
		}

		ganglinie.repaint();
	}

}