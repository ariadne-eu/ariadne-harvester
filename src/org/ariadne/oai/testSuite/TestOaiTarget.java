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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.ariadne.oai.OAIHarvester;
import org.ariadne.oai.utils.HarvesterUtils;
import org.ariadne.oai.utils.ReposProperties;
import org.ariadne.util.JDomUtils;
import org.ariadne.util.OaiUtils;
import org.ariadne.testSuite.Test;
import org.ariadne.testSuite.TestSuiteRegister;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Text;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;

/**
 * <p>
 * Company: K.U.Leuven
 * </p>
 * 
 * @author Bram Vandeputte
 * @version 1.0
 */
public class TestOaiTarget extends Test {

	protected OAIRepository repository;
	protected String name;
	protected Logger harvestlogger = Logger.getLogger("org.ariadne.oai.DoOaiHarvest");
	protected String ListRecordsQuery;
	protected String set;
	protected ReposProperties reposProps;

	public String getType() {
		return "availability of OAI Repository";
	}

	public TestOaiTarget(ReposProperties reposProps, Vector<String> sets) {

		super(false);
		this.reposProps = reposProps;
		this.set = sets.firstElement();
		doTest();
	}

	public void runTest() {
		harvestlogger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		testBaseUrl();
		//		testRepositoryName();
		//		testRepositoryIdentifier();
		testRepositoryGranularity();
		testFromDateStamps();
		String prefix = testMetadataPrefix();
		testXml(prefix);
		testResumption();
	}

	private void testRepositoryGranularity() {
		if (hasError())
			return;
		harvestlogger.info("Checking the granularity provided by " + name + ".");
		String gran = "";
		try {
			gran = repository.getGranularity();
		} catch (OAIException e) {
			harvestlogger.error(e.getMessage());
			setError(e.getMessage());
			setSolutionWithLink("Check the granularity of the repository in the ", reposProps.getBaseURL() + "?verb=Identify", "Identify", " response.");
			return;
		}
		if (!gran.equals(reposProps.getGranularity())) {
			String msg = "Stored granularity doesn't match granularity of repository.";
			harvestlogger.error(msg);
			setError(msg);
			setSolutionWithLink("Check the granularity of the repository in the ", reposProps.getBaseURL() + "?verb=Identify", "Identify", " response.");
		} else if (!gran.equals("YYYY-MM-DDThh:mm:ssZ") && !gran.equals("YYYY-MM-DD")) {
			String msg = "Repository granularity doesn't have a valid pattern : " + gran;
			harvestlogger.error(msg);
			setError(msg);
			setSolution("Change granularity to \"YYYY-MM-DDThh:mm:ssZ\" or \"YYYY-MM-DD\".");
		}
	}

	private void testFromDateStamps() {
		if (hasError())
			return;
		harvestlogger.info("Checking the earliestDateStamp and the latestHarvestedDatestamp of " + name + ".");
		String gran = "";
		try {
			gran = repository.getGranularity();
		} catch (OAIException e) {
			harvestlogger.error(e.getMessage());
			setError(e.getMessage());
			setSolutionWithLink("Check the granularity of the repository in the ", reposProps.getBaseURL() + "?verb=Identify", "Identify", " response.");
			return;
		}
		String earliest = "";
		try {
			earliest = repository.getEarliestDatestamp();
		} catch (OAIException e) {
			harvestlogger.error(e.getMessage());
			setError(e.getMessage());
			setSolutionWithLink("Check the EarliestDatestamp of the repository in the ", reposProps.getBaseURL() + "?verb=Identify", "Identify", " response.");
			return;
		}
		String latest = reposProps.getLatestHarvestedDatestamp();
		Pattern p = null;
		if (gran.equals("YYYY-MM-DDThh:mm:ssZ")) {
			p = Pattern
			.compile("([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]|[0-9][1-9][0-9]{2}|[1-9][0-9]{3})(\\-(0[1-9]|1[0-2])(\\-(0[1-9]|[1-2][0-9]|3[0-1])(T([0-1][0-9]|2[0-3])(:[0-5][0-9](:[0-5][0-9](Z))))))");
		} else {
			p = Pattern
			.compile("([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]|[0-9][1-9][0-9]{2}|[1-9][0-9]{3})(\\-(0[1-9]|1[0-2])(\\-(0[1-9]|[1-2][0-9]|3[0-1])))");
		}
		Matcher m = p.matcher(earliest);
		if (!m.matches()) {
			String msg = "EarliestDatestamp (" + earliest + ") doesn't match granularity of repository(" + gran + ").";
			harvestlogger.error(msg);
			setError(msg);
			setSolutionWithLink("Check the EarliestDatestamp of the repository in the ", reposProps.getBaseURL() + "?verb=Identify", "Identify", " response.");
		} else {
			m = p.matcher(latest);
			if (!m.matches()) {
				String msg = "latestHarvestedDateStamp (" + latest + ") doesn't match granularity of repository(" + gran + ").";
				harvestlogger.error(msg);
				setError(msg);
				setSolution("Check the latestHarvestedDateStamp of the repository");
			}
		}
	}

