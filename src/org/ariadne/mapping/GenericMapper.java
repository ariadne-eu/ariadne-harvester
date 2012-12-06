package org.ariadne.mapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;

import org.ariadne.oai.Record;
import org.ariadne.util.IOUtilsv2;
import org.ariadne.util.JDomUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import uiuc.oai.OAIException;

public abstract class GenericMapper {

	public static XPath lomGeneralIdEntry = createLomXpath("lom:general/lom:identifier/lom:entry");

	public abstract Element map(Record record);

	public SAXBuilder builder = new SAXBuilder();

	public static XPath createLomXpath(String xpathString) {
		XPath xpath = null;
		try {
			xpath = XPath.newInstance("/lom:lom/" + xpathString);
			xpath.addNamespace(JDomUtils.LOM_LOMNS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xpath;
	}

	public void transformFromFiles(String path) throws OAIException, JDOMException, IOException {
		transformFromFiles(path, null);
	}
	
	public void transformFromFiles(String path,String outputFolder) throws OAIException, JDOMException, IOException {

		String output = "loms";
		if(outputFolder != null && !outputFolder.trim().isEmpty()) output = outputFolder;
		File dir = new File(path);
		if(dir.exists() && dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if(file.getName().endsWith(".xml")) {
					String metadata = IOUtilsv2.readStringFromFile(file, "UTF-8");
					Record record = new Record();
					record.setMetadata(builder.build(new StringBufferInputStream(metadata)).getRootElement());
					record.setOaiIdentifier(file.getName().replace(".xml", ""));

					IOUtilsv2.writeStringToFileInEncodingUTF8(JDomUtils.parseXml2string(map(record)), output + "/" + file.getName() );
				}
			}
			//		printValues();

			//		printcreativeCommons();
			int i = 0;
			System.out.println("DONE");
		}
	}

	public static XPath createOaiDcXpath(String xpathString) {
		XPath xpath = null;
		try {
			xpath = XPath.newInstance("/oai_dc:dc/" + xpathString);
			xpath.addNamespace(Namespace.getNamespace("oai_dc","http://www.openarchives.org/OAI/2.0/oai_dc/"));
			xpath.addNamespace(Namespace.getNamespace("dc","http://purl.org/dc/elements/1.1/"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xpath;
	}
}
