package org.ariadne.mapping;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.StringTokenizer;

import org.ariadne.oai.Record;
import org.ariadne.util.IOUtilsv2;
import org.ariadne.util.JDomUtils;
import org.ariadne.util.OaiUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;


public class BSOMapper extends GenericMapper {

	protected static Namespace lomns = Namespace.getNamespace("http://ltsc.ieee.org/xsd/LOM");
	protected static Namespace xsi = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
	protected static Namespace macens = Namespace.getNamespace("mace","http://www.mace-project.eu/xsd/LOM");
	public static Namespace OAINS = Namespace.getNamespace("http://www.openarchives.org/OAI/2.0/");
	public static Namespace OAIOAINS = Namespace.getNamespace("oai","http://www.openarchives.org/OAI/2.0/");

	protected static XPath identifiers = createOaiDcXpath("dc:identifier");
	protected static XPath titles = createOaiDcXpath("dc:title");
	protected static XPath dates = createOaiDcXpath("dc:date");
	protected static XPath types = createOaiDcXpath("dc:type");
	protected static XPath descriptions = createOaiDcXpath("dc:description");
	protected static XPath formats = createOaiDcXpath("dc:format");
	protected static XPath creators = createOaiDcXpath("dc:creator");
	protected static XPath relations = createOaiDcXpath("dc:relation");
	protected static XPath subjects = createOaiDcXpath("dc:subject");
	protected static XPath sources = createOaiDcXpath("dc:source");
	protected static XPath oai_dc = null;
	protected static XPath oaiRecord = null;
	protected static XPath oaiIdentifier = null;

	public static void main(String[] args) throws OAIException, JDOMException, IOException {
		//		listRecords();
		oai_dc = XPath.newInstance("//oai_dc:dc");
		oai_dc.addNamespace(Namespace.getNamespace("oai_dc","http://www.openarchives.org/OAI/2.0/oai_dc/"));
		oai_dc.addNamespace(Namespace.getNamespace("dc","http://purl.org/dc/elements/1.1/"));
		oaiRecord = XPath.newInstance("//oai:record");
		oaiRecord.addNamespace(OAIOAINS);
		oaiIdentifier = XPath.newInstance("//oai:header/oai:identifier");
		oaiIdentifier.addNamespace(OAIOAINS);
		//		getRecordsFromOAIFile();
		listRecords();
	}


	public static void listRecords() throws OAIException, IOException, JDOMException {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://localhost:7080/oaicat_5.0/OAIHandler");
		OAIRecordList records = repos.listRecords("oai_lom","9999-12-31","2000-12-31","");
		int counter = 0;
		records.moveNext();
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
					IOUtilsv2.writeStringToFileInEncodingUTF8(OaiUtils.parseLom2Xmlstring(mapToLom(metadata)), "loms/" + item.getIdentifier().replaceAll("oai:oaicat.ariadne.org:", "") +".xml" );	
				}
				else {
					System.out.println(item.getIdentifier() + " deleted");
				}
			}

			records.moveNext();
		}
		System.out.println(counter);
	}

	public static void getRecordsFromOAIFile() throws IOException, JDOMException, OAIException {
		FileInputStream file = new FileInputStream("/Users/bramv/Library/Mail Downloads/oaiharvest9824.xml");
		byte[] b = new byte[file.available()];
		file.read(b);
		file.close ();
		String oaipmh = new String (b);
		int counter = 0;
		SAXBuilder docBuilder = new SAXBuilder();
		Document xml = docBuilder.build(new StringReader(oaipmh));
		List<Element> xmlList = oaiRecord.selectNodes(xml.getRootElement());
		for (Element record : xmlList) {
			counter++;
			record.detach();
			//			OAIRecord item = records.getCurrentItem();	

			/*get the lom metadata : item.getMetadata();
			 * this return a Node which contains the lom metadata.
			 */
			//			if(!item.deleted()) {
			Element metadata = (Element) oai_dc.selectSingleNode(record);
			String identifier = ((Element)oaiIdentifier.selectSingleNode(record)).getText();
			if(metadata != null) {
				//				System.out.println(item.getIdentifier());
				IOUtilsv2.writeStringToFileInEncodingUTF8(OaiUtils.parseLom2Xmlstring(mapToLom(metadata)), "loms/" + identifier.replaceAll(":", "_") +".xml" );	
			}
			else {
				//				System.out.println(item.getIdentifier() + " deleted");
			}
			System.out.println(counter);
		}

	}


	public static Element mapToLom(Element metadata) throws OAIException, JDOMException, UnsupportedEncodingException {

		Element lom = new Element("lom",lomns);
		new Document(lom);
		lom.setAttribute("schemaLocation","http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd",xsi);
		Element general = OaiUtils.newElement("general", lom);

		//set the General Identifier
		Element identifierNode = (Element) identifiers.selectSingleNode(metadata);
		Element identifier = OaiUtils.newElement("identifier", general);
		String identifierString = getIdentifier(identifierNode.getText());
		OaiUtils.addIdentifier("retro.seals.ch", identifierString, identifier);

		//set the General Title
		Element title = OaiUtils.newElement("title", general);
		String titleString = ((Element)titles.selectSingleNode(metadata)).getText();
		OaiUtils.addLangString("de",titleString, title);

		// General Keywords
		
		String keywordString = ((Element)subjects.selectSingleNode(metadata)).getTextTrim();
		if(!keywordString.equalsIgnoreCase("")) {
			Element keyword1 = OaiUtils.newElement("keyword", general);
			OaiUtils.addLangString("de",keywordString, keyword1);	
		}
		

		// General Language
		Element lang = OaiUtils.newElement("language", general);
		lang.setText("de");

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
			StringTokenizer tokenizer = new StringTokenizer(creator.getText(),",");
			if(tokenizer.hasMoreTokens()) {
				Element entity = OaiUtils.newElement("entity", contribute);


				String surname = tokenizer.nextToken().trim();
				String name = "";
				if(tokenizer.hasMoreTokens()) {
					name = tokenizer.nextToken().trim();
				}
				entity.addContent(OaiUtils.createVcard(name, surname));
			}
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
		OaiUtils.addIdentifier("retro.seals.ch", identifierString, metaIdentifier);

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
//				String[] locationString = id.split("\\?rid=");
//				location.setText(locationString[0] + "?rid=" + URLEncoder.encode(locationString[1],"UTF-8"));
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
		
		//rights
		Element rights = OaiUtils.newElement("rights", lom);
		Element copyrightAndOtherRestrictions = OaiUtils.newElement("copyrightAndOtherRestrictions", rights);
		OaiUtils.addLomVocabularyItem("yes", copyrightAndOtherRestrictions);
		Element description = OaiUtils.newElement("description", rights);
		OaiUtils.addLangString("de", "Mit dem Zugriff auf den vorliegenden Inhalt gelten die Nutzungsbedingungen als akzeptiert. Die angebotenen Dokumente stehen für nicht-kommerzielle Zwecke in Lehre, Forschung und für die private Nutzung frei zur Verfügung. Einzelne Dateien oder Ausdrucke aus diesem Angebot können zusammen mit diesen Nutzungsbedingungen und unter deren Einhaltung weitergegeben werden. Die Speicherung von Teilen des elektronischen Angebots auf anderen Servern ist nur mit vorheriger schriftlicher Genehmigung des Konsortiums der Schweizer Hochschulbibliotheken möglich. Die Rechte für diese und andere Nutzungsarten der Inhalte liegen beim Herausgeber bzw. beim Verlag.", description);
		

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
		return relation.split("rid=")[1];
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
			element = mapToLom(record.getMetadata());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return element;

	}

}
