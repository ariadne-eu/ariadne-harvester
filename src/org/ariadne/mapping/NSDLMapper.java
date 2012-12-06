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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;


public class NSDLMapper extends GenericMapper {

	protected static Namespace lomns = Namespace.getNamespace("http://ltsc.ieee.org/xsd/LOM");
	protected static Namespace xsi = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
	protected static Namespace macens = Namespace.getNamespace("mace","http://www.mace-project.eu/xsd/LOM");
	protected static XPath identifiers = createOaiDcXpath("dc:identifier");
	protected static XPath descriptions = createOaiDcXpath("dc:description");
	protected static XPath titles = createOaiDcXpath("dc:title");
	protected static XPath dates = createOaiDcXpath("dc:date");
	protected static XPath languages = createOaiDcXpath("dc:language");
	protected static XPath types = createOaiDcXpath("dc:type");
	protected static XPath formats = createOaiDcXpath("dc:format");
	protected static XPath creators = createOaiDcXpath("dc:creator");
	//	protected static XPath relations = createOaiDcXpath("dc:relation");
	protected static XPath subjects = createOaiDcXpath("dc:subject");
	
	protected static String output = "/work/OAI/lomDir/mapperLomDir/";


	public static void main(String[] args) throws OAIException, JDOMException, IOException {
//		listRecords();
		getRecord();
	}


	public static void getRecord() throws OAIException, IOException, JDOMException {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://ndr.nsdl.org/oai");
		repos.setValidation(OAIRepository.VALIDATION_LOOSE);
		OAIRecord records = repos.getRecord("oai:nsdl.org:2200-20080622192810841T", "oai_dc");
		int counter = 0;
		if(!records.deleted()) {
			counter++;
			OAIRecord item = records;	

			/*get the lom metadata : item.getMetadata();
			 * this return a Node which contains the lom metadata.
			 */
			if(!item.deleted()) {
				Record record = new Record();
				record.setOaiRecord(item);
				if(record.getMetadata() != null) {
					System.out.println(item.getIdentifier());
					IOUtilsv2.writeStringToFileInEncodingUTF8(OaiUtils.parseLom2Xmlstring(mapDCToLom(record)), output + item.getIdentifier().replaceAll(":", "_").replaceAll("/", ".s.") +".xml" );	
				}
				else {
					System.out.println(item.getIdentifier() + " deleted");
				}
			}

		}
		System.out.println(counter);
	}


	public static void listRecords() throws OAIException, IOException, JDOMException {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://ndr.nsdl.org/oai");
		OAIRecordList records = repos.listRecords("oai_dc","9999-12-31","2000-12-31","");
		int counter = 0;
		records.moveNext();
		while (records.moreItems()) {
			counter++;
			OAIRecord item = records.getCurrentItem();	

			/*get the lom metadata : item.getMetadata();
			 * this return a Node which contains the lom metadata.
			 */
			if(!item.deleted()) {
				Record record = new Record();
				record.setOaiRecord(item);
				if(record.getMetadata() != null) {
					System.out.println(item.getIdentifier());
					IOUtilsv2.writeStringToFileInEncodingUTF8(OaiUtils.parseLom2Xmlstring(mapDCToLom(record)), output + item.getIdentifier().replaceAll(":", "_").replaceAll("/", ".s.") +".xml" );	
				}
				else {
					System.out.println(item.getIdentifier() + " deleted");
				}
			}

			records.moveNext();
		}
		System.out.println(counter);
	}