	private void testResumption() {
		if (hasError())
			return;
		harvestlogger.info("Testing the resumption-support of " + name + ".");
		String token = null;
		SAXBuilder docBuilder = new SAXBuilder();
		try {
			URL url = new URL(ListRecordsQuery);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			Document xml = docBuilder.build(http.getInputStream());
			Element xpathNode = JDomUtils.getXpathNode("//oai:resumptionToken", OaiUtils.OAIOAINS, xml.getRootElement());

			if (xpathNode != null) {
				token = xpathNode.getText();
				token = URLEncoder.encode(token, "UTF-8");
			}
			// }
			// catch (TransformerException e) {//XpathApi
			// harvestlogger.error("Wrong XPath expression in resumptionTest ("+e.getMessage()+")");
			// setError("Wrong XPath expression in resumptionTest ("+e.getMessage()+")");
			// setSolution("Check harvester code !");
		} catch (JDOMException e) {// parse
			harvestlogger.error(e.getMessage());
			setError(e.getMessage());
			setSolution("");
		} catch (IOException e) {
			harvestlogger.error(e.getMessage());
			setError(e.getMessage());
			setSolution("");
		} catch (NullPointerException e) {
			harvestlogger.error(e.getMessage());
			setError(e.getMessage());
			setSolution("");
		}
		if (token != null) {
			String resumptionQuery = reposProps.getBaseURL() + "?verb=ListRecords&resumptionToken=" + token;
			try {
				URL url = new URL(resumptionQuery);
				HttpURLConnection http = (HttpURLConnection) url.openConnection();
				Document xml = docBuilder.build(http.getInputStream());
				Element error = JDomUtils.getXpathNode("//oai:error", OaiUtils.OAIOAINS, xml.getRootElement());
				if (error != null) {
					// String errorName =
					// (String)item.getFirstChild().getAttributes().getNamedItem("code").getNodeValue();
					String errorValue = error.getText();
					String errorCode = error.getAttributeValue("code");
					setError("Resumptiontoken does not work, oai exception is : " + errorValue + " (code : " + errorCode + ")");
					harvestlogger.error("Resumptiontoken does not work, oai exception is : " + errorValue + " (code : " + errorCode + ")");
					setSolutionWithLink("Please check if the target has a correct implementation : Check the ", resumptionQuery, "resumptionQuery",
					"");
					return;
				}

			} catch (MalformedURLException e) {
				harvestlogger.error("Something is wrong with the resumptionURL (" + e.getMessage() + ")");
				setError("Something is wrong with the resumptionURL (" + e.getMessage() + ")");
				setSolution("Check harvester code and the url ! (" + resumptionQuery + ")");
			} catch (IOException e) {
				harvestlogger.error(e.getMessage());
				setError(e.getMessage());
				setSolution("");
			} catch (JDOMException e) {
				harvestlogger.error(e.getMessage());
				setError(e.getMessage());
				setSolution("");
				// } catch (TransformerException e) {
				// harvestlogger.error("Wrong XPath expression in resumptionTest ("+e.getMessage()+")");
				// setError("Wrong XPath expression in resumptionTest ("+e.getMessage()+")");
				// setSolution("Check harvester code !");
			}

		} else {
			harvestlogger.info("No ResumptionToken found !");
		}

	}

