package org.ariadne.mapping;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;

import org.ariadne.config.PropertiesManager;
import org.ariadne.oai.Record;
import org.jdom.Document;
import org.jdom.Element;

import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;

public class Lom2IloxMapper extends GenericMapper {

	private static XslTransformer transformer = null;
	
	public static void main(String[] args) throws Exception {
		listRecords();
	}

	
	public static void listRecords() throws Exception {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://lrecoreprod.eun.org:6080/oaitarget/OAIHandler");
		OAIRecordList records = repos.listRecords("oai_lre3","9999-12-31","2000-12-31","DUNEL");
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
				String ilox = element2string(mapLom2Ilox(metadata));
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
	
	public static Element mapLom2Ilox(Element metadata) throws Exception {
		if( transformer == null) {
			transformer = new XslTransformer(PropertiesManager.getInstance().getProperty("mapper.Lom2IloxMapper.transformer_xsl_path"));
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
			element = mapLom2Ilox(record.getMetadata());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return element;
		
	}
}
