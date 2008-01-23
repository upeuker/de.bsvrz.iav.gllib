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
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.eclipse;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.bsvrz.iav.gllib.gllib.dav.Ganglinienprognose;
import de.bsvrz.iav.gllib.gllib.modell.GanglinienobjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.visukern.Datenverteiler;

/**
 * Plugin, welches die Ganglinienbibliothek verwendet um Anfrage zu stellen und
 * die Antwort anzuzeigen.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class GanglinienprognosePlugin extends AbstractUIPlugin {

	/** Die Eigenschaft {@code plugin}. */
	private static GanglinienprognosePlugin plugin;

	/**
	 * Gibt die Instanz des Plugin zur&uuml;ck, nachdem es initialisiert wurde.
	 * 
	 * @return das Plugin.
	 */
	public static GanglinienprognosePlugin getDefault() {
		return plugin;
	}

	/**
	 * Gibt die Plugin-Id zur&umml;ck, wie sie im Plugin-Manifest definiert ist.
	 * 
	 * @return die Id dieses Plugin.
	 */
	public static String getId() {
		return getDefault().getBundle().getSymbolicName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		ObjektFactory factory;

		factory = ObjektFactory.getInstanz();
		factory.setVerbindung(Datenverteiler.getInstanz().getVerbindung());
		factory.registerStandardFactories();
		factory.registerFactory(new GanglinienobjektFactory());
		Ganglinienprognose.getInstanz();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

}