	//	private void testRepositoryIdentifier() {
	//		if (hasError())
	//			return;
	//		harvestlogger.info("Checking if " + name + " has a repositoryIdentifier set.");
	//		try {
	//			String ident = repository.getRepositoryIdentifier();
	//			if (ident.equals(""))
	//				setError("This repository has no RepositoryIdentifier.");
	//		} catch (OAIException e) {
	//			harvestlogger.error(e.getMessage());
	//			setError(e.getMessage());
	//		}
	//	}

	//	private void testRepositoryName() {
	//		if (hasError())
	//			return;
	//		harvestlogger.info("Testing whether the repository name hasn't changed.");
	//		Vector list = HarvesterUtils.getReposList();
	//		String newName = HarvesterUtils.getReposIdentName(name);
	//		for (int i = 0; i < list.size(); i++) {
	//			String testName = (String) list.elementAt(i);
	//			if (testName.equals(newName))
	//				return;
	//		}
	//		harvestlogger.error("The repository " + name + " has no entry in the ariadneV4.properties file");
	//		setError("The repository " + name + " has no entry in the ariadneV4.properties file");
	//		setSolution("If the name of the repository has changed, look for an entry with the baseUrl \"" + reposProps.getBaseURL() + "\", change the name to "
	//				+ newName + " and reload the application.");
	//	}

	private void testBaseUrl() {
		if (hasError())
			return;
		harvestlogger.info("Testing baseUrl " + reposProps.getBaseURL() + ".");
		try {
			repository = new OAIRepository();
			repository.setBaseURL(reposProps.getBaseURL());
			name = reposProps.getRepositoryName();
		} catch (OAIException e) {
			name = reposProps.getRepositoryName();
			harvestlogger.error("The url " + reposProps.getBaseURL() + " is not a valid base url for an OAI repository");
			setError("The url " + reposProps.getBaseURL() + " is not a valid base url for an OAI repository");
			setSolutionWithLink("Check if the url(", reposProps.getBaseURL(), reposProps.getBaseURL(), ") is correct, and if the repository is reachable");
		} finally {
			setDescription("Checking if repository " + name + " is ready for harvesting");
		}
	}

	private String testMetadataPrefix() {
		if (hasError())
			return "";
		harvestlogger.info("Checking if " + name + " supports a valid metadata format.");
		String prefix = "";
		// try {
		prefix = reposProps.getMetadataPrefix();
		if (prefix.equals("")) {
			harvestlogger.error("No valid MetadataPrefix found for repository " + name);
			setError("No valid MetadataPrefix found for repository " + name);
			setSolution("Check if the repository supports the needed metadataFormat and it is present in the \"HarvestPriority\" property.");
		}
		// } catch (OAIException e) {
		// harvestlogger.error("OAI error : " + e.getMessage());
		// setError("OAI error : " + e.getMessage());
		// setSolution("Check if the repository supports the needed metadataFormat and it is present in the \"HarvestPriority\" property.");
		// }
		return prefix;
	}

	private void setSolutionWithLink(String prefix, String link, String tag, String postfix) {
		org.w3c.dom.Element resultElement = TestSuiteRegister.newElement("solution");
		Text resultText = $description.getOwnerDocument().createTextNode(prefix);
		resultElement.appendChild(resultText);
		resultElement.setAttribute("link", link);
		resultElement.setAttribute("tag", tag);
		resultElement.setAttribute("postfix", postfix);
		// Element resultElement2 = TestSuiteRegister.newElement("link");
		// Text resultText2 =
		// $description.getOwnerDocument().createTextNode(link);
		// resultElement2.appendChild(resultText2);
		// resultElement.appendChild(resultElement2);
		// Element resultElement3 = TestSuiteRegister.newElement("tag");
		// Text resultText3 =
		// $description.getOwnerDocument().createTextNode(tag);
		// resultElement3.appendChild(resultText3);
		// resultElement.appendChild(resultElement3);
		$description.appendChild(resultElement);
	}

