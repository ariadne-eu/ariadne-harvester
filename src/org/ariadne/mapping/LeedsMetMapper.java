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


public class LeedsMetMapper extends GenericMapper {

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
	protected static XPath rights = createOaiDcXpath("rights");
	protected static XPath creators = createOaiDcXpath("dc:creator");
	//	protected static XPath relations = createOaiDcXpath("dc:relation");
	protected static XPath subjects = createOaiDcXpath("dc:subject");


	public static void main(String[] args) throws OAIException, JDOMException, IOException {
		listRecords();
	}


	public static void listRecords() throws OAIException, IOException, JDOMException {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://repository-intralibrary.leedsmet.ac.uk/IntraLibrary-OAI");
		OAIRecordList records = repos.listRecords("oai_dc","9999-12-31","2000-12-31","23");
		int counter = 0;
//		records.moveNext();
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
					IOUtilsv2.writeStringToFileInEncodingUTF8(OaiUtils.parseLom2Xmlstring(mapDCToLom(record)), "loms/" + item.getIdentifier().replaceAll(":", "_").replaceAll("/", ".s.") +".xml" );	
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

		String localCatalog = "LeedsMet";
		String language = "en";
		Element langNode = (Element) languages.selectSingleNode(metadata);
		if(langNode != null) language = langNode.getTextTrim();

		//set the General Identifier
		Element identifier = OaiUtils.newElement("identifier", general);
		String identifierString = record.getOaiRecord().getIdentifier();
		String[] splitIdentifier = identifierString.split(":", 3);
		String localIdentifier = identifierString;
		if(splitIdentifier.length == 3)localIdentifier = splitIdentifier[2];
		OaiUtils.addIdentifier(localCatalog, localIdentifier, identifier);

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

			String creationDate = record.getOaiRecord().getDatestamp();
			Element date = OaiUtils.newElement("date", contribute);
			DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
			DateTimeFormatter fmt2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

			DateTime dt2 = fmt2.parseDateTime(creationDate);
			String dateString = fmt.print(dt2);

			Object selectSingleNode = dates.selectSingleNode(metadata);
			if(selectSingleNode != null) {
				String datestr = ((Element)selectSingleNode).getText();
				System.out.println(datestr);
								fmt2 = new DateTimeFormatterBuilder().appendYear(2,4).appendOptional(
										new DateTimeFormatterBuilder().appendLiteral('-').
										appendMonthOfYear(2).appendOptional(
												new DateTimeFormatterBuilder().appendLiteral('-').
												appendDayOfMonth(2).appendOptional(
														new DateTimeFormatterBuilder().appendLiteral('T').
														appendHourOfDay(2).appendOptional(
																new DateTimeFormatterBuilder().appendLiteral(':').
																appendMinuteOfHour(2).appendOptional(
																		new DateTimeFormatterBuilder().appendLiteral(':').appendSecondOfMinute(2).appendOptional(new DateTimeFormatterBuilder().appendLiteral('.').appendMillisOfSecond(3).toParser())
																		.toParser())
																		.toParser())
																		.toParser()).appendOptional(new DateTimeFormatterBuilder().appendTimeZoneOffset("Z", true, 2, 2).toParser())
																		.toParser())
																		.toParser())
																		.toFormatter();
				dt2 = fmt2.parseDateTime(datestr);
				dateString = fmt.print(dt2);
			}

			addDateTime(dateString, date);
			//			}
		}

		//metaMetadata Identifier
		Element metaMetadata = OaiUtils.newElement("metaMetadata", lom);
		Element metaIdentifier = OaiUtils.newElement("identifier", metaMetadata);
		OaiUtils.addIdentifier(localCatalog, localIdentifier, metaIdentifier);

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
		
		//technical format
		List<Element> typesNode = types.selectNodes(metadata);
		for (Element typesEl : typesNode) {
			String typesString = typesEl.getText(); 
			if(typesString.contains("/")) {
				Element format = OaiUtils.newElement("format", technical);
				format.setText(typesString);
				break;
			}
		}
		
		//educational
		Element educational = new Element("educational",lomns);
		
		//educational learningResourceType
		typesNode = types.selectNodes(metadata);
		for (Element typesEl : typesNode) {
			String typesString = typesEl.getText(); 
			if(!typesString.contains("/")) {
				Element learningResourceType = OaiUtils.newElement("learningResourceType", educational);
				OaiUtils.addLomVocabularyItem(typesString.toLowerCase(), learningResourceType);
				break;
			}
		}
		
		if(educational.getChildren().size() > 0) lom.addContent(educational);
		


		//rights
		Element rightsNode = (Element) rights.selectSingleNode(metadata);
		if(rightsNode != null) {
			Element rights = OaiUtils.newElement("rights", lom);
			Element cost = OaiUtils.newElement("cost", rights);
			OaiUtils.addLomVocabularyItem("no", cost);
			Element copyrightAndOtherRestrictions = OaiUtils.newElement("copyrightAndOtherRestrictions", rights);
			OaiUtils.addLomVocabularyItem("yes", copyrightAndOtherRestrictions);
			Element rightsDescr = OaiUtils.newElement("description", rights);
			OaiUtils.addLangString("x-t-cc-url", rightsNode.getTextTrim(), rightsDescr);
		}

		// Not mapped yet :
		// <dc:relation xmlns:dc="http://purl.org/dc/elements/1.1/" xml:lang="en">Virtual Maths collection</dc:relation>
		
		return lom;


	}

	private static void addDateTime(String creationDate, Element date) {
		Element dateTime = OaiUtils.newElement("dateTime", date);
		dateTime.setText(creationDate);
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
