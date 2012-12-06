<%@ page import="java.net.URL" import="uiuc.oai.OAIRepository"
	import="org.ariadne.config.PropertiesManager"
	import="java.util.StringTokenizer" import="org.w3c.dom.Node"
	import="org.w3c.dom.NodeList" import="java.io.IOException"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.apache.log4j.Logger"%>
<%@page import="org.ariadne.oai.OAIHarvester"%>
<%@page import="uiuc.oai.OAIException"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="org.ariadne.oai.utils.HarvesterUtils"%>
<%@page import="java.util.HashMap"%>

<%@page import="org.ariadne.oai.utils.ReposProperties"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.TreeSet"%><html>



<%
	String title = "";

	ReposProperties reposProps = new ReposProperties();
	reposProps.setBaseURL(request.getParameter("targetURL").trim());
	reposProps.setProviderName(request.getParameter("providerName").trim());
	reposProps.setMetadataPrefix(request.getParameter("metadataPrefix").trim());
	reposProps.setMetadataFormat(request.getParameter("metadataFormat").trim());
	reposProps.setHarvestingSet(request.getParameter("harvestingSet").trim());
	reposProps.setAutoReset(request.getParameter("autoReset").trim());
	reposProps.setValidationUri(request.getParameter("validationUri").trim());
	reposProps.setRepositoryIdentifier(request.getParameter("manualRepositoryIdentifier").trim());
	reposProps.setRepositoryName(request.getParameter("customRepositoryname").trim());	
	reposProps.setTransformationID(request.getParameter("transformID").trim());
	
	boolean valid = true;
	String errorReason = "";
	ReposProperties reposPropsReturn = null;
	try{
		reposPropsReturn = HarvesterUtils.addRepository(reposProps);
	} catch(Exception e){
		valid = false;
		errorReason = e.getMessage();
	}	
	if(valid){
		title = "Info about the new OAI Target";
	}
	else{
		title = "Adding OAI Target Failed";
	}
	%>
<head>
<title><%=title %></title>
<%@ include file="index.jsp"%>
<br />
<%

		
if(valid){
		
		printNewService(out,title,reposPropsReturn );
	}
	else{
		printFailedService(out,title,reposProps.getBaseURL(),errorReason);
	}
%>
<%!

void printFailedService(JspWriter out, String title,  String targetUrl, String reason) throws IOException {
	out.println("<div align=\"center\">");
	 out.println("<div class=\"mainaddnew\">");
	  out.println("<table border=\"0\" width=\"98%\">");
	   out.println("<tr>");
	    out.println("<td align=\"left\" valign=\"middle\">");
	     out.println("<table>");
	      out.println("<td align=\"center\" class=\"user\" valign=\"middle\" width=\"64\">");
	       out.println("<img alt=\"result ok\" src=\"../images/browser_nok.png\"/>");
	      out.println("</td>");
	      out.println("<td align=\"left\" class=\"sectionname\" valign=\"middle\">" + title +  "</td>");
	     out.println("</table>");
	     out.println("<table class=\"adminlist\">");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Target URL</th>");
	       out.println("<td class=\"content\">" + targetUrl + "</td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Error</th>");
	       out.println("<td class=\"content\">" + reason + "</td>");
	      out.println("</tr>");
	      
	     out.println("</table>");
	    out.println("</td>");
	   out.println("</tr>");
	  out.println("</table>");
	 out.println("</div>");
	out.println("</div>");
}

/**
 * Fetch the HTML content of the page as simple text.
 */
 String getPageContent(URL url) throws IOException{
   StringBuffer result = new StringBuffer();
   BufferedReader reader = null;
     reader = new BufferedReader( new InputStreamReader(url.openStream()) );
     String line = null;
     while ( (line = reader.readLine()) != null) {
       result.append(line);
     }
	   try {
		      if (reader != null) reader.close();
		    }
		    catch (IOException ex){
		      System.err.println("Cannot close reader: " + reader);
		    }
   return result.toString();
 }

void printNewService(JspWriter out, String title, ReposProperties reposProps) throws IOException {
	OAIRepository rep = new OAIRepository();
	try{
	rep.setBaseURL(reposProps.getBaseURL());
	out.println("<div align=\"center\">");
	 out.println("<div class=\"mainaddnew\">");
	  out.println("<table border=\"0\" width=\"98%\">");
	   out.println("<tr>");
	    out.println("<td align=\"left\" valign=\"middle\">");
	     out.println("<table class=\"adminheading\">");
	      out.println("<td align=\"center\" class=\"user\" valign=\"middle\" width=\"64\">");
	       out.println("<img alt=\"result ok\" src=\"../images/browser.png\"/>");
	      out.println("</td>");
	      out.println("<td align=\"left\" class=\"sectionname\" valign=\"middle\">" + title +  "</td>");
	     out.println("</table>");
	     out.println("<table class=\"adminlist\">");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Target Name</th>");
	       out.println("<td class=\"content\">" + reposProps.getRepositoryName() + "</td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Base URL</th>");
	       out.println("<td class=\"content\">" + reposProps.getBaseURL() + "</td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Earliest Datestamp</th>");
	       out.println("<td class=\"content\">" + rep.getEarliestDatestamp() + "</td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Admin Email</th>");
	       out.println("<td class=\"content\">" + rep.getAdminEmail() + "</td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Repository Identifier</th>");
	       out.println("<td class=\"content\">" + reposProps.getRepositoryIdentifier() + "</td>");
	      out.println("</tr>");
	      
	     out.println("</table>");
	    out.println("</td>");
	   out.println("</tr>");
	  out.println("</table>");
	 out.println("</div>");
	out.println("</div>");
	} catch(OAIException e){
		out.println("Could not print result, reason : " + e.getMessage());
	}
}

%>
</body>
</html>
