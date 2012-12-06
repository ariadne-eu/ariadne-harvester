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

package org.ariadne.oai.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.log4j.Logger;
import org.ariadne.config.PropertiesManager;
import org.ariadne.oai.OAIHarvester;
import org.ariadne.oai.Record;
import org.ariadne.util.JDomUtils;
import org.ariadne.util.OaiUtils;
import org.ietf.mimedir.MimeDir;
import org.ietf.mimedir.impl.MimeDirImpl;
import org.ietf.mimedir.util.MimeDirUtil;
import org.ietf.mimedir.vcard.impl.VCardImpl;
import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.w3c.util.DateParser;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRepository;

public class HarvesterUtils {

	// public static String getMetadataPrefix(OAIRepository oaiRepository)
	// throws OAIException{
	// OAIMetadataFormatList formats;
	// Vector allprefixes = new Vector();

	// formats = oaiRepository.listMetadataFormats();
	// while(formats.moreItems()){
	// allprefixes.add(formats.getCurrentItem().getMetadataPrefix());
	// formats.moveNext();
	// }
	// String priority = PropertiesManager.getInstance().getProperty("Harvest.Priority");
	// StringTokenizer st = new StringTokenizer(priority, ";");
	// while (st.hasMoreTokens()) {
	// String prefix = st.nextToken();
	// if(allprefixes.contains(prefix)){
	// return prefix;
	// }
	// }
	// return "";
	// }

	public static XPath mmIdOaiCatalog;
	public static XPath gIdOaiCatalog;

	static{
		try {
			mmIdOaiCatalog = XPath.newInstance("//lom:lom/lom:metaMetadata/lom:identifier/lom:catalog/text()=\"oai\"");
			mmIdOaiCatalog.addNamespace(OaiUtils.LOMLOMNS);

			gIdOaiCatalog = XPath.newInstance("//lom:lom/lom:general/lom:identifier/lom:catalog/text()=\"oai\"");
			gIdOaiCatalog.addNamespace(OaiUtils.LOMLOMNS);
		} catch (JDOMException e) {
			//NOOP
		}
	}

	public static void addGlobalMetadataIdentifier(Record record, String reposIdentifier) throws IllegalStateException, JDOMException {

		String ident = "oai:" + reposIdentifier + ":";

		String loIdent = "";

		//		try {
		Element metametadata = JDomUtils.getXpathNode("//lom:lom/lom:metaMetadata", OaiUtils.LOMLOMNS, record.getMetadata());
		if (metametadata != null) {
			Element mmIdentifier = metametadata.getChild("identifier", OaiUtils.LOMNS);
			if (mmIdentifier != null) {
				if(!(Boolean)mmIdOaiCatalog.selectSingleNode(record.getMetadata())) {
					loIdent = mmIdentifier.getChildText("entry", mmIdentifier.getNamespace());

					ident = ident.concat(loIdent);

					Element newIdentifier = new Element("identifier", OaiUtils.LOMNS);
					metametadata.addContent(0, newIdentifier);

					Element catalog = new Element("catalog", OaiUtils.LOMNS);
					catalog.setText("oai");
					newIdentifier.addContent(catalog);

					Element entry = new Element("entry", OaiUtils.LOMNS);
					entry.setText(ident);
					newIdentifier.addContent(entry);
				}
			} else {
				//				throw new IllegalStateException("The LO has no metaMetadata.identifier set");
			}
		} else {
			//			throw new IllegalStateException("The LO has no metaMetadata.identifier set");
		}

	}

