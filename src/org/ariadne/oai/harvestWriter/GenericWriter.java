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

import java.util.Hashtable;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.ariadne.config.PropertiesManager;
import org.ariadne.oai.Record;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

public abstract class GenericWriter {

	protected Object target;

	private static Logger harvestlogger = Logger.getLogger("org.ariadne.oai.DoOaiHarvest");

	public abstract void CreateTarget(String location);

	public abstract void pushAway(Vector<Record> lomVector, String repositoryIdentifier, String set);
	
	public abstract void delete(Vector<Record> lomVector, String repositoryIdentifier, String set);

	public abstract void disconnect();

	public abstract void connect();

	protected static Vector<Namespace> namespaces = null;
	protected static Vector<XPath> xpathQueries = null;

	public static String getTheIdentifier(Record rec, String repositoryIdentifier, boolean makeGlobal) {

		if(xpathQueries == null)getXpathQueries();

		String ident = "";
		try {
			for (XPath xpath : xpathQueries) {
				Element node = (Element) xpath.selectSingleNode(rec.getMetadata());
				if(node != null && !node.getTextTrim().equalsIgnoreCase("")) {
					ident = node.getTextTrim();
					break;
				}
			}
			if (ident.equals("")) {
				harvestlogger.debug(ident + " has no identifier");
				ident = rec.getOaiIdentifier();
				//	if(makeGlobal)ident = repositoryIdentifier + ".local." + ident;
			}
		} catch (Exception e) {
			harvestlogger.error(e);
		}
		return ident;
	}

	protected static void getXpathQueries() {
		getNamespaces();
		xpathQueries = new Vector<XPath>();
		String prefix = "genericwriter.identifier.xpath.query.";
		Hashtable<String,String> props = PropertiesManager.getInstance().getPropertyStartingWith(prefix);

		Vector<Integer> keys = new Vector<Integer>();
		for (String keyString : props.keySet()) {
			if(keyString.split(prefix).length == 2) {
				String indexString = keyString.split(prefix)[1];
				try {
					keys.add(Integer.parseInt(indexString));
				} catch (Exception e) {
					harvestlogger.error(e.getMessage());
				}
			}		
		}
		TreeSet<Integer> sortedKeys = new TreeSet<Integer>(keys);
		for (Integer queryKey : sortedKeys) {
			try {
				String xpathString = props.get(prefix + queryKey);
				XPath xpath = XPath.newInstance(xpathString);
				for (Namespace namespace : namespaces) {
					xpath.addNamespace(namespace);
				}
				xpathQueries.add(xpath);
			} catch (JDOMException e) {
				harvestlogger.error(e.getMessage());
			}
		}

	}

	protected static void getNamespaces() {
		namespaces = new Vector<Namespace>();
		String prefix = "genericwriter.identifier.xpath.ns.";
		Hashtable<String,String> props = PropertiesManager.getInstance().getPropertyStartingWith(prefix);
		for (String keyString : props.keySet()) {
			String nsPrefix = "";
			String nsUri = "";
			if(keyString.endsWith("uri")) {
				nsUri = props.get(keyString);
				nsPrefix = PropertiesManager.getInstance().getProperty(keyString.replace("uri", "prefix"),"");
				namespaces.add(Namespace.getNamespace(nsPrefix, nsUri));
			}
		}
	}
}
