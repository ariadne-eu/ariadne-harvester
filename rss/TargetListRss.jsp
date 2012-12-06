<?xml version="1.0" encoding="UTF-8" ?>
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page import="uiuc.oai.*" import="java.util.StringTokenizer" import="java.util.Vector"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.net.URLEncoder"%>

<%@page import="org.ariadne.exception.IllegalArgException"%>
<%@page import="org.ariadne.oai.utils.HarvesterUtils"%><%@page import="java.util.HashMap"%><%@page import="org.ariadne.oai.OAIHarvester"%>

<%@page import="org.ariadne.oai.utils.ReposProperties"%><rss version="2.0">
  <channel>
    <title><%=request.getContextPath().replaceAll("/","").toUpperCase()%></title>
    <link><%=request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()%>/start/AllOAITargets.jsp</link>
 <ttl>1</ttl>   
<description>This webfeed notifies the status of the harvester.</description><%
              response.setContentType("text/xml");
    Vector<ReposProperties> repositories= HarvesterUtils.getReposProperties();


    for (ReposProperties repo : repositories) {

        try{
                String latestHarvestedDatestamp = repo.getLatestHarvestedDatestamp();
                String active = repo.getActive();
		String validationUrl = "http://ariadne.cs.kuleuven.be/aspect-zipped-logs/" + URLEncoder.encode(repo.getRepositoryName(),"UTF-8").replaceAll("\\+","%20");
    out.println("");
%>   <item>

     <title><%=repo.getRepositoryName()%></title>
     <link><%=repo.getBaseURL()+"?verb=Identify"%></link>
	<pubDate><%=latestHarvestedDatestamp%></pubDate>
     <description><%
	String test = OAIHarvester.harvestStatus.get(Integer.parseInt(repo.getStatusLastHarvest()));
	if (test.equalsIgnoreCase("OK")) {%>Validation Succesful.<%
	} else if (test.equalsIgnoreCase("Validation Errors Present")) { %><%=test%>. Check here: <%=validationUrl%><%
 	} else {%><%=test%>.<%
	}%></description></item><%
        }catch(IllegalArgException e){
                out.println(e.getArgument());

        }
    }

%></channel>
</rss>


