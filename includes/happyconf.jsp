<%@page import="org.ariadne.oai.utils.ReposProperties"%><%@page import="java.util.StringTokenizer,java.util.HashMap,org.ariadne.config.PropertiesManager,org.ariadne.oai.utils.HarvesterUtils
,java.util.Vector,org.ariadne.oai.testSuite.TestOaiTarget, org.ariadne.testSuite.*, org.ariadne.oai.testSuite.TestSpiTarget, org.ariadne.util.OaiUtils
 "%><%
    TestSuiteRegister.flush();
	
    response.setContentType("text/xml");
//    response.setCharacterEncoding("utf-8");
    
    Vector<ReposProperties> repositories = HarvesterUtils.getReposProperties();
    for (int i = 0; i < repositories.size(); i++) {
    	ReposProperties repo = repositories.elementAt(i);
		if(((String)repo.getActive()).equalsIgnoreCase("Yes")){
			new TestOaiTarget(repo,OaiUtils.getSets(repo.getHarvestingSet()));	
		}
		
	}
    
    new TestSpiTarget();
    
    out.println(TestSuiteRegister.regToString());
%>
