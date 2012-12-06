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

//package org.ariadne.oai.testSuite;
//
//import java.util.StringTokenizer;
//import java.util.Vector;
//
//import org.ariadne.config.PropertiesManager;
//import org.ariadne.oai.OaiTargetUtils;
//import org.ariadne.testSuite.Test;
//
//import uiuc.oai.OAIException;
//import uiuc.oai.OAIRepository;
//
///**
// * <p> Company: K.U.Leuven </p>
// * @author Bram Vandeputte
// * @version 1.0
// */
//public class TestOaiTargets extends Test{
//	
//	private String errors;
//
//	public String getType() {
//		return "availability OAI Repositories";
//	}
//
//	public void runTest() {
//		setDescription("Checking wether all OAI Repositories are available");
//		errors = "";
//		Vector repositories = getRepositories();
//		for (int i = 0; i < repositories.size(); i++) {
//			OAIRepository repo = (OAIRepository)repositories.elementAt(i);
//			testXml(repo);
//		}
//		if(!errors.equals("")){
//			setError(errors);
//		}
//	}
//
//	private void testXml(OAIRepository repo) {
//		String reposname = null;
//		try {
//			reposname = repo.getRepositoryName();
//		} catch (OAIException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		String prefix = OaiTargetUtils.getMetadataPrefix(repo);
//		if(prefix.equals("")){
//			addError("No valid MetadataPrefix found for repository " + reposname);
//			return;
//		}
//		try {
//			String earliestDatestamp = repo.getEarliestDatestamp();
//			earliestDatestamp = earliestDatestamp.split("T")[0];
//			repo.listRecords(prefix,OaiTargetUtils.calcUntil(),earliestDatestamp);
//		} catch (OAIException e) {
//			addError("Resultlist of repository " + reposname + " couldn't be Xml-validated");
//		}
//	}
//
//	private void addError(String error){
//		errors += error.concat(" ; ") ;
//	}
//	
//	private Vector getRepositories(){
//		String targets = PropertiesManager.getInstance().getProperty("LocalTargets");
//		StringTokenizer st = new StringTokenizer(targets, ";");
//		Vector repositories = new Vector();
//		while (st.hasMoreTokens()) {
//			String baseUrl = PropertiesManager.getInstance().getProperty(st.nextToken() + ".baseURL");
//			try {
//				OAIRepository repository = new OAIRepository();
//				repository.setBaseURL(baseUrl);
//				repositories.add(repository);
//			} catch (OAIException e) {
//				addError("The url " +baseUrl+" is not a valid base url for an OAI repository");
//				setSolution("Check wether the url(" + baseUrl + ") is correct, and if the repository is reachable");
//			}
//		}
//		return repositories;
//	}
//
//
//
////	public static void main(String[] args) {
////	TestWebServices ti = new TestWebServices();
////	System.out.println(ti.toString());
////	}
//
//}
//
