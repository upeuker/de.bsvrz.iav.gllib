// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 11.05.2007 11:18:32
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   istart.java

import java.awt.*;
import java.io.PrintStream;
import java.util.Arrays;

import javax.sound.midi.SysexMessage;

class IpaxFrame extends Frame {

	public IpaxFrame(double d, Button button) {
		Farbe = (new Color[] { Color.gray, Color.orange, Color.magenta,
				Color.green, Color.red, Color.cyan });
		MAX = 20;
		Punkte = new Point[MAX];
		aktuell = -1;
		toleranz = 6;
		b_info = new Button("Info");
		b_help = new Button("Help");
		b_zoomin = new Button("Zoom In");
		b_zoomout = new Button("Zoom Out");
		MAXZOOM = 8;
		zoomf = new Label("ZF: 9.9 ");
		statuszeile = new Label(MAX + " Punkte von " + MAX + " sind gesetzt.");
		xkoord = new Label("X: -9999");
		ykoord = new Label("y: -9999");
		Binom = new int[MAX][MAX];
		k = 2;
		setTitle("Ipax II");
		zoomfaktor = d;
		start_knopf = button;
		Panel panel = new Panel();
		panel.setLayout(new FlowLayout());
		panel.setBackground(Color.lightGray);
		panel.add(new Button("Clear All"));
		panel.add(new Button("Quit"));
		panel.add(b_zoomin);
		panel.add(new Button("Reset"));
		panel.add(b_zoomout);
		b_zoomout.disable();
		panel.add(b_info);
		panel.add(b_help);
		Panel panel1 = new Panel();
		panel1.setLayout(new FlowLayout());
		panel1.setBackground(Color.lightGray);
		panel1.add(xkoord);
		panel1.add(ykoord);
		Panel panel2 = new Panel();
		panel2.setLayout(new FlowLayout());
		panel2.setBackground(Color.lightGray);
		panel2.add(zoomf);
		p1 = new Panel();
		p1.setLayout(new BorderLayout());
		p1.setBackground(Color.lightGray);
		p1.add("West", panel1);
		p1.add("Center", panel);
		p1.add("East", panel2);
		p2 = new Panel();
		p2.setLayout(new GridLayout(Art.length, 1));
		p2.setBackground(Color.lightGray);
		for (int i = 0; i < Art.length; i++) {
			Art[i].setBackground(Farbe[i]);
			p2.add(Art[i]);
		}

		p3 = new Panel();
		p3.setLayout(new FlowLayout());
		p3.setBackground(Color.lightGray);
		p3.add(statuszeile);
		setForeground(Color.black);
		setBackground(Color.white);
		add("North", p1);
		add("West", p2);
		add("South", p3);
		initBinom();
	}

	public void initBinom() {
		for (int i = 0; i < MAX; i++) {
			Binom[i][0] = 1;
			Binom[i][i] = 1;
		}

		for (int j = 2; j < MAX; j++) {
			for (int l = 1; l <= j / 2; l++) {
				Binom[j][l] = Binom[j - 1][l] + Binom[j - 1][l - 1];
				Binom[j][j - l] = Binom[j][l];
			}

		}

	}

	public boolean handleEvent(Event event) {
		switch (event.id) {
		case 201: // Event.WINDOW_DESTROY
			if (event.target == this) {
				quit();
				return true;
			} else {
				return super.handleEvent(event);
			}
		}
		return super.handleEvent(event);
	}

	public boolean action(Event event, Object obj) {
		if (event.target instanceof Button) {
			if (obj.equals("Clear All"))
				clearAll();
			else if (obj.equals("Quit"))
				quit();
			else if (obj.equals("Zoom In"))
				zoomIn();
			else if (obj.equals("Reset"))
				zoomReset();
			else if (obj.equals("Zoom Out"))
				zoomOut();
			else if (obj.equals("Info"))
				info();
			else if (obj.equals("Help"))
				help();
		} else if (event.target instanceof Checkbox)
			repaint();
		else
			return super.action(event, obj);
		return true;
	}

