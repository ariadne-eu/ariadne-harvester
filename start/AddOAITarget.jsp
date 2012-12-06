
<%@page import="java.util.HashMap"%>
<%@page import="org.ariadne.validation.utils.ValidationUtils"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.ariadne.oai.utils.HarvesterUtils"%><html>
    
<head>
    <title>Add a new OAI Target</title>
	<%@ include file="index.jsp" %>

    <br/>

<div align="center">
    <div class="mainaddnew">
        <form method="post" action="handleNewOAITarget.jsp" >
            <table border="0" cellspacing="1" cellpadding="1" width="98%">
                <tr>
                    <td align="center" class="sectionname" colspan="2">Add a New Target</td>
                </tr>
                <tr><td>
                    <table width="100%">
                            <tr>
                                <td width="100%" valign="top">
                                    <table class="adminform" align="center">
                                        <tr>
                                            <th colspan="3">
                                               Mandatory parameters
                                            </th>
                                        </tr>
                                        <tr>
                                        	<td align="center" class="quote">OAI Target URL</td>
                                        	<td class="quote" width="40%"><input style="width:100%;" type="text" class="inputboxadd" name="targetURL" value=""></td>
                                        	<td align="center" class="quote">The base URL of the OAI target</td>
                                        </tr>
                                        <tr>
                                        	<td align="center" class="quote">Metadata Prefix</td>
                                        	<td class="quote" width="40%"><input style="width:100%;" type="text" class="inputboxadd" name="metadataPrefix" value="oai_lom"></td>
                                        	<td align="center" class="quote">The metadata prefix you want to harvest from</td>
                                        </tr>
                                        <tr>
                                        	<td align="center" class="quote">Metadata Format</td>
                                        	<td class="quote" width="40%"><input style="width:100%;" type="text" class="inputboxadd" name="metadataFormat" value="LOM"></td>
                                        	<td align="center" class="quote">The metadata format of the harvested records</td>
                                        </tr>                                                                                                             
                                        <tr>
                                        	<td align="center" class="quote">Auto Reset</td>
                                        	<td class="quote" width="40%"><select style="width:100%;" name="autoReset"><option selected value="true">Yes</option><option value="false">No</option></select></td>
                                        	<td align="center" class="quote">Resets harvested date after every harvest if true</td>
                                        </tr>
                                        <tr>
                                            <th colspan="3">
                                               Optional parameters
                                            </th>
                                        </tr>
                                        <tr>
                                        	<td align="center" class="quote">Metadata Provider</td>
                                        	<td class="quote" width="40%"><input style="width:100%;" type="text" class="inputboxadd" name="providerName" value=""></td>
                                        	<td align="center" class="quote">Fill in for adding the provider as a contributor</td>
                                        </tr>
                                        <tr>
                                        	<td align="center" class="quote">Harvesting Set</td>
                                        	<td class="quote" width="40%"><input style="width:100%;" type="text" class="inputboxadd" name="harvestingSet" value=""></td>
                                        	<td align="center" class="quote">Use ";" as separator</td>
                                        </tr>
                                        <tr>
                                        	<td align="center" class="quote">Repository Identifier</td>
                                        	<td class="quote" width="40%"><input style="width:100%;" type="text" class="inputboxadd" name="manualRepositoryIdentifier" value=""></td>
                                        	<td align="center" class="quote">Enter a repositoryIdentifier if wanted</td>
                                        </tr>
                                        <tr>
                                        	<td align="center" class="quote">Repository Name</td>
                                        	<td class="quote" width="40%"><input style="width:100%;" type="text" class="inputboxadd" name="customRepositoryname" value=""></td>
                                        	<td align="center" class="quote">Enter a custom name here</td>
                                        </tr>
                                        
                                       <%
                           			HashMap<String, String> schemes = ValidationUtils.getValidationSchemes();
                        			Iterator<String> schemesIter = schemes.values().iterator();
                        			String options = "";
                        				options += "<option selected value=\"\">Use global validation scheme (default)</option>";
                        				options += "<option value=\"none\">Do not validate</option>";
                        			while(schemesIter.hasNext()){
                        				String schemeURI = schemesIter.next();
                        				options += "<option value="+schemeURI+">"+schemeURI+"</option>";
                        			}
                        	            %>
                                        
										<tr>
                                        	<td align="center" class="quote">Validation URI</td>
                                        	<td class="quote" width="40%"><select style="width:100%;" name="validationUri"><%=options%></select></td>
                                        	<td align="center" class="quote">Choose here for target specific validation</td>
                                        </tr>
                                        
                                    <%
                           			HashMap<String, String> transformationSchemes = HarvesterUtils.getPropertiesStartingWith("mapper.mapperClassName");
                        			Iterator<String> transformIter = transformationSchemes.values().iterator();
                        			String transformOptions = "";
                        			transformOptions += "<option selected value=\"\">No specific transformation (default)</option>";
                        			while(transformIter.hasNext()){
                        				String transformID = transformIter.next();
                        				transformOptions += "<option value="+transformID+">"+transformID+"</option>";
                        			}
                        	            %>
                                        
										<tr>
                                        	<td align="center" class="quote">Transformation ID</td>
                                        	<td class="quote" width="40%"><select style="width:100%;" name="transformID"><%=transformOptions%></select></td>
                                        	<td align="center" class="quote">Choose here for target specific transformation</td>
                                        </tr>
                                    </table>
                                    <br><br>
                                </td>
                            </tr>
                            <tr>
                                <td align="center" class="content" colspan="2">
                                    <center><input type="button"
                                                                          onClick="document.forms[0].submit();"
                                                                          value="Submit">
                                    </center>
                                </td>
                            </tr>
                        </table>
                </td></tr>
            </table>
        </form>
    </div>
</div>
</body>
</html>