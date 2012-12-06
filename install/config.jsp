<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page session="true" %>
<%@ page import="org.ariadne.config.*" %>

<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>

<html>
  <head>
      <title>Change the Configuration</title>
<%
      pageContext.include("../start/index.jsp");
%>
  </head>
  <body>
<div class="page">
    <center>

<form ACTION="config.jsp" METHOD=post name="conf" id="conf">
<INPUT TYPE=hidden NAME="usrname" VALUE="<%=request.getParameter("usrname") %>">
<INPUT TYPE=hidden NAME="password" VALUE="<%=request.getParameter("password") %>">

<%
    
    Iterator it = null;

	it = PropertiesManager.getInstance().getTypes().iterator();
         %>
 Location of config file: <%= PropertiesManager.getInstance().getPropFile()%>
 
<br> <br>
 	<input TYPE="submit" NAME="Request" VALUE="Submit ">
   <input TYPE="reset" NAME="Clear" VALUE="Clear"><br><br>

	<table width="90%" style="max-width:800px; min-width:600px;">
 <%
 	
    while ((it !=null)&&(it.hasNext())){
	String fam = (String) it.next();
	%>

		<tr>

							<th colspan="2"><%= fam%></th>
						</tr>
		
	<%
	  Iterator iter = PropertiesManager.getInstance().getPropertyStartingWith(fam).entrySet().iterator();
	  while (iter.hasNext()) {
		Map.Entry entry = (Map.Entry) iter.next();
		if (request.getParameter((String) entry.getKey())!= null) {
			PropertiesManager.getInstance().saveProperty((String) entry.getKey(), request.getParameter((String) entry.getKey()));
		}
		%>
	  	<tr>
    	  <td  class="quote"><label><%= entry.getKey().toString().split("\\.",2)[1]%></label></td>
    	  <%
    	  	String value = PropertiesManager.getInstance().getProperty((String) entry.getKey());
    	  	value = value.replaceAll("\"","&quot;");
    	  %>    	  
    	  <td width="90%" class="quote"><input TYPE="text" NAME="<%= entry.getKey()%>" class="inputbox" value="<%= value%>" style="width:100%; padding:0; margin:0;"></td>
		</tr>
		<%
	  }
	}
	%>
  </table>
		<br>

	<input TYPE="submit" NAME="Request" VALUE="Submit ">
   <input TYPE="reset" NAME="Clear" VALUE="Clear"><br><br>
   <a href="../configuration/testConfiguration.jsp">validate configuration</a><br>
   <a href="../start">home</a><br>

</form>
      </center>
    </div>
<%
    pageContext.include("/layout/footer.jsp");
%>
  </body>
</html>