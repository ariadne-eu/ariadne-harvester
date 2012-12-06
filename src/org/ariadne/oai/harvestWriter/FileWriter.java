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

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.ariadne.config.PropertiesManager;
import org.ariadne.oai.Record;
import org.ariadne.util.IOUtilsv2;
import org.ariadne.util.JDomUtils;
import org.ariadne.util.OaiUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

public class FileWriter extends GenericWriter {

	private String targetDir = "";
	private static Logger harvestlogger = Logger.getLogger("org.ariadne.oai.DoOaiHarvest");

	protected boolean debug = false;

	private int count = 0;

	public void CreateTarget(String location) {
		this.targetDir = location;
	}

	public void pushAway(Vector<Record> xmlVector, String repositoryIdentifier, String set) {
		harvestlogger.info("Writing " + xmlVector.size() + " objects to disk...");

		String dirString = initPush(repositoryIdentifier, set);

		for (int i = 0; i < xmlVector.size(); i++) {
			Record record = xmlVector.elementAt(i);
			Element xml = record.getMetadata();
			String ident = "";

			try {
				ident = GenericWriter.getTheIdentifier(record, repositoryIdentifier, true);
				String identifier = ident.replaceAll(":", "_").replaceAll("/", ".s.");
				String xmlString = JDomUtils.parseXml2string(xml.getDocument(),null);
				// File file = new File(targetDir + File.separator + identifier
				// + "." + count + ".xml"); //NOTE : for testing purposes
				String fileString = dirString + File.separator + identifier + ".xml";
				File file = new File(fileString);

				boolean debug = Boolean.valueOf(PropertiesManager.getInstance().getProperty("harvestToDisk.debug")).booleanValue();

				if (debug && file.exists()) {
					harvestlogger.error("Found DOUBLE : " + identifier);
					identifier = identifier + ".DOUBLE." + count;
					fileString = dirString + File.separator + identifier + ".xml";
				}
				OaiUtils.writeStringToFileInEncodingUTF8(xmlString, fileString);

				count++;
			} catch (IOException e) {
				harvestlogger.error("Document with identifier " + ident + " is not valid because " + e.getMessage());
			}
		}
		harvestlogger.info("Done");
	}

	public void disconnect() {
		// NOOP
	}

	@Override
	public void connect() {
		// NOOP

	}

	@Override
	public void delete(Vector<Record> records, String repositoryIdentifier, String set) {
		harvestlogger.info("Deleting " + records.size() + " objects from disk...");

		String dirString = initPush(repositoryIdentifier, set);

		for (int i = 0; i < records.size(); i++) {
			Record record = records.elementAt(i);
			String ident = "";
			ident = GenericWriter.getTheIdentifier(record, repositoryIdentifier, true);
			String identifier = ident.replaceAll(":", "_").replaceAll("/", ".s.");
			String fileString = dirString + File.separator + identifier + ".xml";
			File file = new File(fileString);

			if (file.exists()) {
				file.delete();
			}
			count++;
		}
		harvestlogger.info("Done");
	}

	private String initPush(String repositoryIdentifier, String set) {

		debug = Boolean.valueOf(PropertiesManager.getInstance().getProperty("harvestToDisk.debug")).booleanValue();

		String saveBySetsString = PropertiesManager.getInstance().getProperty("harvestToDisk.sets");
		boolean saveBySets = false;
		if (saveBySetsString != null) {
			saveBySets = Boolean.valueOf(saveBySetsString).booleanValue();
		}
		saveBySets = saveBySets && set != null && !set.equals("");

		// check target dir:
		String dirString = targetDir + File.separator + repositoryIdentifier.replaceAll(":", "_").replaceAll("/", ".s.");
		File dir = new File(dirString);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}

		if (saveBySets) {
			dirString = dirString + File.separator + set;
			File setDir = new File(dirString);
			if (!setDir.exists() || !setDir.isDirectory()) {
				setDir.mkdir();
			}
		}
		return dirString;
	}
}
