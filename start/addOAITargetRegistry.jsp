<%@ page import="be.cenorm.www.SqiTargetStub" %>
<%@ page import="be.cenorm.www.SqiSessionManagementStub" %>
<%@ page import="be.cenorm.www.*" %>
<%@ page import="org.xml.sax.InputSource" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
<%@ page import="org.apache.xpath.XPathAPI" %>
<%@ page import="org.ariadne.config.PropertiesManager" %>
<%@ page import="net.sf.vcard4j.parser.DomParser" %>
<%@ page import="net.sf.vcard4j.java.VCard" %>
<%@ page import="net.sf.vcard4j.java.AddressBook" %>
<%@ page import="net.sf.vcard4j.java.type.FN" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.*" %>
<%@ page import="org.apache.xerces.dom.DocumentImpl" %>
<%@ page import="net.sf.vcard4j.java.type.N" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.rmi.RemoteException" %>
<%@page import="org.ariadne.config.PropertiesManager"%>
<%@page import="java.util.*"%>
<%@page import="java.net.Authenticator"%>
<%@page import="java.net.PasswordAuthentication"%>
<%@page import="java.net.URL"%>
<%@page import="java.net.URLConnection"%>
<%@page import="org.jdom.input.SAXBuilder"%>
<%@page import="org.jdom.Namespace"%>
<%@ page import="org.ariadne_eu.utils.registry.*"%>




<%! static SqiSessionManagementStub sqiSessionStub;
static SqiTargetStub sqiStub;
String result;
String result_protocol;
String query;
String query_protocol;
String sessionId;
static GetTotalResultsCountResponse countResponse = null;
static SynchronousQueryResponse synchronousQueryResponse = null;
String error = "";%>

<%  

//if (request.getParameter("exception")!=null && !request.getParameter("exception").trim().equals("")){
	%><!--script type="text/javascript">alert("<%//request.getParameter("exception")%>");</script--><%
//}

	String format, language;
	int resultSize, startResult;
	String query_temp="";
	try{
        if (request.getParameter("query") != null){
        	query_temp = request.getParameter("query");
        	query = query_temp+" and metadatacollection.target.targetdescription.protocolidentifier.entry=\"oai-pmh-v2\"";
        }
        else{ 
                query = null;
                countResponse = null;
                synchronousQueryResponse = null;
        }
	}catch(Exception e){
	        query = null;
	        countResponse = null;
	        synchronousQueryResponse = null; 
	}
	resultSize = 10;
    startResult = 1;    
    
    try {
        if (request.getParameter("next") != null)
            startResult = Integer.parseInt(request.getParameter("start_result")) + resultSize;
    } catch (Exception e) {
    }
    
    try {
        if (request.getParameter("previous") != null)
            startResult = Integer.parseInt(request.getParameter("start_result")) - resultSize;
    } catch (Exception e) {
    }
    
    String registry_url = PropertiesManager.getInstance().getProperty("registry.url")+"services";

    if (query != null && query.length() > 0) {
        try {
        	sessionId = createAnonymousSession(registry_url +  "/SqiSessionManagement");
			sqiStub = new SqiTargetStub(registry_url + "/SqiTarget");
			language = "plql1";
			setQueryLanguage(sessionId, language);
			setResultSetSize(sessionId, resultSize);
			format = "lom";
			setResultSetFormat(sessionId, format);

			result = query(sessionId, query, startResult);
			
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            e.printStackTrace(writer);
            error = "Error : " + e.getClass().getName() + " " + e.getMessage();
        }
    }
%>

<%@page import="org.ariadne.oai.utils.HarvesterUtils"%>
<%@page import="org.ariadne.oai.utils.ReposProperties"%><html>
  <head>
      <link media="all" href="<%=request.getContextPath()%>/css/style.css" type="text/css" rel="stylesheet">
      <title>Search page</title>
<%@ include file="index.jsp" %>
<div class="page">
  <center>
      <form action="addOAITargetRegistry.jsp">
        <table align="center">
            <tr>
                <td>
                    <div class="box">
                        <div>

                              <table>
                                  <tr>
                                      <td><p>Enter search query:</p></td>
                                  </tr>
                                  <tr>
                                      <td><input type="text" name="query" value="<%=query_temp != null ? StringEscapeUtils.escapeHtml(query_temp) : ""%>" /></td>
                                  </tr>
                                  <tr>
                                      <td><input type="submit" name="search" value="search" /></td>
                                  </tr>                                  
                                  <tr>
                                      <td align="center"><A href="addOAITargetRegistry.jsp?query=http&search=search">Show all targets</A></td>
                                  </tr>
                              </table>
                            <p class="last">&nbsp;</p>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
      </form>
  </center>