	public boolean mouseDown(Event event, int i, int j) {
		boolean flag;
		if (!(event.target instanceof IpaxFrame))
			flag = false;
		else
			flag = true;
		Graphics g = getGraphics();
		aktuell = find(i, j);
		allePunkteNeu(g);
		neueKoordinaten(aktuell);
		if (aktuell < 0 && flag)
			add(i, j);
		else if (aktuell >= 0 && event.clickCount >= 2)
			remove(aktuell);
		return true;
	}

	public boolean mouseDrag(Event event, int i, int j) {
		if (aktuell >= 0 && innen(i, j)) {
			Graphics g = getGraphics();
			g.setXORMode(getBackground());
			zeichnePunkt(g, aktuell);
			Punkte[aktuell].x = i;
			Punkte[aktuell].y = j;
			zeichnePunkt(g, aktuell);
			repaint();
		}
		return true;
	}

	public int find(int i, int j) {
		for (int l = 0; l < anzahl; l++)
			if (Punkte[l].x - toleranz / 2 <= i
					&& i <= Punkte[l].x + toleranz / 2
					&& Punkte[l].y - toleranz / 2 <= j
					&& j <= Punkte[l].y + toleranz / 2)
				return l;

		return -1;
	}

	public boolean innen(int i, int j) {
		return p2.size().width < i && i < p1.size().width
				&& p1.size().height < j
				&& j < p1.size().height + p2.size().height;
	}

	public void add(int i, int j) {
		if (anzahl < MAX) {
			Punkte[anzahl] = new Point(i, j);
			aktuell = anzahl;
			anzahl++;
			repaint();
		}
	}

	public void remove(int i) {
		anzahl--;
		for (int j = i; j < anzahl; j++)
			Punkte[j] = Punkte[j + 1];

		if (aktuell == i)
			aktuell = -1;
		repaint();
	}

	public void paint(Graphics g) {
		zoomf.setText("ZF: " + zoomfaktor + " ");
		statuszeile.setText(anzahl + " Punkte von " + MAX + " sind gesetzt.");
		neueKoordinaten(aktuell);
		if (Art[0].getState())
			simple();
		if (Art[1].getState())
			interpolation();
		if (Art[2].getState())
			akima();
		if (Art[3].getState())
			spline();
		if (Art[4].getState())
			bezier();
		if (Art[5].getState())
			bSpline();
		allePunkteNeu(g);
	}

	public void neueKoordinaten(int i) {
		if (i >= 0 && i < anzahl) {
			xkoord.setText("X: " + Punkte[i].x);
			ykoord.setText("Y: " + Punkte[i].y);
			return;
		} else {
			xkoord.setText("X: ");
			ykoord.setText("Y: ");
			return;
		}
	}

	public void allePunkteNeu(Graphics g) {
		for (int i = 0; i < anzahl; i++)
			zeichnePunkt(g, i);

	}

	public void zeichnePunkt(Graphics g, int i) {
		g.setColor(getBackground());
		g.fillOval(Punkte[i].x - toleranz / 2, Punkte[i].y - toleranz / 2,
				toleranz, toleranz);
		g.setColor(getForeground());
		if (i == aktuell) {
			g.fillOval(Punkte[i].x - toleranz / 2, Punkte[i].y - toleranz / 2,
					toleranz, toleranz);
			return;
		} else {
			g.drawOval(Punkte[i].x - toleranz / 2, Punkte[i].y - toleranz / 2,
					toleranz, toleranz);
			return;
		}
	}

	public void clearAll() {
		anzahl = 0;
		aktuell = -1;
		zoomReset();
		for (int i = 0; i < Art.length; i++)
			Art[i].setState(false);

		repaint();
	}

	public void quit() {
		clearAll();
		start_knopf.setLabel("Start");
		dispose();
	}

	public void zoomReset() {
		while (zoom > 0)
			zoomOut();
		b_zoomin.enable();
		b_zoomout.disable();
	}

