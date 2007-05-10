package de.bsvrz.iav.gllib.gllib.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sun.jdbc.odbc.OdbcDef;

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

		// Zeichenfläche
		ganglinie = new GanglinienPanel(g);

		// Approximation
		JCheckBox polyline;
		JCheckBox cubicspline;
		JCheckBox bspline;
		JSpinner ordnung;
		polyline = new JCheckBox("Polyline");
		polyline.addChangeListener(this);
		cubicspline = new JCheckBox("Cubic Spline");
		cubicspline.addChangeListener(this);
		bspline = new JCheckBox("B-Spline");
		bspline.addChangeListener(this);
		SpinnerListModel modell = new SpinnerListModel(new Integer[] { 0, 1, 2,
				3, 4, 5, 6, 7, 8, 9, 10 });
		ordnung = new JSpinner(modell);
		ordnung.setPreferredSize(new Dimension(50, 20));
		ordnung.addChangeListener(this);

		// Punkte verbinden
		JCheckBox checkbox;
		checkbox = new JCheckBox("Punkte verbinden");
		checkbox.addChangeListener(this);

		// Anzahl Intervalle
		JSlider slider;
		slider = new JSlider(1, 100, 20);
		ganglinie.setIntervalle(20);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(10);
		slider.addChangeListener(this);
		intervalle = new JLabel(String.valueOf(slider.getValue()));
		slider.addChangeListener(this);

		// Optionen
		JPanel optionen;
		optionen = new JPanel(new FlowLayout());
		optionen.add(polyline);
		optionen.add(cubicspline);
		optionen.add(bspline);
		optionen.add(ordnung);
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
			if ("Punkte verbinden".equals(cb.getText())) {
				ganglinie.setVerbinden(cb.isSelected());
			}
			if ("Polyline".equals(cb.getText())) {
				ganglinie.setPolyline(cb.isSelected());
			}
			if ("Cubic Spline".equals(cb.getText())) {
				ganglinie.setCubicspline(cb.isSelected());
			}
			if ("B-Spline".equals(cb.getText())) {
				ganglinie.setBspline(cb.isSelected());
			}
		} else if (e.getSource() instanceof JSlider) {
			JSlider sl;

			sl = (JSlider) e.getSource();
			ganglinie.setIntervalle(sl.getValue());
			intervalle.setText(String.valueOf(sl.getValue()));
		} else if (e.getSource() instanceof JSpinner) {
			JSpinner s;

			s = (JSpinner) e.getSource();
			ganglinie.setOrdnung(Integer.valueOf(s.getValue().toString()));
		} else {
			return;
		}

		ganglinie.repaint();
	}

}
