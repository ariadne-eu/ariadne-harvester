<%@ page language="java"
	import="java.util.HashMap, java.io.IOException"%>
<%@page import="org.quartz.SimpleTrigger"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="org.ariadne.oai.config.servlet.cron.InitCronServlet"%>
<%@page import="org.quartz.Scheduler"%>
<%@page import="org.quartz.SchedulerException"%>
<%@page import="org.ariadne.config.PropertiesManager"%>
<html>

<head>
<title>Harvesting from Targets</title>
<%@ include file="index.jsp"%>

<%
Scheduler sched = InitCronServlet.schedFact.getScheduler();
if(new Boolean(PropertiesManager.getInstance().getProperty("Harvest.afterHarvestJob.enabled")).booleanValue()){
	try {
		sched = InitCronServlet.schedFact.getScheduler();
		SimpleTrigger trigger = new SimpleTrigger("AfterHarvesting",
				"AfterHarvestingJob", "AfterHarvesting",
				"AfterHarvestingJob", GregorianCalendar.getInstance()
						.getTime(), null, 0, 1);
		sched.scheduleJob(trigger);
	} catch (SchedulerException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
%>

</body>
</html>
