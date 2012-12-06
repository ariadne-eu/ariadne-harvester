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

import java.util.Vector;

import org.apache.log4j.Logger;
import org.ariadne.oai.utils.HarvesterUtils;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.StatefulJob;
import org.quartz.UnableToInterruptJobException;

public class HarvestingCronJob implements StatefulJob, InterruptableJob {

	public final static String QUARTZ_HARVEST_SERV_SECONDS = "seconds";
	public final static String QUARTZ_HARVEST_SERV_MINUTES = "minutes";
	public final static String QUARTZ_HARVEST_SERV_HOURS = "hours";
	public final static String QUARTZ_HARVEST_SERV_DAYMONTH = "day_month";
	public final static String QUARTZ_HARVEST_SERV_MONTH = "month";
	public final static String QUARTZ_HARVEST_SERV_DAYWEEK = "day_week";

	public boolean interruptFlag = false;

	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			Vector<String> reposInteralIds = HarvesterUtils.getReposList();
			SingleHarvestingJob.triggerHarvesting("CronHarvesting", "CronHarvestingJob", "cronAll", reposInteralIds);
		} catch (ObjectAlreadyExistsException e) {
			Logger harvestlogger = Logger.getLogger("org.ariadne.oai.DoOaiHarvest");
			harvestlogger.error("Can not load Cron Harvest, already started : " + e.getMessage());
		}
	}

	public void interrupt() throws UnableToInterruptJobException {
		interruptFlag = true;
		// System.out.println("Interrupt flag has been set to : " +
		// interruptFlag);
	}

}