<%
    if (error.trim().length() > 0) {
%><center>
               
                        <!-- p><%=error %></p>
                        <p><%=query %></p-->

  </center>
<%}
    if (synchronousQueryResponse != null) {
%><center><%
        int nbResults = countResults(sessionId, query);
        if (nbResults > 0)
        {
%>

<div class="container" style="width:85%;">
    <div>
        <center>
            <p>Showing results <%=startResult%> to <%=Math.min(resultSize + startResult - 1, countResponse.getGetTotalResultsCountReturn())%> of <%=countResponse.getGetTotalResultsCountReturn()%>.</p>
            <form action="addOAITargetRegistry.jsp">
                <input type="hidden" name="start_result" value="<%=startResult%>" />
                <input type="hidden" name="query" value="<%=query != null ? StringEscapeUtils.escapeHtml(query) : ""%>" />
                <%if(startResult>1){ %>
                <input type="submit" name="previous" value="<< previous" />
                <%}
                if (startResult<nbResults-resultSize){%>
                <input type="submit" name="next" value="next >>" />                
                <%}%>
            </form>
        </center>

        <table class="searchResults" cellpadding="0" cellspacing="0">
<%
	Results results = new Results();
	results.parseXMLResults(result);
	List<MetadataCollection> list = results.getResults();
    int currentResultCounter = 0;
    for (int i = 0; i < list.size(); i++)
    {
    	MetadataCollection metadataCollection = list.get(i);
%>

            <tr class="searchResultsRow<%=(currentResultCounter%2==1) ? "Odd" : "Even"%>">
                <td>
                    <b>Identifier: </b><%=metadataCollection.getIdentifier().getEntry()%>
                </td><td/>                 
            </tr>
	<%
	    if (metadataCollection.getDescription().getString() != null && metadataCollection.getDescription().getString().length()>0)
	    {
	%>
	            <tr class="searchResultsRow<%=(currentResultCounter%2==1) ? "Odd" : "Even"%>">
	                <td colspan="2" class="searchResultsDescription"><b>Description:</b><br /><%=metadataCollection.getDescription().getString()%></td>
	            </tr>
	<%
	    }
		List<TargetDescription> targets = metadataCollection.getTarget();
		for (int node=0;node<targets.size();node++){
			TargetDescription targetDescription = targets.get(node);
			if (targetDescription.getProtocolIdentifier().getEntry().compareTo("oai-pmh-v2")==0){
				ReposProperties repoProps = HarvesterUtils.getRegistryTarget(targetDescription.getIdentifier().getCatalog(),targetDescription.getIdentifier().getEntry());
				if (targetDescription.getIdentifier().getEntry() != null && targetDescription.getIdentifier().getEntry().length()>0)
				{				
%>
				<tr class="searchResultsRow<%=(currentResultCounter%2==1) ? "Odd" : "Even"%>">
					<td colspan="1" class="searchResultsDescription" align="right"><b>Target <%=node%>:</b>
				</td><td/></tr>
				<tr class="searchResultsRow<%=(currentResultCounter%2==1) ? "Odd" : "Even"%>">
					<td colspan="1"/><td colspan="1" class="searchResultsDescription"><form action="handleNewOAITargetRegistry.jsp"><b>Entry: </b><%=targetDescription.getIdentifier().getEntry()%>				
<%				}
				if (targetDescription.getIdentifier().getCatalog() != null && targetDescription.getIdentifier().getCatalog().length()>0)
				{				
%>					
					<br/><b>Catalog :</b> <%=targetDescription.getIdentifier().getCatalog()%>				
<%				}
				if (targetDescription.getLocation() != null && targetDescription.getLocation().length()>0)
				{				
%>					
					<br/><b>Location :</b> <a href="<%=targetDescription.getLocation()%>"><%=targetDescription.getLocation()%></a>				
<%				}
				if ((targetDescription.getProtocolIdentifier().getEntry() != null && targetDescription.getProtocolIdentifier().getEntry().length()>0) && (targetDescription.getProtocolIdentifier().getCatalog() != null && targetDescription.getProtocolIdentifier().getCatalog().length()>0))
				{				
					try{
						query_protocol = "(protocol.identifier.entry = \""+targetDescription.getProtocolIdentifier().getEntry()+"\") and (protocol.identifier.catalog= \""+targetDescription.getProtocolIdentifier().getCatalog()+"\")";	

						sqiStub = new SqiTargetStub(registry_url + "/SqiTarget");
						int startResult_protocol=1;
						language = "plql1";
						setQueryLanguage(sessionId, language);
						setResultSetSize(sessionId, resultSize);
						format = "lom";
						setResultSetFormat(sessionId, format);
						
						result_protocol = query(sessionId, query_protocol, startResult_protocol);
						targetDescription.parseXMLProtocol(result_protocol);
						if (targetDescription.getProtocol().getName() != null && targetDescription.getProtocol().getName().length()>0)
						{%>					
							<br/><b>Protocol Name :</b> <%=targetDescription.getProtocol().getName()%>				
<%						}									
						if (targetDescription.getProtocol().getProtocolDescriptionBindingNamespace() != null && targetDescription.getProtocol().getProtocolDescriptionBindingNamespace().length()>0)
						{%>					
							<br/><b>Protocol Description Binding Name Space :</b> <%=targetDescription.getProtocol().getProtocolDescriptionBindingNamespace()%>	
	<%					}										
						if (targetDescription.getProtocol().getProtocolDescriptionBindingLocation() != null && targetDescription.getProtocol().getProtocolDescriptionBindingLocation().length()>0)
						{%>					
							<br/><b>Protocol Description Binding Location :</b> <%=targetDescription.getProtocol().getProtocolDescriptionBindingLocation()%>	
	<%					}
					    } catch (Exception e) {out.println(e);}			            
				}
				try{
				for (int node_prefix=0;node_prefix<targetDescription.getProtocolImplementationDescription().getOaiPmh().getMetadataFormats().size();node_prefix++){
					String oai_prefix = targetDescription.getProtocolImplementationDescription().getOaiPmh().getMetadataFormats().get(node_prefix).getMetadataPrefix();
					%><br/><b>Metadata prefix :</b> <input  type="radio" value="<%=oai_prefix%>" name="metadata_prefix" <%
					String prefix = null;
					if(repoProps != null)prefix = repoProps.getMetadataPrefix();
					if (prefix!=null){
						if ((prefix.compareTo(oai_prefix)==0)||(node_prefix==0)) out.println("checked");
						}else{
							if (node_prefix==0) out.println("checked");
						}
						%>/><%=oai_prefix%>	<% 
				}
				%><input  type="hidden" value="<%=targetDescription.getProtocolImplementationDescription().getOaiPmh().getSets().size()%>" name="numberSets"/>
				<input  type="hidden" value="<%=targetDescription.getProtocolImplementationDescription().getOaiPmh().getGranularity()%>" name="granularity"/>
				<input  type="hidden" value="<%=targetDescription.getProtocolImplementationDescription().getOaiPmh().getEarliestDateStamp()%>" name="earliestDateStamp"/>
				<input  type="hidden" value="<%=targetDescription.getLocation()%>" name="location"/>
				<input  type="hidden" value="<%=targetDescription.getIdentifier().getEntry()%>" name="target_id"/>
				<input  type="hidden" value="<%=targetDescription.getIdentifier().getEntry()%>" name="registry_entry"/>
				<input  type="hidden" value="<%=targetDescription.getIdentifier().getCatalog()%>" name="registry_catalog"/>
				<input type="hidden" name="query" value="<%=query != null ? StringEscapeUtils.escapeHtml(query) : ""%>" />
					<% 
				for (int node_set=0;node_set<targetDescription.getProtocolImplementationDescription().getOaiPmh().getSets().size();node_set++){
					String oai_set = targetDescription.getProtocolImplementationDescription().getOaiPmh().getSets().get(node_set);
					%><br/><b>Sets :</b><input  type="checkbox" value="<%=oai_set%>" name="set<%=node_set%>" 
						<%String listSets = null;
						  if(repoProps != null)listSets = repoProps.getHarvestingSet(); 
						  if (listSets!=null){
							  if ((listSets.lastIndexOf(";"+oai_set)>0)||(listSets.lastIndexOf(oai_set+";")>=0)) out.println("checked");
							  else if (listSets.compareTo(oai_set)==0) out.println("checked");
						  }
						  %>/> 
					<%=oai_set%>	<% 
				}		
				if (HarvesterUtils.getRegistryTarget(targetDescription.getIdentifier().getCatalog(),targetDescription.getIdentifier().getEntry())!=null){
						out.println("<br/><input type=\"submit\" name=\"next\" value=\"Update harvester\" />");
						out.println("<input type=\"submit\" name=\"next\" value=\"Delete from harvester\" /></form></form></td></tr>");
				}else{
				    	out.println("<br/><input type=\"submit\" name=\"next\" value=\"Add to harvester\" /></form></form></td></tr>");
				}}catch(Exception e){out.println(e);}
			}
       } 
            currentResultCounter++;
    }
%>
        </table>
        </div>
    </div>
<%
        }
        else
        {
%>
<div class="container">
    <div>
        <center>
            <p>Nothing found</p>
        </center>
    </div>
</div>
      </center>
<%
        }
	closeSession(sessionId);
    }
