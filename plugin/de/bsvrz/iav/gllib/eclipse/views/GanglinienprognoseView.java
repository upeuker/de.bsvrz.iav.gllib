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

package de.bsvrz.iav.gllib.eclipse.views;

import java.util.Calendar;
import java.util.Collections;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.bitctrl.util.Interval;

import de.bsvrz.iav.gllib.eclipse.GanglinienprognosePlugin;
import de.bsvrz.iav.gllib.eclipse.draw2d.GlGanglinieMQ;
import de.bsvrz.iav.gllib.eclipse.draw2d.GlKoordinatensystem;
import de.bsvrz.iav.gllib.eclipse.draw2d.GlSkalierung;
import de.bsvrz.iav.gllib.eclipse.views.provider.GanglinieLabelProvider;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.Ganglinienprognose;
import de.bsvrz.iav.gllib.gllib.dav.GlProgAnfrage;
import de.bsvrz.iav.gllib.gllib.dav.GlProgAntwortEvent;
import de.bsvrz.iav.gllib.gllib.dav.GlProgAntwortListener;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensendeException;
import de.bsvrz.sys.funclib.bitctrl.modell.ModellTools;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;

/**
 * Diese View erlaubt das Absetzen einer einfachen Ganglinienanfrage und zeigt
 * ebenfalls die Antwort an.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class GanglinienprognoseView extends ViewPart implements
		GlProgAntwortListener {

	/** Die Eigenschaft {@code ganglinienListe}. */
	private TableViewer ganglinienListe;

	/** Die Eigenschaft {@code txtAntwort}. */
	private Text txtAntwort;

	/** Die Eigenschaft {@code kos}. */
	private GlKoordinatensystem kos;

	/** Die Eigenschaft {@code ganglinie}. */
	private GlGanglinieMQ ganglinie;

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.dav.GlProgAntwortListener#antwortEingetroffen(de.bsvrz.iav.gllib.gllib.dav.GlProgAntwortEvent)
	 */
	public void antwortEingetroffen(final GlProgAntwortEvent e) {
		getSite().getShell().getDisplay().asyncExec(new Runnable() {

			public void run() {
				GanglinieMQ g;
				GlSkalierung skal;

				g = e.getGanglinien().iterator().next();
				txtAntwort.setText(g.toString());
				ganglinienListe.setInput(g.getStuetzstellen());

				skal = new GlSkalierung();
				skal.setMinZeit(g.getPrognoseIntervall().getStart());
				skal.setMaxZeit(g.getPrognoseIntervall().getEnd());
				kos.setSkalierung(skal);
				ganglinie.setGanglinie(g);
			}

		});
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		GridData gd;

		parent.setLayout(new GridLayout());

		gd = new GridData(GridData.FILL, GridData.FILL, true, false);
		erzeugeAnfrage(parent).setLayoutData(gd);

		gd = new GridData(GridData.FILL, GridData.FILL, true, true);
		erzeugeAntwort(parent).setLayoutData(gd);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// nix
	}

	/**
	 * Erzeugt die Gruppe für Anfrage.
	 * 
	 * @param elter
	 *            das übergeordnete Widget.
	 * @return die erzeugte Gruppe.
	 */
	private Group erzeugeAnfrage(Composite elter) {
		Group grp;
		Label lbl;
		final ComboViewer cmbMQ;
		final DateTime datumVon;
		final DateTime zeitVon;
		final DateTime datumBis;
		final DateTime zeitBis;
		final Button chkNurLangfristig;
		Button startAnfrage;
		GridData gd;

		grp = new Group(elter, SWT.NONE);
		grp.setLayout(new GridLayout(4, false));
		grp.setText("Prognoseanfrage");

		// Erste Zeile
		lbl = new Label(grp, SWT.NONE);
		lbl.setText("Messquerschnitt");
		gd = new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1);
		cmbMQ = new ComboViewer(grp, SWT.BORDER);
		cmbMQ.getControl().setLayoutData(gd);
		cmbMQ.getCombo().setVisibleItemCount(25);
		cmbMQ.setContentProvider(new ArrayContentProvider());
		cmbMQ.setInput(ModellTools.sortiere(ObjektFactory.getInstanz()
				.bestimmeModellobjekte("typ.messQuerschnittAllgemein")));
		new Label(grp, SWT.NONE);

		// Zweite Zeile
		lbl = new Label(grp, SWT.NONE);
		lbl.setText("Zeitraum von");
		datumVon = new DateTime(grp, SWT.DATE | SWT.MEDIUM);
		zeitVon = new DateTime(grp, SWT.TIME | SWT.LONG);
		zeitVon.setSeconds(0);
		new Label(grp, SWT.NONE);

		// Dritte Zeile
		lbl = new Label(grp, SWT.NONE);
		lbl.setText("Zeitraum bis");
		datumBis = new DateTime(grp, SWT.DATE | SWT.MEDIUM);
		// TODO Wenn Tag der letzte des Monats ist, dann geht es nicht!
		// In dem Fall wird er Monat nicht hochgezählt.
		datumBis.setDay(datumBis.getDay() + 1);
		zeitBis = new DateTime(grp, SWT.TIME | SWT.LONG);
		zeitBis.setHours(0);
		zeitBis.setMinutes(0);
		zeitBis.setSeconds(0);
		new Label(grp, SWT.NONE);

		// Vierte Zeile
		new Label(grp, SWT.NONE);
		gd = new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1);
		chkNurLangfristig = new Button(grp, SWT.CHECK);
		chkNurLangfristig.setLayoutData(gd);
		chkNurLangfristig.setText("Nur langfristige Auswahl");
		chkNurLangfristig.setSelection(true);
		gd = new GridData(GridData.END, GridData.FILL, false, false);
		startAnfrage = new Button(grp, SWT.PUSH);
		startAnfrage.setLayoutData(gd);
		startAnfrage.setText("Anfrage ausführen");
		startAnfrage.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Ganglinienprognose glprog;
				GlProgAnfrage anfrage;
				Interval intervall;
				Calendar von, bis;

				von = Calendar.getInstance();
				von.set(Calendar.YEAR, datumVon.getYear());
				von.set(Calendar.MONTH, datumVon.getMonth());
				von.set(Calendar.DAY_OF_MONTH, datumVon.getDay());
				von.set(Calendar.HOUR_OF_DAY, zeitVon.getHours());
				von.set(Calendar.MINUTE, zeitVon.getMinutes());
				von.set(Calendar.SECOND, zeitVon.getSeconds());
				von.set(Calendar.MILLISECOND, 0);

				bis = Calendar.getInstance();
				bis.set(Calendar.YEAR, datumBis.getYear());
				bis.set(Calendar.MONTH, datumBis.getMonth());
				bis.set(Calendar.DAY_OF_MONTH, datumBis.getDay());
				bis.set(Calendar.HOUR_OF_DAY, zeitBis.getHours());
				bis.set(Calendar.MINUTE, zeitBis.getMinutes());
				bis.set(Calendar.SECOND, zeitBis.getSeconds());
				bis.set(Calendar.MILLISECOND, 0);

				intervall = new Interval(von.getTimeInMillis(), bis
						.getTimeInMillis());
				glprog = Ganglinienprognose.getInstanz();
				glprog.addAntwortListener(GanglinienprognoseView.this);
				anfrage = new GlProgAnfrage(
						(MessQuerschnittAllgemein) ((IStructuredSelection) cmbMQ
								.getSelection()).getFirstElement(), intervall,
						chkNurLangfristig.getSelection());
				try {
					glprog.sendeAnfrage("Test", Collections.singleton(anfrage));
				} catch (DatensendeException ex) {
					Policy.getLog().log(
							new Status(IStatus.ERROR, GanglinienprognosePlugin
									.getId(),
									"Kann die Anfrage nicht an die Ganglinienprognose senden. "
											+ ex.getLocalizedMessage(), ex));
				}
			}

		});

		return grp;
	}

	/**
	 * Erzeugt die Gruppe für Antwort.
	 * 
	 * @param elter
	 *            das übergeordnete Widget.
	 * @return die erzeugte Gruppe.
	 */
	private Group erzeugeAntwort(Composite elter) {
		Group grpAntwort;
		GridData gd;
		TableColumn col;
		TabFolder tab;
		TabItem tabItem;
		FigureCanvas fc;
		ScalableLayeredPane pane;

		grpAntwort = new Group(elter, SWT.NONE);
		grpAntwort.setLayout(new GridLayout());
		grpAntwort.setText("Prognoseanfrage");

		gd = new GridData(GridData.FILL, GridData.FILL, true, false);
		txtAntwort = new Text(grpAntwort, SWT.BORDER);
		txtAntwort.setLayoutData(gd);
		txtAntwort.setEditable(false);

		gd = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1);
		tab = new TabFolder(grpAntwort, SWT.NONE);
		tab.setLayoutData(gd);

		ganglinienListe = new TableViewer(tab);
		ganglinienListe.setContentProvider(new ArrayContentProvider());
		ganglinienListe.setLabelProvider(new GanglinieLabelProvider());
		ganglinienListe.getTable().setHeaderVisible(true);
		ganglinienListe.getTable().setLinesVisible(true);
		tabItem = new TabItem(tab, SWT.None);
		tabItem.setText("Tabelle");
		tabItem.setControl(ganglinienListe.getControl());

		fc = new FigureCanvas(tab);
		ganglinie = new GlGanglinieMQ(getViewSite().getShell().getDisplay());
		kos = new GlKoordinatensystem();
		kos.add(ganglinie);
		pane = new ScalableLayeredPane();
		pane.add(kos);
		fc.setContents(pane);
		tabItem = new TabItem(tab, SWT.None);
		tabItem.setText("Diagramm");
		tabItem.setControl(fc);

		col = new TableColumn(ganglinienListe.getTable(), SWT.LEFT);
		col.setText("Zeitstempel");
		col.setWidth(125);

		col = new TableColumn(ganglinienListe.getTable(), SWT.RIGHT);
		col.setText("QKfz");
		col.setWidth(100);

		col = new TableColumn(ganglinienListe.getTable(), SWT.RIGHT);
		col.setText("QPkw");
		col.setWidth(100);

		col = new TableColumn(ganglinienListe.getTable(), SWT.RIGHT);
		col.setText("QLkw");
		col.setWidth(100);

		col = new TableColumn(ganglinienListe.getTable(), SWT.RIGHT);
		col.setText("VKfz");
		col.setWidth(100);

		col = new TableColumn(ganglinienListe.getTable(), SWT.RIGHT);
		col.setText("VPkw");
		col.setWidth(100);

		col = new TableColumn(ganglinienListe.getTable(), SWT.RIGHT);
		col.setText("VLkw");
		col.setWidth(100);

		col = new TableColumn(ganglinienListe.getTable(), SWT.RIGHT);
		col.setText("QB");
		col.setWidth(100);

		return grpAntwort;
	}
}
