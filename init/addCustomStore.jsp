<jsp:useBean id="oai" class="org.ariadne.oai.config.installation.beans.OaiParameters" scope="session"/>
<jsp:setProperty name="oai" property="*"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.ariadne.config.PropertiesManager"%>

<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.ariadne.validation.utils.ValidationUtils"%><html xmlns="http://www.w3.org/1999/xhtml">
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

</script>
</head>
<body>
<FORM METHOD=POST name=form id=form ACTION="selectCustomStoreConnection.jsp">
<div id="wrapper">
<div id="header">
<div id="mambo"><img src="images/Harvest_Config.png" alt="Harvester Installation" /></div>
</div>

</div>

<div id="ctr" align="center">
<div class="install">
<div id="stepbar">
<div class="step-off">Pre-installation</div>
<div class="step-on">Store selection</div>
<div class="step-off">Connection</div>
<div class="step-off">Logging</div>
<div class="step-off">Options</div>
<div class="step-off">Finish</div>

</div>

<div id="right">

<div id="step">Add Custom Store properties</div>

<div class="far-right">
<input name="Button2" type="submit" class="button" value="Next >>" />
</div>
<div class="clr"></div>

<h1>Please enter the details of the new custom store</h1>

<div class="install-text">
     			<p><font color=FF0000><b>
                </b></font></p>
  			</div>
			<div class="install-form">
  	   			<div class="form-block">
  	     			<table class="content2">
  		  			<tr>
  		    			<td>Choose a Name for the custom store<br/><input class="inputbox" style="width:100%;" type="text" name="storeName"/></td>
  		    		</tr>
  		  			<tr>	 	
			  		    <td><em>for eg. Oracle</em><br></td>
			  		</tr>
			  		<tr>
  		    			<td>Enter the full classname of the custom store<br/><input class="inputbox" style="width:100%;" type="text" name="className"/></td>
  		    		</tr>
  		  			<tr>	 	
			  		    <td><em>for eg. org.ariadne.oai.harvestWriter.FileWriter</em><br></td>
			  		</tr>
			  		<tr>
  		    			<td>Give an enumeration of the names of the additional property (separated by ";")<br/><input class="inputbox" style="width:100%;" type="text" name="propertyNames"/></td>
  		    		</tr>
  		  			<tr>	 	
			  		    <td><em>for eg. URI;username;password</em><br></td>
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