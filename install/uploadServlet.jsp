<%@page import="java.io.DataInputStream"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.File"%>
<%@page import="org.ariadne.config.PropertiesManager"%>
<%@page import="org.ariadne.oai.config.servlet.logging.Log4jInit"%>

<%@page import="java.util.Properties"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.IOException"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Enumeration"%>
<%@page import="org.ariadne.validation.utils.ValidationConstants"%>
<%@page import="org.ariadne.validation.Validator"%>
<%@page import="org.ariadne.oai.config.UploadServletHandler"%><html>

<head>
<title>Upload a configuration file</title>
<%
	UploadServletHandler.getInstance().handleUpload(request,application);
%>
<br>

<br />
</body>
</html>
