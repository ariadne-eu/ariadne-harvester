<jsp:useBean id="oai" class="org.ariadne.oai.config.installation.beans.OaiParameters" scope="session"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.ariadne.config.PropertiesManager"%>
<html xmlns="http://www.w3.org/1999/xhtml">
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
<FORM METHOD=POST name=form id=form ACTION="select.jsp" onsubmit="return check();">
<input type="hidden" name="database" value="database">
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

<h1>How to connect to the CEN Soap SPI Target?</h1>

<%
String spiUrl = oai.getCenSoapSpiTargetUrl();
if(spiUrl.equals("")){
	spiUrl = PropertiesManager.getInstance().getProperty("harvestToCenSoapSpi.URI");
}
String sessionManagementUrl = oai.getCenSoapSessionManagementUrl();
if(sessionManagementUrl.equals("")){
	sessionManagementUrl = PropertiesManager.getInstance().getProperty("harvestToCenSoapSpi.Session.URI");
}
String username = oai.getCenSoapUsername();
if(username.equals("")){
	username = PropertiesManager.getInstance().getProperty("harvestToCenSoapSpi.Session.username");
}
String password = oai.getCenSoapPassword();
if(password.equals("")){
	password = PropertiesManager.getInstance().getProperty("harvestToCenSoapSpi.Session.password");
}
%>

<div class="install-text">
    			<p>To install the SPI target you have to provide the following information:</p>
  	   			<p>* The URL of the SPI target service</p>
  	   			<p>* The URL of the Session Management service</p>
  	   			<p>* The correct credentials to create a session</p>
       			<p><font color=FF0000><b>
                </b></font></p>
  			</div>
			<div class="install-form">
  	   			<div class="form-block">
  	     			<table class="content2">
  		  			<tr>
  		    			<td>SPI Target Service Location :<br/><input class="inputbox" style="width:100%;" type="text" class="inputboxadd" name="cenSoapSpiTargetUrl" value="<%=spiUrl%>"/></td>
  		    		</tr>
  		  			<tr>	 	
			  		    <td><em>for eg. http://localhost:8080/AriadneRepository/services/SPI </em></td>
			  		</tr>
			  		<tr>
  		    			<td><br></td>
  		    		</tr>
  		  			<tr>
  		    			<td>Session Management Service Location :<br/><input class="inputbox" style="width:100%;" type="text" class="inputboxadd" name="cenSoapSessionManagementUrl" value="<%=sessionManagementUrl%>"/></td>
  		    		</tr>
  		  			<tr>	 	
			  		    <td><em>for eg. http://localhost:8080/AriadneRepository/services/SqiSessionManagement </em></td>
			  		</tr>
  		  			<tr>
  		    			<td><br></td>
  		    		</tr>
  		  			<tr>
  		    			<td>Username :<br/><input class="inputbox" type="text" class="inputboxadd" name="cenSoapUsername" value="<%=username%>"/></td>
  		    		</tr>
  		  			<tr>	 	
			  		    <td><br></td>
			  		</tr>
  		  			<tr>
  		    			<td>Password :<br/><input class="inputbox" type="text" class="inputboxadd" name="cenSoapPassword" value="<%=password%>"/></td>
  		    		</tr>
		  	     	</table>
  				</div>
			</div>

<div class="clr"></div>
</div>
<div class="clr"></div>
</div>
<div class="clr"></div>

<input type="hidden" id="previous" name="previous" value="harvestToCenSoapSpi"/>

<div class="ctr">
Ariadne Foundation<br />
<a href="http://www.ariadne-eu.org/" target="_blank">ARIADNE</a> is an European Association open to the World, for Knowledge Sharing and Reuse.<br> The core of the ARIADNE infrastructure is a distributed network of learning repositories.
</div>
</form>
</body>
</html>