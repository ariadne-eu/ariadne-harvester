<%@page import="java.util.Properties" %>
<%@page import="java.io.*" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@page import="java.net.URL"%>
<%@page import="java.net.URLConnection"%>
<%@page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="org.ariadne.config.PropertiesManager"%>
<%@page import="java.net.Authenticator"%>
<%@page import="java.net.PasswordAuthentication"%>
<%@page import="org.ariadne.util.ClientHttpRequest"%>
<%@page import="uiuc.oai.OAIRepository"%>

<%@page import="org.ariadne.oai.utils.HarvesterUtils"%>
<%@page import="org.ariadne.oai.utils.ReposProperties"%>
<%@page import="java.net.MalformedURLException"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

</head>
<body>
<% 
String exception = "";
		if (request.getParameter("next").compareTo("Delete from harvester")==0){
			out.println("<br/>Delete<br/>");
			ReposProperties reposProps = HarvesterUtils.getRegistryTarget(request.getParameter("registry_catalog"),request.getParameter("registry_entry"));
			HarvesterUtils.removeRepository(reposProps.getRepositoryIdentifierInteral());
			String list = PropertiesManager.getInstance().getProperty("AllTargets.list");
			out.println("<br/>Comparison<br/>");
		}else if (request.getParameter("next").compareTo("Add to harvester")==0){
			ReposProperties reposProps = new ReposProperties();
//			OAIRepository repository = new OAIRepository();
	//		repository.setBaseURL(request.getParameter("location"));
			reposProps.setBaseURL(request.getParameter("location"));
			reposProps.setAutoReset("true");
			reposProps.setMetadataPrefix(request.getParameter("metadata_prefix"));

			String sets="";
			for (int i=0;i<Integer.parseInt(request.getParameter("numberSets"));i++){
				if (request.getParameter("set"+i)!=null && !request.getParameter("set"+i).trim().equals("") ){
					if (sets.trim().equalsIgnoreCase("")) sets += request.getParameter("set"+i);
					else sets += ";"+request.getParameter("set"+i);
				}
			}
			reposProps.setHarvestingSet(sets);
			reposProps.setRegistryIdentifierEntry(request.getParameter("registry_entry"));
			reposProps.setRegistryIdentifierCatalog(request.getParameter("registry_catalog"));
			
			try{
				ReposProperties newReposProps = HarvesterUtils.addRepository(reposProps);
			} catch(Exception e){
				exception = "Could not add repository to the harvester. Reason : "+ e.getMessage() +".";
			}
			
			
			
		}else{
			ReposProperties reposProps = HarvesterUtils.getRegistryTarget(request.getParameter("registry_catalog"),request.getParameter("registry_entry"));
			if(reposProps != null){
				
				String sets="";
				for (int i=0;i<Integer.parseInt(request.getParameter("numberSets"));i++){
					if (request.getParameter("set"+i)!=null && !request.getParameter("set"+i).trim().equals("") ){
						if (sets.trim().equalsIgnoreCase("")) sets += request.getParameter("set"+i);
						else sets += ";"+request.getParameter("set"+i);
					}
				}
				reposProps.setHarvestingSet(sets);
				reposProps.setMetadataPrefix(request.getParameter("metadata_prefix"));
				
				HarvesterUtils.saveDetails(reposProps.getRepositoryIdentifierInteral(),reposProps);
			}
		}

		String query = request.getParameter("query");
		pageContext.forward("addOAITargetRegistry.jsp?query="+query);
%>
<input  type="hidden" value="<%=exception%>" name="exception"/>
</body>
</html>
