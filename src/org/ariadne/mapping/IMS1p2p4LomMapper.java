package org.ariadne.mapping;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;

import org.ariadne.config.PropertiesManager;
import org.ariadne.oai.Record;
import org.ariadne.util.JDomUtils;
import org.ariadne.util.OaiUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;

public class IMS1p2p4LomMapper extends GenericMapper {

	private static XslTransformer transformer = null;

	public static void main(String[] args) throws Exception {
		//		listRecords();
		listIdentifiersAndGetRecord();
	}


	public static void listRecords() throws Exception {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://cnx.org/content/OAI");
		//		OAIRecordList records = repos.listRecords("oai_lre3","9999-12-31","2000-12-31","DUNEL");
		OAIRecord records = repos.getRecord("oai:cnx.org:m11184", "ims1_2_1");

		int counter = 0;
		//		records.moveNext();
		//		while (records.moreItems()) {
		//			counter++;
		OAIRecord item = records;	

		/*get the lom metadata : item.getMetadata();
		 * this return a Node which contains the lom metadata.
		 */
		if(!item.deleted()) {
			Element metadata = item.getMetadata();
			if(metadata != null) {
				System.out.println(item.getIdentifier());
				String ilox = element2string(mapToIeeeLom(metadata));
				String filename = "loms/" + item.getIdentifier().replaceAll(":", "_") + ".xml";
				FileOutputStream f = new FileOutputStream(filename);
				writeFileUTF8(ilox, f);	
			}
			else {
				System.out.println(item.getIdentifier() + " deleted");
			}
		}

		//			records.moveNext();
		//		}
		System.out.println(counter);
	}

	public static void listIdentifiersAndGetRecord() throws Exception {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://cnx.org/content/OAI");
		OAIRecordList records = repos.listIdentifiers("9999-12-31","2000-12-31","ims1_2_1","");

		int counter = 0;
		records.moveNext();
		while (records.moreItems()) {
			counter++;
			OAIRecord item = repos.getRecord(records.getCurrentItem().getIdentifier(), "ims1_2_1");;	

			/*get the lom metadata : item.getMetadata();
			 * this return a Node which contains the lom metadata.
			 */
			if(!item.deleted()) {
				Element metadata = item.getMetadata();
				if(metadata != null) {
					System.out.println(item.getIdentifier());
					String ilox = element2string(mapToIeeeLom(metadata));
					String filename = "loms/" + item.getIdentifier().replaceAll(":", "_") + ".xml";
					FileOutputStream f = new FileOutputStream(filename);
					writeFileUTF8(ilox, f);	
				}
				else {
					System.out.println(item.getIdentifier() + " deleted");
				}
			}

			records.moveNext();
		}
		System.out.println(counter);
	}

	public static void getRecord() throws Exception {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://cnx.org/content/OAI");
		//		OAIRecordList records = repos.listRecords("oai_lre3","9999-12-31","2000-12-31","DUNEL");
		OAIRecord records = repos.getRecord("oai:cnx.org:m11184", "ims1_2_1");

		int counter = 0;
		//		records.moveNext();
		//		while (records.moreItems()) {
		//			counter++;
		OAIRecord item = records;	

		/*get the lom metadata : item.getMetadata();
		 * this return a Node which contains the lom metadata.
		 */
		if(!item.deleted()) {
			Element metadata = item.getMetadata();
			if(metadata != null) {
				System.out.println(item.getIdentifier());
				String ilox = element2string(mapToIeeeLom(metadata));
				String filename = "loms/" + item.getIdentifier().replaceAll(":", "_") + ".xml";
				FileOutputStream f = new FileOutputStream(filename);
				writeFileUTF8(ilox, f);	
			}
			else {
				System.out.println(item.getIdentifier() + " deleted");
			}
		}

		//			records.moveNext();
		//		}
		System.out.println(counter);
	}

	public static Element mapToIeeeLom(Element metadata) throws Exception {
		if( transformer == null) {
			//			transformer = new XslTransformer(PropertiesManager.getInstance().getProperty("mapper.Lom2IloxMapper.transformer_xsl_path"));
			transformer = new XslTransformer("/Users/bramv/Downloads/metadatav1p3/transforms/mdv1p2p4ToLOMv1p0/XSLs/LRMv1p2p4-LOMv1p0.xsl");
		}
		String lom = element2string(metadata);
		String ilox = transformer.convert(lom);
		return string2element(ilox);
	}

	private static String element2string( Element lomElement) {
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		outputter.setFormat(format);
		String output = outputter.outputString(lomElement);
		return output;
	}

	private static Element string2element(String iloxStr) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document iloxDoc = builder.build(new StringReader(iloxStr));
		return iloxDoc.getRootElement();
	}

	static public void writeFileUTF8( String str, FileOutputStream f ) throws Exception
	{
		Writer output = new BufferedWriter(new OutputStreamWriter( f, "UTF8"));
		output.write( str );
		output.close();
	}

	@Override
	public Element map(Record record) {
		Element element = null;
		try {
			element = mapToIeeeLom(record.getMetadata());
			element = addIdentifiers(element,record.getOaiIdentifier());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return element;

	}


	private Element addIdentifiers(Element element, String identifier) {
		try {
			addGlobalIdentifier(element, identifier);
			addMetadataIdentifier(element, identifier);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return element;
	}

	public void addMetadataIdentifier(Element element,String identifier) throws JDOMException {
		//		try {
		Element metametadata = JDomUtils.getXpathNode("//lom:lom/lom:metaMetadata", OaiUtils.LOMLOMNS, element);
		if (metametadata == null) {
			metametadata = new Element("metaMetadata", OaiUtils.LOMNS);
			element.addContent(2, metametadata);
		}

		Element newIdentifier = new Element("identifier", OaiUtils.LOMNS);
		metametadata.addContent(0, newIdentifier);

		Element catalog = new Element("catalog", OaiUtils.LOMNS);
		catalog.setText("oai");
		newIdentifier.addContent(catalog);

		Element entry = new Element("entry", OaiUtils.LOMNS);
		entry.setText(identifier);
		newIdentifier.addContent(entry);
	}

	public void addGlobalIdentifier(Element element,String identifier) throws JDOMException {
		//		try {
		Element general = JDomUtils.getXpathNode("//lom:lom/lom:general", OaiUtils.LOMLOMNS, element);
		if (general == null) {
			general = new Element("general", OaiUtils.LOMNS);
			element.addContent(0, general);
		}

		Element newIdentifier = new Element("identifier", OaiUtils.LOMNS);
		general.addContent(0, newIdentifier);

		Element catalog = new Element("catalog", OaiUtils.LOMNS);
		catalog.setText("oai");
		newIdentifier.addContent(catalog);

		Element entry = new Element("entry", OaiUtils.LOMNS);
		entry.setText(identifier);
		newIdentifier.addContent(entry);
	}
}
