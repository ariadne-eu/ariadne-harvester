<jsp:useBean id="oai" class="org.ariadne.oai.config.installation.beans.OaiParameters" scope="session"/>

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
	var f = document.form;
	var path = f.fileSystemDir.value;
	if ( path == '') {
		alert('Please enter a directory to store the metadata to');
		f.fileSystemDir.focus();
		return false;
	}

	return true;
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

<h1>What directory should be used on the File System</h1>

<%
String dir = oai.getFileSystemDir();
if(dir.equals("")){
	dir = PropertiesManager.getInstance().getProperty("harvestToDisk.URI");
	if(dir == null || dir.equals("")){
		dir = application.getRealPath("mds")+ File.separator;
	}
}
%>

<div class="install-text">
    			<p>To use the File System to store the metadata you have to provide the following information:</p>
  	   			<p>* The targetted directory on the File System</p>
       			<p><font color=FF0000><b>
                </b></font></p>
  			</div>
			<div class="install-form">
  	   			<div class="form-block">
  	     			<table class="content2">
  		  			<tr>
  		    			<td>Storing Directory Location :<br/><input class="inputbox" style="width:100%;" type="text" class="inputboxadd" name="fileSystemDir" value="<%=dir%>"/></td>
  		    		</tr>
  		    		<tr>
  		    			<td><br>Default location :<br/><%=application.getRealPath("mds")+ File.separator%></td>
  		    		</tr>

  		  			<!--tr>	 	
			  		    <td><br><em>DON'T use "\", use "/" instead. <br>Windows : C:/MetadataStore/ <br>Unix : /home/user/MetadataStore</em></td>
			  		</tr-->
		  	     	</table>
  				</div>
			</div>

<div class="clr"></div>
</div>
<div class="clr"></div>
</div>
<div class="clr"></div>

<input type="hidden" id="previous" name="previous" value="harvestToDisk"/>

<div class="ctr">
Ariadne Foundation<br />
<a href="http://www.ariadne-eu.org/" target="_blank">ARIADNE</a> is an European Association open to the World, for Knowledge Sharing and Reuse.<br> The core of the ARIADNE infrastructure is a distributed network of learning repositories.
</div>
</form>
</body>
</html>