	public void zoomIn() {
		transx = size().width / 2;
		transy = size().height / 2;
		for (int i = 0; i < anzahl; i++) {
			Punkte[i].x = (int) ((double) (Punkte[i].x - transx) * zoomfaktor)
					+ transx;
			Punkte[i].y = (int) ((double) (Punkte[i].y - transy) * zoomfaktor)
					+ transy;
		}

		zoom++;
		b_zoomout.enable();
		if (zoom >= MAXZOOM)
			b_zoomin.disable();
		repaint();
	}

	public void zoomOut() {
		for (int i = 0; i < anzahl; i++) {
			Punkte[i].x = (int) ((double) (Punkte[i].x - transx) / zoomfaktor)
					+ transx;
			Punkte[i].y = (int) ((double) (Punkte[i].y - transy) / zoomfaktor)
					+ transy;
		}

		zoom--;
		b_zoomin.enable();
		if (zoom <= 0)
			b_zoomout.disable();
		repaint();
	}

	public void info() {
		String as[] = {
				"Ipax II - Info",
				"Ipax II entstand in Anlehnung an das Programm IPAX (von Thomas Psik",
				"und Michael Schramm). Es wurde von Alexander Polansky im Zuge seiner",
				"Diplomarbeit f\374r das Elektronische, interaktive Skriptum (EiS) in Java",
				"implementiert.", " ", "Ipax II \251 1996 A.Polansky" };
		b_info.disable();
		info = new MsgBox(this, b_info, as, 1, false);
		info.show();
		info.move(location().x + (size().width - info.size().width) / 2,
				location().y + 100);
	}

	public void help() {
		String as[] = {
				"Ipax II - Help",
				"            PUNKTE",
				"Punkt setzen: Klicken Sie in der Zeichenfl\344che.",
				"Punkt l\366schen: Klicken Sie ihn zweimal an.",
				"Punkt verschieben: Ziehen Sie ihn an die neue Position.",
				"            Die Koordinaten des aktuellen Punkts werden links oben angezeigt.",
				" ",
				"            BUTTONS",
				"<Clear All>...L\366scht alle Punkte und alle Auswahlen.",
				"<Quit>...Beendet das Programm.",
				"<Zoom In>...Vergr\366\337ert das Bild um den Zoom-Faktor.",
				"            Der Wert steht rechts oben (\"ZF\"). Der Zoom-Faktor kann auch ",
				"            als HTML-Parameter angegeben werden. Der Default-Wert ist 1.2.",
				"<Reset>...Setzt das Bild in die urspr\374ngliche Gr\366\337e zur\374ck.",
				"<Zoom Out>...Verkleinert das Bild um den Zoom-Faktor.",
				"<Info>...Zeigt allgemeine Informationen zum Programm an.",
				"<Help>...Blendet dieses Hilfe-Fenster ein.",
				" ",
				"            FUNKTIONEN",
				"Simple: Verbindet die Punkte mit geraden Linien.",
				"Interp.: Polynom-Interpolation n-ten Grades.",
				"Akima: Interpolation nach Akima. (mind. 5 Punkte)",
				"Spline: Spline-Interpolation.",
				"Bezier: Zeichnet eine Bezier-Kurve.",
				"B-Spline: Zeichnet eine B-Spline-Kurve. (k = 4; mind. k Punkte)",
				" ",
				"Beachten Sie die maximale Anzahl an Punkten in der Statuszeile.",
				"Beim Zeichnen der Kurven werden fixe Schrittweiten verwendet." };
		b_help.disable();
		help = new MsgBox(this, b_help, as, 0, false);
		help.show();
		help.move(location().x + (size().width - help.size().width) / 2,
				location().y + 60);
	}

	public void simple() {
		Graphics g = getGraphics();
		g.setColor(Farbe[0]);
		for (int i = 0; i < anzahl - 1; i++)
			g.drawLine(Punkte[i].x, Punkte[i].y, Punkte[i + 1].x,
					Punkte[i + 1].y);

	}

