<%@ page contentType="text/html; charset=UTF-8"%> 
<%@page import="org.ariadne.config.PropertiesManager"%>
<link rel="stylesheet" href="../css/template_css.css" type="text/css" />
<link rel="stylesheet" href="../css/theme.css" type="text/css" />
<script language="JavaScript" src="../includes/js/JSCookMenu.js" type="text/javascript"></script>
<script language="JavaScript" src="../includes/js/admin/ThemeOffice/theme.js" type="text/javascript"></script>
<script language="JavaScript" src="../includes/js/mambojavascript.js" type="text/javascript"></script>
<%="</head><body>"%>
<div id="wrapper">
    <div id="header">
           <div id="mambo"><img src="../images/Harvest_Config.png" alt="ARIADNE" /></div>
    </div>
</div>
<table width="100%" class="menubar" cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td class="menubackgr">		<div id="myMenuID"></div>

		<script language="JavaScript" type="text/javascript">
		var myMenu =
		[
			[null,'Configuration',null,null,'Configuration',
				['<img src="../images/checkin_small.png" />','Test Configuration','../configuration/testConfiguration.jsp',null,'testConfirguration'],
				_cmSplit,
				['<img src="../images/config_small.png" />','Change Configuration','../init/index.jsp',null,'changeConfiguration'],			
				['<img src="../images/cpanel_small.png" />','Advanced Configuration','../install/config.jsp',null,'advancedConfiguration'],		
				['<img src="../images/restore_small.png" />','Upload Configuration','../start/uploadConfig.jsp',null,'loadConfiguration'],		
				['<img src="../images/filesave.png" />','Download Configuration','../start/saveProperties.jsp',null,'saveConfiguration'],
//				_cmSplit,	
//				['<img src="../images/cpanel_small.png" />','Debugging Options','../install/config.jsp',null,'debuggingConfiguration'],		
//				_cmSplit,	
//				['<img src="../images/config_small.png" />','Harvesting Options','../install/config.jsp',null,'harvestingConfiguration'],	
			],
			_cmSplit,
			 [null,'OAI-PMH Targets',null,null,'OAI-PMH Targets',
			 					['<img src="../images/preview_small.png" />','View all targets','../start/AllOAITargets.jsp',null,null],
                				_cmSplit,
                                ['<img src="../images/favourites.png" />','Add new target','../start/AddOAITarget.jsp',null,null],
                                <%if (PropertiesManager.getInstance().getProperty("registry.url") != null && !PropertiesManager.getInstance().getProperty("registry.url").equalsIgnoreCase("")){ %>
                                ['<img src="../images/favourites.png" />','Add from Registry','../start/addOAITargetRegistry.jsp',null,null],                               
                                <%} %>
                         ],			
			_cmSplit,
			 [null,'Harvesting',null,null,'Harvesting',
                                ['<img width=16px src="../images/cancel_f2.png" />','Abort Harvesting','../start/stopHarvesting.jsp',null,null],
                                				_cmSplit,
                                ['<img width=16px src="../images/small_download_f2.png" />','Manually start harvesting','../start/DoOAIHarvest.jsp',null,null],
                                ['<img width=16px src="../images/small_download_f2.png" />','View/Edit harvesting schedule','../start/harvestingCron.jsp',null,null],
                                				_cmSplit,
   								['<img width=16px src="../images/document_small.png" />','View History','../start/viewHistory.jsp',null,'viewHistory'],
                                				_cmSplit,
   								['<img width=16px src="../images/extra.png" />','Start After-Harvest-Job now','../start/AfterHarvestingJob.jsp',null,'viewHistory'],
                        ],
             _cmSplit,
             [null,'About','about.jsp',null,'About',null],
                        
		];
		cmDraw ('myMenuID', myMenu, 'hbr', cmThemeOffice, 'ThemeOffice');
		</script>
</td>
    <td class="menubackgr" align="right">
    	</td>
    <td class="menubackgr" align="right"><a href="../start/logout.jsp" style="color: #333333; font-weight: bold">Logout</a>&nbsp;</td>

    </tr>
</table>

