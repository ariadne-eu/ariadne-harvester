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

//package org.ariadne.oai.harvestWriter;
//
//import java.util.Vector;
//
//import javax.xml.bind.JAXBException;
//
//import org.apache.log4j.Logger;
//import org.ariadne.config.PropertiesManager;
//import org.ariadne.util.OaiUtils;
//import org.ariadne_eu.metadata.insert.InsertMetadataXQuerySqlDbImpl;
//import org.ariadne_eu.utils.ConfigReader;
//
//public class IBMDB2Writer extends GenericWriter {
//
//	InsertMetadataXQuerySqlDbImpl db;
//	
//	private static Logger harvestlogger = Logger.getLogger("org.ariadne.oai.DoOaiHarvest");
//	
//	private int count = 0;
//	
//	public void CreateTarget(String location) {
//		ConfigReader.readConfigFile(PropertiesManager.getInstance().getProperty("CHANGEME"));
//		db = new InsertMetadataXQuerySqlDbImpl(0);
//	}
//
//	public void pushAway(Vector lomVector, String repositoryIdentifier) {
//		harvestlogger.info("Writing " + lomVector.size() + " objects to disk...");
//		for (int i = 0; i < lomVector.size(); i++) {
//			LOMImpl lom = (LOMImpl)lomVector.elementAt(i);
//			try {
//				String lomString = OaiUtils.parseLom2Xmlstring(lom);
//				
//				String identifier = "";
//				int j = 0;
//				try {
//					while(identifier.equals("")) {
//						Identifier ident = lom.metaMetadata().identifier(j++);
//						if(ident.getCatalog() != null && ident.getCatalog().equals("oai")) {
//							identifier = ident.getEntry();
//						}
//					}
//				} catch (RuntimeException e) {
//					harvestlogger.error(identifier + "has no oai identifier");
//					identifier = repositoryIdentifier + ".local." + lom.metaMetadata().identifier(0).getEntry();
//				}
//				count++;
//				db.insertMetadata(identifier + count + (int)(Math.random()*1000.0), lomString);
//			} catch (JAXBException ex) {
//				harvestlogger.info("Document is not valid because " + ex.getMessage());
//			}
//		}
//		harvestlogger.info("Done");
//
//	}
//
//}
