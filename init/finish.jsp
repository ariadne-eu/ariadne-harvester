<jsp:useBean id="oai" class="org.ariadne.oai.config.installation.beans.OaiParameters" scope="session"/>
<jsp:setProperty name="oai" property="*"/>
<%@ page language="java" import="java.io.*,org.ariadne.config.*,org.ariadne.oai.config.servlet.logging.Log4jInit" %>
<%
String error = "";
//out.println(application.getRealPath("WEB-INF") + File.separator + "oaicat.properties");
//PropertiesManager.getInstance().init(application.getRealPath("WEB-INF") + File.separator + "oaicat.properties");
//out.println(lucene.getLucenePath() + lucene.getIdentifier());


PropertiesManager.getInstance().saveProperty("Harvest.storeTo",oai.getDatabaseList());
if(oai.getDatabaseTypes().contains("harvestToSpi")){
	PropertiesManager.getInstance().saveProperty("harvestToSpi.URI",oai.getSpiTargetUrl());
	PropertiesManager.getInstance().saveProperty("harvestToSpi.Session.URI",oai.getSessionManagementUrl());
	PropertiesManager.getInstance().saveProperty("harvestToSpi.Session.username",oai.getUsername());
	PropertiesManager.getInstance().saveProperty("harvestToSpi.Session.password",oai.getPassword());
}

if(oai.getDatabaseTypes().contains("harvestToCenSoapSpi")){
	PropertiesManager.getInstance().saveProperty("harvestToCenSoapSpi.URI",oai.getSpiTargetUrl());
	PropertiesManager.getInstance().saveProperty("harvestToCenSoapSpi.Session.URI",oai.getSessionManagementUrl());
	PropertiesManager.getInstance().saveProperty("harvestToCenSoapSpi.Session.username",oai.getUsername());
	PropertiesManager.getInstance().saveProperty("harvestToCenSoapSpi.Session.password",oai.getPassword());
}

String storeDir = oai.getFileSystemDir();
if(!storeDir.equals("")){
	if(exists(storeDir)){
		if(storeDir.charAt(storeDir.length()-1) != File.separatorChar)
			{
			storeDir += File.separator;
			}
		PropertiesManager.getInstance().saveProperty("harvestToDisk.URI",storeDir);
	}
	else{
		error = "The Storing Directory("+ oai.getFileSystemDir() +") does not exist, please go to <a href=\"select.jsp\">the Storing System setup page</a> and fill in an existing directory.";

	}
}

if(exists(oai.getLogFilesDir())){
	PropertiesManager.getInstance().saveProperty("log.logDirectory",oai.getLogFilesDir());
}
else{
	error = "The logging directory("+ oai.getLogFilesDir() +") does not exist, please go to <a href=\"logging.jsp\">the log setup page</a> and fill in an existing directory.";
}

boolean addGlobal = Boolean.valueOf(request.getParameter("addGlobalLOIdentifier")).booleanValue();
oai.setAddGlobalLOIdentifier(addGlobal);
boolean addMeta = Boolean.valueOf(request.getParameter("addGlobalMetadataIdentifier")).booleanValue();
oai.setAddGlobalMetadataIdentifier(addMeta);
boolean validate = Boolean.valueOf(request.getParameter("validation")).booleanValue();
oai.setValidation(validate);

PropertiesManager.getInstance().saveProperty("Harvest.addGlobalLOIdentifier",String.valueOf(oai.getAddGlobalLOIdentifier()));
PropertiesManager.getInstance().saveProperty("Harvest.addGlobalMetadataIdentifier",String.valueOf(oai.getAddGlobalMetadataIdentifier()));
PropertiesManager.getInstance().saveProperty("Harvest.validation",String.valueOf(oai.getValidation()));
PropertiesManager.getInstance().saveProperty("Harvest.validation.scheme",oai.getValidationScheme());
PropertiesManager.getInstance().saveProperty("Harvest.metadataProvider.source",oai.getMetadataProviderSource());
PropertiesManager.getInstance().saveProperty("Harvest.metadataProvider.value",oai.getMetadataProviderValue());
PropertiesManager.getInstance().saveProperty("registry.url",request.getParameter("registryUrl"));

Log4jInit.reloadLive("default");

%>
<%!

boolean exists (String dir) {
	File testfile = new File(dir);
    if (testfile.exists() && testfile.isDirectory()) {
        return true;
    }
    else{
    	return false;
    }
}

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="org.ariadne.validation.exception.InitialisationException"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ariadne OAI Harvester - Web Installer</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

<link rel="stylesheet" href="css/install.css" type="text/css" />
<script type="text/javascript">
<!--


//-->
</script>
</head>
<body>

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
<div class="step-off">Connection</div>
<div class="step-off">Logging</div>
<div class="step-off">Options</div>
<div class="step-on">Finish</div>

</div>

<div id="right">

<div id="step">Finish</div>

<div class="far-right">

</div>
<div class="clr"></div>

<%
//(new Log4jInit()).reloadLogging();

if(error.equals("")){
	out.println("<h1>Installation Successfull</h1>");
	out.println("The configuration files have been created and the configuration details have been saved.<br>");
	out.println("Please go to <a href=../configuration/testConfiguration.jsp>Test Configuration</a> to fully test the current configuration.");	
	out.println("<div class=\"center\"><br/><br/><br/><br/><br/><br/><br/>");
	out.println("<input name=\"Button2\" type=\"submit\" class=\"button\" value=\"Home\" onclick=\"window.location='../start/index.jsp'\"/></div>");
}
else{
	out.println("<h1>Installation Failed</h1>");
	out.println(error);
}

	
	%>

<div class="clr"></div>
</div>
<div class="clr"></div>
</div>
<div class="clr"></div>


<div class="ctr">
Ariadne Foundation<br />
<a href="http://www.ariadne-eu.org/" target="_blank">ARIADNE</a> is an European Association open to the World, for Knowledge Sharing and Reuse.<br> The core of the ARIADNE infrastructure is a distributed network of learning repositories.
</div>

</body>
</html>