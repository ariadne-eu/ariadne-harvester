<jsp:useBean id="oai" class="org.ariadne.oai.config.installation.beans.OaiParameters" scope="session"/>
<jsp:setProperty name="oai" property="*"/> 

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.ariadne.config.PropertiesManager"%>

<%@page import="java.util.Hashtable"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="java.util.TreeSet"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ariadne OAI Harvester - Web Installer</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" href="css/install.css" type="text/css" />

</head>
<body>

<%
	if (request.getParameter("storeName")!=null){
		String storeName = "harvestTo" + request.getParameter("storeName");
		String propertyNames = request.getParameter("propertyNames");
		String className = request.getParameter("className");
		PropertiesManager.getInstance().saveProperty(storeName + ".writerClassName",className);
		StringTokenizer tokenizer = new StringTokenizer(propertyNames,";");
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			PropertiesManager.getInstance().saveProperty(storeName + "." + token,"");
		}
	}
%>

<FORM METHOD=POST ACTION="customConnection.jsp">
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

<div id="step">Custom Store selection</div>

<div class="far-right">
<input name="Button2" type="submit" class="button" value="Next >>">
</div>

<%
Hashtable list = PropertiesManager.getInstance().getPropertyStartingWith("harvestTo");
Set<String> storeStrings = (Set<String>)list.keySet();
Set<String> stores = new HashSet<String>();
for(String storeString : storeStrings){
	String store = storeString.split("\\.")[0];
	if(!store.equals("harvestToDisk") && !store.equals("harvestToSpi")){
		stores.add(store);
	}
}
%>

<div class="clr"></div>

<h1>Where to save the harvested metadata ?</h1>

<div class="install-text">
    			<p>Select the Custom Store where you want to save the harvested metadata</p>
  	   			<p></p>
     			<p><font color=FF0000><b>
                </b></font></p>
  			</div>
			<div class="install-form">
    			<div class="form-block">
  							<fieldset><legend>Storing System</legend>
								<table cellpadding="1" cellspacing="1" border="0">
<%

String storesString = PropertiesManager.getInstance().getProperty("Harvest.storeTo");
TreeSet<String> set = new TreeSet<String>();
StringTokenizer tokenizer = new StringTokenizer(storesString,";");
while (tokenizer.hasMoreTokens()){
	String token = tokenizer.nextToken();
	set.add(token);
}

for(String store : stores){
	out.println("<tr>");
	if(set.contains(store)){
		out.println("<td><input type=\"radio\" id=\""+store+"\" name=\"customStoreType\" value=\""+store+"\" checked=\"checked\" /></td>");
	}
	else{
		out.println("<td><input type=\"radio\" id=\""+store+"\" name=\"customStoreType\" value=\""+store+"\" /></td>");
	}
	out.println("<td><label for=\""+store+"\">"+store+"</label></td>");
	out.println("</tr>");
}

%>
                            		<!-- tr>
										<td><input type="radio" id="Disk" name="databaseType" value="harvestToDisk" checked="checked" /></td>
										<td><label for="Disk">File System</label></td>
									</tr-->


								</table>

							</fieldset>
						<br>
						<input type="button" onclick="window.location='addCustomStore.jsp';" value="Add a Custom Store"/>
    			</div>
    		</div>
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