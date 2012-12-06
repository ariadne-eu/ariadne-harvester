<%@ page import="java.io.File" %>
<%@ page import="org.ariadne.config.PropertiesManager" %>
<%
try {
    PropertiesManager.getInstance().setPropertiesFile(application.getRealPath("/install")+File.separator+"ariadneV4.properties");
    if (PropertiesManager.getInstance().getPropertiesFile().exists()) 
    	{
    		PropertiesManager.getInstance().init();
    	}
    else {
        //out.println(application.getRealPath("/install")+File.separator+"ariadneV4.properties");
        response.sendRedirect( "../init/installation.jsp" );
    }
        } catch (Exception e) {
            e.printStackTrace();
        }
%>
<?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SILO Manager - Login</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
<style type="text/css">
@import "../free/css/admin_login.css";
</style>
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
           <div id="mambo"><img src="../free/images/SILO_Config.png" alt="Ariadne Logo" /></div>

    </div>
</div>
<div id="ctr" align="center">
	<div class="login">
		<div class="login-form">
			<img src="../free/images/login.gif" alt="Login" />
        	<form action="j_security_check" method="post" name="loginForm" id="loginForm">
			<div class="form-block">
	        	<div class="inputlabel">Username</div>

		    	<div><input name="j_username" type="text" class="inputbox" size="15" /></div>
	        	<div class="inputlabel">Password</div>
		    	<div><input name="j_password" type="password" class="inputbox" size="15" /></div>
	        	<div align="left"><input type="submit" name="submit" class="button" value="Login" /></div>
        	</div>
			</form>
    	</div>
		<div class="login-text">

			<div class="ctr"><img src="../free/images/security.png" width="64" height="64" alt="security" /></div>
        	<p>Welcome to AWS Manager</p>
			<p>Use a valid username and password to gain access to the administration console.</p>
    	</div>
		<div class="clr"></div>
	</div>
</div>
<div id="break"></div>
<noscript>

!Warning! Javascript must be enabled for proper operation of the Administrator
</noscript>
<div class="footer" align="center">
<div align="center">Ariadne Foundation<br />
<a href="http://www.ariadne-eu.org/" target="_blank">ARIADNE</a> is an European Association open to the World, for Knowledge Sharing and Reuse.<br> The core of the ARIADNE infrastructure is a distributed network of learning repositories.
</div>
</body>
</html>
