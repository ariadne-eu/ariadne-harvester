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

package org.ariadne.spi;

import java.rmi.RemoteException;
import java.util.Vector;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ariadne.config.PropertiesManager;
import org.ariadne.oai.Record;
import org.ariadne.oai.harvestWriter.GenericWriter;
import org.ariadne.util.JDomUtils;
import org.ariadne.util.OaiUtils;
import org.jdom.Element;
import org.jdom.JDOMException;

import be.cenorm.www.CreateSession;
import be.cenorm.www.CreateSessionResponse;
import be.cenorm.www.DestroySession;
import be.cenorm.www.SpiTargetStub;
import be.cenorm.www.SqiSessionManagementStub;
import be.cenorm.www._SQIFaultException;

public class CenSpiTarget {

	private SpiTargetStub spiStub = null;
	private Logger harvestLogger = Logger
	.getLogger("org.ariadne.oai.DoOaiHarvest");
	private String sessionId = "";
	private SqiSessionManagementStub sqiSessionStub = null;

	public CenSpiTarget(String url) throws AxisFault {
		spiStub = new SpiTargetStub(url);
		int secs = Integer.parseInt(PropertiesManager.getInstance()
				.getProperty("harvestToCenSoapSpi.timeout"));
		spiStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(
				secs * 1000);

		sqiSessionStub = new SqiSessionManagementStub(
				PropertiesManager.getInstance().getProperty("harvestToCenSoapSpi.Session.URI"));
	}

	public void openNewSession() {
		try {
			CreateSession createSession = new CreateSession();
			createSession.setUserID(PropertiesManager.getInstance()
					.getProperty("harvestToCenSoapSpi.Session.username"));
			createSession.setPassword(PropertiesManager.getInstance()
					.getProperty("harvestToCenSoapSpi.Session.password"));
			CreateSessionResponse sessionResponse = sqiSessionStub
			.createSession(createSession);
			sessionId = sessionResponse.getCreateSessionReturn();
		} catch (AxisFault e) {
			harvestLogger.error(e.getMessage());
		} catch (RemoteException e) {
			harvestLogger.error(e.getMessage());
		} catch (_SQIFaultException e) {
			harvestLogger.error(e.getFaultMessage().getMessage());
		}
	}

	public void submitMetadata(Vector<Record> lomVector,
			String repositoryIdentifier, String set) {
		try {
			String loginfo = "Pushing " + lomVector.size()
			+ " objects to SpiTarget...";
			harvestLogger.info(loginfo);
			for (int i = 0; i < lomVector.size(); i++) {
				Record rec = lomVector.elementAt(i);
				//SubmitMetadataRecord rec = new SubmitMetadataRecord();

				String identifier = "";

				identifier = GenericWriter.getTheIdentifier(rec, repositoryIdentifier, true);
				if (identifier.equals("")) {
					harvestLogger.error(identifier
							+ "has no oai identifier");
					identifier = repositoryIdentifier + ".local."
					+ identifier;
				}
				//rec.setGlobalIdentifier(identifier);
				//rec.setMetadata(OaiUtils.parseLom2Xmlstring(element));
				//rec.setTargetSessionID(sessionId);
				spiStub.submitMetadataRecordWithId(sessionId, JDomUtils.parseXml2string(rec.getMetadata(), null), identifier);

			}
			harvestLogger.info("Done");
		} catch (RemoteException e) {
			harvestLogger.error(e.getMessage());
		} catch (be.cenorm.www.SpiFaultException e) {
			harvestLogger.error("SPI Error: "+e.getMessage());
			//e.printStackTrace();
		}
	}

	public void closeSession() {
		if (!sessionId.equals("")) {
			DestroySession destroySession = new DestroySession();
			destroySession.setSessionID(sessionId);
			try {
				sqiSessionStub.destroySession(destroySession);
			} catch (RemoteException e) {
				harvestLogger.error(e.getMessage());
			} catch (_SQIFaultException e) {
				harvestLogger.error(e.getFaultMessage().getMessage());
			} finally {
				sessionId = "";
			}
		}
	}

}
