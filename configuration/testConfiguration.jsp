<%@ page import="org.w3c.dom.Document,
                 javax.xml.transform.dom.DOMResult,
                 javax.xml.transform.Transformer,
                 javax.xml.transform.dom.DOMSource,
                 javax.xml.transform.TransformerFactory,
                 javax.xml.parsers.DocumentBuilder,
                 javax.xml.parsers.DocumentBuilderFactory,
                 java.io.File,
                 java.net.URL,
                 org.ariadne.util.xml.XMLUtilities,
                 java.io.InputStream"
         contentType="text/html; charset=UTF-8" %>
<html>
<head>
<title>Test Harvester configuration</title>
<%@ include file="../start/index.jsp" %>
<%
//if(!OAIHarvester.busy()){
//	OAIHarvester.setBusy(true);
     try {
        response.setContentType("text/html");
		if (true) {
			
out.println("<table width=\"100%\" border=\"0\">");
			out.println("  <tr>");
				out.println("    <td valign=\"middle\" align=\"center\">");
							out.println("<br />");
				out.println("<div align=\"center\">");
				out.println("<div class=\"main\">");
         	File xslt = new File(application.getRealPath("/configuration")+"/transTest.xsl");
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         	dbf.setNamespaceAware(true);
         	DocumentBuilder theBuilder = dbf.newDocumentBuilder();
        	Transformer theTransformer = TransformerFactory.newInstance().newTransformer(new DOMSource(theBuilder.parse(xslt)));
            InputStream happyconf = null;
            try {
                URL u = new URL(
                    request.getScheme()+"://"+
                    request.getServerName()+":"+request.getServerPort()+
                    "/"+request.getContextPath()+"/includes/happyconf.jsp");
                happyconf = u.openStream();
            } catch (Exception e) {
                //URL u = new URL(PropertiesManager.getInstance().getProperty("gen.axisEndpoint")+"/includes/happyconf.jsp");
                //happyconf = u.openStream();
                e.printStackTrace();
            }
            DOMSource theInputSource = new DOMSource(theBuilder.parse(happyconf));
         	DOMResult dr = new DOMResult();
         	theTransformer.transform(theInputSource, dr);
			
         	out.println(XMLUtilities.getXML((Document) dr.getNode()));
        	}
     } catch (IllegalArgumentException iae) {
         throw iae;
     } catch (Exception e) {
         e.printStackTrace();
     }
//     OAIHarvester.setBusy(false);
//}
//else{
//	out.println("<br/>");
//	out.println("<h3>Harvester busy, please check the <a href=\"../start/viewHistory.jsp\">History</a> for progress</h3>");
//	out.println("<br/>");
//}
%>

</div>
</div>
</table>
</body>
</html>