	public void interpolation() {
		if (anzahl > 1) {
			Graphics g = getGraphics();
			g.setColor(Farbe[1]);
			double ad[][] = new double[anzahl][anzahl + 1];
			for (int i = 0; i < anzahl; i++) {
				double d = (double) i / (double) (anzahl - 1);
				for (int j = 0; j < anzahl; j++)
					ad[i][j] = Math.pow(d, j);

				ad[i][anzahl] = Punkte[i].x;
			}

			double ad1[] = new double[anzahl];
			ad1 = GSloesen(ad, anzahl);
			for (int l = 0; l < anzahl; l++) {
				double d1 = (double) l / (double) (anzahl - 1);
				for (int i1 = 0; i1 < anzahl; i1++)
					ad[l][i1] = Math.pow(d1, i1);

				ad[l][anzahl] = Punkte[l].y;
			}

			double ad2[] = new double[anzahl];
			ad2 = GSloesen(ad, anzahl);
			int j1 = 10 * (1 + zoom / 4) * anzahl;
			Point point = new Point(0, 0);
			Point point2 = Punkte[0];
			for (int k1 = 1; k1 <= j1; k1++) {
				double d2 = (double) k1 / (double) j1;
				Point point1 = new Point(poly(d2, ad1, anzahl), poly(d2, ad2,
						anzahl));
				g.drawLine(point2.x, point2.y, point1.x, point1.y);
				point2 = point1;
			}

		}
	}

	public double[] GSloesen(double ad[][], int i) {
		for (int j = 0; j < i - 1; j++) {
			double d;
			if ((d = ad[j][j]) != 1.0D) {
				for (int l = j; l <= i; l++)
					ad[j][l] = ad[j][l] / d;

			}
			for (int i1 = j + 1; i1 < i; i1++) {
				double d1;
				if ((d1 = ad[i1][j]) != 0.0D) {
					for (int k1 = j; k1 <= i; k1++)
						ad[i1][k1] = ad[i1][k1] - ad[j][k1] * d1;

				}
			}

		}

		ad[i - 1][i] = ad[i - 1][i] / ad[i - 1][i - 1];
		ad[i - 1][i - 1] = 1.0D;
		for (int j1 = i - 1; j1 > 0; j1--) {
			for (int l1 = j1 - 1; l1 >= 0; l1--) {
				ad[l1][i] = ad[l1][i] - ad[j1][i] * ad[l1][j1];
				ad[l1][j1] = 0.0D;
			}

		}

		double ad1[] = new double[i];
		for (int i2 = 0; i2 < i; i2++)
			ad1[i2] = ad[i2][i];

		return ad1;
	}

	public int poly(double d, double ad[], int i) {
		double d1 = 0.0D;
		for (int j = 0; j < i; j++)
			d1 += Math.pow(d, j) * ad[j];

		return (int) d1;
	}

