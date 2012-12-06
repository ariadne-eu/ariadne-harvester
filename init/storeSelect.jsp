<jsp:useBean id="oai" class="org.ariadne.oai.config.installation.beans.OaiParameters" scope="session"/>
<jsp:setProperty name="oai" property="*"/> 

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.ariadne.config.PropertiesManager"%>

<%@page import="java.util.TreeSet"%>
<%@page import="java.util.StringTokenizer"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ariadne OAI Harvester - Web Installer</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" href="css/install.css" type="text/css" />
</head>
<body>
<FORM METHOD=POST ACTION="select.jsp">
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

<div id="step">Store selection</div>

<div class="far-right">
<input name="Button2" type="submit" class="button" value="Next >>">
</div>
<div class="clr"></div>

<h1>Where to save the harvested metadata ?</h1>

<%
String stores = PropertiesManager.getInstance().getProperty("Harvest.storeTo");
TreeSet<String> set = new TreeSet<String>();
boolean custom = false;
StringTokenizer tokenizer = new StringTokenizer(stores,";");
while (tokenizer.hasMoreTokens()){
	String token = tokenizer.nextToken();
	set.add(token);
	if(!token.equals("harvestToDisk") && !token.equals("harvestToSpi") && !token.equals("harvestToCenSoapSpi")){
		custom = true;
	}
}
%>

<div class="install-text">
    			<p>Select the store where you want to save the harvested metadata</p>
  	   			<p></p>
     			<p><font color=FF0000><b>
                </b></font></p>
  			</div>
			<div class="install-form">
    			<div class="form-block">
  							<fieldset><legend>Storing System</legend>
								<table cellpadding="1" cellspacing="1" border="0">

                            		<tr>
										<td><input type="checkbox" id="databaseDisk" name="databaseDisk" value="harvestToDisk" <%if(set.contains("harvestToDisk"))out.println("checked");%></td>
										<td><label for="Disk">File System</label></td>
									</tr>
									<!--tr>
                                		<td><input type="radio" id="Lucene" name="databaseType" value="Lucene" /></td>
										<td><label for="Lucene">Lucene Index</label></td>

									</tr-->
									<tr>
                                		<td><input type="checkbox" id="databaseSPI" name="databaseSPI" value="harvestToSpi" <%if(set.contains("harvestToSpi"))out.println("checked");%> /></td>
										<td><label for="SPI">ProLearn SPI Target</label></td>

									</tr>
									<tr>
                                		<td><input type="checkbox" id="databaseCenSoapSPI" name="databaseCenSoapSPI" value="harvestToCenSoapSpi" <%if(set.contains("harvestToCenSoapSpi"))out.println("checked");%> /></td>
										<td><label for="CenSoapSPI">CEN Soap SPI Target</label></td>

									</tr>
									<tr>
                                		<td><input type="checkbox" id="databaseCustom" name="databaseCustom" value="selectCustomStore" <%if(custom)out.println("checked");%>/></td>
										<td><label for="Lucene">Custom store</label></td>

									</tr>
								</table>
							</fieldset>



    			</div>
    		</div>
</div>

<div class="clr"></div>
</div>

<div class="clr"></div>

<input type="hidden" id="previous" name="previous" value="storeSelect"/>

<div class="ctr">
Ariadne Foundation<br />
<a href="http://www.ariadne-eu.org/" target="_blank">ARIADNE</a> is an European Association open to the World, for Knowledge Sharing and Reuse.<br> The core of the ARIADNE infrastructure is a distributed network of learning repositories.
</div>
</form>
</body>
</html>