%>
</body></html>
<%!
 
    //Copy of functions from SqiTest
    
    public static String createAnonymousSession(String target){
		try {
			sqiSessionStub = new SqiSessionManagementStub(target);
			CreateAnonymousSession createASession = new CreateAnonymousSession();
			CreateAnonymousSessionResponse sessionResponse = sqiSessionStub.createAnonymousSession(createASession);
			String sessionId = sessionResponse.getCreateAnonymousSessionReturn();
			return sessionId;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		
	}
	
	public static void setQueryLanguage(String sessionId, String language) throws RemoteException, _SQIFaultException {
		SetQueryLanguage queryLanguage = new SetQueryLanguage();
		queryLanguage.setQueryLanguageID(language);
		queryLanguage.setTargetSessionID(sessionId);
		sqiStub.setQueryLanguage(queryLanguage);

	}
	
	public static void setResultSetSize(String sessionId, int resultSize) throws RemoteException, _SQIFaultException {
		SetResultsSetSize resultsSetSize = new SetResultsSetSize();
		resultsSetSize.setResultsSetSize(resultSize);
		resultsSetSize.setTargetSessionID(sessionId);
		sqiStub.setResultsSetSize(resultsSetSize);
	}
	
	public static void setResultSetFormat(String sessionId, String format) throws RemoteException, _SQIFaultException {
		SetResultsFormat resultsSetFormat = new SetResultsFormat();
		resultsSetFormat.setResultsFormat(format);
		resultsSetFormat.setTargetSessionID(sessionId);
		sqiStub.setResultsFormat(resultsSetFormat);
	}
	
	public static String query(String sessionId, String query, int startResult) throws RemoteException, _SQIFaultException {
		SynchronousQuery syncQuery = new SynchronousQuery();
		syncQuery.setQueryStatement(query);
		syncQuery.setStartResult(startResult);
		syncQuery.setTargetSessionID(sessionId);
		
		synchronousQueryResponse = sqiStub.synchronousQuery(syncQuery);
		String synchronousQueryReturn = synchronousQueryResponse.getSynchronousQueryReturn();
		return synchronousQueryReturn;
		
	}
	
	public static int countResults(String sessionId, String query) throws RemoteException, _SQIFaultException {
		GetTotalResultsCount getTotalResultsCount = new GetTotalResultsCount();
		getTotalResultsCount.setQueryStatement(query);
		getTotalResultsCount.setTargetSessionID(sessionId);
		countResponse = sqiStub.getTotalResultsCount(getTotalResultsCount);
		return countResponse.getGetTotalResultsCountReturn();
	}
	
	public static void closeSession(String sessionId){
	       if(!sessionId.equals("")) {
	           DestroySession destroySession = new DestroySession();
	           destroySession.setSessionID(sessionId);
	           try {
	               sqiSessionStub.destroySession(destroySession);
	           } catch (RemoteException e) {
	               e.printStackTrace();
	           } catch (_SQIFaultException e) {
	               e.printStackTrace();
	           }
	           finally {
	               sessionId = "";
	           }
	       }
	   }
%>