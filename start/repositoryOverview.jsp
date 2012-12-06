<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="uiuc.oai.*" import="java.util.StringTokenizer" import="java.util.Vector"%>
<%@ page import="java.io.IOException"%>
<%@page import="org.ariadne.exception.IllegalArgException"%>
<%@page import="org.ariadne.oai.utils.HarvesterUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.ariadne.oai.OAIHarvester"%>

<%@page import="org.ariadne.oai.utils.ReposProperties"%>
<%@page import="java.net.URL"%>
<%@page import="java.net.URLConnection"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%><html>

<head>
<title>The Numbers</title>
<%!void printServices(JspWriter out, String title, HashMap<String,String> targets) throws IOException,OAIException {
out.println("<div align=\"center\">");
 out.println("<div width=\"500\"");
     out.println("<table align=\"center\">");
      out.println("<tr>");
       out.println("<th class=\"title\">Target Name</th>");
       out.println("<th class=\"title\">Number of records</th>");
      out.println("</tr>");

    for (String reposName : targets.keySet()) {
    	String reposUrl = targets.get(reposName);
    	
	try{
    out.println("<tr>");
    out.println("<td ><a href=\"" + reposUrl + "/api/sqitarget?query=lom.solr=%22all%22\">" + reposName + "</a></td>");
    out.println("<td align=\"right\">" + getCount(reposUrl + "/api/sqitarget?query=lom.solr=%22all%22") + "</a></td>");
    
     out.println("</td>");
    out.println("</tr>");
	}
	catch(IllegalArgException e){
		//out.println(e.getArgument());
		//NOOP
	}
    }

     out.println("</table>");
    out.println("</td>");
   out.println("</tr>");
  out.println("</table>");
  //out.println("<br/>Note : to check wether all repositories are available, please run \"Test Configuration\" under \"Configuration\" ");
  //out.println("</div>");
out.println("</div>");
    }

String getCount(String url){
	try{
		URL theUrl = new URL(url);
		URLConnection yc = theUrl.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null){
			if(inputLine.contains("<results cardinality=\"")){
				return inputLine.split("\"")[1];
			}
		}
		
		in.close();

	}catch(Exception e){
		
	}
	
	return "0";
}

%>

<br/>
<% 
HashMap<String,String> targets = new HashMap<String,String>(); 
targets.put("GLOBE","http://ariadne.cs.kuleuven.be/globe-ws");
targets.put("MACE","http://ariadne.cs.kuleuven.be/mace-ws");
targets.put("ARIADNE Members","http://ariadne.cs.kuleuven.be/ariadne-members");
targets.put("ARIADNE Partners","http://ariadne.cs.kuleuven.be/ariadne-partners");
targets.put("ICOPER","http://ariadne.cs.kuleuven.be/icoper-ws");
targets.put("ASPECT","http://ariadne.cs.kuleuven.be/aspect-ws");
//targets.put("GLOBE","http://ariadne.cs.kuleuven.be/globe-ws");


printServices(out, "Available Repositories", targets); %>

<br/>
    </body>
</html>
