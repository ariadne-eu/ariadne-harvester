package org.ariadne.mapping;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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
import org.joda.time.format.ISODateTimeFormat;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;


public class DC2BuRSTMapper extends GenericMapper {

	protected static XPath identifiers = createOaiDcXpath("dc:identifier");
	protected static XPath titles = createOaiDcXpath("dc:title");
	protected static XPath dates = createOaiDcXpath("dc:date");
	protected static XPath types = createOaiDcXpath("dc:type");
	protected static XPath descriptions = createOaiDcXpath("dc:description");
	protected static XPath formats = createOaiDcXpath("dc:format");
	protected static XPath creators = createOaiDcXpath("dc:creator");
	protected static XPath language = createOaiDcXpath("dc:language");
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
		oaiRecord.addNamespace(JDomUtils.OAI_OAINS);
		oaiIdentifier = XPath.newInstance("//oai:header/oai:identifier");
		oaiIdentifier.addNamespace(JDomUtils.OAI_OAINS);
		//		getRecordsFromOAIFile();
		listRecords();
	}


	public static void listRecords() throws OAIException, IOException, JDOMException {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://localhost:4381/burst-xml-target/OAIHandler");
		OAIRecordList records = repos.listRecords("oai_lom","9999-12-31","2000-12-31","stellar-oa");
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


		String folder = "/work/workspaces/workspace/Lirias/data/u0016838/"; //Erik Duval
		//		String folder = "/work/workspaces/workspace/Lirias/data/u0054020/";
		Document doc = new Document();
		Element item = new Element("item");
		doc.setRootElement(item);
		item.setNamespace(JDomUtils.RSSNS);
		item.addNamespaceDeclaration(JDomUtils.RDF_RDFNS);
		item.addNamespaceDeclaration(JDomUtils.DC_DCNS);
		item.addNamespaceDeclaration(JDomUtils.FOAF_FOAFNS);
		item.addNamespaceDeclaration(JDomUtils.SWRC_SWRCNS);
		item.addNamespaceDeclaration(JDomUtils.BURST_BURSTNS);

		Element langNode = (Element)language.selectSingleNode(metadata);
		String lang = "en";
		if(langNode != null) {
			lang = langNode.getTextTrim();
		}
		
		String identifier = "";
		
		List<Element> identifierNodes = identifiers.selectNodes(metadata);
		for (Element identifierEl : identifierNodes) {
			String id = identifierEl.getText(); 
			if(id.startsWith("http://oa.stellarnet.eu/open-archive")) {
				identifier = id;
				break;
			}
		}

		item.setAttribute("about", identifier, JDomUtils.RDF_RDFNS);
		item.setAttribute("lang", lang,JDomUtils.XML_XMLNS);

		//		System.out.print(liriasHandle + ";");
		Element itemTitle = JDomUtils.newElement("title", item, JDomUtils.RSSNS);
		Element titleNode = (Element)titles.selectSingleNode(metadata);
		String titleString = "no_title";
		if(titleNode != null && !titleNode.getTextTrim().equalsIgnoreCase("")) {
			titleString = titleNode.getTextTrim();
		}
		
		itemTitle.setText(titleString);
		itemTitle.setAttribute("lang", lang,JDomUtils.XML_XMLNS);

		//		System.out.println(liriasItem.getTitle());

		Element itemDescr = JDomUtils.newElement("description", item, JDomUtils.RSSNS);
		Element descrNode = (Element)descriptions.selectSingleNode(metadata);
		if(descrNode != null && !descrNode.getTextTrim().equalsIgnoreCase("")) {
			itemDescr.setText(descrNode.getTextTrim());
		}else {
			itemDescr.setText("?");
		}
		itemDescr.setAttribute("lang", lang,JDomUtils.XML_XMLNS);

		Element itemLink = JDomUtils.newElement("link", item, JDomUtils.RSSNS);
		//		LiriasBitstream[] liriasBitstream = liriasItem.getBitstreams();
		//		if(liriasBitstream != null && liriasBitstream.length > 0) {
		//			//			Element itemLink = JDomUtils.newElement("link", item, rssns);
		//			itemLink.setText(liriasBitstream[0].getLink());
		//		} else {
		itemLink.setText(identifier);
		//		}

		//			Element itemDescr = JDomUtils.newElement("description", item, rssns);
		//			itemDescr.setText(liriasItem.get)

		Element itemDate = JDomUtils.newElement("date", item, JDomUtils.DC_DCNS);
		itemDate.setText(getDate());

		Element publication = JDomUtils.newElement("publication", item, JDomUtils.BURST_BURSTNS);
		Element inPub = null;

		inPub = getPubType(publication, null);

		Element title = JDomUtils.newElement("title", inPub, JDomUtils.SWRC_SWRCNS);
		title.setText(titleString);

		List<Element> creatorNodes = creators.selectNodes(metadata);
		addAuthors(inPub, creatorNodes);
		
		return item;
	}

	private static void addAuthors(Element inPub, List<Element> authors) {
		if(authors != null && authors.size() > 0) {
			for (Element authorNode : authors) {
				Element author =JDomUtils.newElement("author", inPub, JDomUtils.SWRC_SWRCNS);
				Element person =JDomUtils.newElement("Person", author, JDomUtils.SWRC_SWRCNS);
				String nameString = authorNode.getTextTrim();
				Element name = JDomUtils.newElement("name", person, JDomUtils.SWRC_SWRCNS);
				name.setText(nameString);
				//				System.out.println(liriasContributor.getId());
//				if(authorNode.getId() != null && !authorNode.getId().trim().equalsIgnoreCase("")) {
//					String affilString = "Katholieke Universiteit Leuven, Belgium";
//					Element affiliation = JDomUtils.newElement("affiliation", person, JDomUtils.SWRC_SWRCNS);
//					affiliation.setText(affilString);
//									} else if(extraAuthorAffiliations.containsKey(nameString)){
//										String affilString = extraAuthorAffiliations.get(nameString);
//										Element affiliation = JDomUtils.newElement("affiliation", person, JDomUtils.SWRC_SWRCNS);
//										affiliation.setText(affilString);
//				}else {
//					unknown.add(nameString);
//				}
			}
		}
	}
	
	private static Element getPubType(Element publication, String docType) {
		// @see : http://www.kuleuven.be/research/bibliometrics/types.html
		HashMap<String, Integer> docTypes = new HashMap<String, Integer>();
		docTypes.put("IT",0);
		docTypes.put("AT",1);
		docTypes.put("IBa",2);
		docTypes.put("ABa",3);
		docTypes.put("IBe",4);
		docTypes.put("ABe",5);
		docTypes.put("IHb",6);
		docTypes.put("AHb",7);
		docTypes.put("IC",8);
		docTypes.put("NC",9);
		docTypes.put("IMa",10);
		docTypes.put("AMa",11);
		docTypes.put("TH",12);
		docTypes.put("IR",13);
		docTypes.put("RE",14);
		docTypes.put("DI",15);

		Element inPub = null;
		switch (100) {
//		switch (docTypes.get(docType)) {
		case 0 : inPub  = JDomUtils.newElement("Article", publication, JDomUtils.SWRC_SWRCNS);break;
		case 1 : inPub = JDomUtils.newElement("Article", publication, JDomUtils.SWRC_SWRCNS);break;

		case 2 : inPub = JDomUtils.newElement("Book", publication, JDomUtils.SWRC_SWRCNS);break;
		case 3 : inPub = JDomUtils.newElement("Book", publication, JDomUtils.SWRC_SWRCNS);break;
		case 4 : inPub = JDomUtils.newElement("Book", publication, JDomUtils.SWRC_SWRCNS);break;
		case 5 : inPub = JDomUtils.newElement("Book", publication, JDomUtils.SWRC_SWRCNS);break;

		case 6 : inPub = JDomUtils.newElement("InBook", publication, JDomUtils.SWRC_SWRCNS);break;
		case 7 : inPub = JDomUtils.newElement("InBook", publication, JDomUtils.SWRC_SWRCNS);break;

		case 8 : inPub = JDomUtils.newElement("InProceedings", publication, JDomUtils.SWRC_SWRCNS);break;
		case 9 : inPub = JDomUtils.newElement("InProceedings", publication, JDomUtils.SWRC_SWRCNS);break;
//		case 12 : inPub = JDomUtils.newElement("PhDThesis", publication, JDomUtils.SWRC_SWRCNS;break;
		case 12 : inPub = JDomUtils.newElement("Thesis", publication, JDomUtils.SWRC_SWRCNS);break;

		default : inPub = JDomUtils.newElement("Publication", publication, JDomUtils.SWRC_SWRCNS);break;
		}
		return inPub;
	}
	
	
	private static String getDate() {
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		//		DateTime dt2 = fmt.parseDateTime(dateString);
		DateTime dateTime = new DateTime();
//		if(time > 0) dateTime = new DateTime(time);
		String creationDate = fmt.print(dateTime);
		return creationDate;
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