	public void akima() {
		if (anzahl > 4) {
			Graphics g = getGraphics();
			g.setColor(Farbe[2]);
			double ad[][] = new double[3][4];
			double ad1[] = new double[3];
			double ad2[] = new double[anzahl - 1];
			double ad3[] = new double[anzahl];
			for (int i = 0; i < anzahl - 1; i++)
				ad2[i] = (Punkte[i + 1].x - Punkte[i].x) * (anzahl - 1);

			for (int j = 2; j < anzahl - 2; j++)
				if (Math.abs(ad2[j + 1] - ad2[j])
						+ Math.abs(ad2[j - 1] - ad2[j - 2]) != 0.0D)
					ad3[j] = (ad2[j - 1] * Math.abs(ad2[j + 1] - ad2[j]) + ad2[j]
							* Math.abs(ad2[j - 1] - ad2[j - 2]))
							/ (Math.abs(ad2[j + 1] - ad2[j]) + Math
									.abs(ad2[j - 1] - ad2[j - 2]));
				else
					ad3[j] = (ad2[j - 1] + ad2[j]) / 2D;

			for (int l = 0; l <= 2; l++) {
				ad[l][0] = 1.0D;
				ad[l][1] = (double) l / (double) (anzahl - 1);
				ad[l][2] = (((double) l / (double) (anzahl - 1)) * (double) l)
						/ (double) (anzahl - 1);
				ad[l][3] = Punkte[l].x;
			}

			ad1 = GSloesen(ad, 3);
			ad3[0] = ad1[1];
			ad3[1] = ad1[1] + (2D * ad1[2]) / (double) (anzahl - 1);
			for (int i1 = 0; i1 <= 2; i1++) {
				ad[i1][0] = 1.0D;
				ad[i1][1] = (double) ((anzahl - 3) + i1)
						/ (double) (anzahl - 1);
				ad[i1][2] = (((double) ((anzahl - 3) + i1) / (double) (anzahl - 1)) * (double) ((anzahl - 3) + i1))
						/ (double) (anzahl - 1);
				ad[i1][3] = Punkte[(anzahl - 3) + i1].x;
			}

			ad1 = GSloesen(ad, 3);
			ad3[anzahl - 1] = ad1[1] + 2D * ad1[2];
			ad3[anzahl - 2] = ad1[1] + (2D * ad1[2] * (double) (anzahl - 2))
					/ (double) (anzahl - 1);
			double ad4[] = new double[anzahl - 1];
			double ad5[] = new double[anzahl];
			for (int j1 = 0; j1 < anzahl - 1; j1++)
				ad4[j1] = (Punkte[j1 + 1].y - Punkte[j1].y) * (anzahl - 1);

			for (int k1 = 2; k1 < anzahl - 2; k1++)
				if (Math.abs(ad4[k1 + 1] - ad4[k1])
						+ Math.abs(ad4[k1 - 1] - ad4[k1 - 2]) != 0.0D)
					ad5[k1] = (ad4[k1 - 1] * Math.abs(ad4[k1 + 1] - ad4[k1]) + ad4[k1]
							* Math.abs(ad4[k1 - 1] - ad4[k1 - 2]))
							/ (Math.abs(ad4[k1 + 1] - ad4[k1]) + Math
									.abs(ad4[k1 - 1] - ad4[k1 - 2]));
				else
					ad5[k1] = (ad4[k1 - 1] + ad4[k1]) / 2D;

			for (int l1 = 0; l1 <= 2; l1++) {
				ad[l1][0] = 1.0D;
				ad[l1][1] = (double) l1 / (double) (anzahl - 1);
				ad[l1][2] = (((double) l1 / (double) (anzahl - 1)) * (double) l1)
						/ (double) (anzahl - 1);
				ad[l1][3] = Punkte[l1].y;
			}

			ad1 = GSloesen(ad, 3);
			ad5[0] = ad1[1];
			ad5[1] = ad1[1] + (2D * ad1[2]) / (double) (anzahl - 1);
			for (int i2 = 0; i2 <= 2; i2++) {
				ad[i2][0] = 1.0D;
				ad[i2][1] = (double) ((anzahl - 3) + i2)
						/ (double) (anzahl - 1);
				ad[i2][2] = (((double) ((anzahl - 3) + i2) / (double) (anzahl - 1)) * (double) ((anzahl - 3) + i2))
						/ (double) (anzahl - 1);
				ad[i2][3] = Punkte[(anzahl - 3) + i2].y;
			}

			ad1 = GSloesen(ad, 3);
			ad5[anzahl - 1] = ad1[1] + 2D * ad1[2];
			ad5[anzahl - 2] = ad1[1] + (2D * ad1[2] * (double) (anzahl - 2))
					/ (double) (anzahl - 1);
			int j2 = 10 * (1 + zoom / 4);
			Point point = new Point(0, 0);
			Point point1 = new Point(0, 0);
			point1.x = Punkte[0].x;
			point1.y = Punkte[0].y;
			for (int k2 = 0; k2 <= anzahl - 2; k2++) {
				for (int l2 = 1; l2 <= j2; l2++) {
					double d = (double) l2 / (double) j2;
					double d1 = 0.5D + (d - 0.5D)
							* (2D * (d - 0.5D) * (d - 0.5D) - 1.5D);
					double d2 = 1.0D - d1;
					double d3 = d * (d - 1.0D) * (d - 1.0D);
					double d4 = d * d * (d - 1.0D);
					point.x = (int) ((double) Punkte[k2].x * d1
							+ (double) Punkte[k2 + 1].x * d2 + (d3 * ad3[k2])
							/ (double) (anzahl - 1) + (d4 * ad3[k2 + 1])
							/ (double) (anzahl - 1));
					point.y = (int) ((double) Punkte[k2].y * d1
							+ (double) Punkte[k2 + 1].y * d2 + (d3 * ad5[k2])
							/ (double) (anzahl - 1) + (d4 * ad5[k2 + 1])
							/ (double) (anzahl - 1));
					g.drawLine(point1.x, point1.y, point.x, point.y);
					point1.x = point.x;
					point1.y = point.y;
				}

			}

		}
	}