	private void testXml(String prefix) {
		if (hasError())
			return;
		harvestlogger.info("Checking the structure of the XML returned by " + name + ".");
		try {
			String earliestDatestamp = "";
			earliestDatestamp = repository.getEarliestDatestamp();
			earliestDatestamp = OaiUtils.calcFrom(earliestDatestamp, reposProps.getGranularity());
			String until = OaiUtils.calcUntil(OaiUtils.getCurrentTime(), reposProps.getGranularity());
			ListRecordsQuery = repository.getBaseURL() + "?verb=ListRecords&from=" + earliestDatestamp + "&until=" + until + "&metadataPrefix="
			+ prefix;
			if (set != null && !set.equals(""))
				ListRecordsQuery += "&set=" + set;
			try {
				OAIRecordList list = repository.listRecords(prefix, until, earliestDatestamp, set);
				if (reposProps.getMetadataFormat().equals(OAIHarvester.OAI_LOM)) {
					OAIRecord currentItem = list.getCurrentItem();
					Document lom = null;
					if (currentItem == null) {
						harvestlogger.error("there is no currentItem in the OAIRecordList");
						setError("there is no currentItem in the OAIRecordList");
						setSolutionWithLink("Please check if the OAI target is available and has metadata to offer.", ListRecordsQuery, "This",
						" is the query that does not return any results.");
						return;
					} else if (!currentItem.deleted()) {
						Element metadata = currentItem.getMetadata();
						String xml = OaiUtils.parseLom2Xmlstring(metadata);
						lom = OaiUtils.parseXmlString2Lom(xml);


						String namespace = lom.getRootElement().getNamespaceURI();
						if (!namespace.equals("http://ltsc.ieee.org/xsd/LOM")) {
							throw new JDOMException("Namespace is not \"http://ltsc.ieee.org/xsd/LOM\", but \"" + namespace + "\" instead.");
						}

						Element mmIdentifier = JDomUtils
						.getXpathNode("/lom:lom/lom:metaMetadata/lom:identifier", OaiUtils.LOMLOMNS, lom.getRootElement());
						if (mmIdentifier == null || mmIdentifier.getChildText("entry", OaiUtils.LOMLOMNS).equals("")) {
							Element rootElement = lom.getRootElement();
							Element xpathNode = JDomUtils.getXpathNode("/lom:lom/lom:general/lom:identifier/lom:entry", OaiUtils.LOMLOMNS, rootElement);
							String ident = "";
							if (xpathNode != null)
								ident = xpathNode.getText();
							throw new JDOMException("no valid lom.metametadata.identifier ! (lom.general.identifier.entry = " + ident + ")");
						}
					}
				}
			} catch (OAIException e) {
				harvestlogger.error("Problems with repository \"" + name + "\" : " + e.getMessage());
				setError("Resultlist of repository " + name + " couldn't be Xml-validated (" + e.getMessage() + ").");
				// setSolution("Run an external Xml-validator on the returned records. Look ");
				setSolutionWithLink("Run an external Xml-validator on the returned records. ", ListRecordsQuery, "This",
				" is the query that is not correctly parsable.");
			} catch (JDOMException e) {
				harvestlogger.error("Xml-parsing error : " + e.getMessage());
				setError("Xml-parsing error : " + e.getMessage());
				setSolution("Resolve the error in the LOM structure");
			} catch (IOException e) {
				harvestlogger.error("Xml-parsing error : " + e.getMessage());
				setError("Xml-parsing error : " + e.getMessage());
				setSolution("Resolve the error in the LOM structure");
			} catch (NullPointerException e) {
				harvestlogger.error("Error retrieving xml from the repository, time-out occured or null was returned.");
				setError("Error retrieving xml from the repository, time-out occured or null was returned.");
				setSolution("Please try again.");
			}
		} catch (OAIException e1) {
			harvestlogger.error(e1.getMessage());
			setError("getEarliestDatestamp() didn't work (" + e1.getMessage() + ").");
			setSolution("Check if the earliest date is set in the Identify verb");
		} catch (ParseException e) {
			harvestlogger.error(e.getMessage());
			setError("Could not calc \"from\" parameter from earliestDateStamp : (" + e.getMessage() + ").");
			setSolution("Check if the earliest date is set correctly in the Identify verb");
		}
	}
}
