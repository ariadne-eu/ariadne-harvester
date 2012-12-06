package org.ariadne.mapping;
import java.io.File;
import java.io.IOException;
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
import org.jdom.xpath.XPath;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;


public class ESEMapper extends GenericMapper {

	protected static Namespace lomns = JDomUtils.LOMNS;
	protected static Namespace dcns = JDomUtils.DC_DCNS;

	protected static Namespace esens = Namespace.getNamespace("ese","http://www.europeana.eu/schemas/ese/");
	protected static XPath identifiers = null;
	protected static XPath titles = null;
	protected static XPath descriptions = null;
	protected static XPath formats = null;
	protected static XPath creators = null;
	protected static XPath subjects = null;

	protected static XPath objects = null;
	protected static XPath rights = null;

	static {
		try {
			identifiers = XPath.newInstance("//dc:identifier");
			identifiers.addNamespace(dcns);
			titles = XPath.newInstance("//dc:title");
			titles.addNamespace(dcns);
			descriptions = XPath.newInstance("//dc:description");
			descriptions.addNamespace(dcns);
			formats = XPath.newInstance("//dc:format");
			formats.addNamespace(dcns);
			creators = XPath.newInstance("//dc:creator");
			creators.addNamespace(dcns);
			subjects = XPath.newInstance("//dc:subject");
			subjects.addNamespace(dcns);

			objects = XPath.newInstance("//ese:isShownBy");
			objects.addNamespace(esens);
			rights = XPath.newInstance("//ese:rights");
			rights.addNamespace(esens);

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws OAIException, IOException, JDOMException {
				listRecords();
//		transformFromFiles();
//		regexTest();
		
	}
	
	private static void regexTest() {
		String[] keyw = {"Candiacervus ropalophorus,        ,",
				"...    ,  dewdewdew, .    dewdew",
				"    ..     ,  dewdewdewde",
				"This is a scentence.",
				"This is a scentence. And another one ",
				"1995,                ,              .           .        ,     .       ( )  ,               .",
				"Dendarus moesiacus (Coleoptera, Tenebrionidae).       . ."
		};

		for (String string : keyw) {
			cleanString(string);
		}
	}

	public static void transformFromFiles() {
		try {
			ESEMapper mapper = new ESEMapper();
			mapper.transformFromFiles("/work/tmp/basic-harvest/nateur-ese");
		} catch (OAIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String cleanString(String str) {
		
		String regex = "[ ,\\.\\(][ ,\\.\\(\\)]{1,100}$";
		String regex2 = "^[ ,\\.\\(\\)]*";
		String regex3 = "[ ,\\.\\(\\)]{4,100}";
		String newStr = str.replaceAll(regex, "");
		newStr = newStr.replaceAll(regex2, "");
		newStr = newStr.replaceAll(regex3, ", ");
//		if(!str.equals(newStr)) {
//			System.out.println(str);
//			System.out.println("---" + newStr + "---");
//		}
		
		return newStr;
	}


	public static void listRecords() throws OAIException, IOException, JDOMException {
		
		ESEMapper mapper = new ESEMapper();
		
		OAIRepository repos = new OAIRepository();
		String folderName = "/work/tmp/nateur-ese/tnhm";
		File file = new File(folderName);
		file.mkdirs();
//		AC: http://147.27.41.128:8080/ac/oai/
//			NHMC: http://147.27.41.128:8080/nhmc/oai/
//			HNHM: http://147.27.41.128:8080/hnhm/oai/
//			MNHNL: http://147.27.41.128:8080/mnhnl/oai/
//			JME: http://147.27.41.128:8080/jme/oai/
//			TNHM: http://147.27.41.128:8080/tnhm/oai/
//		repos.setBaseURL("http://147.27.41.128:8080/ac/oai/");
//		repos.setBaseURL("http://147.27.41.128:8080/nhmc/oai/");
//		repos.setBaseURL("http://147.27.41.128:8080/hnhm/oai/");
//		repos.setBaseURL("http://147.27.41.128:8080/mnhnl/oai/");
//		repos.setBaseURL("http://147.27.41.128:8080/jme/oai/");
		repos.setBaseURL("http://147.27.41.128:8080/tnhm/oai/");
		
		OAIRecordList records = repos.listRecords("ese","9999-12-31","2000-12-31","");
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
					Record rec = new Record();
					rec.setOaiRecord(item);
					rec.setMetadata(item.getMetadata());
					rec.setOaiIdentifier(item.getIdentifier());
					IOUtilsv2.writeStringToFileInEncodingUTF8(OaiUtils.parseLom2Xmlstring(mapper.map(rec)), folderName + "/" + item.getIdentifier().replaceAll(":", "_") +".xml" );	
				}
				else {
					System.out.println(item.getIdentifier() + " deleted");
				}
			}

			records.moveNext();
		}
		System.out.println(counter);
	}


	@Override
	public Element map(Record record) {
		Element lom = null;
		try {
			Element metadata = record.getMetadata();
			lom = new Element("lom",lomns);
			new Document(lom);
			lom.setAttribute("schemaLocation","http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd",JDomUtils.XSI_XSINS);
			Element general = OaiUtils.newElement("general", lom);

			//set the General Identifier
			Element identifier = OaiUtils.newElement("identifier", general);
			OaiUtils.addIdentifier("natural-europe.eu", record.getOaiIdentifier(), identifier);	
//			List<Element> idNodes = identifiers.selectNodes(metadata);
//			for (Element element : idNodes) {
//				String theString = element.getText();
//				if(theString != null && !theString.trim().isEmpty()) {
//					Element identifier = OaiUtils.newElement("identifier", general);
//					OaiUtils.addIdentifier("natural-europe.eu", theString, identifier);	 
//				}
//			}

			//set the General Title
			List<Element> titleNodes = titles.selectNodes(metadata);
			for (Element element : titleNodes) {
				String theString = cleanString(element.getText());
				if(theString != null && !theString.trim().isEmpty()) {
					Element title = OaiUtils.newElement("title", general);
					OaiUtils.addLangString(element.getAttributeValue("lang",JDomUtils.XML_XMLNS),theString, title); 
				}
			}

			//set the General Description
			List<Element> descrNodes = descriptions.selectNodes(metadata);
			for (Element element : descrNodes) {
				String theString = cleanString(element.getText());
				if(theString != null && !theString.trim().isEmpty()) {
					Element title = OaiUtils.newElement("description", general);
					OaiUtils.addLangString(element.getAttributeValue("lang",JDomUtils.XML_XMLNS),theString, title); 
				}
			}

			// General Keywords
			List<Element> kwNodes = subjects.selectNodes(metadata);
			for (Element element : kwNodes) {
				String theString = cleanString(element.getText());
				if(theString != null && !theString.trim().isEmpty()) {
					Element el = OaiUtils.newElement("keyword", general);
					OaiUtils.addLangString(element.getAttributeValue("lang",JDomUtils.XML_XMLNS),theString, el); 
				}
			}

			//metaMetadata Identifier
			Element metaMetadata = OaiUtils.newElement("metaMetadata", lom);
			identifier = OaiUtils.newElement("identifier", metaMetadata);
			OaiUtils.addIdentifier("natural-europe.eu", record.getOaiIdentifier(), identifier);	
 
//			List<Element> midNodes = identifiers.selectNodes(metadata);
//			for (Element element : midNodes) {
//				String theString = element.getText();
//				if(theString != null && !theString.trim().isEmpty()) {
//					Element identifier = OaiUtils.newElement("identifier", metaMetadata);
//					OaiUtils.addIdentifier("natural-europe.eu", theString, identifier);	 
//				}
//			}

			//technical
			Element technical = OaiUtils.newElement("technical", lom);
			//technical location
			List<Element> identifierNodes = objects.selectNodes(metadata);
			for (Element identifierEl : identifierNodes) {
				String id = identifierEl.getText(); 
				Element location = OaiUtils.newElement("location", technical);
				location.setText(id);
			}
			//technical format
			List<Element> formatNodes = formats.selectNodes(metadata);
			for (Element element : formatNodes) {
				String theString = element.getText();
				if(theString != null && !theString.trim().isEmpty()) {
					Element format = OaiUtils.newElement("format", technical);
					format.setText(theString);
				}
			}

			//rights
			List<Element> rightsNodes =  rights.selectNodes(metadata);
			if(rightsNodes != null) {
				String descr = "";
				boolean url = false;
				for (Element element : rightsNodes) {
					descr = element.getTextTrim();
					if(descr.startsWith("http")) {
						url = true;
						break;
					}
				}
				if(url) {
					descr = descr.split("/")[4];
					Element rights = OaiUtils.newElement("rights", lom);
					Element cost = OaiUtils.newElement("cost", rights);
					OaiUtils.addLomVocabularyItem("no", cost);
					Element copyrightAndOtherRestrictions = OaiUtils.newElement("copyrightAndOtherRestrictions", rights);
					OaiUtils.addLomVocabularyItem("yes", copyrightAndOtherRestrictions);
					Element rightsDescr = OaiUtils.newElement("description", rights);
					OaiUtils.addLangString("x-t-cc-url", descr, rightsDescr);
				}
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lom;
	}

}
