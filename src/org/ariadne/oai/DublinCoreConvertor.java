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

//package org.ariadne.oai;
//
//import java.util.Date;
//import java.util.Vector;
//
//import org.ariadne.util.OaiUtils;
//import org.ieee.ltsc.datatype.impl.VocabularyImpl.SourceImpl;
//import org.ieee.ltsc.datatype.impl.VocabularyImpl.ValueImpl;
//import org.ieee.ltsc.lom.LOM;
//import org.ieee.ltsc.lom.impl.LOMImpl;
//import org.ieee.ltsc.lom.impl.LOMImpl.Relation.Resource;
//import org.ieee.ltsc.lom.impl.LOMImpl.Technical.Location;
//import org.ietf.mimedir.MimeDir;
//import org.ietf.mimedir.impl.MimeDirImpl;
//import org.ietf.mimedir.vcard.VCard;
//import org.ietf.mimedir.vcard.impl.VCardImpl;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//public class DublinCoreConvertor {
//
//	private static Vector dcFields;
//	
//	static {
//		try {			
//			dcFields = new Vector();
//			dcFields.add("dc:identifier");
//			dcFields.add("dc:title");
//			dcFields.add("dc:creator");
//			dcFields.add("dc:subject");
//			dcFields.add("dc:description");
//			dcFields.add("dc:contributor");
//			dcFields.add("dc:publisher");
//			dcFields.add("dc:date");
//			dcFields.add("dc:format");
//			dcFields.add("dc:source");
//			dcFields.add("dc:language");
//			dcFields.add("dc:relation");
//			dcFields.add("dc:rights");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * This method converts DC-data to a LOMImpl-object 
//	 * @param n : The node that houses the DC-data
//	 * @param oai_id
//	 * @return LOMImpl : the created object that contains a LOM instance
//	 */
//	public static LOMImpl createLOMfromDC(Node n, String oai_id){
//
//		//todo: better processing the DC, in case of getting multiple values
//
//		LOMImpl lom = new LOMImpl();
//		LOMImpl.General.Identifier id = lom.newGeneral().newIdentifier(-1);
//		LOMImpl.MetaMetadata.Identifier meta_id = lom.newMetaMetadata().newIdentifier(-1);
//		id.newEntry().setString(oai_id);
//		meta_id.newEntry().setString(oai_id);
//
//		NodeList nl = n.getChildNodes();
//		for (int i = 0; i < nl.getLength(); i++) {
//			Node innernode = nl.item(i);
//			if (innernode.hasChildNodes()) {
//				NodeList innernl = innernode.getChildNodes();
//				for (int j = 0; j < innernl.getLength(); j++) {
//					switch (dcFields.indexOf(innernode.getNodeName())) {
//					case 0: //identifier
//						String identif = innernl.item(j).getNodeValue();
//						if(identif.startsWith("http://")){
//							Location loc = lom.newTechnical().newLocation(-1);
//							loc.setString(identif);
//						}
//						break;
//					case 1: //title
//						org.ieee.ltsc.datatype.impl.LangStringImpl.StringImpl lsi_title = lom.newGeneral().newTitle().newString(-1);
//						lsi_title.setString(innernl.item(j).getNodeValue());
//						lsi_title.newLanguage().setValue("en"); //todo: obtain language automatically
//						break;
//					case 2: //creator
//						Vector authorsVCards = getAuthors(innernl.item(j).getNodeValue());
//						for (int k = 0; k < authorsVCards.size(); k++) {
//							lom.newLifeCycle().newContribute(k).newRole().newSource().setString(LOM.LOM_V1P0_VOCABULARY);
//							lom.newLifeCycle().newContribute(k).newRole().newValue().setString(LOM.LifeCycle.Contribute.Role.AUTHOR);
//							lom.newLifeCycle().newContribute(k).newEntity(-1).setVCard((VCard) authorsVCards.elementAt(k));
//						}
//						break;
//					case 3: //subject
//						//todo: dc:subject -> 9.2.2.2. Classification.Taxon Path.Taxon.Entry
//						//System.out.println(innernl.item(j).getNodeValue());
//						break;
//					case 4: //description
//						org.ieee.ltsc.datatype.impl.LangStringImpl.StringImpl lsi_desc = lom.newGeneral().newDescription(-1).newString(-1);
//						lsi_desc.setString(innernl.item(j).getNodeValue());
//						lsi_desc.newLanguage().setValue("en"); //todo: obtain language automatically
//						break;
//					case 5: //contributor
//						//todo: contributor -> 2.3.2 Lifecycle.Contribute.Entity with 2.3.1 LifeCycle.Contribute.Role "contributor" ? or unknown ?
//						//System.out.println(innernl.item(j).getNodeValue());
//						break;
//					case 6: //publisher
//						//todo: dc:publisher -> 2.3.2 Lifecycle.Contribute.Entity when 2.3.1 Role has a value of "Publisher" vCard of Publisher address is included when available
//						//System.out.println(innernl.item(j).getNodeValue());
//						break;
//					case 7: //date
//						//todo: dc:date
//						String dateString = innernl.item(j).getNodeValue();
//						org.ieee.ltsc.lom.impl.LOMImpl.LifeCycle.Contribute contribute = lom.newLifeCycle().newContribute(-1);
//						org.ieee.ltsc.lom.impl.LOMImpl.ContributeElement.Date meta_date = lom.newMetaMetadata().newContribute(-1).newDate();
//						org.ieee.ltsc.lom.impl.LOMImpl.ContributeElement.Date d = contribute.newDate();
//						Date date = OaiUtils.parseStringToDate(dateString);
//						d.setDateTime(date);
//						meta_date.setDateTime(date);
//						break;
//					case 8: //format
//						lom.newTechnical().newFormat(-1).setString(innernl.item(j).getNodeValue());
//						break;
//					case 9: //source
//						LOMImpl.Relation relation = lom.newRelation(-1);
//						LOMImpl.Relation.Kind kind = relation.newKind();
//						SourceImpl source = kind.newSource();
//						source.setString(LOM.LOM_V1P0_VOCABULARY);
//						ValueImpl value = kind.newValue();
//						value.setString(org.ieee.ltsc.lom.impl.LOMImpl.Relation.Kind.IS_BASED_ON);
//						Resource resource = relation.newResource();
//						Resource.Identifier.Entry entry = (Resource.Identifier.Entry)resource.newIdentifier(-1).newEntry();
//						entry.setString(innernl.item(j).getNodeValue());
//						//todo: dc:source -> 7.2.1.2 Relation.Resource.Identifier.Entry
//						break;
//					case 10: //language
//						//org.ieee.ltsc.lom.impl.LOMImpl.General.Language lang = lom.newGeneral().newLanguage(-1);
//						//lang.                        	
//						//todo: dc:language -> 1.3 General.Language
//						break;
//					case 11: //relation
//						//todo: dc:relation -> 7.1 Relation.Kind
//						break;
//					case 12: //rights
//						//todo: dc:rights -> 6.3 RightsDescription
//						break;
//					default:
//						break;
//					}
//				}
//			}
//		}
//		return lom;
//	}
//	
//
//	private static Vector getAuthors(String authors) {
//		String[] authorsArray = authors.split(",");
//		Vector authorsVCard = new Vector();
//		for (int i = 0; i < authorsArray.length; i++) {
//			String[] names = authorsArray[i].split(" ");
//			if (names.length == 2) { //todo: support authors witn more than name and surname
//				VCard vcard = getVCard(names[0], names[1]);
//				authorsVCard.add(vcard);
//			}
//		}
//		return authorsVCard;
//	}
//
//	private static VCard getVCard(String FirstName, String LastName) {
//		Vector clines = new Vector();
//		if (FirstName != null || LastName != null) {
//			MimeDir.TextValueType[] tvt;
//			int i;
//			if (FirstName != null && LastName != null) {
//				i = 2;
//			} else {
//
//				i = 1;
//			}
//			tvt = new MimeDir.TextValueType[i];
//			String FN = "";
//			if (FirstName != null) {
//				tvt[--i] = new MimeDirImpl.TextValueType(new String[]{FirstName});
//				FN += FirstName;
//			}
//			if (LastName != null) {
//				tvt[--i] = new MimeDirImpl.TextValueType(new String[]{LastName});
//				if (FN.length() != 0) FN += " ";
//				FN += LastName;
//			}
//			clines.add(new MimeDirImpl.ContentLine(null, "FN", null, new MimeDirImpl.TextValueType(new String[]{FN})));
//			clines.add(new MimeDirImpl.ContentLine(null, "N", null, new VCardImpl.StructuredValueType(tvt)));
//		}
//		clines.add(new MimeDirImpl.ContentLine(null, "VERSION", null, new MimeDirImpl.TextValueType(new String[]{"3.0"})));
//		MimeDir.ContentLine[] r = new MimeDirImpl.ContentLine[clines.size()];
//		for (int i = 0; i < clines.size(); i++) {
//			MimeDirImpl.ContentLine contentLine = (MimeDirImpl.ContentLine) clines.elementAt(i);
//			r[i] = contentLine;
//		}
//		return new VCardImpl(r);
//	}
//}
