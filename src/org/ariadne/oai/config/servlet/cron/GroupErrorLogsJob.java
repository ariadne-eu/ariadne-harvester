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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.ariadne.config.PropertiesManager;
import org.ariadne.validation.utils.ValidationUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class GroupErrorLogsJob implements StatefulJob {

	static final int BUFFER = 2048;
	private static Logger harvestlogger = Logger.getLogger("org.ariadne.oai.DoOaiHarvest");

	public static void main(String[] args) {
		GroupErrorLogsJob job = new GroupErrorLogsJob();

		String dir = "/work/tmp/OaiHarvester/log/";
		String file = "harvester";
		job.createGroupedLogFile(file,dir);
//		createZips(dir);
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		String dir = PropertiesManager.getInstance().getProperty("log.logDirectory");
		String file = PropertiesManager.getInstance().getProperty("log.logFile");
		createGroupedLogFile(file,dir);
		createZips(dir);
	}

	protected void createZips(String dir) {

		Logger harvestlogger = Logger.getLogger("org.ariadne.oai.DoOaiHarvest");

		String match = ".*_.+_report.log.GroupedErrors.log";

		File directory = new File(dir);
		String[] files = directory.list();

		SimpleDateFormat sdf = new SimpleDateFormat("-yyyy.MM.dd'T'HH.mm.ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// return sdf.format(sdf.parse(fromDate));

		for (int i = 0; i < files.length; i++) {
			String groupedErrorsFileName = files[i];
			if (groupedErrorsFileName.matches(match)) {
				String logReportFileName = groupedErrorsFileName.split(".GroupedErrors.log")[0];
				String errorSummaryFileName = logReportFileName + ".ErrorSummary.log";
				String[] log = logReportFileName.split("_report");
				String logFileName = log[0] + log[1];
				// These are the files to include in the ZIP file
				String[][] filenames = new String[][] { { errorSummaryFileName, "yes" }, { groupedErrorsFileName, "yes" },
						{ logReportFileName, "yes" }, { logFileName, "yes" } };
				String providerFolder = log[0].split(PropertiesManager.getInstance().getProperty("log.logFile") + "_")[1];
				String outputDir = PropertiesManager.getInstance().getProperty("Harvest.afterHarvestJob.outputDir");
				outputDir = outputDir + File.separator + providerFolder;
				if (!new File(outputDir).exists() && !new File(outputDir).mkdir()) {
					harvestlogger.error("could not create directory : " + outputDir);
				}
				try {
					BufferedInputStream origin = null;
					GregorianCalendar now = (GregorianCalendar) GregorianCalendar.getInstance();
					String zipFileNameString = outputDir + File.separator + logFileName + sdf.format(now.getTime()) + ".zip";
					FileOutputStream dest = new FileOutputStream(zipFileNameString);
					System.out.println("Creating zip file at : " + zipFileNameString);
					ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
					// out.setMethod(ZipOutputStream.DEFLATED);
					byte data[] = new byte[BUFFER];

					Vector<String> deleteFiles = new Vector<String>();

					for (int j = 0; j < filenames.length; j++) {
						String filePath = dir + File.separator + filenames[j][0];
						if (new File(filePath).isFile()) {
							System.out.println("Adding: " + filePath);
							FileInputStream fi = new FileInputStream(filePath);
							origin = new BufferedInputStream(fi, BUFFER);
							ZipEntry entry = new ZipEntry(filenames[j][0]);
							out.putNextEntry(entry);
							int count;
							while ((count = origin.read(data, 0, BUFFER)) != -1) {
								out.write(data, 0, count);
							}
							origin.close();
							if (filenames[j][1].equalsIgnoreCase("yes"))
								deleteFiles.add(filePath);
						}
					}
					out.close();

					delete(deleteFiles);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void delete(Vector<String> deleteFiles) {
		for (int i = 0; i < deleteFiles.size(); i++) {
			new File(deleteFiles.elementAt(i)).delete();
		}
	}

	protected void createGroupedLogFile(String file, String dir) {


		String match = file + "_.+_report.log";
		String match2 = file + "_default_report.log";
		HashMap<String, HashMap<String, TreeSet<String>>> errors = new HashMap<String, HashMap<String, TreeSet<String>>>();

		File directory = new File(dir);

		String[] files = directory.list();

		for (int i = 0; i < files.length; i++) {
			String fileName = files[i];
			if (fileName.matches(match) && !fileName.matches(match2)) {
				errors = collectErrors(directory, fileName);
				writeGroupedLogFile(errors, directory, fileName);
				writeSummaryLogFile(errors, directory, fileName);
			}
		}
	}

	protected void writeSummaryLogFile(HashMap<String, HashMap<String, TreeSet<String>>> errors, File directory, String fileName) {
		try {
			BufferedWriter output;
			String outputFile = directory + File.separator + fileName + ".ErrorSummary.log";
			Iterator errorTypeIter = errors.keySet().iterator();
			if (errorTypeIter.hasNext()) {
				output = new BufferedWriter(new FileWriter(outputFile));
				while (errorTypeIter.hasNext()) {
					String errorType = (String) errorTypeIter.next();
					HashMap<String, TreeSet<String>> errorTypeMap = errors.get(errorType);
					output.write("==========");
					output.newLine();
					output.write("ErrorType : " + errorType);
					output.newLine();
					Iterator errorIter = errorTypeMap.keySet().iterator();
					while (errorIter.hasNext()) {
						String error = (String) errorIter.next();
						TreeSet idList = errorTypeMap.get(error);
						output.write("----------");
						output.newLine();
						output.write("Error : " + error);
						output.newLine();
						output.write("Number of instances : " + idList.size());
						output.newLine();
					}
				}
				output.flush();
				output.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void writeGroupedLogFile(HashMap<String, HashMap<String, TreeSet<String>>> errors, File directory, String fileName) {
		try {
			BufferedWriter output;
			String outputFile = directory + File.separator + fileName + ".GroupedErrors.log";
			Iterator errorTypeIter = errors.keySet().iterator();
			if (errorTypeIter.hasNext()) {
				output = new BufferedWriter(new FileWriter(outputFile));
				while (errorTypeIter.hasNext()) {
					String errorType = (String) errorTypeIter.next();
					HashMap<String, TreeSet<String>> errorTypeMap = errors.get(errorType);
					output.write("ErrorType : " + errorType);
					output.newLine();
					Iterator errorIter = errorTypeMap.keySet().iterator();
					while (errorIter.hasNext()) {
						String error = (String) errorIter.next();
						TreeSet idList = errorTypeMap.get(error);
						output.write("Error : " + error);
						output.newLine();
						output.write("Matching Identifiers :");
						output.newLine();
						Iterator<String> iter = idList.iterator();
						while (iter.hasNext()) {
							output.write(iter.next());
							output.newLine();
						}
					}
				}
				output.flush();
				output.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected HashMap<String, HashMap<String, TreeSet<String>>> collectErrors(File directory, String fileName) {
		HashMap<String, HashMap<String, TreeSet<String>>> errors = new HashMap<String, HashMap<String, TreeSet<String>>>();
		try {
			String error = "";
			String path = directory + File.separator + fileName;
			System.out.println(path);
			//			Scanner scanner = new Scanner(new File(path));
			//			while (scanner.hasNextLine()) {
			//				String str = scanner.nextLine();

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
			String str = reader.readLine();
			try {
				while (str != null) {

					Pattern pattern = Pattern.compile("Invalid metadata found. OAI Identifier was (.+?). Error is : \\((.+?)\\)(.*)",
							Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(str);
					while (matcher.find()) {
						String id = matcher.group(1);
						error = matcher.group(3);

						HashMap<String, HashMap<String, Integer>> collectedErrors = ValidationUtils.collectErrors(error);

						for (String errorType : collectedErrors.keySet()) {

							HashMap<String, TreeSet<String>> errorTypeErrors = errors.get(errorType);
							if (errorTypeErrors == null) {
								errorTypeErrors = new HashMap<String, TreeSet<String>>();
								errors.put(errorType, errorTypeErrors);
							}

							for (String singleError : collectedErrors.get(errorType).keySet()) {
								// System.out.println(id);
								TreeSet<String> idList = errorTypeErrors.get(singleError);
								if (idList == null) {
									idList = new TreeSet<String>();
									idList.add(id);
									errorTypeErrors.put(singleError, idList);
								} else {
									idList.add(id);
								}
							}
						}
					}
					str = reader.readLine();
				}

			} finally {
				reader.close();
			}

		} catch (Exception e) {
			harvestlogger.error(e.getMessage());
		}
		return errors;
	}

}
