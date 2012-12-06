<%@ page language="java"
	import="java.util.HashMap, java.io.IOException"%>
<%@page import="org.ariadne.util.OaiUtils"%>
<%@page import="java.util.Calendar"%>
<%@page import="org.quartz.SimpleTrigger"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="org.ariadne.oai.config.servlet.cron.InitCronServlet"%>
<%@page import="org.quartz.Scheduler"%>
<%@page import="org.quartz.ObjectAlreadyExistsException"%>
<%@page import="org.quartz.UnableToInterruptJobException"%>
<html>

<head>
<script type="text/javascript">
function confirm_abort()
  {
  var r=confirm("Are you sure you want to abort harvesting ?")
  if (r==true){
    document.getElementById("param").value="stop";
    document.forms[0].submit();
    }
  else{
    }
  }
</script>
<title>Abort Harvesting</title>
<%@ include file="index.jsp"%>
<form method=POST action="stopHarvesting.jsp" >
<input type="hidden" id="param" name="param" value="start"/>
</form>
<%
if (request.getParameter("param")!=null && request.getParameter("param").equals("stop")){
	Scheduler sched = InitCronServlet.schedFact.getScheduler();
	try{
		//SimpleTrigger trigger = new SimpleTrigger("ManualHarvesting", "ManualHarvestingJob","Harvesting", "HarvestingCronJob", GregorianCalendar.getInstance().getTime(),null,0,1);
		sched.interrupt("Harvesting", "HarvestingCronJob");
		sched.interrupt("Harvesting", "SingleHarvestingJob");
		sched.interrupt("Harvesting", "HarvestingJob");
		//sched.triggerJob("Harvesting", "HarvestingCronJob");
		//pageContext.forward("viewHistory.jsp" );
	}
	catch(UnableToInterruptJobException e){
		out.println("<br/>");
		out.println("<h3>Can not stop the Harvesting process.</h3>");
		out.println("<br/>");
	}
	pageContext.forward("viewHistory.jsp" );
}
else{
	out.print("<script type=\"text/javascript\">confirm_abort()</script>");
}
%>
</body>
</html>
