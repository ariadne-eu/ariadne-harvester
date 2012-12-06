
<%@page import="java.util.Vector"%>
<%@page import="java.util.SortedSet"%>
<%@page import="java.util.TreeSet"%><jsp:useBean id="oai" class="org.ariadne.oai.config.installation.beans.OaiParameters" scope="session"/>
<jsp:setProperty name="oai" property="*"/> 
<%
//String path = request.getContextPath();
//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

//out.println("DB type : " + "connection"+ oai.getDatabaseType() +".jsp" );
String forward = "";
if (request.getParameter("previous")!=null){
	String previous = request.getParameter("previous");
	TreeSet<String> databaseType = oai.getDatabaseTypes();
	if(previous.equals("storeSelect")){
		databaseType = new TreeSet<String>();
		String database = request.getParameter("databaseDisk");
		if(database != null)databaseType.add(database);
		database = request.getParameter("databaseSPI");
		if(database != null)databaseType.add(database);
		database = request.getParameter("databaseCenSoapSPI");
		if(database != null)databaseType.add(database);
		database = request.getParameter("databaseCustom");
		if(database != null)databaseType.add(database);
		oai.setDatabaseTypes(databaseType);
		if(databaseType.size() > 0){
			forward = databaseType.first();
			pageContext.forward( forward +"Connection.jsp" );			
		}else{
			pageContext.forward( "logging.jsp" );
		}
	}
	else{
		SortedSet<String> tailSet = databaseType.tailSet(previous);
		if(tailSet.size() > 1){
			forward = (String)tailSet.toArray()[1];
			pageContext.forward( forward +"Connection.jsp" );
		}
		else{
			pageContext.forward( "logging.jsp" );
		}
	}
}
else{
	pageContext.forward( "logging.jsp" );
}
%>