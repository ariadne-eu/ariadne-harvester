<%@ page import="uiuc.oai.*"
	import="org.ariadne.config.PropertiesManager"
	import="java.util.StringTokenizer" import="java.util.Vector"%>
<%@ page import="java.io.IOException"%>
<%@page import="org.ariadne.oai.utils.HarvesterUtils"%>
<%@page import="java.util.HashMap"%>

<%@page import="org.ariadne.validation.utils.ValidationUtils"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.ariadne.oai.utils.ReposProperties"%><html>

<head>
<script type="text/javascript">
 
function submitDetails(reposInternalId)
  {
      document.getElementById("reposInternalId").value=reposInternalId;
      document.getElementById("param").value='submit_details';
	  document.forms[0].submit();
  }
  </script>
<title>View OAI-PMH target details</title>
<%@ include file="index.jsp"%>
<%
	if (request.getParameter("param")!=null){
	String param = request.getParameter("param");
	if(param.equals("submit_details")){
		ReposProperties reposProps = new ReposProperties();
		reposProps.setRepositoryName(request.getParameter("repositoryName"));
		reposProps.setRepositoryIdentifier(request.getParameter("repositoryIdentifier"));
		reposProps.setProviderName(request.getParameter("providerName"));
		reposProps.setLatestHarvestedDatestamp(request.getParameter("latestHarvestedDatestamp"));
		reposProps.setActive(request.getParameter("activeSelect"));
		reposProps.setMetadataPrefix(request.getParameter("metadataPrefix"));
		reposProps.setMetadataFormat(request.getParameter("metadataFormat"));
		reposProps.setHarvestingSet(request.getParameter("harvestingSet"));
		reposProps.setAutoReset(new Boolean(request.getParameter("autoResetSelect").equalsIgnoreCase("yes")));
		reposProps.setValidationUri(request.getParameter("validationScheme"));
		reposProps.setTransformationID(request.getParameter("transformID"));
		reposProps.setGranularity(request.getParameter("granularity"));
		HarvesterUtils.saveDetails(request.getParameter("reposInternalId"),reposProps);
	}
}
%>
<%!void printDetails(JspWriter out, ReposProperties reposProps) throws IOException {
	try{
		OAIRepository repos = new OAIRepository();
		repos.setBaseURL(reposProps.getBaseURL());

	out.println("<div align=\"center\">");
	 out.println("<div class=\"mainaddnew\">");
	  out.println("<table border=\"0\" width=\"98%\">");
	   out.println("<tr>");
	    out.println("<td align=\"left\" valign=\"middle\">");
	     out.println("<table>");
	      out.println("<td align=\"center\" class=\"user\" valign=\"middle\" width=\"64\">");
	       out.println("<img alt=\"result ok\" src=\"../images/browser.png\"/>");
	      out.println("</td>");
	      out.println("<td align=\"left\" class=\"sectionname\" valign=\"middle\">Repository details of "+reposProps.getRepositoryName()+"</td>");
	     out.println("</table>");
	     out.println("<table class=\"adminlist\">");
	     out.println("<tr><th colspan=\"2\">Fixed Values from Repository</th></tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Base URL</td>");
	       try{
	    	   out.println("<td class=\"quote\">" + repos.getBaseURL() + "</td>");
	       } catch(Exception e){
	    	   out.println("<td class=\"quote\"><p style=\"color:red\">Error fetching info</p></td>");
	       }
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Repository Name</td>");
	       try{
	    	   out.println("<td class=\"quote\">" + repos.getRepositoryName() + "</td>");
	       } catch(Exception e){
	    	   out.println("<td class=\"quote\"><p style=\"color:red\">Error fetching info</p></td>");
	       }
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Repository Identifier</td>");
	       try{
				out.println("<td class=\"quote\">" + repos.getRepositoryIdentifier() + "</td>");
			} catch(Exception e){
				out.println("<td class=\"quote\"><p style=\"color:red\">Error fetching info</p></td>");
		    }
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Earliest Datestamp</td>");
	       try{
				out.println("<td class=\"quote\">" + repos.getEarliestDatestamp() + "</td>");
			} catch(Exception e){
				out.println("<td class=\"quote\"><p style=\"color:red\">Error fetching info</p></td>");
		    }
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Admin Email</td>");
	       try{
				out.println("<td class=\"quote\">" + repos.getAdminEmail() + "</td>");
			} catch(Exception e){
				out.println("<td class=\"quote\"><p style=\"color:red\">Error fetching info</p></td>");
		    }
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Granularity</td>");
	       try{
				out.println("<td class=\"quote\">" + repos.getGranularity() + "</td>");
			} catch(Exception e){
				out.println("<td class=\"quote\"><p style=\"color:red\">Error fetching info</p></td>");
		    }
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Sample Identifier</td>");
	       try{
				out.println("<td class=\"quote\">" + repos.getSampleIdentifier() + "</td>");
			} catch(Exception e){
				out.println("<td class=\"quote\"><p style=\"color:red\">Error fetching info</p></td>");
		    }
	      out.println("</tr>");
		     out.println("<tr><th colspan=\"2\">Mutable parameters</th></tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Repository Name</td>");
	       out.println("<td class=\"quote\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"repositoryName\" value=\"" + reposProps.getRepositoryName() + "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Repository Identifier</td>");
	       out.println("<td class=\"quote\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"repositoryIdentifier\" value=\"" + reposProps.getRepositoryIdentifier() + "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Metadata Provider Name</td>");
	       out.println("<td class=\"quote\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"providerName\" value=\"" + reposProps.getProviderName() + "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Latest Harvested Datestamp</td>");
	       out.println("<td class=\"quote\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"latestHarvestedDatestamp\" value=\"" + reposProps.getLatestHarvestedDatestamp() + "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Harvesting Metadata Prefix</td>");
	       out.println("<td class=\"quote\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"metadataPrefix\" value=\"" + reposProps.getMetadataPrefix() + "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Harvesting Metadata Format</td>");
	       out.println("<td class=\"quote\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"metadataFormat\" value=\"" + reposProps.getMetadataFormat() + "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Saved granularity</td>");
	       out.println("<td class=\"quote\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"granularity\" value=\"" + reposProps.getGranularity() + "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Harvesting Set(s) (separator is \";\")</td>");
	       out.println("<td class=\"quote\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"harvestingSet\" value=\"" + reposProps.getHarvestingSet()+ "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Specific validation scheme</td>");
	       
			HashMap<String, String> schemes = ValidationUtils.getValidationSchemes();
			Iterator<String> schemesIter = schemes.values().iterator();
			String scheme = reposProps.getValidationUri();
			String options = "";
			if(scheme == null || scheme.equalsIgnoreCase("")){
				options += "<option  selected value=\"\">Use global validation scheme (default)</option>";
				options += "<option value=\"none\">Do not validate</option>";
			}else{
				options += "<option value=>Use global validation scheme (default)</option>";
				if(scheme.equalsIgnoreCase("none")){
					options += "<option selected value=\"none\">Do not validate</option>";
				}else{
					options += "<option value=\"none\">Do not validate</option>";
				}
			}

			while(schemesIter.hasNext()){
				String schemeURI = schemesIter.next();
				if(scheme != null && scheme.equals(schemeURI)){
					options += "<option selected value="+schemeURI+">"+schemeURI+"</option>";
				}
				else{
					options += "<option value="+schemeURI+">"+schemeURI+"</option>";
				}
				
			}
	       
			if(new Boolean(PropertiesManager.getInstance().getProperty("Harvest.validation")).booleanValue()){
				out.println("<td class=\"quote\"><select name=\"validationScheme\"> "+options+" </select></td>");
			}else{
	      	out.println("<td class=\"quote\"><select disabled name=\"validationScheme\"> "+options+" </select></td>");
			}
	      out.println("</tr>");
	      
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Specific transformation</td>");
	       
  			HashMap<String, String> transformationSchemes = HarvesterUtils.getPropertiesStartingWith("mapper.mapperClassName");
			Iterator<String> transformIter = transformationSchemes.values().iterator();
			String transformationID = reposProps.getTransformationID();
			String transformOptions = "";
			
			if(transformationID == null || transformationID.equalsIgnoreCase("")){
				transformOptions += "<option selected value=\"\">No Specific Transformation (default)</option>";
			}else{
				transformOptions += "<option value=\"\">No Specific Transformation (default)</option>";
		//		if(transformationID.equalsIgnoreCase("none")){
		//			transformOptions += "<option selected value=\"none\">Do not transform</option>";
		//		}else{
		//			transformOptions += "<option value=\"none\">Do not transform</option>";
		//		}
			}
			
			while(transformIter.hasNext()){
				String tranformID = transformIter.next();
				if(transformationID != null && transformationID.equals(tranformID)){
					transformOptions += "<option selected value="+tranformID+">"+tranformID+"</option>";
				}
				else{
					transformOptions += "<option value="+tranformID+">"+tranformID+"</option>";
				}
			}
				out.println("<td class=\"quote\"><select name=\"transformID\"> "+transformOptions+" </select></td>");
	      out.println("</tr>");
	      
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Auto Reset after harvesting</td>");
	       out.println("<td class=\"quote\">");
	      if(reposProps.getAutoReset()){
	          out.println("<select name=\"autoResetSelect\"><option selected value=\"Yes\">Yes</option><option value=\"No\">No</option></select></td>");
	      }else{
	          out.println("<select name=\"autoResetSelect\"><option value=\"Yes\">Yes</option><option selected value=\"No\">No</option></select></td>");    	 
	      }
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<td class=\"quote\">Harvesting is activated</td>");
	       out.println("<td class=\"quote\">");
	      if(reposProps.getActive().equalsIgnoreCase("Yes")){
	          out.println("<select name=\"activeSelect\"><option selected value=\"Yes\">Yes</option><option value=\"No\">No</option></select></td>");
	      }else{
	          out.println("<select name=\"activeSelect\"><option value=\"Yes\">Yes</option><option selected value=\"No\">No</option></select></td>");    	 
	      }
	      out.println("</tr>");

	     out.println("</table>");
	    out.println("</td>");
	   out.println("</tr>");
	  out.println("</table>");
	 out.println("</div>");
	out.println("</div>");
	}
	catch(OAIException e){
		printFailedDetails(out,e,reposProps);
	}
}

	void printFailedDetails(JspWriter out, OAIException e, ReposProperties reposProps)
			throws IOException {
		out.println("<div align=\"center\">");
		out.println("<div class=\"mainaddnew\">");
		out.println("<table border=\"0\" width=\"98%\">");
		out.println("<tr>");
		out.println("<td align=\"left\" valign=\"middle\">");
		out.println("<table>");
		out.println("<td align=\"center\" class=\"user\" valign=\"middle\" width=\"64\">");
		out.println("<img alt=\"result ok\" src=\"../images/browser_nok.png\"/>");
		out.println("</td>");
		out.println("<td align=\"left\" class=\"sectionname\" valign=\"middle\">details failed</td>");
		out.println("</table>");
		out.println("<table class=\"adminlist\">");
		out.println("<tr>");
		out.println("<th class=\"title\">Target URL</th>");
		out.println("<td class=\"content\">" + reposProps.getBaseURL() + "</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<th class=\"title\">Error</th>");
		out.println("<td class=\"content\">" + e.getMessage() + "</td>");
		out.println("</tr>");

		out.println("</table>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("</div>");
		out.println("</div>");
	}
%>

<br />
<form name="changeDetails" method=POST action="viewDetails.jsp">
<%
String reposInternalId = request.getParameter("reposInternalId");
printDetails(out,HarvesterUtils.getReposProperties(reposInternalId));
%> <input type="hidden" id="reposInternalId" name="reposInternalId"
	value="" /> <input type="hidden" id="param" name="param" value="" />
<br>
<center><input type="button" value="Go Back"
	onclick="window.location='AllOAITargets.jsp';" />&nbsp;&nbsp;&nbsp;&nbsp;<input
	type="button" onClick="submitDetails('<%=reposInternalId%>');"
	value="Submit" />
</form>
&nbsp;&nbsp;&nbsp;&nbsp;
</center>
<br />
</body>
</html>
