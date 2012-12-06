<%@page import="java.io.File"%>
<%@page import="org.ariadne.util.IOUtils"%>
<%@page import="java.io.FileInputStream"%>
<html>

<head>
<title>Download the configuration file</title>
<%@ include file="index.jsp" %>
<%
String propsFile = application.getRealPath("install")+File.separator+"ariadneV4.properties";
FileInputStream fis = new FileInputStream(new File(propsFile));
if (fis !=null) {
//    response.setContentType(mimeType);
    response.setHeader("Content-Disposition","attachment;filename=\"ariadneV4.properties\"");
    IOUtils.copyInto(fis,response.getOutputStream());
}

%>

<br/>
    </body>
</html>
