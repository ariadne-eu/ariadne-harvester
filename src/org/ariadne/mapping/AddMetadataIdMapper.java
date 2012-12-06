package org.ariadne.mapping;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;

import org.ariadne.oai.Record;
import org.ariadne.util.IOUtilsv2;
import org.ariadne.util.JDomUtils;
import org.ariadne.util.OaiUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;

import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;

public class AddMetadataIdMapper extends GenericMapper {

	protected static Namespace lomlomns = Namespace.getNamespace("lom","http://ltsc.ieee.org/xsd/LOM");
	protected static Namespace lomns = Namespace.getNamespace("http://ltsc.ieee.org/xsd/LOM");
	
	protected static XPath lomIdentifier = createLomNsXpath("general/identifier");
	protected static XPath lomMeta = createLomNsXpath("metaMetadata");
	

	public static void main(String[] args) throws Exception {
		listRecords();
	}


	public static void listRecords() throws OAIException, IOException, JDOMException {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://wszoeken.edurep.kennisnet.nl:8000/edurep/oai");
		OAIRecordList records = repos.listRecords("LOMv1.0");
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
					IOUtilsv2.writeStringToFileInEncodingUTF8(JDomUtils.parseXml2string(addGlobalMetadataIdentifier(metadata, item.getIdentifier()),null), "loms/" + item.getIdentifier().replaceAll(":", "_") +".xml" );
					
				}
				else {
					System.out.println(item.getIdentifier() + " deleted");
				}
			}

			records.moveNext();
		}
		System.out.println(counter);
	}

	public static Element addGlobalMetadataIdentifier(Element metadata, String oaiId) throws IllegalStateException, JDOMException {
		Element lomId = (Element) lomIdentifier.selectSingleNode(metadata);
		String catalog = null;
		String entry = null;
		if(lomId != null) {
			catalog = lomId.getChildText("catalog",lomns);
			entry = lomId.getChildText("entry",lomns);
		} else {
			entry = oaiId;
		}
		if(catalog == null || catalog.trim().equalsIgnoreCase("")) catalog = "added-catalog";
		Element metaMetadata = (Element) lomMeta.selectSingleNode(metadata);
		if(metaMetadata == null) metaMetadata = JDomUtils.newElement("metaMetadata", metadata, lomns);
		Element id = JDomUtils.newElement("identifier", metaMetadata, lomns);
		JDomUtils.addLomIdentifier(catalog, entry, id);
		return metadata;
	}

	static public void writeFileUTF8( String str, FileOutputStream f ) throws Exception
	{
		Writer output = new BufferedWriter(new OutputStreamWriter( f, "UTF8"));
		output.write( str );
		output.close();
	}

	private static XPath createLomNsXpath(String xpathString) {
		XPath xpath = null;
		try {
			xpath = XPath.newInstance("//lom:" + xpathString.replaceAll("/", "/lom:"));
			xpath.addNamespace(lomlomns);
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
			element = addGlobalMetadataIdentifier(record.getMetadata(), record.getOaiIdentifier());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return element;

	}
}
