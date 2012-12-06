<%@page import="java.io.DataInputStream"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.File"%>
<%@page import="org.ariadne.config.PropertiesManager"%>
<%@page import="org.ariadne.oai.config.servlet.logging.Log4jInit"%>

<%@page import="java.util.Properties"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.IOException"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Enumeration"%>
<%@page import="org.ariadne.validation.utils.ValidationConstants"%>
<%@page import="org.ariadne.validation.Validator"%><html>

<head>
<title>Upload a configuration file</title>
<%@ include file="index.jsp"%>
<%
	String contentType = request.getContentType();
	System.out.println("Content type is :: " + contentType);
	if ((contentType != null)
			&& (contentType.indexOf("multipart/form-data") >= 0)) {
		DataInputStream in = new DataInputStream(request
		.getInputStream());
		int formDataLength = request.getContentLength();

		byte dataBytes[] = new byte[formDataLength];
		int byteRead = 0;
		int totalBytesRead = 0;
		while (totalBytesRead < formDataLength) {
			byteRead = in.read(dataBytes, totalBytesRead,
			formDataLength);
			totalBytesRead += byteRead;
		}

		String file = new String(dataBytes);
		//String saveFile = file.substring(file.indexOf("filename=\"") + 10);
		//saveFile = saveFile.substring(0, saveFile.indexOf("\n"));
		//saveFile = saveFile.substring(saveFile.lastIndexOf("\\") + 1,saveFile.indexOf("\""));
		String propFile = application.getRealPath("install")
		+ File.separator + "ariadneV4.properties";

		//out.print(dataBytes);

		int lastIndex = contentType.lastIndexOf("=");
		String boundary = contentType.substring(lastIndex + 1,
		contentType.length());
		//out.println(boundary);
		int pos;
		pos = file.indexOf("filename=\"");

		pos = file.indexOf("\n", pos) + 1;

		pos = file.indexOf("\n", pos) + 1;

		pos = file.indexOf("\n", pos) + 1;

		int boundaryLocation = file.indexOf(boundary, pos) - 4;
		int startPos = ((file.substring(0, pos)).getBytes()).length;
		int endPos = ((file.substring(0, boundaryLocation)).getBytes()).length;

		FileOutputStream fileOut = new FileOutputStream(propFile);

		//fileOut.write(dataBytes);
		fileOut.write(dataBytes, startPos, (endPos - startPos));
		fileOut.flush();
		fileOut.close();

		out.println("<center><br>File successfully saved as "
		+ propFile + "</center>");

		PropertiesManager.getInstance().setPropertiesFile(propFile);
		if (!PropertiesManager.getInstance().getPropertiesFile().exists())
			out.println("Could not find ariadneV4.properties template at '"	+ PropertiesManager.getInstance().getPropertiesFile()	+ "'");
		PropertiesManager.getInstance().init();
		
	    Properties properties = new Properties();
	    try {
	        properties.load(new FileInputStream(application.getRealPath("install")+ File.separator + "ariadneV4.template"));
	    } catch (IOException e) {
	    }
		Enumeration keys = properties.keys();
		while(keys.hasMoreElements()){
			String key = (String)keys.nextElement();
			if(!key.startsWith(ValidationConstants.VAL_PROP_PREFIX)){
				try{
					PropertiesManager.getInstance().getProperty(key);
					if(PropertiesManager.getInstance().getProperty(key) == null)PropertiesManager.getInstance().saveProperty(key,properties.getProperty(key));
				}catch(IllegalArgumentException e){
				PropertiesManager.getInstance().saveProperty(key,properties.getProperty(key));
				}
			}
		}
		Validator.updatePropertiesFileFromRemote();
		
		Log4jInit.reloadLive("default");
		
	}
%>
<br>

<br />
</body>
</html>
