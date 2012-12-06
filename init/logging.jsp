<jsp:useBean id="oai" class="org.ariadne.oai.config.installation.beans.OaiParameters" scope="session"/>
<jsp:setProperty name="oai" property="*"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.ariadne.config.PropertiesManager"%>
<%@page import="java.io.File"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ariadne OAI Harvester - Web Installer</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" href="css/install.css" type="text/css" />

<script type="text/javascript">

function check()
{
	// form validation check
	var formValid=false;
	var f = document.form;
	var path = f.logFilesDir.value;
	if ( path == '') {
		alert('Please enter a valid path');
		f.logFilesDir.focus();
		formValid=false;
	}
	else{
		formValid=true;
	}

	return formValid;
}

</script>
</head>
<body>
<FORM METHOD=POST name=form id=form ACTION="options.jsp" onsubmit="return check();">
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
<div class="step-off">Connection</div>
<div class="step-on">Logging</div>
<div class="step-off">Options</div>
<div class="step-off">Finish</div>

</div>

<div id="right">

<div id="step">Logging Information</div>

<div class="far-right">
<input name="Button2" type="submit" class="button" value="Next >>" />
</div>
<div class="clr"></div>

<h1>Where to put the log files?</h1>

<%
String logsdir = oai.getLogFilesDir();
if(logsdir.equals("")){
	logsdir = PropertiesManager.getInstance().getProperty("log.logDirectory");
	if(logsdir == null || logsdir.equals("")){
		logsdir = application.getRealPath("log")+ File.separator;
	}
}
%>

<div class="install-text">
  	   			<p>Enter the directory where the logs should be saved.</p>
     			<p><font color=FF0000><b>
                </b></font></p>
  			</div>
			<div class="install-form">
  	   			<div class="form-block">
  	     			<table class="content2">
  		  			<tr>
  		    			<td>Log directory location<br/><input class="inputbox" style="width:100%;" type="text" name="logFilesDir" value="<%=logsdir%>"/></td>
  		    		</tr>  		    		
			  		<tr>
  		    			<td><br>Default location :<br/><%=application.getRealPath("log")+ File.separator%></td>
  		    		</tr>
			  		<!--tr>	 	
			  		    <td><br><em>DON'T use "\", use "/" instead. <br>Windows : C:/LogFiles/ <br>Unix : /home/user/LogFiles/</em></td>
			  		</tr-->
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