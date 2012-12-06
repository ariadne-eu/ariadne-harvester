/*******************************************************************************
 * Copyright (c) 2008 Ariadne Foundation.
 * 
 * This file is part of Ariadne Harvester.
 * 
 * Ariadne Harvester is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Ariadne Harvester is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Ariadne Harvester.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.ariadne.oai.config.servlet.cron;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.ariadne.config.PropertiesManager;
import org.ariadne.oai.OAIHarvester;
import org.ariadne.oai.config.servlet.logging.Log4jInit;
import org.ariadne.oai.testSuite.TestSpiTarget;
import org.ariadne.oai.utils.HarvestSessionProperties;
import org.ariadne.oai.utils.HarvesterUtils;
import org.ariadne.oai.utils.ReposProperties;
import org.ariadne.util.OaiUtils;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.StatefulJob;
import org.quartz.Trigger;
import org.quartz.UnableToInterruptJobException;

public class SingleHarvestingJob implements StatefulJob, InterruptableJob {

	// private String reposIdentName = null;

	private static Logger harvestlogger = Logger.getLogger("org.ariadne.oai.DoOaiHarvest");

	private Vector<String> reposInteralIds = null;

	public boolean interruptFlag = false;

	public void execute(JobExecutionContext context) throws JobExecutionException {

		Scheduler sched = null;
		try {
			sched = InitCronServlet.schedFact.getScheduler();

			Trigger[] triggers = sched.getTriggersOfJob("AfterHarvesting", "AfterHarvestingJob");
			while (triggers.length > 0) {
				Thread.sleep(2000);
				harvestlogger.info("Waiting for 2secs.");
				triggers = sched.getTriggersOfJob("AfterHarvesting", "AfterHarvestingJob");
			}
		} catch (SchedulerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JobDataMap dataMap = context.getMergedJobDataMap();
		// reposIdentName = dataMap.getString("reposIdentName");
		reposInteralIds = (Vector<String>) dataMap.get("reposInteralIds");
		String type = dataMap.getString("type");

		boolean repositoryLogs = new Boolean(PropertiesManager.getInstance().getProperty("log.repositoryLogs")).booleanValue();

		for (String reposInteralId : reposInteralIds) {
			if (reposInteralIds.indexOf(reposInteralId) > 0)
				harvestlogger.info("--------------------------------------");
			ReposProperties repos = HarvesterUtils.getReposProperties(reposInteralId);
			if (type.equalsIgnoreCase("single") || repos.getActive().equalsIgnoreCase("Yes")) {
				TestSpiTarget spitest = new TestSpiTarget();
				if (!spitest.hasError()) {
					String status;
					java.util.Calendar until = OaiUtils.getCurrentTime();
					HarvestSessionProperties sessionProps = HarvesterUtils.getSessionProperties();
					//sessionProps.setRepositoryLogs(repositoryLogs);
					sessionProps.setJob(this);
					status = OAIHarvester.checkHarvesting(repos, sessionProps, until);
				} else {
					String text = "Harvesting failed : The SPI target is not available.";
					harvestlogger.info(text);
				}
			} else {
				harvestlogger.info("No harvesting from " + repos.getRepositoryName() + " (reason : not set active).");
				// status =
				// "Not harvested. If you want to harvest from this repository, set it to active <a href=\"AllOAITargets.jsp\">here</a>."
				// ;//+ test.getError();
			}
		}
		if (repositoryLogs)
			Log4jInit.reloadLive("default");

		// if(single) {
		harvestlogger.info("================= Harvesting Finished ====================================");

		if (!interruptFlag && new Boolean(PropertiesManager.getInstance().getProperty("Harvest.afterHarvestJob.enabled")).booleanValue()) {
			// start something following this Job
			try {
				SimpleTrigger trigger = new SimpleTrigger("AfterHarvesting", "AfterHarvestingJob", "AfterHarvesting", "AfterHarvestingJob",
						GregorianCalendar.getInstance().getTime(), null, 0, 1);
				sched.scheduleJob(trigger);
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		interruptFlag = false;
		// }
	}

	public static void triggerHarvesting(String triggerName, String triggerGroupName, String type, Vector<String> reposInteralIds)
			throws ObjectAlreadyExistsException {
		try {
			Scheduler sched = InitCronServlet.schedFact.getScheduler();

			SimpleTrigger trigger = new SimpleTrigger(triggerName, triggerGroupName, "Harvesting", "SingleHarvestingJob", GregorianCalendar
					.getInstance().getTime(), null, 0, 1);
			JobDataMap dataMap = new JobDataMap();
			dataMap.put("reposInteralIds", reposInteralIds);
			dataMap.put("type", type);
			trigger.setJobDataMap(dataMap);
			sched.scheduleJob(trigger);
		} catch (ObjectAlreadyExistsException e) {
			throw e;
		} catch (SchedulerException e) {
			harvestlogger.error(e.getMessage());
		}

	}

	public void interrupt() throws UnableToInterruptJobException {
		interruptFlag = true;
		// System.out.println("Interrupt flag has been set to : " +
		// interruptFlag);
	}

}