	public void spline() {
		if (anzahl > 1) {
			Graphics g = getGraphics();
			g.setColor(Farbe[3]);
			double ad[][] = new double[4 * (anzahl - 1)][4 * (anzahl - 1) + 1];
			boolean flag = true;
			initSpline(ad, flag);
			double ad1[] = new double[4 * (anzahl - 1)];
			ad1 = GSloesen(ad, 4 * (anzahl - 1));
			for (int i = 0; i < 4 * (anzahl - 1); i++) {
				for (int j = 0; j <= 4 * (anzahl - 1); j++)
					ad[i][j] = 0.0D;

			}

			flag = false;
			initSpline(ad, flag);
			double ad2[] = new double[4 * (anzahl - 1)];
			ad2 = GSloesen(ad, 4 * (anzahl - 1));
			int l = 10 * (1 + zoom / 4);
			double ad3[] = new double[4];
			double ad4[] = new double[4];
			double d = 0.0D;
			for (int i1 = 0; i1 < anzahl - 1; i1++) {
				Point point1 = Punkte[i1];
				for (int j1 = 0; j1 < 4; j1++) {
					ad3[j1] = ad1[4 * i1 + j1];
					ad4[j1] = ad2[4 * i1 + j1];
				}

				for (int k1 = 1; k1 <= l; k1++) {
					double d1 = ((double) i1 + (double) k1 / (double) l)
							/ (double) (anzahl - 1);
					Point point = new Point(poly(d1, ad3, 4), poly(d1, ad4, 4));
					g.drawLine(point1.x, point1.y, point.x, point.y);
					point1 = point;
				}

			}

		}
	}

