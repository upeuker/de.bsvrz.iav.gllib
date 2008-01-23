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

package de.bsvrz.iav.gllib.eclipse.views.provider;

import java.text.DateFormat;
import java.util.Calendar;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;

/**
 * Generiert die Zelleninhalte für {@link Stuetzstelle} vom Typ
 * {@link Messwerte}.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class GanglinieLabelProvider extends BaseLabelProvider implements
		ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
	 *      int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
	 *      int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof Stuetzstelle<?>) {
			Stuetzstelle<Messwerte> s;

			s = (Stuetzstelle<Messwerte>) element;
			switch (columnIndex) {
			case 0:
				Calendar kalender;
				DateFormat format;

				format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
						DateFormat.MEDIUM);
				kalender = Calendar.getInstance();
				kalender.setTimeInMillis(s.getZeitstempel());
				return format.format(kalender.getTime());
			case 1:
				return String.valueOf(s.getWert().getQKfz());
			case 2:
				return String.valueOf(s.getWert().getQPkw());
			case 3:
				return String.valueOf(s.getWert().getQLkw());
			case 4:
				return String.valueOf(s.getWert().getVKfz());
			case 5:
				return String.valueOf(s.getWert().getVPkw());
			case 6:
				return String.valueOf(s.getWert().getVLkw());
			case 7:
				return String.valueOf(s.getWert().getQB());
			default:
				return null;
			}

		}
		return null;
	}

}
