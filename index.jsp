<%@ page import="java.io.File" %>
<%@ page import="org.ariadne.config.PropertiesManager" %>
<%
try {
    //PropertiesManager.getInstance().setPropertiesFile(application.getRealPath("/install")+File.separator+"ariadneV4.properties");
	
    if (!PropertiesManager.getInstance().getProperty("log.logDirectory").equals("")) 
    	{
//    		PropertiesManager.getInstance().init();
    	}
    else response.sendRedirect( "init/index.jsp" );
        } catch (Exception e) {
            e.printStackTrace();
        }
%>
<?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ariadne Harvester - Login</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="css/admin_login.css" type="text/css" />

<script language="javascript" type="text/javascript">
	function setFocus() {
		document.loginForm.usrname.select();
		document.loginForm.usrname.focus();
	}
</script>
</head>
<body onload="setFocus();">
<div id="wrapper">
    <div id="header">
           <div id="mambo"><img src="images/Harvest_Config.png" alt="Mambo Logo" /></div>

    </div>
</div>
<div id="ctr" align="center">
	<div class="login">
		<div class="login-form">
			<img src="images/login.gif" alt="Login" />
        	<form action="start/AllOAITargets.jsp" method="post" name="loginForm" id="loginForm">
        	<!-- REPLACE ABOVE LINE WITH THIS TO ENABLE USER AUTHENTICATION form action="start/login/logIn.jsp" method="post" name="loginForm" id="loginForm"-->
			<div class="form-block">
	        	<div class="inputlabel">Username</div>

		    	<div><input name="userid" type="text" class="inputbox" size="15" /></div>
	        	<div class="inputlabel">Password</div>
		    	<div><input name="password" type="password" class="inputbox" size="15" /></div>
	        	<div align="left"><input type="submit" name="submit" class="button" value="Login" /></div>
        	</div>
			</form>
    	</div>
		<div class="login-text">

			<div class="ctr"><img src="images/security.png" width="64" height="64" alt="security" /></div>
        	<p>Welcome to Harvester Configuration</p>
			<p>Use a valid username and password to gain access to the administration console.</p>
    	</div>
		<div class="clr"></div>
	</div>
</div>
<div id="break"></div>
<noscript>

!Warning! Javascript must be enabled for proper operation of the Administrator
</noscript>
<div class="footer"  align="center">Ariadne Foundation<br />
<a href="http://www.ariadne-eu.org/" target="_blank">ARIADNE</a> is an European Association open to the World, for Knowledge Sharing and Reuse.<br> The core of the ARIADNE infrastructure is a distributed network of learning repositories.
<br><br><br><br>
<a href="http://ec.europa.eu/information_society/activities/econtentplus/index_en.htm" class="external text" title="http://ec.europa.eu/information_society/activities/econtentplus/index_en.htm" rel="nofollow">This project is co-funded by the <br/>European Union<br/> </a><br/><img alt="EU flag" src="http://www.eea.europa.eu/eu-flag.gif" height="40px"/>

</div>
</body>
</html>