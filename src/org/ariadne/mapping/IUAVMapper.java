package org.ariadne.mapping;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.ariadne.oai.Record;
import org.ariadne.util.IOUtilsv2;
import org.ariadne.util.OaiUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;


public class IUAVMapper extends GenericMapper {

	protected static Namespace lomns = Namespace.getNamespace("http://ltsc.ieee.org/xsd/LOM");
	protected static Namespace xsi = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
	protected static Namespace macens = Namespace.getNamespace("mace","http://www.mace-project.eu/xsd/LOM");
	protected static XPath identifiers = createOaiDcXpath("dc:identifier");
	protected static XPath titles = createOaiDcXpath("dc:title");
	protected static XPath dates = createOaiDcXpath("dc:date");
	protected static XPath types = createOaiDcXpath("dc:type");
	protected static XPath formats = createOaiDcXpath("dc:format");
	protected static XPath creators = createOaiDcXpath("dc:creator");
	protected static XPath relations = createOaiDcXpath("dc:relation");
	protected static XPath subjects = createOaiDcXpath("dc:subject");
	
	
	public static void main(String[] args) throws OAIException, JDOMException, IOException {
		listRecords();
	}

	
	public static void listRecords() throws OAIException, IOException, JDOMException {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://rice.iuav.it/cgi/oai2");
		OAIRecordList records = repos.listRecords("oai_dc","9999-12-31","2000-12-31","");
		int counter = 0;
//		records.moveNext();
		while (records.moreItems()) {
			counter++;
			OAIRecord item = records.getCurrentItem();	
			
			/*get the lom metadata : item.getMetadata();
			 * this return a Node which contains the lom metadata.
			*/
			if(!item.deleted()) {
			Element metadata = item.getMetadata();
			if(metadata != null) {
				System.out.println(item.getIdentifier());
				IOUtilsv2.writeStringToFileInEncodingUTF8(OaiUtils.parseLom2Xmlstring(mapIUAVToLom(metadata)), "loms/" + item.getIdentifier().replaceAll(":", "_") +".xml" );	
			}
			else {
				System.out.println(item.getIdentifier() + " deleted");
			}
			}
			
			records.moveNext();
		}
		System.out.println(counter);
	}


	public static Element mapIUAVToLom(Element metadata) throws OAIException, JDOMException {
		
		Element lom = new Element("lom",lomns);
		new Document(lom);
		lom.setAttribute("schemaLocation","http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd",xsi);
		Element general = OaiUtils.newElement("general", lom);

		//set the General Identifier
		Element relationNode = (Element) relations.selectSingleNode(metadata);
		Element identifier = OaiUtils.newElement("identifier", general);
		String identifierString = getIdentifier(relationNode.getText());
		OaiUtils.addIdentifier(getReposId(relationNode.getText()), identifierString, identifier);

		//set the General Title
		Element title = OaiUtils.newElement("title", general);
		String titleString = ((Element)titles.selectSingleNode(metadata)).getText();
		OaiUtils.addLangString("it",titleString, title);

		// General Keywords
		Element keyword1 = OaiUtils.newElement("keyword", general);
		String keywordString = ((Element)subjects.selectSingleNode(metadata)).getText();
		OaiUtils.addLangString("it",keywordString, keyword1);
		
		// General Language
		Element lang = OaiUtils.newElement("language", general);
		lang.setText("it");

		// general LOK
		Element lok = new Element("learningObjectKind", macens);
		Element src = new Element("source", macens).setText("MACEv1.0");
		Element val = new Element("value", macens).setText("media object");
		lok.addContent(src);
		lok.addContent(val);
		general.addContent(lok); 
		
		//set lifeCycle contribute
		Element lifeCycle = OaiUtils.newElement("lifeCycle", lom);

		List<Element> creatorNodes = creators.selectNodes(metadata);
		for (Element creator : creatorNodes) {
			Element contribute = OaiUtils.newElement("contribute", lifeCycle);
			Element role = OaiUtils.newElement("role", contribute);
			OaiUtils.addLomVocabularyItem("author", role);
			Element entity = OaiUtils.newElement("entity", contribute);
			
			StringTokenizer tokenizer = new StringTokenizer(creator.getText(),",");
			String surname = tokenizer.nextToken().trim();
			String name = tokenizer.nextToken().trim();
			entity.addContent(OaiUtils.createVcard(name, surname));
			
			Object selectSingleNode = dates.selectSingleNode(metadata);
			if(selectSingleNode != null) {
				String dateString = ((Element)selectSingleNode).getText();
				DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
				DateTimeFormatter fmt2 = new DateTimeFormatterBuilder().appendYear(2,4).appendOptional(new DateTimeFormatterBuilder().appendLiteral('-').appendMonthOfYear(2).appendOptional(new DateTimeFormatterBuilder().appendLiteral('-').appendDayOfMonth(2).toParser()).toParser()).toFormatter();
				DateTime dt2 = fmt2.parseDateTime(dateString);
				String creationDate = fmt.print(dt2);
				
				Element date = OaiUtils.newElement("date", contribute);
				addDateTime(creationDate, date);
			}


		}

		//metaMetadata Identifier
		Element metaMetadata = OaiUtils.newElement("metaMetadata", lom);
		Element metaIdentifier = OaiUtils.newElement("identifier", metaMetadata);
		OaiUtils.addIdentifier(getReposId(relationNode.getText()), identifierString, metaIdentifier);
		
		// metaMetadata Language
		Element metaLang = OaiUtils.newElement("language", metaMetadata);
		metaLang.setText("en");
		
		//technical
		Element technical = OaiUtils.newElement("technical", lom);
		//technical location
		List<Element> identifierNodes = identifiers.selectNodes(metadata);
		for (Element identifierEl : identifierNodes) {
			String id = identifierEl.getText(); 
			if(id.startsWith("http")) {
				Element location = OaiUtils.newElement("location", technical);
				location.setText(id);
				break;
			}
		}
		//technical format
		Object selectSingleNode = formats.selectSingleNode(metadata);
		if(selectSingleNode != null) {
			String formatString = ((Element)selectSingleNode).getText();
			Element format = OaiUtils.newElement("format", technical);
			format.setText(formatString);
		}

		return lom;
		

	}

	private static void addDateTime(String creationDate, Element date) {
		Element dateTime = OaiUtils.newElement("dateTime", date);
		dateTime.setText(creationDate);
	}



	private static String getReposId(String relation) {
		relation = relation.replaceFirst("http://", "");
		StringTokenizer tokenizer = new StringTokenizer(relation,"/");
		return tokenizer.nextToken();
	}

	private static String getIdentifier(String relation) {
		relation = relation.replaceFirst("http://", "");
		StringTokenizer tokenizer = new StringTokenizer(relation,"/");
		tokenizer.nextToken();
		return tokenizer.nextToken();
	}

	private static void printXpath(Element metadata, XPath xpath) throws JDOMException {
		List<Element> selectNodes = xpath.selectNodes(metadata);
		System.out.println("Xpath " + xpath.getXPath() + " :");
		for (Element node : selectNodes) {
			System.out.println(node.getText());	
		}
		System.out.println();
	}


	@Override
	public Element map(Record record) {
		Element element = null;
		try {
			element = mapIUAVToLom(record.getMetadata());
		} catch (OAIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return element;
		
	}

}