	public static Element mapDCToLom(Record record) throws OAIException, JDOMException {

		Element metadata = record.getMetadata();
		Element lom = new Element("lom",lomns);
		new Document(lom);
		lom.setAttribute("schemaLocation","http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd",xsi);
		Element general = OaiUtils.newElement("general", lom);

		String urlString = "";
		//technical
		Element technical = OaiUtils.newElement("technical", lom);
		//technical location
		List<Element> identifierNodes = identifiers.selectNodes(metadata);
		for (Element identifierEl : identifierNodes) {
			String id = identifierEl.getText(); 
			if(id.startsWith("http")) {
				Element location = OaiUtils.newElement("location", technical);
				location.setText(id);
				urlString = id;
				break;
			}
		}
		
		String language = "en";
		Element langNode = (Element) languages.selectSingleNode(metadata);
		if(langNode != null) language = langNode.getTextTrim();
		
		//set the General Identifier
		Element identifier = OaiUtils.newElement("identifier", general);
		String identifierString = record.getOaiRecord().getIdentifier();
		String[] splitIdentifier = identifierString.split(":", 3);
		String localIdentifier = identifierString;
		if(splitIdentifier.length == 3)localIdentifier = splitIdentifier[2];
		OaiUtils.addIdentifier("NSDL", localIdentifier, identifier);

		//set the General Title
		Element titleNode = (Element)titles.selectSingleNode(metadata);
		if(titleNode != null) {
			String titleString = titleNode.getText();
			Element title = OaiUtils.newElement("title", general);
			OaiUtils.addLangString(language,titleString, title);
		}
		
		//set the General Description
		Element descrNode = (Element)descriptions.selectSingleNode(metadata);
		if(descrNode != null) {
			String descrString = descrNode.getText();
			Element descr = OaiUtils.newElement("description", general);
			OaiUtils.addLangString(language,descrString, descr);
		}

		// General Keywords
		List<Element> subjectNodes = subjects.selectNodes(metadata);
		for (Element subject : subjectNodes) {
			Element keyword1 = OaiUtils.newElement("keyword", general);
			String keywordString = subject.getText();
			OaiUtils.addLangString(language,keywordString, keyword1);
		}


		// General Language
		Element lang = OaiUtils.newElement("language", general);
		lang.setText(language);

		//		// general LOK
		//		Element lok = new Element("learningObjectKind", macens);
		//		Element src = new Element("source", macens).setText("MACEv1.0");
		//		Element val = new Element("value", macens).setText("media object");
		//		lok.addContent(src);
		//		lok.addContent(val);
		//		general.addContent(lok); 

		//set lifeCycle contribute
		Element lifeCycle = OaiUtils.newElement("lifeCycle", lom);

		List<Element> creatorNodes = creators.selectNodes(metadata);
		for (Element creator : creatorNodes) {
			Element contribute = OaiUtils.newElement("contribute", lifeCycle);
			Element role = OaiUtils.newElement("role", contribute);
			OaiUtils.addLomVocabularyItem("author", role);
			Element entity = OaiUtils.newElement("entity", contribute);

//			StringTokenizer tokenizer = new StringTokenizer(creator.getText(),",");
			String surname = creator.getText();
			String name = creator.getText();
			entity.addContent(OaiUtils.createVcard(name, surname));

//			Object selectSingleNode = dates.selectSingleNode(metadata);
//			if(selectSingleNode != null) {
//				String dateString = ((Element)selectSingleNode).getText();
//				DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
//				DateTimeFormatter fmt2 = new DateTimeFormatterBuilder().appendYear(2,4).appendOptional(new DateTimeFormatterBuilder().appendLiteral('-').appendMonthOfYear(2).appendOptional(new DateTimeFormatterBuilder().appendLiteral('-').appendDayOfMonth(2).toParser()).toParser()).toFormatter();
//				DateTime dt2 = fmt2.parseDateTime(dateString);
//				String creationDate = fmt.print(dt2);
			String creationDate = record.getOaiRecord().getDatestamp();
				Element date = OaiUtils.newElement("date", contribute);
				DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
				DateTimeFormatter fmt2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
				
				DateTime dt2 = fmt2.parseDateTime(creationDate);
				String dateString = fmt.print(dt2);
				addDateTime(dateString, date);
//			}


		}

		//metaMetadata Identifier
		Element metaMetadata = OaiUtils.newElement("metaMetadata", lom);
		Element metaIdentifier = OaiUtils.newElement("identifier", metaMetadata);
		OaiUtils.addIdentifier("NSDL", localIdentifier, metaIdentifier);

		// metaMetadata Language
		Element metaLang = OaiUtils.newElement("language", metaMetadata);
		metaLang.setText(language);

		//technical size
//		List<Element> formatsNode = formats.selectNodes(metadata);
//		for (Element formatsEl : formatsNode) {
//			String formatsString = formatsEl.getText(); 
//			if(formatsString.contains("bytes")) {
//				Element size = OaiUtils.newElement("size", technical);
//				size.setText(formatsString.replaceAll("bytes", "").trim());
//				break;
//			}
//		}
		
		List<Element> typesNode = types.selectNodes(metadata);
		for (Element typesEl : typesNode) {
			String typesString = typesEl.getText(); 
			if(typesString.contains("/")) {
				Element format = OaiUtils.newElement("format", technical);
				format.setText(typesString);
				break;
			}
		}

		return lom;


	}

	private static void addDateTime(String creationDate, Element date) {
		Element dateTime = OaiUtils.newElement("dateTime", date);
		dateTime.setText(creationDate);
	}



	private static String getReposId(String relation) {
		return "kids-d.org";
	}

	private static String getIdentifier(String relation) {
		relation = relation.replaceFirst(".*handle/", "");
		return relation;
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
			element = mapDCToLom(record);
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
