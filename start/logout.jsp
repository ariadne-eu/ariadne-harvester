<%--
  Created by IntelliJ IDEA.
  User: stefaan
  Date: Sep 29, 2005
  Time: 3:01:10 PM
  To change this template use File | Settings | File Templates.
--%>

<%
    session.invalidate();
    response.sendRedirect( "../start/index.jsp" );
%>
