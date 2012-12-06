<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="uiuc.oai.*" import="java.util.StringTokenizer" import="java.util.Vector"%>
<%@ page import="java.io.IOException"%>
<%@page import="org.ariadne.exception.IllegalArgException"%>
<%@page import="org.ariadne.oai.utils.HarvesterUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.ariadne.oai.OAIHarvester"%>

<%@page import="org.ariadne.oai.utils.ReposProperties"%><html>

<head>
<script type="text/javascript">
function confirm_delete(repos,name)
  {
  var r=confirm("Are you sure you want to delete repository " + name + " ?")
  if (r==true){
    document.getElementById("repos").value=repos;
    document.getElementById("param").value="remove";
    document.forms[0].submit();
    }
  else{
    }
  }
  
function set_harvest()
  {
  i = 0;
  var harvest =eval("document.forms[0].harvest"+ i);
  while(harvest != null){
    document.getElementById("repos").value= document.getElementById("repos").value + ";" + harvest.value;
    document.getElementById("param").value="set_harvesting";
	i = i + 1;
    harvest = eval("document.forms[0].harvest"+ i);
    }
   document.forms[0].submit();
  }

function clear_all_dates()
  {
  var r=confirm("Are you sure you want to reset the latest harvesting date of ALL repositories ?")
  if (r==true){
    document.getElementById("repos").value='ALL';
    document.getElementById("param").value="clear_date";
    document.forms[0].submit();
    }
  else{
    }
  }
    
function clear_date(repos,name)
  {
  var r=confirm("Are you sure you want to reset the latest harvesting date of repository " + name + " ?")
  if (r==true){
    document.getElementById("repos").value=repos;
    document.getElementById("param").value="clear_date";
    document.forms[0].submit();
    }
  else{
    }
  }
  
function view_details(reposInternalId)
  {
      document.getElementById("reposInternalId").value=reposInternalId;
              document.forms[1].submit();      
 }
 
function single_harvest(reposInternalId)
  {
      document.getElementById("reposInternalIdHarvest").value=reposInternalId;
              document.forms[2].submit();      
 } 
  
</script>
<title>View OAI Services</title>
<%@ include file="index.jsp" %>
<%
//    String action = request.getParameter("todo");
//    if (action != null){
//        if (action.equals("delete")); //UDDIConnector.deleteRepository(request.getParameter("key"));
//   }
        if (request.getParameter("param")!=null && request.getParameter("param").equals("remove")){
        	HarvesterUtils.removeRepository(request.getParameter("repos"));
        }
        if (request.getParameter("param")!=null && request.getParameter("param").equals("set_harvesting")){
        	HarvesterUtils.setHarvestingRepositories(request.getParameter("repos"));
        }
        if (request.getParameter("param")!=null && request.getParameter("param").equals("clear_date")){
        	HarvesterUtils.resetHarvestingDate(request.getParameter("repos"));
        }
