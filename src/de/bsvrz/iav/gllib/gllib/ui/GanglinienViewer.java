package de.bsvrz.iav.gllib.gllib.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

import de.bsvrz.iav.gllib.gllib.Ganglinie;
import de.bsvrz.iav.gllib.gllib.Polyline;

public class GanglinienViewer extends JFrame {

	public GanglinienViewer(Ganglinie g) {
		setTitle("Ganglinienviewer");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		setSize(1000, 500);

		Container content;
		JPanel optionen;
		JCheckBox checkbox;
		JSlider slider;
		GanglinienPanel panel;
		int maxIntervalle;

		content = new JPanel(new BorderLayout());

		// Zeichenfläche
		panel = new GanglinienPanel(g);
		content.add(panel, BorderLayout.CENTER);

		// Optionen
		optionen = new JPanel(new FlowLayout());
		checkbox = new JCheckBox("Punkte verbinden");
		checkbox.addChangeListener(panel);
		optionen.add(checkbox);
		maxIntervalle = (int) g.getIntervall().breite;
		slider = new JSlider(1, maxIntervalle, maxIntervalle / 2);
		panel.setIntervalle(maxIntervalle / 2);
		slider.addChangeListener(panel);
		optionen.add(slider);
		content.add(optionen, BorderLayout.NORTH);

		setContentPane(content);

		setVisible(true);
	}

	public static void main(String argv[]) {
		Ganglinie g;

		g = new Ganglinie();
		g.set(0, 0);
		g.set(300, 300);
		g.set(400, 200);
		g.set(600, 400);
		g.set(900, 100);

		g.setApproximation(Polyline.class);
		new GanglinienViewer(g);
	}

}
