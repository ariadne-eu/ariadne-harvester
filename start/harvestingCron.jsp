<%@ page import="org.ariadne.config.PropertiesManager"
import="java.util.StringTokenizer" import="java.util.Vector"%>
<%@page import="org.ariadne.oai.utils.HarvesterUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.ariadne.oai.config.servlet.cron.InitCronServlet"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.quartz.Trigger"%>
<html>

<head>
<script type="text/javascript">
 
function updateCron()
  {
	  document.forms[0].submit();
  }
  </script>
<title>View OAI-PMH target details</title>
<%@ include file="index.jsp" %>
<form method=POST action="harvestingCron.jsp" >
<%
if (request.getParameter("enabled")!=null){
	InitCronServlet.deleteCronTrigger();
	
	HashMap<String,String> cronProps = new HashMap<String,String>();
	boolean enabled = request.getParameter("enabled").equalsIgnoreCase("Yes");
	cronProps.put("enabled",new Boolean(enabled).toString());
	cronProps.put("schedule.seconds",request.getParameter("seconds"));
	cronProps.put("schedule.minutes",request.getParameter("minutes"));
	cronProps.put("schedule.hours",request.getParameter("hours"));
	cronProps.put("schedule.day_month",request.getParameter("day_month"));
	cronProps.put("schedule.month",request.getParameter("month"));
	cronProps.put("schedule.day_week",request.getParameter("day_week"));
	HarvesterUtils.saveDetails("cron",cronProps);
	
if(enabled){
		InitCronServlet.addCronTrigger();
	}
}
%>
<%

boolean enabled =  Boolean.valueOf((PropertiesManager.getInstance().getProperty("cron.enabled"))).booleanValue();
HashMap schedule =  HarvesterUtils.getPropertiesStartingWith("cron.schedule");

		out.println("<br/><form method=POST action=\"viewDetails.jsp\">");
	out.println("<div align=\"center\">");
	 out.println("<div class=\"cron\">");
	  out.println("<table border=\"0\" width=\"98%\">");
	   out.println("<tr>");
	    out.println("<td align=\"left\" valign=\"middle\">");
	     out.println("<table>");
	      out.println("<td align=\"center\" class=\"user\" valign=\"middle\" width=\"64\">");
	       out.println("<img alt=\"result ok\" src=\"../images/browser.png\"/>");
	      out.println("</td>");
	      out.println("<td align=\"left\" class=\"sectionname\" valign=\"middle\">Schedule for harvesting</td>");
	     out.println("</table>");
	     out.println("<table class=\"adminlist\">");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Seconds</th>");
	       out.println("<td class=\"content\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"seconds\" value=\"" + schedule.get("seconds") + "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Minutes</th>");
	       out.println("<td class=\"content\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"minutes\" value=\"" + schedule.get("minutes") + "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Hours</th>");
	       out.println("<td class=\"content\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"hours\" value=\"" + schedule.get("hours") + "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Day(s) of the month</th>");
	       out.println("<td class=\"content\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"day_month\" value=\"" + schedule.get("day_month") + "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Month</th>");
	       out.println("<td class=\"content\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"month\" value=\"" + schedule.get("month") + "\"/></td>");
	      out.println("</tr>");
	      out.println("<tr>");
	       out.println("<th class=\"title\">Day(s) of the week</th>");
	       out.println("<td class=\"content\"><input class=\"inputbox\" style=\"width:100%;\" type=\"text\" name=\"day_week\" value=\"" + schedule.get("day_week") + "\"/></td>");
	      out.println("</tr>");

	      out.println("<tr>");
	       out.println("<th class=\"title\">Harvest schedule active</th>");
	       out.println("<td class=\"content\">");
	      if(enabled){
	          out.println("<select name=\"enabled\"><option selected value=\"Yes\">Yes</option><option value=\"No\">No</option></select></td>");
	      }else{
	          out.println("<select name=\"enabled\"><option value=\"Yes\">Yes</option><option selected value=\"No\">No</option></select></td>");    	 
	      }
	      out.println("</tr>");

	     out.println("</table>");
	    out.println("</td>");
	   out.println("</tr>");
	  out.println("</table>");
      if(enabled){
    	  Trigger cronTrigger = InitCronServlet.schedFact.getScheduler().getTrigger("HarvestingCronTrigger", "HarvestingCronTrigger");
    	  Date nextDate = cronTrigger.getNextFireTime();
    	  SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    	  String nextDateString = sdf.format(nextDate);
    	  nextDate = cronTrigger.getFireTimeAfter(nextDate);
    	  String nextNextDateString = sdf.format(nextDate);
      out.println("<br>");
       out.println("Next scheduled harvesting timestamps : <br>");
       out.println(nextDateString + "<br>");
       out.println(nextNextDateString + "<br>");
       out.println("...");
      out.println("<br>");
      }
	 out.println("</div>");
	out.println("</div>");

%>

<br/>

<input type="hidden" id="reposIdentName" name="reposIdentName" value=""/>
<input type="hidden" id="param" name="param" value=""/>
<br>
<center><input type="button" value="Refresh" onclick="window.location='harvestingCron.jsp';"/>&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" onClick="updateCron('');" value="Set"/></center>
</form>
<br/>
    </body>
</html>
