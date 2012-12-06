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
import org.ariadne_eu.spidev.SPIDevStub;
import org.ariadne_eu.spidev.SpiDevFaultException;
import org.ariadne_eu.spidev.SubmitMetadataRecord;
import org.jdom.Element;
import org.jdom.JDOMException;

import be.cenorm.www.CreateSession;
import be.cenorm.www.CreateSessionResponse;
import be.cenorm.www.DestroySession;
import be.cenorm.www.SqiSessionManagementStub;
import be.cenorm.www._SQIFaultException;

public class SpiDevTarget {

	private SPIDevStub spiStub = null;
	private Logger harvestLogger = Logger.getLogger("org.ariadne.oai.DoOaiHarvest");
	private String sessionId = "";
	private SqiSessionManagementStub sqiSessionStub = null;

	public SpiDevTarget(String url) throws AxisFault {
		spiStub = new SPIDevStub(url);
		int secs = Integer.parseInt(PropertiesManager.getInstance().getProperty("harvestToSpiDev.timeout"));
		spiStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(secs * 1000);

		sqiSessionStub = new SqiSessionManagementStub(PropertiesManager.getInstance().getProperty("harvestToSpiDev.Session.URI"));
	}

	public void openNewSession() {
		try {
			CreateSession createSession = new CreateSession();
			createSession.setUserID(PropertiesManager.getInstance().getProperty("harvestToSpiDev.Session.username"));
			createSession.setPassword(PropertiesManager.getInstance().getProperty("harvestToSpiDev.Session.password"));
			CreateSessionResponse sessionResponse = sqiSessionStub.createSession(createSession);

			sessionId = sessionResponse.getCreateSessionReturn();

			// CreateIdentifier createId = new CreateIdentifier();
			// createId.setCatalog("a_catalog");
			// createId.setTargetSessionID(sessionId);
			// spiStub.createIdentifier(createId);
		} catch (AxisFault e) {
			harvestLogger.error(e.getMessage());
		} catch (RemoteException e) {
			harvestLogger.error(e.getMessage());
		} catch (_SQIFaultException e) {
			harvestLogger.error(e.getFaultMessage().getMessage());
		}
	}

	public void submitMetadata(Vector<Record> lomVector, String repositoryIdentifier, String set) {
		// if(sessionId.equals("")){
		// return;//openNewSession();
		// }
		String loginfo = "Pushing " + lomVector.size() + " objects to SpiTarget...";
		harvestLogger.info(loginfo);
		for (int i = 0; i < lomVector.size(); i++) {
			String identifier = "";
			try {
				Record record = lomVector.elementAt(i);
				Element element = record.getMetadata();
				SubmitMetadataRecord rec = new SubmitMetadataRecord();

				identifier = GenericWriter.getTheIdentifier(record, repositoryIdentifier, true);

				rec.setGlobalIdentifier(identifier);
				rec.setCollection(repositoryIdentifier);
				rec.setMetadata(JDomUtils.parseXml2string(element,null));
				rec.setTargetSessionID(sessionId);
				spiStub.submitMetadataRecord(rec);
			} catch (RemoteException e) {
				harvestLogger.error(e.getMessage() + "(record identifier : " + identifier + ")");
			} catch (SpiDevFaultException e) {
				harvestLogger.error(e.getFaultMessage().getMessage() + "(record identifier : " + identifier + ")");
			}
		}
		harvestLogger.info("Done");
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