	public static void addGlobalLOIdentifier(Record record, String reposIdentifier) throws IllegalStateException, JDOMException {

		String ident = "oai:" + reposIdentifier + ":";

		String loIdent = "";

		//		try {
		Element general = JDomUtils.getXpathNode("//lom:lom/lom:general", OaiUtils.LOMLOMNS, record.getMetadata());
		if (general != null) {
			Element generalIdentifier = general.getChild("identifier", OaiUtils.LOMNS);

			if (generalIdentifier != null) {
				if(!(Boolean)gIdOaiCatalog.selectSingleNode(record.getMetadata())) {
					loIdent = generalIdentifier.getChildText("entry", generalIdentifier.getNamespace());
					ident = ident.concat(loIdent);

					Element newIdentifier = new Element("identifier", OaiUtils.LOMNS);
					general.addContent(0, newIdentifier);

					Element catalog = new Element("catalog", OaiUtils.LOMNS);
					catalog.setText("oai");
					newIdentifier.addContent(catalog);

					Element entry = new Element("entry", OaiUtils.LOMNS);
					entry.setText(ident);
					newIdentifier.addContent(entry);
				}
			} else {
				//				throw new IllegalStateException("The LO has no general.identifier set");
			}
		} else {
			//			throw new IllegalStateException("The LO has no general.identifier set");
		}
		//		} catch (JDOMException e) {
		//			harvestlogger.error("An error has occured while adding the global LO identifier : " + e.getMessage());
		//		}
	}

	public static void addReposContributor(Record record, String name) throws JDOMException {

		final MimeDir.ContentLine fn = new MimeDirImpl.ContentLine(null, "FN", null, new MimeDirImpl.TextValueType(new String[] { name }));
		final MimeDir.ContentLine n = new MimeDirImpl.ContentLine(null, "N", null, new MimeDirImpl.TextValueType(new String[] { name }));
		final MimeDir.ContentLine org = new MimeDirImpl.ContentLine(null, "ORG", null, new MimeDirImpl.TextValueType(new String[] { name }));
		final MimeDir.ContentLine version = new MimeDirImpl.ContentLine(null, "VERSION", null, new MimeDirImpl.TextValueType(new String[] { "3.0" }));

		Element metaMetadata = null;
		//		try {
		metaMetadata = JDomUtils.getXpathNode("//lom:lom/lom:metaMetadata", OaiUtils.LOMLOMNS, record.getMetadata());
		if(metaMetadata != null) {
			// contribute
			Element contribute = new Element("contribute", OaiUtils.LOMNS);
			metaMetadata.addContent(contribute);
			// contribute.entity
			Element entity = new Element("entity", OaiUtils.LOMNS);
			VCardImpl vcard = new VCardImpl(new MimeDir.ContentLine[] { fn, n, org, version });
			CDATA cdata = new CDATA(MimeDirUtil.toString(vcard));
			entity.addContent(cdata);
			contribute.addContent(entity);
			// contribute.role
			Element role = new Element("role", OaiUtils.LOMNS);
			contribute.addContent(role);
			Element value = new Element("value", OaiUtils.LOMNS);
			String valueString = PropertiesManager.getInstance().getProperty("Harvest.metadataProvider.value");
			value.setText(valueString);
			role.addContent(value);
			Element source = new Element("source", OaiUtils.LOMNS);
			String sourceString = PropertiesManager.getInstance().getProperty("Harvest.metadataProvider.source");
			source.setText(sourceString);
			role.addContent(source);
			// contribute.date
			Element date = new Element("date", OaiUtils.LOMNS);
			contribute.addContent(date);
			Element dateTime = new Element("dateTime", OaiUtils.LOMNS);
			Calendar dateCalendar = Calendar.getInstance();
			dateCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
			// String time = Calendar.getInstance().getTime().toString();
			dateTime.setText(DateParser.getIsoDate(dateCalendar));
			date.addContent(dateTime);
		}
	}

	//	public static String getReposIdentName(String reposName) {
	//		return reposName.replaceAll(" ", "_").replaceAll(":", "_c_").replaceAll("\n", "").replaceAll("\\(", "_opar_").replaceAll("\\)", "_clpar_");
	//	}
	//
	//	public static String getReposName(String reposIdentName) {
	//		return reposIdentName.replaceAll("_c_", ":").replaceAll("_opar_", "\\(").replaceAll("_clpar_", "\\)").replaceAll("_", " ");
	//	}

