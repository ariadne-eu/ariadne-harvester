
<%@page import="java.util.Hashtable"%>
<%@page import="org.ariadne.config.PropertiesManager"%>
<%@page import="java.util.Set"%><jsp:useBean id="oai" class="org.ariadne.oai.config.installation.beans.OaiParameters" scope="session"/>
<jsp:setProperty name="oai" property="*"/> 
<%
//String path = request.getContextPath();
//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

//out.println("DB type : " + "connection"+ oai.getDatabaseType() +".jsp" );
String customStoreType = request.getParameter("customStoreType");
Hashtable<String,String> properties = PropertiesManager.getInstance().getPropertyStartingWith(customStoreType);
oai.getDatabaseTypes().remove("selectCustomStore");
oai.getDatabaseTypes().add(customStoreType);
Set<String> props= properties.keySet();
for(String propName : props){
//	out.println(propName);
	//String shortName = propName.split("\\.",1)[1];
	PropertiesManager.getInstance().saveProperty(propName,request.getParameter(propName));
}

pageContext.forward( "select.jsp" );

%>
