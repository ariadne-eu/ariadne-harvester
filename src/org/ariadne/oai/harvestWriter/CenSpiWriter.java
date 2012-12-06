/*******************************************************************************
 * Copyright (c) 2008 Ariadne Foundation.
 * 
 * This file is part of Ariadne Harvester.
 * 
 * Ariadne Harvester is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Ariadne Harvester is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Ariadne Harvester.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.ariadne.oai.harvestWriter;

import java.util.Vector;

import org.apache.axis2.AxisFault;
import org.ariadne.oai.Record;
import org.ariadne.spi.CenSpiTarget;

public class CenSpiWriter extends GenericWriter {

	public void CreateTarget(String spiTargetUrl) {
		CenSpiTarget target = null;
		try {
			target = new CenSpiTarget(spiTargetUrl);
		} catch (AxisFault e) {
			e.printStackTrace();
		}
		this.target = target;
	}

	@Override
	public void pushAway(Vector<Record> lomVector, String repositoryIdentifier,
			String set) {
		((CenSpiTarget) target).submitMetadata(lomVector, repositoryIdentifier,
				set);
	}

	public void connect() {
		if (target != null) {
			((CenSpiTarget) target).openNewSession();
		}
	}

	public void disconnect() {
		if (target != null) {
			((CenSpiTarget) target).closeSession();
		}
	}

	@Override
	public void delete(Vector<Record> lomVector, String repositoryIdentifier, String set) {
		// TODO Auto-generated method stub
		
	}
}
