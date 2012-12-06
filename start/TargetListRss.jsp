<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="uiuc.oai.*" import="java.util.StringTokenizer" import="java.util.Vector"%>
<%@ page import="java.io.IOException"%>
<%@page import="org.ariadne.exception.IllegalArgException"%>
<%@page import="org.ariadne.oai.utils.HarvesterUtils"%><%@page import="java.util.HashMap"%><%@page import="org.ariadne.oai.OAIHarvester"%>

<%@page import="org.ariadne.oai.utils.ReposProperties"%><rss version="2.0">
  <channel>
    <title>MELT harvester</title> 
    <link>http://localhost:1381/melt-oaiharvester/start/AllOAITargets.jsp</link> 
    <description>This webfeed notifies the status of the Melt harvester.</description><%
	      response.setContentType("text/xml");
    Vector<ReposProperties> repositories= HarvesterUtils.getReposProperties();


    for (ReposProperties repo : repositories) {
    	
	try{
		String latestHarvestedDatestamp = repo.getLatestHarvestedDatestamp();
		String active = repo.getActive();
    out.println("");
%>   <item>

     <title><%=repo.getRepositoryName()%></title>
     <link><%=repo.getBaseURL()+"?verb=Identify"%></link>
     <description><%=OAIHarvester.harvestStatus.get(Integer.parseInt(repo.getStatusLastHarvest()))%></description><%
//    out.println(reposIdentName);
//    out.println(HarvesterUtils.getReposName(reposIdentName));
//    out.println(OAIHarvester.harvestStatus.get(Integer.parseInt(oaiRepository.get("statusLastHarvest"))));
//    out.println(oaiRepository.get("baseURL"));
//    out.println(latestHarvestedDatestamp);
%>   </item>
<%
	}catch(IllegalArgException e){
		out.println(e.getArgument());
		
	}
    }

%> </channel>
</rss>



