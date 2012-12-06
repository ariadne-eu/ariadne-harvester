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

import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.ariadne.config.PropertiesManager;
import org.ariadne.validation.Validator;
import org.ariadne.validation.exception.InitialisationException;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class InitCronServlet extends HttpServlet {

	public static SchedulerFactory schedFact;

	// public void init() throws ServletException {

	// org.ariadne_eu.metrics.ranking.RankingMetrics.initialize();

	// }

	// init for quartz
	public void init() throws ServletException {
		// super.init(config);

		// this.config = cfg;

//		try {
//			Validator.updatePropertiesFileFromRemote();
//		} catch (InitialisationException e) {
//			//
//		}
		
		System.out.println("Initializing Scheduler PlugIn for Harvesting.");
		this.initJobScheduler();
		System.out.println("End of the Initializing Scheduler PlugIn for Harvesting.");
	}

	public void initJobScheduler() {
		
		try {
			String configFile = getServletContext().getRealPath("install/quartz.properties");
			schedFact = new StdSchedulerFactory(configFile);
			boolean cronned = Boolean.valueOf((PropertiesManager.getInstance().getProperty("cron.enabled"))).booleanValue();
			Scheduler sched = schedFact.getScheduler();
			JobDetail detail = new JobDetail("HarvestingCron", "HarvestingCronJob", HarvestingCronJob.class);
			detail.setDurability(true);
			sched.addJob(detail, true);
			JobDetail detail1 = new JobDetail("Harvesting", "SingleHarvestingJob", SingleHarvestingJob.class);
			detail1.setDurability(true);
			sched.addJob(detail1, true);
			JobDetail detail2 = new JobDetail("AfterHarvesting", "AfterHarvestingJob", Class.forName(PropertiesManager.getInstance().getProperty("Harvest.afterHarvestJob.class")));
			detail2.setDurability(true);
			sched.addJob(detail2, true);
			sched.start();
			if (cronned) {
				addCronTrigger();
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void addCronTrigger() {
		try {
			Scheduler sched = schedFact.getScheduler();
			if (sched != null) {

				String sec = (String) PropertiesManager.getInstance().getProperty("cron.schedule." + HarvestingCronJob.QUARTZ_HARVEST_SERV_SECONDS);
				String min = (String) PropertiesManager.getInstance().getProperty("cron.schedule." + HarvestingCronJob.QUARTZ_HARVEST_SERV_MINUTES);
				String hora = (String) PropertiesManager.getInstance().getProperty("cron.schedule." + HarvestingCronJob.QUARTZ_HARVEST_SERV_HOURS);
				String dia_num = (String) PropertiesManager.getInstance().getProperty("cron.schedule." + HarvestingCronJob.QUARTZ_HARVEST_SERV_DAYMONTH);
				String mes = (String) PropertiesManager.getInstance().getProperty("cron.schedule." + HarvestingCronJob.QUARTZ_HARVEST_SERV_MONTH);
				String dias = (String) PropertiesManager.getInstance().getProperty("cron.schedule." + HarvestingCronJob.QUARTZ_HARVEST_SERV_DAYWEEK);

				String horario = sec + " " + min + " " + hora + " " + dia_num + " " + mes + " " + dias;
				CronTrigger trigger = new CronTrigger("HarvestingCronTrigger", "HarvestingCronTrigger", horario);
				trigger.setJobName("HarvestingCron");
				trigger.setJobGroup("HarvestingCronJob");
				sched.scheduleJob(trigger);
				System.out.println("Added CronTrigger");
				System.out.println("Scheduled time for HarvestingCronTrigger: " + horario);
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void deleteCronTrigger() {
		try {
			Scheduler sched = schedFact.getScheduler();
			sched.unscheduleJob("HarvestingCronTrigger", "HarvestingCronTrigger");
			// JobDetail detail = new JobDetail("Harvesting",
			// "HarvestingCronJob", HarvestingCronJob.class);
			// sched.addJob(detail, true);
			System.out.println("Removed CronTrigger");
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
