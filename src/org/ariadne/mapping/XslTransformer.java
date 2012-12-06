package org.ariadne.mapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ariadne.config.PropertiesManager;

public class XslTransformer 
{
	//public static String TRANSFORMER_XSL_PATH = "/configuration/lom2iloxOaiMapper.xsl";
	
	public static String TRANSFORMER_XSL_PATH = "file:///work/workspaces/workspace/OaiHarvester/install/lom2iloxOaiMapper.xsl";
	
	
	protected String xslFilename = TRANSFORMER_XSL_PATH;
	
	protected Transformer transformer;
	protected String xslSystranStr;
	
	/**
	 * 
	 * @param xslFilename	E.g, "/lom2ilox.xsl" - 
	 * 						lom2ilox.xsl should be in the root of the jar file
	 * @throws Exception
	 */
	public XslTransformer(String xslFileName) throws Exception
	{
		xslFilename = xslFileName;
		prepare( xslFilename);
	}
	
	/**
	 * 
	 * @param xslFilename	E.g, "/lom2ilox.xsl" - 
	 * 						lom2ilox.xsl should be in the root of the jar file
	 * @throws Exception
	 */
	protected void prepare( String xslFilename) throws Exception
	{
		this.xslFilename = xslFilename;
		
		//xslSystranStr = getXslStr(xslFilename);
		
	  	TransformerFactory tFactory = TransformerFactory.newInstance();
//	  	Source xslSource = new StreamSource(new StringReader(xslSystranStr));
	  	Source xslSource = new StreamSource(xslFilename);
	  	
	  	transformer = tFactory.newTransformer(xslSource);
	}
	
	/**
	 * Should be Override if needed
	 * 
	 * @param src
	 * @param cmrId
	 * @return
	 * @throws Exception
	 */
	public String convert( String src) throws Exception
	{
		try
		{
			Source xmlSource = new StreamSource(new StringReader(src));
		  	Writer writer = new StringWriter();
		  	
		  	transformer.transform(xmlSource, new StreamResult(writer));
			
			return writer.toString().trim();	
		}
    	catch (Exception e) 
    	{
    		e.printStackTrace();
    		throw e;
    	} 		
	}
	   
	public String getXslStr(String filename) throws Exception
	{
		try 
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
										this.getClass().getResourceAsStream(filename)));

			StringBuilder sb = new StringBuilder();
			String line = null;
			
			while ( (line = reader.readLine() )  != null ) 
			{
				sb.append( line + "\n" ) ;
			}
			return sb.toString();
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace() ;
			throw new Exception("CANNOT_LOAD_XSL: " + ex.getMessage());
		}
	}
}
