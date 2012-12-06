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

package org.ariadne.oai.config.servlet.logging;

import java.io.File;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.PropertyConfigurator;
import org.ariadne.config.PropertiesManager;

public class Log4jInit extends HttpServlet {

	protected static String logDir = "";
	protected static String logFile = "";
	protected static String prefix = "";
	protected static String file = "";

	public void init() {
		reloadLogging("default");
	}

	public void reloadLogging(String reposIdent) {
		logDir = PropertiesManager.getInstance().getProperty("log.logDirectory");
		logFile = PropertiesManager.getInstance().getProperty("log.logFile");
		if (!logDir.equals("")) {
			System.setProperty("logdir", logDir);
		}
		if (!logFile.equals("")) {
			System.setProperty("logfile", logFile);
		} else {
			System.setProperty("logfile", "harvester");
		}
		System.setProperty("reposIdent", reposIdent);
		prefix = getServletContext().getRealPath("install");
		file = getInitParameter("log4j-init-file");
		// if the log4j-init-file is not set, then no point in trying
		if (file != null) {
			PropertyConfigurator.configure(prefix + File.separator + file);
		}
	}

	public static void reloadLive(String reposIdent) {
		logDir = PropertiesManager.getInstance().getProperty("log.logDirectory");
		logFile = PropertiesManager.getInstance().getProperty("log.logFile");
		if (!logDir.equals("")) {
			System.setProperty("logdir", logDir);
		}
		if (!logFile.equals("")) {
			System.setProperty("logfile", logFile);
		} else {
			System.setProperty("logfile", "harvester");
		}
		System.setProperty("reposIdent", reposIdent);
		// if the log4j-init-file is not set, then no point in trying
		if (file != null) {
			PropertyConfigurator.configure(prefix + File.separator + file);
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) {
	}
}
