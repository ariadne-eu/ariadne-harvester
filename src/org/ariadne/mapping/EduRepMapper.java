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


public class EduRepMapper extends GenericMapper {

	protected static Namespace lomns = Namespace.getNamespace("lom","http://ltsc.ieee.org/xsd/LOM");
	protected static Namespace xsi = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
	protected static Namespace macens = Namespace.getNamespace("mace","http://www.mace-project.eu/xsd/LOM");
	protected static XPath generalIds = createXpath("//lom:general/lom:identifier[lom:catalog=\"URI\"]/lom:entry");
	protected static XPath relationIds = createXpath("//lom:relation/lom:resource/lom:identifier[lom:catalog=\"URI\"]/lom:entry");


	public static void main(String[] args) throws OAIException, JDOMException, IOException {
		listRecords();
	}


	public static void listRecords() throws OAIException, IOException, JDOMException {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://localhost:4381/xml-target/OAIHandler");
		OAIRecordList records = repos.listRecords("oai_lom","9999-12-31","2000-12-31","slo");
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
					IOUtilsv2.writeStringToFileInEncodingUTF8(OaiUtils.parseLom2Xmlstring(doMapping(record)), "loms/" + item.getIdentifier().replaceAll(":", "_").replaceAll("/", ".s.") +".xml" );	
				}
				else {
					System.out.println(item.getIdentifier() + " deleted");
				}
			}

			records.moveNext();
		}
		System.out.println(counter);
	}


	public static Element doMapping(Record record) throws OAIException, JDOMException {

		Element metadata = record.getMetadata();

		String urlString = "";
		//technical
		Element technical = metadata.getChild("technical",lomns);
		if(technical == null)technical = OaiUtils.newElement("technical", metadata);
		//technical location
		Element location = technical.getChild("location",lomns);
		if(location == null)location = OaiUtils.newElement("location", technical);
		if(location == null || location.getTextTrim().equalsIgnoreCase("")) {

			List<Element> identifierNodes = generalIds.selectNodes(metadata);
			if(identifierNodes.size() > 0) {
				for (Element identifierEl : identifierNodes) {
					String id = identifierEl.getText(); 
					if(id.startsWith("http")) {
						location.setText(id);
						urlString = id;
						break;
					}
				}
			} else {
				identifierNodes = relationIds.selectNodes(metadata);
				if(identifierNodes.size() > 0) {
					for (Element identifierEl : identifierNodes) {
						String id = identifierEl.getText(); 
						if(id.startsWith("http")) {
							location.setText(id);
							urlString = id;
							break;
						}
					}
				}
				else {
//					throw new OAIException(new Short("2"), "No technical.location found in " + record.getOaiIdentifier());
				}
			}
		}else {
			urlString = location.getTextTrim();
		}

		//set the General Identifier
		Element general = metadata.getChild("general",lomns);
		if(general == null)general = OaiUtils.newElement("general", metadata);
		Element identifier = OaiUtils.newElement("identifier", general);
		String identifierString = IOUtilsv2.md5Hash(urlString);
		OaiUtils.addIdentifier("MD5", identifierString, identifier);

		//metaMetadata Identifier
		Element metaMetadata = metadata.getChild("metaMetadata",lomns);
		if(metaMetadata == null)metaMetadata = OaiUtils.newElement("metaMetadata", metadata);
		identifier = OaiUtils.newElement("identifier", metaMetadata);
		identifierString = IOUtilsv2.md5Hash(urlString);
		OaiUtils.addIdentifier("MD5", identifierString, identifier);

		return metadata;

	}
	
	private static XPath createXpath(String xpathString) {
		XPath xpath = null;
		try {
			xpath = XPath.newInstance(xpathString);
			xpath.addNamespace(lomns);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xpath;
	}


	@Override
	public Element map(Record record) {
		Element element = null;
		try {
			element = doMapping(record);
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
