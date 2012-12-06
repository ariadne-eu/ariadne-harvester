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

package org.ariadne.oai.testSuite;

import java.rmi.RemoteException;
import java.util.StringTokenizer;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ariadne.config.PropertiesManager;
import org.ariadne.testSuite.Test;
import org.ariadne_eu.spi.CreateIdentifier;
import org.ariadne_eu.spi.SPIStub;
import org.ariadne_eu.spi.SpiFaultException;

import be.cenorm.www.CreateSession;
import be.cenorm.www.CreateSessionResponse;
import be.cenorm.www.DestroySession;
import be.cenorm.www.SqiSessionManagementStub;
import be.cenorm.www._SQIFaultException;

public class TestSpiTarget extends Test {

	private Logger harvestlogger = Logger.getLogger("org.ariadne.oai.DoOaiHarvest");

	public String getType() {
		return "availability of SPI Target";
	}

	public TestSpiTarget() {
		super(false);
		String stores = PropertiesManager.getInstance().getProperty("Harvest.storeTo");
		boolean spiFound = false;
		StringTokenizer st = new StringTokenizer(stores, ";");
		while (!spiFound && st.hasMoreTokens()) {
			if (st.nextToken().equals("harvestToSpi")) {
				doTest();
				spiFound = true;
			}
		}
	}

	public void runTest() {
		harvestlogger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		harvestlogger.info("Testing SPI Target");
		String targetUrl = PropertiesManager.getInstance().getProperty("harvestToSpi.URI");
		String sessionUrl = PropertiesManager.getInstance().getProperty("harvestToSpi.Session.URI");
		String username = PropertiesManager.getInstance().getProperty("harvestToSpi.Session.username");
		String passwd = PropertiesManager.getInstance().getProperty("harvestToSpi.Session.password");
		setDescription("Testing if the SPI target is reachable and ready to accept objects");
		try {
			// URL u = new URL(targetUrl);
			// getPageContent(u);
			// SpiTarget target = new SpiTarget(targetUrl);
			SqiSessionManagementStub sqiSessionStub = new SqiSessionManagementStub(sessionUrl);
			CreateSession createSession = new CreateSession();
			createSession.setUserID(username);
			createSession.setPassword(passwd);
			CreateSessionResponse sessionResponse = sqiSessionStub.createSession(createSession);
			String sessionId = sessionResponse.getCreateSessionReturn();
			SPIStub spiStub = new SPIStub(targetUrl);
			CreateIdentifier createId = new CreateIdentifier();
			createId.setCatalog("a_catalog");
			createId.setTargetSessionID(sessionId);
			spiStub.createIdentifier(createId);
			DestroySession destroysession = new DestroySession();
			destroysession.setSessionID(sessionId);
			sqiSessionStub.destroySession(destroysession);
		} catch (AxisFault e) {
			setError("SPI target not available, an AxisFault has occured(" + e.getMessage() + ").");
			setSolution("Check if the session and target URL's are correct.");
		} catch (_SQIFaultException e) {
			setError("SQI session management has produced an error (" + e.getFaultMessage().getMessage() + "("
					+ e.getFaultMessage().getSqiFaultCode() + ")).");
			setSolution("Check if the username and password of the SPI target are correct.");
		} catch (RemoteException e) {
			setError("SPI target not available, an RemoteException has occured(" + e.getMessage() + ").");
		} catch (SpiFaultException e) {
			setError("SPI target not available, an SpiFaultException has occured(" + e.getFaultMessage().getMessage() + ").");
		} catch (Exception e) {
			setError("SPI target not available, an uknown exception has occured(" + e.getClass().getName() + " : "  + e.getMessage() + ").");
		}
	}

	// /**
	// * Fetch the HTML content of the page as simple text.
	// */
	// String getPageContent(URL url) throws IOException{
	// StringBuffer result = new StringBuffer();
	// BufferedReader reader = null;
	// try {
	// reader = new BufferedReader( new InputStreamReader(url.openStream()) );
	// String line = null;
	// while ( (line = reader.readLine()) != null) {
	// result.append(line);
	// //result.append(fNEWLINE);
	// }
	// }
	// // catch ( IOException ex ) {
	// // System.err.println("Cannot retrieve contents of: " + url);
	// // }
	// finally {
	// try {
	// if (reader != null) reader.close();
	// }
	// catch (IOException ex){
	// System.err.println("Cannot close reader: " + reader);
	// }
	// }
	// return result.toString();
	// }
}
