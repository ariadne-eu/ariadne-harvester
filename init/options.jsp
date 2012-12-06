<jsp:useBean id="oai" class="org.ariadne.oai.config.installation.beans.OaiParameters" scope="session"/>
<jsp:setProperty name="oai" property="*"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.ariadne.config.PropertiesManager"%>

<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.ariadne.validation.utils.ValidationUtils"%>
<%@page import="org.ariadne.validation.Validator"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ariadne OAI Harvester - Web Installer</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" href="css/install.css" type="text/css" />

<script type="text/javascript">

function check()
{
	// form validation check
	var formValid=true;
	var f = document.form;
	var path = f.metadataProviderSource.value;
	if ( path == '') {
		alert('Please enter a source');
		f.metadataProviderSource.focus();
		formValid=false;
	}
	if ( f.metadataProviderValue.value == '') {
		alert('Please enter a value');
		f.metadataProviderValue.focus();
		formValid=false;
	}

	return formValid;
}

function Disab() {
	frm=document.forms[0]
	if(frm.validation.checked)
	{frm.validationScheme.disabled=false}
	else {frm.validationScheme.disabled=true}
	}

</script>
</head>
<body>
<FORM METHOD=POST name=form id=form ACTION="finish.jsp">
<div id="wrapper">
<div id="header">
<div id="mambo"><img src="images/Harvest_Config.png" alt="Harvester Installation"></div>
</div>

</div>

<div id="ctr" align="center">
<div class="install">
<div id="stepbar">
<div class="step-off">Pre-installation</div>
<div class="step-off">Store selection</div>
<div class="step-off">Connection</div>
<div class="step-off">Logging</div>
<div class="step-on">Options</div>
<div class="step-off">Finish</div>

</div>

<div id="right">

<div id="step">Options</div>

<div class="far-right">
<input name="Button2" type="submit" class="button" value="Next >>" />
</div>
<div class="clr"></div>

<h1>Please choose the options matching your criteria</h1>

<%
boolean addGlobal = oai.getAddGlobalLOIdentifier();
	
boolean addMeta = oai.getAddGlobalMetadataIdentifier();

boolean validate = oai.getValidation();

String scheme = oai.getValidationScheme();
if(scheme.equals("")){
	scheme = PropertiesManager.getInstance().getProperty("Harvest.validation.scheme");
}
String source = oai.getMetadataProviderSource();
if(source.equals("")){
	source = PropertiesManager.getInstance().getProperty("Harvest.metadataProvider.source");
}
String value = oai.getMetadataProviderValue();
if(value.equals("")){
	value = PropertiesManager.getInstance().getProperty("Harvest.metadataProvider.value");
}

String registryUrl = oai.getRegistryUrl();
if(registryUrl.equals("")){
	registryUrl = PropertiesManager.getInstance().getProperty("registry.url");
}

%>

<div class="install-text">
     			<p><font color=FF0000><b>
                </b></font></p>
  			</div>
			<div class="install-form">
  	   			<div class="form-block">
  	     			<table class="content2">
  		  			<tr>
  		    			<td>Do you want to add a Global LO Identifier ? <input type="checkbox" name="addGlobalLOIdentifier" value="true" <%if(addGlobal)out.print("checked=\"true\"");%>/></td>
  		    		</tr>
  		  			<tr>
  		    			<td>Do you want to add a Global Metadata Identifier ? <input type="checkbox" name="addGlobalMetadataIdentifier" value="true" <%if(addMeta)out.print("checked=\"true\"");%>/></td>
  		    		</tr>
  		  			<tr>
  		    			<td>Do you want to validate the metadata ? <input type="checkbox" name="validation" value="true" <%if(validate)out.print("checked=\"true\"");%> onClick="Disab();"/></td>
  		    		</tr>
  		    		<tr>	 	
			  		    <td><br></td>
			  		</tr>
		  			<tr>
  		    			<td>What is the URI of the validation scheme ?<br>
					<select name="validationScheme" <%if(!validate)out.print("disabled=\"true\"");%> style="width:100%;">
		<%
		Validator.updatePropertiesFileFromRemote();
		HashMap<String, String> schemes = ValidationUtils.getValidationSchemes();
		Iterator<String> schemesIter = schemes.values().iterator();
		if(scheme == null || scheme.equalsIgnoreCase("")){
			out.println("<option selected value=\"none\">None</option>");
		}else{
			out.println("<option value=\"none\">None</option>");
		}
		while(schemesIter.hasNext()){
			String schemeURI = schemesIter.next();
			if(scheme != null && scheme.equals(schemeURI)){
				out.println("<option selected value="+schemeURI+">"+schemeURI+"</option>");
			}
			else{
				out.println("<option value="+schemeURI+">"+schemeURI+"</option>");
			}
			
		}
		%>
		</select>
					</td>
  		    		</tr>
  		  			<tr>
  		    			<td>What is the source of the metadata provider field ?<br><input class="inputbox" style="width:100%;" type="text" name="metadataProviderSource" value="<%=source%>"/></td>
  		    		</tr>
  		  			<tr>	 	
			  		    <td><em>for eg. MELTv1.0 </em><br></td>
			  		</tr>
			  		<tr>
  		    			<td>What is the value of the metadata provider field ?<br><input class="inputbox" style="width:100%;" type="text" name="metadataProviderValue" value="<%=value%>"/></td>
  		    		</tr>
  		  			<tr>	 	
			  		    <td><em>for eg. provider </em><br></td>
			  		</tr>
			  		<tr>
  		    			<td>Do you want to add targets using a Registry?<br><input class="inputbox" style="width:100%;" type="text" name="registryUrl" value="<%=registryUrl%>"/></td>
  		    		</tr>
  		    		<tr>	 	
			  		    <td><em>for eg. http://ariadne.cs.kuleuven.be/ariadne-registry/</em><br></td>
			  		</tr>
		  	     	</table>
  				</div>
			</div>

<div class="clr"></div>
</div>
<div class="clr"></div>
</div>
<div class="clr"></div>


<div class="ctr">
Ariadne Foundation<br />
<a href="http://www.ariadne-eu.org/" target="_blank">ARIADNE</a> is an European Association open to the World, for Knowledge Sharing and Reuse.<br> The core of the ARIADNE infrastructure is a distributed network of learning repositories.
</div>
</form>
</body>
</html>