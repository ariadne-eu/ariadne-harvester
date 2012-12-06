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

//package org.ariadne.lucene.lomlucene;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.StringReader;
//import java.util.Calendar;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Vector;
//
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;
//
//import org.apache.log4j.Logger;
//import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.IndexWriter;
//import org.ariadne.oai.utils.OaiUtils;
//import org.jdom.Element;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//
///**
// * Created by IntelliJ IDEA.
// * User: Gonzalo Parra
// * Date: 30/11/2005
// * Time: 12:49:50 AM
// * To change this template use File | Settings | File Templates.
// */
//public class LOMLuceneIndexCreator {
//
//	private String luceneDirectory;
//	
//	public static String LOGGER = "org.ariadne.oai.DoOaiHarvest";
//	
//	private static final String LOM_ = "lom.";
//	private static final String FULLLOM = "fullLOM";
//	private static final String GENERAL_ = "general.";
//	private static final String TITLE = "title";
//	private static final String IDENTIFIER = "identifier";
//	private static final String ENTRY = "entry";
//	private static final String LANGUAGE = "language";
//	private static final String KEYWORDS = "keywords";
//	private static final String DESCRIPTION = "description";
//	private static final String LIFECYCLE_ = "lifecycle.";
//	private static final String CONTRIBUTE_ = "contribute.";
//	private static final String ENTITY = "entity";
//	private static final String DATE = "date";
//	private static final String RIGHTS_ = "rights.";
//	private static final String TECHNICAL_ = "technical.";
//	private static final String EDUCATIONAL_ = "educational.";
//	private static final String SIZE = "size";
//	private static final String VALUE = "value";
//	private static final String INTENDEDENDUSERROLE = "intendedEndUserRole";
//	private static final String LOCATION = "location";
//	private static final String FORMAT = "format";
//	private static final String SEARCHFIELD = "searchField";
//
//	public static final String FIELD_SEPARATOR = " aaaabbbbaaaa ";
//
//	public LOMLuceneIndexCreator(String luceneDirectory) {
//		this.luceneDirectory = luceneDirectory;
//	}
//
//	public String getLuceneDirectory() {
//		return luceneDirectory;
//	}
//
//	public void setLuceneDirectory(String luceneDirectory) {
//		this.luceneDirectory = luceneDirectory;
//	}
//
//	public synchronized void createIndex(Vector LOMVector) throws IOException {
//		// IndexWriter writer = openIndex(getLuceneDirectory(), new SimpleKeepNumbersAnalyzer());
//		IndexWriter writer = openIndex(getLuceneDirectory(), new StandardAnalyzer());
//		Logger.getLogger(LOGGER).info("Start writing to LuceneIndex : ");
//		for (int i = 0; i < LOMVector.size(); i++) {
//			Document doc = createLuceneDocManual((Element) LOMVector.elementAt(i));
//			writer.addDocument(doc);
//		}
//		// writer.optimize();
//		closeIndexWriter(writer);
//		Logger.getLogger(LOGGER).info("Done");
//	}
//
//	private IndexWriter openIndex(String luceneDirectory, Analyzer analyzer) throws IOException {
//		File lucenePath = new File(luceneDirectory);
//		boolean wipe_existing;
//		if ((!lucenePath.exists()) || (lucenePath.list().length == 0)) { // if the directory does not exist, or is empty
//			Logger.getLogger(LOGGER).info("luceneDirectory does not exist yet, so we will wipe... ");
//			wipe_existing = true;
//		} else {
//			Logger.getLogger(LOGGER).info("luceneDirectory exists, so we will NOT wipe... ");
//			wipe_existing = false;
//		}
//		IndexWriter writer = new IndexWriter(lucenePath, analyzer, wipe_existing);
//		writer.setMaxFieldLength(Integer.MAX_VALUE);
//		return writer;
//	}
//
////	public static Document createLuceneDoc(LOM lom) throws Exception {
////		String xmlString = OaiUtils.parseLom2Xmlstring(lom);
////		return parseXmlStringToLuceneDoc(xmlString);
////	}
//	
//    private static Document parseXmlStringToLuceneDoc(String xmlString){
//
//        SAXParserFactory fac = SAXParserFactory.newInstance();
//        LuceneHandler lh = new LuceneHandler();
//
//         try {
//
//             SAXParser test = fac.newSAXParser();
//
//             StringReader strRead = new StringReader(xmlString);
//             InputSource source = new InputSource(strRead);
//             test.parse(source, lh);
//
//         } catch (ParserConfigurationException e) {
//             e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//         } catch (SAXException e) {
//             e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//         } catch (IOException e) {
//                 e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//             }
//
//         Document toAdd = lh.getDocument();
////         Field fulldoc = new Field("fullDoc", xmlString, Field.Store.YES, Field.Index.UN_TOKENIZED);
////         toAdd.add(fulldoc);
////         System.out.println("Fulldoc added");
//         return toAdd;
//     }
//	
//
//	public static Document createLuceneDocManual(Element lom) {
//		Document doc = new Document();
//		Vector searchFields = new Vector();
//		
//		addGeneralIdentifier(lom, doc, searchFields);
//		addGeneralTitle(lom, doc, searchFields);
//		//addGeneralDescription(lom, doc, searchFields);
//		//addGeneralKeyword(lom, doc, searchFields);
//		//addGeneralLanguage(lom, doc);
//		//addLifecycleContributeEntity(lom, doc, searchFields);
//		//addLifecycleContributeDate(lom, doc);
//		//addRightsDescription(lom, doc);
//		//addTechnicalSize(lom, doc);
//		addTechnicalLocation(lom, doc);
//		addTechnicalFormat(lom, doc);
//		addEducationalIntendedEndUserRole(lom, doc);
//		
//		//todo: poner el field content, donde va todos los campos..
//		//doc.add(Field.Text("content", stringDesc.replace('\n', ' ')));
//
//		//addFullLom(lom, doc);
//		//addSearchField(searchFields, doc);
//		addLastModDateLucene(lom,doc);
//		addTechnicalSize(lom,doc);
//		return doc;
//	}
//
//	private static void addSearchField(Vector searchFields, Document doc) {
//		StringBuffer searchField = new StringBuffer();
//		for (Iterator iter = searchFields.iterator(); iter.hasNext();) {
//			String element = (String) iter.next();
//			element.replaceAll(FIELD_SEPARATOR, " ");
//			searchField.append(element);
//			searchField.append(" ");
//		}		
//		doc.add(new Field(SEARCHFIELD, searchField.toString(),Field.Store.YES, Field.Index.TOKENIZED));
//	}
//	
//	private static void addLastModDateLucene(Element lom, Document doc) {
//		String entry;
//		// ... Technical Location
//		try {
//			entry = "";
//			Element element = OaiUtils.getXpathNode("/lom:lom/lom:metaMetadata/lom:contribute/lom:date/lom:dateTime", OaiUtils.LOMLOMNS, lom);
//			
//			if(element != null) {
//				String date = element.getText();
//				entry = date.split("\\.")[0];
//				entry = entry.replaceAll("(?::|T|-)", "");
//				//entry = entry.replaceAll("T", "");
//				//entry = entry.replaceAll("-", "");
//			}
//
//			doc.add(new Field("lastModDate", entry,Field.Store.YES, Field.Index.UN_TOKENIZED));
//		} catch (Exception e) {
//			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
//		}
//	}
//
//	private static void addFullLom(Element lom, Document doc) {
//		// Full LOM XML serialized
//		try {
//			
//			String result = OaiUtils.parseLom2Xmlstring(lom);
//
//				//String replacement = "<lom xmlns=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd\">";
//				//result = result.replaceFirst("<lom>", replacement);
//				result = result.replaceFirst("\\<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?\\>" + System.getProperty("line.separator"), "");
//				//result = result.replaceAll("\\r\\n", "");
//				//result = result.replaceAll("\\n", "");
////				replacement = "</oai_lom:lom>";
////				result = result.replaceFirst("</lom>", replacement);
//
//			// Remove ascii control characters, because they are forbidden in XML
//			// so we need to remove the ASCII control characters from the generated String xml
//			// REMARK: in SamgiUtil, we provide a method removeAsciiControlCharacters.
//			// We should make this method globally available, by:
//			// 1. putting the method eg in AriadneUtils
//			// 2. or by making SamgiUtil seperately usable and available
////			char[] ch = new char[]{(char)0x0,(char)0x1,(char)0x2,(char)0x3,(char)0x4,(char)0x5,
////					(char)0x6,(char)0x7,(char)0x8,/*(char)0x9,(char)0xa,*/(char)0xb,(char)0xc,/*(char)0xd,*/
////					(char)0xe,(char)0xf,(char)0x10,(char)0x11,(char)0x12,(char)0x13,(char)0x14,(char)0x15,
////					(char)0x16,(char)0x17,(char)0x18,(char)0x19,(char)0x1a,(char)0x1b,(char)0x1c,(char)0x1d,
////					(char)0x1f};
////			for (int j = 0; j < ch.length; j++) {
////				char c = ch[j];
////				result = result.replaceAll(String.valueOf(c), "");
////			}
//			doc.add(new Field(lomfield_FullLom(), result,Field.Store.YES, Field.Index.UN_TOKENIZED));
//		} catch (Exception e) {
//			Logger.getLogger(LOGGER).error("ERROR : The fulllom couldn't be stored in the index");
//			doc.add(new Field(lomfield_FullLom(), lom.toString(),Field.Store.YES, Field.Index.UN_TOKENIZED));
//			
//		}
//	}
//
//	private static void addTechnicalFormat(Element lom, Document doc) {
//		String entry;
//		// ... Technical Format
//		try {
//			entry = "";
//			List<Element> elements = OaiUtils.getXpathList("/lom:lom/lom:technical/lom:format", OaiUtils.LOMLOMNS, lom);
//			for(Element element : elements) {
//					if (entry != "") {
//						entry += FIELD_SEPARATOR;
//					}
//					entry += element.getText();
//			}
//			doc.add(new Field(lomfield_TechnicalFormat(), entry,Field.Store.YES, Field.Index.TOKENIZED));
//		} catch (Exception e) {
//			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
//		}
//	}
//
//	private static void addTechnicalSize(Element lom, Document doc) {
//		String entry;
//		// ... Technical Size
//		try {
//			entry = "";
//			List<Element> elements = OaiUtils.getXpathList("/lom:lom/lom:technical/lom:size", OaiUtils.LOMLOMNS, lom);
//			for(Element element : elements) {
//					if (entry != "") {
//						entry += FIELD_SEPARATOR;
//					}
//					entry += element.getText();
//			}
//			doc.add(new Field(lomfield_TechnicalSize(), entry,Field.Store.YES, Field.Index.TOKENIZED));
//		} catch (Exception e) {
//			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
//		}
//	}
//	
//	private static void addTechnicalLocation(Element lom, Document doc) {
//		String entry;
//		// ... Technical Location
//		try {
//			entry = "";
//			List<Element> elements = OaiUtils.getXpathList("/lom:lom/lom:technical/lom:location", OaiUtils.LOMLOMNS, lom);
//			for(Element element : elements) {
//					if (entry != "") {
//						entry += FIELD_SEPARATOR;
//					}
//					entry += element.getText();
//			}
//			doc.add(new Field(lomfield_TechnicalLocation(), entry,Field.Store.YES, Field.Index.UN_TOKENIZED));
//		} catch (Exception e) {
//			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
//		}
//	}
//
//	private static void addEducationalIntendedEndUserRole(Element lom, Document doc) {
//		String entry;
//		// ... Educational IntendedEndUserRole
//		try {
//			entry = "";
//			List<Element> elements = OaiUtils.getXpathList("/lom:lom/lom:educational/lom:intendedEndUserRole/lom:value", OaiUtils.LOMLOMNS, lom);
//			for(Element element : elements) {
//					if (entry != "") {
//						entry += FIELD_SEPARATOR;
//					}
//					entry += element.getText();
//			}
//			doc.add(new Field(lomfield_EducationalIntendedEndUserRole(), entry,Field.Store.YES, Field.Index.UN_TOKENIZED));
//		} catch (Exception e) {
//			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
//		}
//	}
//	
////	private static void addRightsDescription(LOM lom, Document doc) {
////		int i;
////		String entry;
////		// ... Rights Description
////		try {
////			entry = "";
////			i = 0;
////			Description rights = LOMUtil.getRightsDescription(lom);
////			if(rights != null){
////				while (rights.string(i) != null) {
////					try {
////						if (entry != "") {
////							entry += FIELD_SEPARATOR;
////						}
////						entry += LOMUtil.getRightsDescription(lom).string(i).string();
////					} catch (Exception e) {
////					} finally {
////						i++;
////					}
////				}
////			}
////
////			doc.add(new Field(lomfield_RightsDescription(), entry,Field.Store.YES, Field.Index.TOKENIZED));
////		} catch (Exception e) {
////			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
////		}
////	}
////
//	private static String getLuceneSearchDateString(Calendar calendar){
//
//		String date = Integer.toString(calendar.get(Calendar.YEAR));
//		String nbr = Integer.toString(calendar.get(Calendar.MONTH) + 1);
//		if(nbr.length() < 2){
//			nbr = "0" + nbr;
//		}
//		date = date.concat(nbr);
//		nbr = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
//		if(nbr.length() < 2){
//			nbr = "0" + nbr;
//		}
//		date = date.concat(nbr);
//		nbr = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
//		if(nbr.length() < 2){
//			nbr = "0" + nbr;
//		}
//		date = date.concat(nbr);
//		nbr = Integer.toString(calendar.get(Calendar.MINUTE));
//		if(nbr.length() < 2){
//			nbr = "0" + nbr;
//		}
//		date = date.concat(nbr);
//		nbr = Integer.toString(calendar.get(Calendar.SECOND));
//		if(nbr.length() < 2){
//			nbr = "0" + nbr;
//		}
//		date = date.concat(nbr);
//		return date;
//	}
////	
////	private static void addLifecycleContributeDate(LOM lom, Document doc) {
////		int i;
////		String entry;
////		String fullDate = "";
////		// ... Lifecycle contribute Date
////		try {
////			entry = "";
////			i = 0;
////			while (LOMUtil.getLifeCycleContribute(lom, i) != null) {
////				try {
////					LOM.LifeCycle.Contribute.Date lifecycleContributeDate = LOMUtil.getLifeCycleContribute(lom, i).date();
////					if (lifecycleContributeDate != null) {
////						if (entry != "") {
////							entry += FIELD_SEPARATOR;
////						}
////						if (fullDate != "") {
////							fullDate += FIELD_SEPARATOR;
////						}
////						Calendar calendar = Calendar.getInstance();
////						entry += getLuceneSearchDateString(calendar);
////						calendar = Calendar.getInstance();
////						calendar.setTime(lifecycleContributeDate.dateTime()); //NOTE : put this after getLuceneSearchDateString to set harvested time in the "luceneDate" field  
////						fullDate += createResponseDate(calendar.getTime());
////
////						
////					}
////				} catch (Exception e) {
////					//e.printStackTrace();
////					//System.out.println("test");
////					
////				} finally {
////					i++;
////				}
////			}
////			doc.add(new Field(lomfield_LifecycleContributeDate(), fullDate,Field.Store.YES, Field.Index.TOKENIZED));
////			doc.add(new Field("luceneDate", entry,Field.Store.YES, Field.Index.TOKENIZED));
////		} catch (Exception e) {
////			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
////		}
////	}
////
////    /**
////     * Create an OAI response date from the specified date
////     *
////     * @param date the date to be transformed to an OAI response date
////     * @return a String representation of the OAI response Date.
////     */
////    public static String createResponseDate(Date date) {
////        StringBuffer sb = new StringBuffer();
////        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
////	TimeZone tz = TimeZone.getTimeZone("UTC");
////	formatter.setTimeZone(tz);
////        sb.append(formatter.format(date));
////        return sb.toString();
////    }
////	
////	private static void addLifecycleContributeEntity(LOM lom, Document doc, Vector searchFields) {
////		int i;
////		String entry;
////		// ... Lifecycle contribute
////		//LOMUtil.getLifeCycleContribute(lom,1).entity(0).vcard().getContentLine(0).getValueType().toString();
////		try {
////			entry = "";
////			i = 0;
////			while (LOMUtil.getLifeCycleContribute(lom, i) != null) {
////				try {
////					LOM.LifeCycle.Contribute.Entity lifecycleContributeEntity = LOMUtil.getLifeCycleContribute(lom, i).entity(0);
////					if (lifecycleContributeEntity != null) {
////						if (entry != "") {
////							entry += FIELD_SEPARATOR;
////						}
////						entry += lifecycleContributeEntity.string();
////					}
////				} catch (Exception e) {
////				} finally {
////					i++;
////				}
////			}
////			doc.add(new Field(lomfield_LifecycleContributeEntity(), entry,Field.Store.YES, Field.Index.TOKENIZED));
////			searchFields.add(entry);
////		} catch (Exception e) {
////			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
////		}
////	}
////
////	private static void addGeneralLanguage(LOM lom, Document doc) {
////		int i;
////		String entry;
////		// ... General Language
////		try {
////			entry = "";
////			i = 0;
////			LOM.General.Language generalLanguage = LOMUtil.getGeneralLanguage(lom, i);
////			while (generalLanguage != null) {
////				try {
////					if (entry != "") {
////						entry += FIELD_SEPARATOR;
////					}
////					entry += generalLanguage.string();
////				} catch (Exception e) {
////				} finally {
////					i++;
////					generalLanguage = LOMUtil.getGeneralLanguage(lom, i);
////				}
////			}
////			doc.add(new Field(lomfield_GeneralLanguage(), entry,Field.Store.YES, Field.Index.TOKENIZED));
////		} catch (Exception e) {
////			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
////		}
////	}
////
////	private static void addGeneralKeyword(LOM lom, Document doc, Vector searchFields) {
////		int i;
////		String entry;
////		// ... General Keyword
////		try {
////			entry = "";
////			i = 0;
////			LOM.General.Keyword generalKeyword = LOMUtil.getGeneralKeyword(lom, i);
////			while (generalKeyword != null) {
////				try {
////					if (entry != "") {
////						entry += FIELD_SEPARATOR;
////					}
////					entry += generalKeyword.string(0).string();
////				} catch (Exception e) {
////				} finally {
////					i++;
////					generalKeyword = LOMUtil.getGeneralKeyword(lom, i);
////				}
////			}
////			doc.add(new Field(lomfield_GeneralKeyWords(), entry,Field.Store.YES, Field.Index.TOKENIZED));
////			searchFields.add(entry);
////		} catch (Exception e) {
////			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
////		}
////	}
////
////	private static void addGeneralDescription(LOM lom, Document doc, Vector searchFields) {
////		int i;
////		String entry;
////		// ... General Description
////		try {
////			entry = "";
////			i = 0;
////			while (LOMUtil.getGeneralDescription(lom, i) != null) {
////				try {
////					if (entry != "") {
////						entry += FIELD_SEPARATOR;
////					}
////					entry += LOMUtil.getGeneralDescription(lom, i).string(0).string();
////				} catch (Exception e) {
////				} finally {
////					i++;
////				}
////				//todo: language
////			}
////			doc.add(new Field(lomfield_GeneralDescription(), entry,Field.Store.YES, Field.Index.TOKENIZED));
////			searchFields.add(entry);
////		} catch (Exception e) {
////			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
////		}
////	}
//
//	private static void addGeneralTitle(Element lom, Document doc, Vector searchFields) {
//		int i;
//		String entry;
//		// ... General Title
//		try {
//			entry = "";
//			List<Element> elements = OaiUtils.getXpathList("/lom:lom/lom:general/lom:title", OaiUtils.LOMLOMNS, lom);
//			for(Element element : elements) {
//					if (entry != "") {
//						entry += FIELD_SEPARATOR;
//					}
//					entry += element.getChildText("string", OaiUtils.LOMNS);
//			}
//			doc.add((new Field(lomfield_GeneralTitle(), entry,Field.Store.YES, Field.Index.TOKENIZED)));
//			searchFields.add(entry);
//		} catch (Exception e) {
//			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
//		}
//	}
//
//	private static void addGeneralIdentifier(Element lom, Document doc, Vector searchFields) {
//		int i;
//		String entry;
//		// ... General Identifier
//		try {
//			entry = "";
//			List<Element> elements = OaiUtils.getXpathList("/lom:lom/lom:general/lom:identifier", OaiUtils.LOMLOMNS, lom);
//			for(Element element : elements) {
//					if (entry != "") {
//						entry += FIELD_SEPARATOR;
//					}
//					entry += element.getChildText("entry", OaiUtils.LOMNS);
//			}
//			doc.add(new Field(lomfield_GeneralIdentifierEntry(), entry,Field.Store.YES, Field.Index.TOKENIZED));
//			searchFields.add(entry);
//		} catch (Exception e) {
//			Logger.getLogger(LOGGER).error("LOMLuceneIndexCreator::createLuceneDoc(LOM lom) error: " + e);
//		}
//	}
//
//
//	public synchronized void updateIndex(Document doc) throws IOException {
//		// The deletion based on general identifier results in problems, namely in case there is no such
//		// identifier. Then each update of the index with a new doc will replace the previous one,
//		// resulting in just 1 document in the index.
//		// TODO: find a better way to do the delete...
////		try {
////		deleteIdWithoutOptimize(doc.getField(LUCENE_GENERALIDENTIFIER).stringValue());
////		} catch (IOException e) {
////		// EG the index did not exist yet...
////		}
//		IndexWriter writer = null;
//		try {
//			// writer = openIndex(getLuceneDirectory(), new SimpleKeepNumbersAnalyzer());
//			writer = openIndex(getLuceneDirectory(), new StandardAnalyzer());
//			writer.addDocument(doc);
//			// writer.optimize();
//		} catch (IOException e) {
//			throw e;
//		} catch (Exception e) {
//			throw new IOException(e.getMessage());
//		} finally {
//			closeIndexWriter(writer);
//		}
//
//	}
//
//
//	public void deleteId(String id) throws IOException {
//		deleteIdWithoutOptimize(id);
//	}
//
//	public void deleteIdWithoutOptimize(String id) throws IOException {
//		IndexReader ir = IndexReader.open((getLuceneDirectory()));
//		for (int i = 0; i < ir.maxDoc(); i++) {
//			try {
//				if (ir.document(i).get(lomfield_GeneralIdentifierEntry()).equalsIgnoreCase(id)) {
//					ir.deleteDocument(i);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		closeIndexReader(ir);
//	}
//
//	private void closeIndexWriter(IndexWriter writer) {
//		if (writer != null)
//			try {
//				writer.close();
//			} catch (IOException e) {
//			}
//	}
//	private void closeIndexReader(IndexReader reader) {
//		if (reader != null)
//			try {
//				reader.close();
//			} catch (IOException e) {
//			}
//	}
//
//	public static String lomfield_GeneralIdentifierEntry() {
//		return IDENTIFIER;
//	}
//
//	public static String lomfield_GeneralLanguage() {
//		return LOM_ + GENERAL_ + LANGUAGE;
//	}
//
//	public static String lomfield_GeneralTitle() {
//		return TITLE;
//	}
//
//	public static String lomfield_GeneralDescription() {
//		return LOM_ + GENERAL_ + DESCRIPTION;
//	}
//
//	public static String lomfield_GeneralKeyWords() {
//		return LOM_ + GENERAL_ + KEYWORDS;
//	}
//	public static String lomfield_LifecycleContributeEntity() {
//		return LOM_ + LIFECYCLE_ + CONTRIBUTE_ + ENTITY;
//	}
//	public static String lomfield_LifecycleContributeDate() {
//		return LOM_ + LIFECYCLE_ + CONTRIBUTE_ + DATE;
//	}
//	public static String lomfield_RightsDescription() {
//		return LOM_ + RIGHTS_ + DESCRIPTION;
//	}
//	public static String lomfield_TechnicalSize() {
//		return SIZE;
//	}
//	public static String lomfield_TechnicalLocation() {
//		return LOCATION;
//	}
//	public static String lomfield_TechnicalFormat() {
//		return FORMAT;
//	}
//	public static String lomfield_EducationalIntendedEndUserRole() {
//		return INTENDEDENDUSERROLE;
//	}
//	public static String lomfield_FullLom() {
//		return LOM_ + FULLLOM;
//	}
//	public static String searchField() {
//		return SEARCHFIELD;
//	}
//
//}
