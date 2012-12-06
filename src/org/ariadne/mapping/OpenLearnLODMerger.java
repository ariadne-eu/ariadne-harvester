package org.ariadne.mapping;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.ariadne.oai.Record;
import org.ariadne.util.ClientHttpRequest;
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
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;


public class OpenLearnLODMerger extends GenericMapper {

	protected static Namespace lomns = Namespace.getNamespace("http://ltsc.ieee.org/xsd/LOM");
	protected static Namespace lomlomns = Namespace.getNamespace("lom","http://ltsc.ieee.org/xsd/LOM");
	protected static Namespace yahoons = Namespace.getNamespace("urn\\:yahoo\\:cate");
	protected static Namespace xsi = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
	protected static Namespace lodns = Namespace.getNamespace("lod","http://www.icoper.org/schema/lodv1.0");
	protected static XPath classificationKeywords = createNoNsXpath("classificationKeywords/keyword");
	protected static XPath paragraph = createNoNsXpath("educationalDescription/Paragraph");
	protected static XPath learningOutcomes = createNoNsXpath("educationalDescription/LearningOutcome");
	protected static XPath lomEducational = createLomNsXpath("educational");
	protected static XPath lomIdentifier = createLomNsXpath("general/identifier/entry");
	protected static XPath yahooResult = createYahooNsXpath("//Result");
	protected static SAXBuilder docBuilder = new SAXBuilder();

	public static void main(String[] args) throws OAIException, JDOMException, IOException {
		listRecords();
	}




	public static void listRecords() throws OAIException, IOException, JDOMException {
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL("http://openlearn.open.ac.uk/local/oai/oai2.php");
		OAIRecordList records = repos.listRecords("oai_lom");
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
					IOUtilsv2.writeStringToFileInEncodingUTF8(OaiUtils.parseLom2Xmlstring(mergeWithLOD(metadata)), "loms/" + item.getIdentifier().replaceAll(":", "_") +".xml" );
					
				}
				else {
					System.out.println(item.getIdentifier() + " deleted");
				}
			}

			records.moveNext();
		}
		System.out.println(counter);
	}


	public static Element mergeWithLOD(Element metadata) throws OAIException, JDOMException {

		String id = ((Element)lomIdentifier.selectSingleNode(metadata)).getTextTrim();
		if(id != null && !id.equalsIgnoreCase("")) {
			try {
				String url = "http://openlearn.open.ac.uk/rss/file.php/stdfeed/"+id+"/formats/metadata.xml";
				String unit = IOUtilsv2.httpGet(url);
				Document doc = docBuilder.build(new StringReader(unit));

				Object keyword = classificationKeywords.selectSingleNode(doc);
				String title = "LearningOutcome";
				if (keyword != null) {
					title = ((Element) keyword).getTextTrim();
				}
				
//				Object selectedPara = paragraph.selectSingleNode(doc);
//				String paraString = "";
//				
//				if (selectedPara != null) {
//					paraString = ((Element) selectedPara).getTextTrim();
//					if(!paraString.trim().equals(""))paraString += " ";
//				}
				
				List<Element> lods = learningOutcomes.selectNodes(doc);
				for (Element lod : lods) {
					Object educational = lomEducational.selectSingleNode(metadata);
					if(educational == null) {
						educational = new Element("educational",lomns);
						metadata.addContent((Element)educational);
					}
					String lodDescr = lod.getTextTrim();
					Thread.sleep(100);
					Vector<String> yahooKeywords = getYahooKeywords(lodDescr);
					if(!yahooKeywords.isEmpty())title = yahooKeywords.firstElement();
					addLearningOutcome( title, lodDescr, (Element) educational);
				}

			} catch (IOException e) {
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		return metadata;


	}
	
	public static Vector<String> getYahooKeywords(String context) {
		Vector<String> keywords = new Vector<String>();
		try {

			ClientHttpRequest request = new ClientHttpRequest("http://search.yahooapis.com/ContentAnalysisService/V1/termExtraction");
			request.setParameter("appid", "GFM0K5rV34HGj4zxfGAFQZO1BoEveA6WgbeWwTwA8KEQt_f5P6PTcASu7Z1Nvg--");
			request.setParameter("context", context);
			InputStream postReturn = request.post();
			
			for(Element el : (List<Element>)docBuilder.build(postReturn).getRootElement().getChildren()){
				keywords.add(el.getTextTrim());
			}
			
		} catch (IOException e) {
			try {
				Thread.sleep(100);
				ClientHttpRequest request = new ClientHttpRequest("http://search.yahooapis.com/ContentAnalysisService/V1/termExtraction");
				request.setParameter("appid", "GFM0K5rV34HGj4zxfGAFQZO1BoEveA6WgbeWwTwA8KEQt_f5P6PTcASu7Z1Nvg--");
				request.setParameter("context", context);
				InputStream postReturn = request.post();
				
				for(Element el : (List<Element>)docBuilder.build(postReturn).getRootElement().getChildren()){
					keywords.add(el.getTextTrim());
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JDOMException e3) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			try {
				Thread.sleep(100);
				ClientHttpRequest request = new ClientHttpRequest("http://search.yahooapis.com/ContentAnalysisService/V1/termExtraction");
				request.setParameter("appid", "GFM0K5rV34HGj4zxfGAFQZO1BoEveA6WgbeWwTwA8KEQt_f5P6PTcASu7Z1Nvg--");
				request.setParameter("context", context);
				InputStream postReturn = request.post();
				
				for(Element el : (List<Element>)docBuilder.build(postReturn).getRootElement().getChildren()){
					keywords.add(el.getTextTrim());
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JDOMException e3) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return keywords;

	}

	private static void addLearningOutcome(String title, String description, Element parentNode) {
		Element lod = new Element("learningOutcome",lodns);

		Element lodid = new Element("identifier",lodns);
		lod.addContent(lodid);
		String md5Hash = IOUtilsv2.md5Hash(description);
		String idString = "OpenLearn:"+md5Hash;
		JDomUtils.addIdentifier("ICOPER", idString, lodid);
		
		Element lodRef = (Element) lod.clone();
		
		Element titleEl = new Element("title",lodns);
		lod.addContent(titleEl);
		JDomUtils.addLangString("en", title, titleEl);
		Element descrEl = new Element("description",lodns);
		lod.addContent(descrEl);
		JDomUtils.addLangString("en", description, descrEl);
		
		saveLearningOutcome(lod, idString);
		parentNode.addContent(lodRef);
		
	}
	
	private static void saveLearningOutcome(Element lod, String id) {
		try {
			IOUtilsv2.writeStringToFileInEncodingUTF8(JDomUtils.parseXml2string(lod, null), "lods/" + id.replaceAll("/", ".s.").replaceAll(":", "_")  +".xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static XPath createNoNsXpath(String xpathString) {
		XPath xpath = null;
		try {
			xpath = XPath.newInstance("//" + xpathString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xpath;
	}

	private static XPath createYahooNsXpath(String xpathString) {
		XPath xpath = null;
		try {
			xpath = XPath.newInstance(xpathString);
			xpath.addNamespace(yahoons);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xpath;
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
			element = mergeWithLOD(record.getMetadata());
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