	public void initSpline(double ad[][], boolean flag) {
		for (int i = 0; i < anzahl - 2; i++) {
			double d = (double) i / (double) (anzahl - 1);
			double d2 = (double) (i + 1) / (double) (anzahl - 1);
			for (int j = 0; j < 4; j++) {
				ad[4 * i][4 * i + j] = Math.pow(d, j);
				ad[4 * i + 1][4 * i + j] = Math.pow(d2, j);
				ad[4 * i + 2][4 * i + j] = (double) j * Math.pow(d2, j - 1);
				ad[4 * i + 2][4 * (i + 1) + j] = (double) (-1 * j)
						* Math.pow(d2, j - 1);
				ad[4 * i + 3][4 * i + j] = (double) (j * (j - 1))
						* Math.pow(d2, j - 2);
				ad[4 * i + 3][4 * (i + 1) + j] = (double) (-1 * j * (j - 1))
						* Math.pow(d2, j - 2);
			}

			if (flag)
				ad[4 * i][4 * (anzahl - 1)] = Punkte[i].x;
			else
				ad[4 * i][4 * (anzahl - 1)] = Punkte[i].y;
			if (flag)
				ad[4 * i + 1][4 * (anzahl - 1)] = Punkte[i + 1].x;
			else
				ad[4 * i + 1][4 * (anzahl - 1)] = Punkte[i + 1].y;
			ad[4 * i + 2][4 * (anzahl - 1)] = 0.0D;
			ad[4 * i + 3][4 * (anzahl - 1)] = 0.0D;
		}

		double d1 = (double) (anzahl - 2) / (double) (anzahl - 1);
		double d3 = 1.0D;
		for (int l = 0; l < 4; l++) {
			ad[4 * (anzahl - 2)][4 * (anzahl - 2) + l] = Math.pow(d1, l);
			ad[4 * (anzahl - 2) + 1][4 * (anzahl - 2) + l] = Math.pow(d3, l);
		}

		if (flag)
			ad[4 * (anzahl - 2)][4 * (anzahl - 1)] = Punkte[anzahl - 2].x;
		else
			ad[4 * (anzahl - 2)][4 * (anzahl - 1)] = Punkte[anzahl - 2].y;
		if (flag)
			ad[4 * (anzahl - 2) + 1][4 * (anzahl - 1)] = Punkte[anzahl - 1].x;
		else
			ad[4 * (anzahl - 2) + 1][4 * (anzahl - 1)] = Punkte[anzahl - 1].y;
		ad[4 * (anzahl - 2) + 2][2] = 2D;
		ad[4 * (anzahl - 2) + 3][4 * (anzahl - 2) + 2] = 2D;
		ad[4 * (anzahl - 2) + 3][4 * (anzahl - 2) + 3] = 6D;
	}

	public void bezier() {
		if (anzahl > 1) {
			Graphics g = getGraphics();
			g.setColor(Farbe[4]);
			int i = 5 * (zoom + 1) * anzahl;
			Point point = new Point(0, 0);
			Point point2 = Punkte[0];
			for (int j = 1; j <= i; j++) {
				double d = (double) j / (double) i;
				Point point1 = bez(d);
				g.drawLine(point2.x, point2.y, point1.x, point1.y);
				point2 = point1;
			}

		}
	}

	public Point bez(double d) {
		double d2 = 0.0D;
		double d3 = 0.0D;
		for (int i = 0; i <= anzahl - 1; i++) {
			double d1 = Math.pow(d, i) * Math.pow(1.0D - d, anzahl - 1 - i);
			d2 += d1 * (double) (Punkte[i].x * Binom[anzahl - 1][i]);
			d3 += d1 * (double) (Punkte[i].y * Binom[anzahl - 1][i]);
		}

		Point point = new Point((int) d2, (int) d3);
		return point;
	}

	public void debug(double ad[]) {
		for (int i = 0; i < ad.length; i++)
			System.out.print(" " + (int) (100D * ad[i] + 0.5D) / 100);

		System.out.println();
	}

	public void bSpline() {
		if (anzahl >= k) {
			Graphics g = getGraphics();
			g.setColor(Farbe[5]);
			int ai[] = new int[anzahl + k];
			for (int i = 0; i <= (anzahl - 1) + k; i++)
				if (i < k)
					ai[i] = 0;
				else if (k <= i && i <= anzahl - 1)
					ai[i] = (i - k) + 1;
				else if (anzahl - 1 < i)
					ai[i] = (anzahl - 1 - k) + 2;

			int j = (anzahl - 1 - k) + 2;
			double d = 1.0D / (double) (10 + anzahl + zoom);
			Point point = new Point(0, 0);
			Point point2 = Punkte[0];
			int zaehler = 0;
			for (double d1 = 0.0D; d1 < (double) j; d1 += d) {
				Point point1 = bspl(ai, d1);
				zaehler++;
				g.drawLine(point2.x, point2.y, point1.x, point1.y);
				point2 = point1;
			}

			g.drawLine(point2.x, point2.y, Punkte[anzahl - 1].x,
					Punkte[anzahl - 1].y);
		}
	}