	//	public static Vector<ReposProperties> getRegistryReposProperties(){
	//		Vector<ReposProperties> list = getReposProperties();
	//		Vector<ReposProperties> repositories = new Vector<ReposProperties>();
	//		for (ReposProperties repo : list) {
	//			if(repo.getRegistryTarget().equalsIgnoreCase("true")) {
	//				repositories.add(repo);
	//			}
	//		}
	//		return repositories;
	//	}


	public static Vector<String> getReposList() {
		String targets = PropertiesManager.getInstance().getProperty("AllTargets.list");
		StringTokenizer st = new StringTokenizer(targets, ";");
		Vector<String> repositories = new Vector<String>();
		while (st.hasMoreTokens()) {
			repositories.add(st.nextToken());
		}
		return repositories;
	}

	public static Vector<ReposProperties> getReposProperties() {
		Vector<String> list = HarvesterUtils.getReposList();
		Vector<ReposProperties> repositories = new Vector<ReposProperties>();
		for (int i = 0; i < list.size(); i++) {
			String ident = list.elementAt(i);
			ReposProperties repos = getReposProperties(ident);
			repositories.add(repos);
		}
		return repositories;
	}

	public static void saveDetails(String startString, HashMap<String, String> props) {
		Iterator<String> iter = props.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			PropertiesManager.getInstance().saveProperty(startString + "." + key, props.get(key));
		}
	}

	public static void saveDetails(String startString, ReposProperties reposProps) {
		Iterator<String> iter = reposProps.getProperties().keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			PropertiesManager.getInstance().saveProperty(startString + "." + key, reposProps.getProperties().get(key));
		}
	}

	public static ReposProperties getReposProperties(String reposIdInternal) {
		HashMap<String, String> repos = getPropertiesStartingWith(reposIdInternal);
		return checkReposProperties(reposIdInternal,repos);
	}

	public static HarvestSessionProperties getSessionProperties() {
		HarvestSessionProperties sessionProps = new HarvestSessionProperties();
		sessionProps.setValidationUri(PropertiesManager.getInstance().getProperty("Harvest.validation.scheme"));
		sessionProps.setValidate(new Boolean(PropertiesManager.getInstance().getProperty("Harvest.validation")).booleanValue());
		sessionProps.setRepositoryLogs(new Boolean(PropertiesManager.getInstance().getProperty("log.repositoryLogs")).booleanValue());
		return sessionProps;
	}

	public static HashMap<String, String> getPropertiesStartingWith(String startString) {
		HashMap<String, String> props = new HashMap<String, String>();
		Hashtable table = PropertiesManager.getInstance().getPropertyStartingWith(startString + ".");
		Collection propsCollection = table.keySet();
		Iterator propIter = propsCollection.iterator();
		while (propIter.hasNext()) {
			String key = (String) propIter.next();
			String newKey = key.replaceFirst(startString + ".", "");
			props.put(newKey, (String) table.get(key));
		}
		return props;
	}

	protected static ReposProperties checkReposProperties(String reposIdInternal, HashMap<String, String> props) {

		ReposProperties repoProps = new ReposProperties();

		Iterator propIter = ReposProperties.getDefaultProperties().keySet().iterator();
		while (propIter.hasNext()) {
			String key = (String) propIter.next();
			if (!props.containsKey(key)) {
				String defaultVal = ReposProperties.getDefaultProperties().get(key);
				props.put(key, defaultVal);
				PropertiesManager.getInstance().saveProperty(reposIdInternal + "." + key, defaultVal);
			}
		}
		repoProps.setRepositoryIdentifierInteral(reposIdInternal);
		repoProps.setProperties(props);

		return repoProps;
	}

	public static Vector<OAIRepository> getRepositories() throws OAIException {
		Vector list = getReposList();
		Vector<OAIRepository> repositories = new Vector<OAIRepository>();
		for (int i = 0; i < list.size(); i++) {
			try {
				OAIRepository repository = new OAIRepository();
				repository.setBaseURL(PropertiesManager.getInstance().getProperty(list.elementAt(i) + ".baseURL"));
				repositories.add(repository);
			} catch (OAIException e) {
				// NOOP
			}
		}
		return repositories;
	}

	public static void resetHarvestingDate(String repos) {
		if (repos.equals("ALL")) {
			Vector<String> list = HarvesterUtils.getReposList();
			Iterator<String> iter = list.iterator();
			while (iter.hasNext()) {
				String repoString = (String) iter.next();
				resetHarvestingDate(repoString);
			}
		} else {

			OAIRepository repository = new OAIRepository();
			try {
				repository.setBaseURL(PropertiesManager.getInstance().getProperty(repos + ".baseURL"));
				String date = repository.getEarliestDatestamp();
				PropertiesManager.getInstance().saveProperty(repos + ".latestHarvestedDatestamp", date);
			} catch (OAIException e) {
				// NOOP
			}

		}
	}

	public static void setHarvestingRepositories(String list) {
		StringTokenizer tokens = new StringTokenizer(list, ";");
		while (tokens.hasMoreTokens()) {
			String repo = tokens.nextToken();
			String harvest = tokens.nextToken();
			PropertiesManager.getInstance().saveProperty(repo + ".active", harvest);
		}
	}

	public static void removeRepository(String repository) {
		PropertiesManager.getInstance().removeKeyFromPropertiesFile(repository + ".");

		Vector list = HarvesterUtils.getReposList();
		String newRepositories = "";
		for (int i = 0; i < list.size(); i++) {

			String repos = (String) list.elementAt(i);
			if (!repos.equals(repository)) {
				if (!newRepositories.equals("")) {
					newRepositories = newRepositories + ";";
				}
				newRepositories = newRepositories + repos;
			}
		}

		PropertiesManager.getInstance().saveProperty("AllTargets.list", newRepositories);
	}

	public static ReposProperties getRegistryTarget(String registryIdCatalog, String registryIdEntry ) {
		for (ReposProperties repo : getReposProperties()) {
			if(repo.getRegistryIdentifierCatalog().equals(registryIdCatalog) && repo.getRegistryIdentifierEntry().equals(registryIdEntry)) return repo;
		};
		return null;
	}

	public static ReposProperties addRepository(ReposProperties repoProps) throws Exception {

		OAIRepository repository = new OAIRepository();	
		String fallBackReposId = repoProps.getBaseURL().replaceFirst("http.?://", "").split("/", 2)[0].split(":", 2)[0];
		String internalId = fallBackReposId.replaceAll("\\.","_");

		Hashtable ids = PropertiesManager.getInstance().getPropertyStartingWith(internalId);
		TreeSet<String> allIds = new TreeSet<String>();
		for(Object o:ids.keySet()){
			allIds.add(((String)o).split("\\.")[0]);		
		}
		int i = 1;
		while(allIds.contains(internalId)){
			internalId = internalId.split("_u_")[0] + "_u_" + i++;
		}

		String urlString = repoProps.getBaseURL() + "?verb=Identify";
		try{
			URL u = new URL(urlString);
			HttpURLConnection http = (HttpURLConnection) u.openConnection();
			if(http.getResponseCode() == 200) {
				repository.setBaseURL(repoProps.getBaseURL());
				String url = repository.getBaseURL();
			} else {
				throw new IOException(Integer.toString(http.getResponseCode()));
			}
		}
		catch ( IOException ex ) {
			throw new Exception("The given Url isn't a valid OAI Repository (Could not connect to Url \""+urlString+"\". ErrorMsg was : "+ ex.getMessage() +") ");
		}
		catch(OAIException e){
			throw new Exception("The given Url \""+urlString+"\" isn't a valid OAI Repository (" + e.getMessage() + ").");
		}
		catch(IllegalStateException e){
			try {
				throw new Exception("The baseUrl of the OAI Repository is not correct (found url in Identify verb : " + repository.getBaseURL() + ")");
			} catch (OAIException e1) {
				throw new Exception("The baseUrl of the OAI Repository is not correct (Error : " + e1.getMessage() + ")");
			}
		}
		catch(Exception e) {
			throw new Exception("The following error occured : ("+e.getClass().getName()+") " + e.getMessage() + ".");
		}

		return addTarget(repoProps, repository, fallBackReposId, internalId);
	}

	private static ReposProperties addTarget(ReposProperties repoProps, OAIRepository repository, String fallBackReposId, String internalId) {
		String targets = PropertiesManager.getInstance().getProperty("AllTargets.list");
		StringTokenizer st = new StringTokenizer(targets, ";");
		boolean targetExists = false;
		while (st.hasMoreTokens()) {

			if(st.nextToken().equals(internalId)){
				targetExists = true;
				break;
			}
		}
		if(!targetExists){
			if(!targets.equals(""))targets += ";";
			targets += internalId;
			PropertiesManager.getInstance().saveProperty("AllTargets.list",targets);
		}
		ReposProperties reposPropsReturn = new ReposProperties();

		String repositoryIdent = null;
		try {
			repositoryIdent = repository.getRepositoryIdentifier().trim();
		} catch (OAIException e1) {
			// NOOP
		}
		if(repoProps.getRepositoryIdentifier() != null && !repoProps.getRepositoryIdentifier().trim().equals("")){
			repositoryIdent = repoProps.getRepositoryIdentifier().trim();
		}
		if (repositoryIdent == null || repositoryIdent.equals("")){
			repositoryIdent = fallBackReposId;
			Logger.getLogger(OAIHarvester.class.getName()).warn("Repository " + repoProps.getBaseURL() + "has not specified a repositoryIdentifier. Using fallback option " + fallBackReposId);
		}
		reposPropsReturn.setRepositoryIdentifier(repositoryIdent.trim());

		String repositoryName = null;
		try {
			repositoryName = repository.getRepositoryName().trim().replaceAll("\n"," ");
		} catch (OAIException e) {
			//NOOP
		}
		if(repoProps.getRepositoryName() != null && !repoProps.getRepositoryName().trim().equals("")){
			repositoryName = repoProps.getRepositoryName().trim().replaceAll("\n"," ");
		}
		if (repositoryName == null || repositoryName.equals("")){
			repositoryName = repositoryIdent;
			Logger.getLogger(OAIHarvester.class.getName()).warn("Repository " + repoProps.getBaseURL() + "has not specified a repositoryName. Using fallback option " + repositoryIdent);
		}
		reposPropsReturn.setRepositoryName(repositoryName.trim());

		reposPropsReturn.setBaseURL(repoProps.getBaseURL());
		reposPropsReturn.setProviderName(repoProps.getProviderName());
		try {
			reposPropsReturn.setLatestHarvestedDatestamp(repository.getEarliestDatestamp());
		} catch (OAIException e) {
			//NOOP
		}
		reposPropsReturn.setActive("Yes");
		reposPropsReturn.setMetadataPrefix(repoProps.getMetadataPrefix());
		reposPropsReturn.setMetadataFormat(repoProps.getMetadataFormat());
		reposPropsReturn.setHarvestingSet(repoProps.getHarvestingSet());
		reposPropsReturn.setAutoReset(repoProps.getAutoReset());
		reposPropsReturn.setValidationUri(repoProps.getValidationUri());
		reposPropsReturn.setTransformationID(repoProps.getTransformationID());
		reposPropsReturn.setRegistryIdentifierCatalog(repoProps.getRegistryIdentifierCatalog());
		reposPropsReturn.setRegistryIdentifierEntry(repoProps.getRegistryIdentifierEntry());
		try {
			reposPropsReturn.setGranularity(repository.getGranularity());
		} catch (OAIException e) {
			//NOOP
		}
		HarvesterUtils.saveDetails(internalId,reposPropsReturn);
		return reposPropsReturn;
	}

	public static void removeAllTargets() {
		Vector list = HarvesterUtils.getReposList();
		for (int i = 0; i < list.size(); i++) {

			String repos = (String) list.elementAt(i);
			removeRepository(repos);
		}
	}
}