%>
<%!void printServices(JspWriter out, String title, Vector<ReposProperties> repositories) throws IOException,OAIException {
out.println("<div align=\"center\">");
 out.println("<div class=\"main\">");
  out.println("<table border=\"0\" width=\"100%\">");
   out.println("<tr>");
    out.println("<td align=\"left\" valign=\"middle\">");
      out.println("<td align=\"left\" class=\"user\" valign=\"middle\" width=\"64\">");
       out.println("<img alt=\"result ok\" src=\"../images/browser.png\"/>");
      out.println("</td>");
      out.println("<td align=\"left\" class=\"sectionname\" valign=\"middle\">" + title +  "</td>");
     out.println("</table>");
     out.println("<table  class=\"adminlist\">");
      out.println("<tr>");
       out.println("<th class=\"title\"></th>");
       out.println("<th class=\"title\"></th>");
       out.println("<th class=\"title\"></th>");       
       out.println("<th class=\"title\">Target Name</th>");
       out.println("<th class=\"title\">Last Harvest Status</th>");
      // out.println("<th class=\"title\">Supported Metadata Formats</th>");
       out.println("<th class=\"title\">Latest Harvested Date</th>");
       out.println("<th class=\"title\"><a href=\"javascript:clear_all_dates();\">clear all</a></th>");
       out.println("<th class=\"title\">Active</th>");
      out.println("</tr>");

    for (int i = 0; i < repositories.size(); i++) {
    	ReposProperties oaiRepository = repositories.elementAt(i);
    	String reposInteralId = oaiRepository.getRepositoryIdentifierInteral();
    	String reposName = oaiRepository.getRepositoryName();
    	
	try{
		String latestHarvestedDatestamp = oaiRepository.getLatestHarvestedDatestamp();
		String active = oaiRepository.getActive();
//    	OAIMetadataFormatList formats = oaiRepository.listMetadataFormats();
//    	String supportedFormats = "";
//    	while(formats.moreItems()){
//    		OAIMetadataFormat format = formats.getCurrentItem();
//    		if(!supportedFormats.equals(""))supportedFormats += ",";
//    		supportedFormats += "<a href=\"" + format.getSchema() + "\">" + format.getMetadataPrefix() + "</a>";
//    		formats.moveNext();
//    	}
    out.println("<tr>");
    out.println("<td class=\"content\"><a href=\"javascript:confirm_delete('" + reposInteralId + "','" + reposName + "');\"><img src=\"../images/cancel_f2.png\" width=\"12\" height=\"12\" border=\"0\" alt=\"delete\"/></a></td>");
    out.println("<td class=\"content\"><input type=\"button\" onClick=\"javascript:single_harvest('" + reposInteralId + "');\" value=\"Harvest\"/></td>");
    out.println("<td class=\"content\"><input type=\"button\" onClick=\"javascript:view_details('" + reposInteralId + "');\" value=\"View Details\"/></td>");    
    out.println("<td class=\"content\"><a href=\"" + oaiRepository.getBaseURL() + "?verb=Identify\">" + reposName + "</a></td>");
     out.println("<td class=\"content\">" + OAIHarvester.harvestStatus.get(Integer.parseInt(oaiRepository.getStatusLastHarvest())) + "</td>");
    // out.println("<td class=\"content\">" + supportedFormats + "</td>");
     out.println("<td align=\"center\" class=\"content\">" + latestHarvestedDatestamp + "</td><td><a href=\"javascript:clear_date('" + reposInteralId + "','" + reposName + "');\">clear</a>" + "</td>");
     if(active.equalsIgnoreCase("Yes")){
         out.println("<td align=\"center\" class=\"content\"><select name=\"harvest"+i+"\"><option selected=\"selected\" value=\""+reposInteralId+";Yes\">Yes</option><option value=\""+reposInteralId+";No\">No</option></select></td>");
     }else{
         out.println("<td align=\"center\" class=\"content\"><select name=\"harvest"+i+"\"><option value=\""+reposInteralId+";Yes\">Yes</option><option selected value=\""+reposInteralId+";No\">No</option></select></td>");    	 
     }

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
out.println("<br/>Note : to check wether all repositories are available, please run <a href=\"../configuration/testConfiguration.jsp\">Test Configuration</a>");
  out.println("</div>");
out.println("</div>");
    }

%>

<br/>
<form method=POST action="AllOAITargets.jsp" >
<% printServices(out, "Validated Targets", HarvesterUtils.getReposProperties()); %>
<input type="hidden" id="repos" name="repos" value=""/><input type="hidden" id="param" name="param" value=""/>
<br>
<center><input type="button" onClick="set_harvest();" value="Submit"/></center>
</form>
<form method=POST action="viewDetails.jsp" >
<input type="hidden" id="reposInternalId" name="reposInternalId" value=""/>
</form>
<form method=POST action="DoSingleOAIHarvest.jsp" >
<input type="hidden" id="reposInternalIdHarvest" name="reposInternalIdHarvest" value=""/>
</form>

<br/>
    </body>
</html>