	public Point bspl(int ai[], double d) {
		double d1 = 0.0D;
		double d2 = 0.0D;
		double ad[] = new double[k];
		double d3 = 0.0D;
		double d5 = 0.0D;
		int i = ((int) d + k) - 1;
		// ad[3] = 1.0D;
		ad[k - 1] = 1.0D;
		for (int l = 2; l <= k; l++) {
			for (int i1 = l - 1; i1 >= 0; i1--) {
				int j = i - i1;
				double d4;
				if (i1 == l - 1)
					d4 = 0.0D;
				else
					d4 = ((d - (double) ai[j]) * ad[k - 1 - i1])
							/ (double) (ai[(j + l) - 1] - ai[j]);
				double d6;
				if (i1 == 0)
					d6 = 0.0D;
				else
					d6 = (((double) ai[j + l] - d) * ad[(k - 1 - i1) + 1])
							/ (double) (ai[j + l] - ai[j + 1]);
				ad[k - 1 - i1] = d4 + d6;
			}

		}

		System.err.print("n = ");
		for (int j = 0; j < ad.length; j++) {
			System.err.print(ad[j]);
			if (j < ad.length - 1) {
				System.err.print(", ");
			}
		}
		System.out.println();

		for (int j1 = 0; j1 < k; j1++) {
			d1 += (double) Punkte[((j1 + i) - k) + 1].x * ad[j1];
			d2 += (double) Punkte[((j1 + i) - k) + 1].y * ad[j1];
		}

		Point point = new Point((int) d1, (int) d2);
		return point;
	}

	Button start_knopf;

	Checkbox Art[] = { new Checkbox("Simple"), new Checkbox("Interp."),
			new Checkbox("Akima"), new Checkbox("Spline"),
			new Checkbox("Bezier"), new Checkbox("B-Spline") };

	Color Farbe[];

	int MAX;

	Point Punkte[];

	int anzahl;

	int aktuell;

	int toleranz;

	Button b_info;

	Button b_help;

	Button b_zoomin;

	Button b_zoomout;

	int zoom;

	int MAXZOOM;

	double zoomfaktor;

	Label zoomf;

	int transx;

	int transy;

	Label statuszeile;

	Label xkoord;

	Label ykoord;

	MsgBox info;

	MsgBox help;

	Panel p1;

	Panel p2;

	Panel p3;

	int Binom[][];

	int k;

	public static void main(String[] argv) {
		IpaxFrame pax;

		pax = new IpaxFrame(1.0, null);
		pax.add(0, 0);
		pax.add(30, 30);
		pax.add(40, 20);
		pax.add(60, 40);
		pax.add(90, 10);

		pax.k = 2;

		int ai[] = new int[pax.anzahl + pax.k];
		for (int i = 0; i <= (pax.anzahl - 1) + pax.k; i++)
			if (i < pax.k)
				ai[i] = 0;
			else if (pax.k <= i && i <= pax.anzahl - 1)
				ai[i] = (i - pax.k) + 1;
			else if (pax.anzahl - 1 < i)
				ai[i] = (pax.anzahl - 1 - pax.k) + 2;

		System.err.print("t = ");
		for (int i = 0; i < ai.length; i++) {
			System.err.print(ai[i]);
			if (i < ai.length - 1) {
				System.err.print(", ");
			}
		}
		System.err.println();

		System.out.println("Soll: " + pax.Punkte[0] + ", Ist=" + pax.bspl(ai, 0));
		System.out.println("Soll: " + pax.Punkte[1] + ", Ist=" + pax.bspl(ai, 4f/3f));
		System.out.println("Soll: " + pax.Punkte[2] + ", Ist=" + pax.bspl(ai, 16f/9f));
		System.out.println("Soll: " + pax.Punkte[3] + ", Ist=" + pax.bspl(ai, 8f/3f));
		System.out.println("Soll: " + pax.Punkte[4] + ", Ist=" + pax.bspl(ai, 4));

	}
}
