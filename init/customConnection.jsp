<jsp:useBean id="oai" class="org.ariadne.oai.config.installation.beans.OaiParameters" scope="session"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.ariadne.config.PropertiesManager"%>

<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.Set"%><html xmlns="http://www.w3.org/1999/xhtml">
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
	var path = f.spiTargetUrl.value;
	if ( path == '') {
		alert('Please enter a valid URL in the SPI Target Service Location field');
		f.spiTargetUrl.focus();
		return false;
	}
	//else if ( confirm('The system will now attempt to connect to the database.  If there is an error the message will appear in red on the screen, if everything is sucessful, you will go to the next step.  Click Ok to continue, Cancel to review the properties.')) {
	if ( f.sessionManagementUrl.value == '') {
		alert('Please enter a valid URL in the Session Management Service Location field');
		f.sessionManagementUrl.focus();
		return false;
	}
		if ( f.username.value == '') {
		alert('Please enter a username');
		f.username.focus();
		return false;
	}
		if ( f.password.value == '') {
		alert('Please enter a password');
		f.password.focus();
		return false;
	}

	return formValid;
}
</script>
</head>
<body>
<FORM METHOD=POST name=form id=form ACTION="saveCustomStoreConnection.jsp">
<div id="wrapper">
<div id="header">
<div id="mambo"><img src="images/Harvest_Config.png" alt="Harvester Installation" /></div>
</div>

</div>

<div id="ctr" align="center">
<div class="install">
<div id="stepbar">
<div class="step-off">Pre-installation</div>
<div class="step-off">Store selection</div>
<div class="step-on">Connection</div>
<div class="step-off">Logging</div>
<div class="step-off">Options</div>
<div class="step-off">Finish</div>

</div>

<div id="right">

<div id="step">Connection Information</div>

<div class="far-right">
<input name="Button2" type="submit" class="button" value="Next >>" />
</div>
<div class="clr"></div>

<h1>Please fill out the details of the custom store :</h1>

<div class="install-text">
    			<p>To install the Custom Store you have to provide the following information:</p>

       			<p><font color=FF0000><b>
                </b></font></p>
  			</div>
			<div class="install-form">
  	   			<div class="form-block">
  	     			<table class="content2">
  	     			
<%
String customStoreType = request.getParameter("customStoreType");
Hashtable<String,String> properties = PropertiesManager.getInstance().getPropertyStartingWith(customStoreType);
Set<String> props= properties.keySet();
for(String propName : props){
	String shortName = propName.split("\\.",2)[1];
	out.println("<tr><td>"+shortName+"<br/><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" class=\"inputboxadd\" name=\""+propName+"\" value=\""+PropertiesManager.getInstance().getProperty(propName)+"\"/></td></tr>");	
}
out.println("<input type=\"hidden\" id=\"customStoreType\" name=\"customStoreType\" value=\""+ customStoreType +"\" />");
%>  	     			
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