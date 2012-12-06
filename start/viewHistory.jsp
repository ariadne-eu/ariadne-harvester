<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page  import="uiuc.oai.*" import="uiuc.oai.OAIException" import="org.ariadne.config.PropertiesManager"
import="java.util.StringTokenizer" import="java.util.Vector"%>
<%@page import="org.ariadne.util.OaiUtils"%>
<%@page import="java.io.File"%>
<html>

<head>
<script type="text/javascript" language="javascript">
<!--
// Declare a global variable and initialize to null.

var reloadTimer = null;

// This function gets called when the window has completely loaded.
// It starts the reload timer with a default time value.

window.onload = function()
{
 var reloadTime = document.getElementById("reload").value;
 if(reloadTime != null){
 	if(reloadTime != "#")setReloadTime(reloadTime);
 	}
 	else{
 setReloadTime(5); // Pass a default value of 5 seconds.
 }
}

// setReloadTime() does two different things:
// (1) If a value is passed as an argument, the function clears the current timer
//     (if its running) and starts the timer again with the new time
//     and saves a reference to the timer: reloadTimer.
// (2) If no argument is passed, the page is reloaded. The only time no argument
//     will be passed is when the 'setTimeout()' function times out and calls
//     this function.

function setReloadTime(secs)
{
if (secs == "#"){
 secs = '5';
 document.inverseForm.reload.value = secs;
 document.inverseForm.submit();
}
  if (arguments.length == 1) {
    document.inverseForm.reload.value = secs;
    if (reloadTimer) clearTimeout(reloadTimer);
    reloadTimer = setTimeout("setReloadTime()", Math.ceil(Math.abs(parseFloat(secs)) * 1000));
  }
  else {
    document.inverseForm.submit();
  }
}

function setLength(lines)
{
    document.getElementById("length").value=lines;
    document.inverseForm.submit();
}

// disableReload() clears the current timer if its running, and since it will
// never timeout, setReloadTime() will not be called (with no args) and
// the page will not reload.

function disableReload()
{
  if (reloadTimer)
    clearTimeout(reloadTimer);
  document.reloadForm.reloadTime.value = '#';
  document.inverseForm.reload.value = '#';
  document.inverseForm.submit();
}

function inverse(flow)
  {
    document.getElementById("inverse").value=!flow;
    document.inverseForm.submit();
  }

//-->
</script>
<title>View history of harvesting</title>
<%@ include file="index.jsp" %>
<center>
<h2>History</h2>
<%
boolean inversed;
String stateReversed = request.getParameter("inverse");
if (stateReversed!=null){
	inversed = Boolean.valueOf(stateReversed).booleanValue();
	PropertiesManager.getInstance().saveProperty("log.view.inverse",String.valueOf(inversed));
}
else{
	inversed = Boolean.valueOf(PropertiesManager.getInstance().getProperty("log.view.inverse")).booleanValue();
}
String reload;
String reloadTime = request.getParameter("reload");
if (reloadTime!=null){
	try{
		if(reloadTime.equals("#")){
			reload = reloadTime;
			PropertiesManager.getInstance().saveProperty("log.view.reload",reload);
		}else{
			int l = Integer.parseInt(reloadTime);
			if (l != 0){
				reload = Integer.toString(Math.abs(l));
				PropertiesManager.getInstance().saveProperty("log.view.reload",reload);
			}
			else{
				reload = PropertiesManager.getInstance().getProperty("log.view.reload");
			}
		}
	}catch(NumberFormatException e){
		reload = PropertiesManager.getInstance().getProperty("log.view.reload");
	}
}
else{
	reload = PropertiesManager.getInstance().getProperty("log.view.reload");
}
String length;
String histLength = request.getParameter("length");
if (histLength!=null){
	try{
		int l = Integer.parseInt(histLength);
		if (l != 0){
			length = Integer.toString(Math.abs(l));
			PropertiesManager.getInstance().saveProperty("log.view.length",length);
		}
		else{
			length = PropertiesManager.getInstance().getProperty("log.view.length");
		}
	}catch(NumberFormatException e){
		length = PropertiesManager.getInstance().getProperty("log.view.length");
	}
}
else{
	length = PropertiesManager.getInstance().getProperty("log.view.length");
}

String inverseName = "Up";
if(inversed){
	inverseName = "Down";
}

String reloadName = "Set";
if(reload.equals("#")){
	reloadName = "Start";
}
%>
<form name="reloadForm">

Auto-reload (sec)  <input type="text" style="width:25px;" name="reloadTime" value="<%=reload %>">
<input type="button" value="<%=reloadName %>" onclick="setReloadTime(document.reloadForm.reloadTime.value)">
<input type="button" value="Disable" onclick="disableReload()">
<br><br>
Order : 
<input type="button" value="<%=inverseName %>" onclick="inverse(<%=inversed %>)">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Length : 
<input type="text" style="width:35px;" name="histLength" value="<%=length %>">
<input type="button" value="Set" onclick="setLength(document.reloadForm.histLength.value)">
</form>
<form name="inverseForm" method=POST action="viewHistory.jsp">
<input type="hidden" id="inverse" name="inverse" value="<%=inversed %>"/>
<input type="hidden" id="reload" name="reload" value="<%=reload %>"/>
<input type="hidden" id="length" name="length" value="<%=length %>"/>
</form>

<br>
<%
String[] log = OaiUtils.readN(Integer.parseInt(length),new File(PropertiesManager.getInstance().getProperty("log.logDirectory") + "harvester_report.log"));


out.println("<table border=\"0\" width=\"500px\">");
out.println("<tr>");
 out.println("<td align=\"left\" valign=\"middle\">");

 if(inversed){
		for(int i = log.length-1; i >= 0 ; i--){
			out.println(log[i] + "<br>");
		}
	}
	else{
		for(int i = 0; i < log.length ; i++){
			out.println(log[i] + "<br>");
		}
	}
  out.println("</table>");



%>
</center>
    </body>
</html>
