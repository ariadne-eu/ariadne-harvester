package org.ariadne.mapping;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.xmlbeans.impl.xb.xmlschema.LangAttribute;
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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;


public class MerlotMapper extends GenericMapper {

	protected Namespace lomns = Namespace.getNamespace("http://ltsc.ieee.org/xsd/LOM");
	protected Namespace xsi = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
	protected SAXBuilder builder = new SAXBuilder();

	protected HashMap<String, String> langMapping;
	protected HashMap<String, HashMap<String,Integer>> values = new HashMap<String, HashMap<String,Integer>>();
	protected HashMap<String, String> ccValues = new HashMap<String, String>();

	public static void main(String[] args) throws OAIException, JDOMException, IOException {

		String toReplace = "&lt;p&gt;this is the security investment firm in Southern CA.&lt;/p&gt;";


		//		createCC();


		MerlotMapper mapper = new MerlotMapper();
		mapper.transformFromFile();
		//System.out.println(mapper.cleanString(toReplace));


		try {
			mapper.transformFromFile();
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

	public MerlotMapper() {
		langMapping = (HashMap<String, String>)IOUtilsv2.doLoad("/work/workspaces/workspace/jdomsandbox/mappingISOCodes.dat");
		if(langMapping == null)langMapping = new HashMap<String, String>();
	}

	private void createCC() {
		String cc = "by-nc-nd il";
		transformCreativeCommons(cc);
	}

	public void transformFromFile() throws OAIException, JDOMException, IOException {
		//		listRecords();
		String path = "/work/tmp/merlot.xml";
		String outputpath = "/work/tmp/merlot/loms";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		String str = reader.readLine();
		String item = "";
		try {
			while (str != null) {
				if(str.contains("<material>")) {
					item = str;
				}else if (str.contains("</material>")) {
					item += str;
					Element lomDoc = transformMerlotToLom(parseMerlotDoc(item));
					String id = getId(lomDoc);
					IOUtilsv2.writeStringToFileInEncodingUTF8(JDomUtils.parseXml2string(lomDoc.getDocument()), outputpath + "/" + id.replaceAll(":", "_").replaceAll("/", ".s.") +".xml" );
				}else {
					item += str;
				}
				str = reader.readLine();
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		//		printValues();

		//		printcreativeCommons();
		int i = 0;
		System.out.println("DONE");
	}


	private String getId(Element lomDoc) {
		try {
			return ((Element)lomGeneralIdEntry.selectSingleNode(lomDoc)).getTextTrim();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "NO_ID_FOUND";
	}


	private Document parseMerlotDoc(String item) {
		try {
			Document doc = builder.build(new StringBufferInputStream(item));
			return doc;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public Element transformMerlotToLom(Document merlotDoc) throws OAIException, JDOMException {

		Element metadata = merlotDoc.getRootElement();

		Element lom = new Element("lom",lomns);
		new Document(lom);
		lom.setAttribute("schemaLocation","http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd",xsi);
		Element general = OaiUtils.newElement("general", lom);

		String localCatalog = "Merlot";
		String org = "merlot.org";
		String metaLanguage = "en";
		//		Element langNode = (Element) languages.selectSingleNode(metadata);
		//		if(langNode != null) {
		//			String textTrim = langNode.getTextTrim();
		//			if(!textTrim.equalsIgnoreCase("ger")) {
		//				language = textTrim;
		//				System.out.println("record : " + record.getOaiIdentifier() + " has language : " + language);
		//			}
		//		} else {
		//			System.out.println("No lang found for record : " + record.getOaiIdentifier());
		//		}

		//set the General Identifier
		Element identifier = OaiUtils.newElement("identifier", general);
		String localIdentifier = metadata.getChildText("id");
		OaiUtils.addIdentifier(localCatalog, localIdentifier, identifier);
		identifier = OaiUtils.newElement("identifier", general);
		OaiUtils.addIdentifier("oai", "oai:" + org + ":" + localIdentifier, identifier);

		//set the General Title

		String titleString = metadata.getChildText("title");
		//		if(titleString.contains("<"))System.out.println(titleString);
		Element title = OaiUtils.newElement("title", general);
		OaiUtils.addLangString(metaLanguage,cleanString(titleString), title);

		//set the General Description
		String descrString = metadata.getChildText("description");
		//		if(descrString.contains("<"))System.out.println(descrString);
		Element descr = OaiUtils.newElement("description", general);
		OaiUtils.addLangString(metaLanguage,cleanString(descrString), descr);

		//		// General Keywords
		//		List<Element> subjectNodes = subjects.selectNodes(metadata);
		//		for (Element subject : subjectNodes) {
		//			Element keyword1 = OaiUtils.newElement("keyword", general);
		//			String keywordString = subject.getText();
		//			OaiUtils.addLangString(language,keywordString, keyword1);
		//		}

		// General Language
		String language = metaLanguage;
		String childTextTrim = metadata.getChildTextTrim("language");

		if(childTextTrim != null && !childTextTrim.equals("")) {
			if(childTextTrim.length() == 2) {
				language = childTextTrim;
			}else {
				String metaLangString = langMapping.get(childTextTrim);
				if(metaLangString != null && metaLangString.trim().length() > 0) {
					language = metaLangString;
				}else {
					language = null;
					//					System.out.println("NO MAPPING  : " + childTextTrim);
				}
			}
		}
		if(language != null) {
			Element lang = OaiUtils.newElement("language", general);
			lang.setText(language);
		}

		//metaMetadata Identifier
		Element metaMetadata = OaiUtils.newElement("metaMetadata", lom);
		Element metaIdentifier = OaiUtils.newElement("identifier", metaMetadata);
		OaiUtils.addIdentifier(localCatalog, localIdentifier, metaIdentifier);
		metaIdentifier = OaiUtils.newElement("identifier", metaMetadata);
		OaiUtils.addIdentifier("oai", "oai:" + org + ":" + localIdentifier, metaIdentifier);

		// metaMetadata Language

		Element metaLang = OaiUtils.newElement("language", metaMetadata);
		metaLang.setText(language);

		String urlString = "";
		//technical
		Element technical = OaiUtils.newElement("technical", lom);
		//technical location

		Element location = OaiUtils.newElement("location", technical);
		location.setText(metadata.getChildText("url"));

		//educational
		Element educational = JDomUtils.newElement("educational",lom,lomns);

		//educational learningResourceType
		String materialType = metadata.getChildText("materialtype");
		if(materialType == null)System.out.println(metadata.getChildText("id"));

		Element learningResourceType = OaiUtils.newElement("learningResourceType", educational);
		OaiUtils.addVocabularyItem("MERLOTv1.0",materialType, learningResourceType);



		//rights
		String creativeCommons = metadata.getChildText("creativecommons");
		if(creativeCommons != null) {
			Element rights = OaiUtils.newElement("rights", lom);
			Element cost = OaiUtils.newElement("cost", rights);
			OaiUtils.addLomVocabularyItem("no", cost);
			Element copyrightAndOtherRestrictions = OaiUtils.newElement("copyrightAndOtherRestrictions", rights);
			OaiUtils.addLomVocabularyItem("yes", copyrightAndOtherRestrictions);
			Element rightsDescr = OaiUtils.newElement("description", rights);
			OaiUtils.addLangString("x-t-cc-url", transformCreativeCommons(creativeCommons), rightsDescr);
		}

		// Not mapped yet :
		// <dc:relation xmlns:dc="http://purl.org/dc/elements/1.1/" xml:lang="en">Virtual Maths collection</dc:relation>

		return lom;


	}

	private String transformCreativeCommons(String cc) {
		cc = cc.trim();
		if(ccValues.get(cc) != null) return ccValues.get(cc);

		String ccUrl = "http://creativecommons.org/licenses/";
		ccUrl += cc;
		String ccUrlValid = ccUrl.replaceFirst(" ", "/3.0/");
		if(testCCUrl(ccUrlValid)) {
			ccValues.put(cc, ccUrlValid);
			return ccUrlValid;
		}

		ccUrlValid = ccUrl.replaceFirst(" ", "/2.5/");
		if(testCCUrl(ccUrlValid)) {
			ccValues.put(cc, ccUrlValid);
			return ccUrlValid;
		}

		ccUrlValid = ccUrl.replaceFirst(" ", "/2.0/");
		if(testCCUrl(ccUrlValid)) {
			ccValues.put(cc, ccUrlValid);
			return ccUrlValid;
		}

		ccUrlValid = ccUrl.replaceFirst(" ", "/1.0/");
		if(testCCUrl(ccUrlValid)) {
			ccValues.put(cc, ccUrlValid);
			return ccUrlValid;
		}
		System.out.println("NOT FOUND : " + cc);
		return null;

	}

	private boolean testCCUrl(String ccUrl3) {
		try {
			HttpURLConnection http = (HttpURLConnection) new URL(ccUrl3).openConnection();
			InputStream httpStream = http.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(httpStream));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = in.readLine()) != null) {
				//			    buffer.append(line + "\n");
				if(line.contains("The page you were looking for was not found.") || line.contains("The resource could not be found.")) return false;
			}
			return true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	protected String cleanString(String str) {
		if(str != null) {
			//			return str.replaceAll("&lt;p&gt;", " ").replaceAll("&lt;/p&gt;", " ").replaceAll("&lt;br/&gt;", " ");
			String replaceAll = str.replaceAll("\\<.*?\\>", " ");
			//			String[] splitted = replaceAll.split(" ");
			//			for (String string : splitted) {
			//				if(string.contains("&amp;") && string.length() > 5)System.out.println(string);	
			//			}

			return replaceAll;
		}else {
			return "";
		}
	}

	@Override
	public Element map(Record record) {
		Element element = null;
		//		try {
		//			element = mapDCToLom(record);
		//		} catch (OAIException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		} catch (JDOMException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		return element;

	}

}
