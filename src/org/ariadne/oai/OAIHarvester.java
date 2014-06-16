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

/**
 * Copyright 2006 - 2008 Ariadne Foundation
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
 *
 */

package org.ariadne.oai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.ariadne.config.PropertiesManager;
import org.ariadne.mapping.GenericMapper;
import org.ariadne.oai.config.servlet.logging.Log4jInit;
import org.ariadne.oai.harvestWriter.GenericWriter;
import org.ariadne.oai.testSuite.TestOaiTarget;
import org.ariadne.oai.utils.HarvestSessionProperties;
import org.ariadne.oai.utils.HarvesterUtils;
import org.ariadne.oai.utils.ReposProperties;
import org.ariadne.util.IOUtilsv2;
import org.ariadne.util.JDomUtils;
import org.ariadne.util.OaiUtils;
import org.ariadne.validation.Validator;
import org.ariadne.validation.exception.InitialisationException;
import org.ariadne.validation.exception.ValidationException;
import org.jdom.Element;
import org.jdom.JDOMException;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;

public class OAIHarvester {

	public static String OAI_DC = "DC";
	public static String OAI_LOM = "LOM";
	public static String OAI_LRE4 = "LRE4";
	public static String OAI_LRE3 = "LRE3";
	private static Logger harvestlogger = Logger.getLogger("org.ariadne.oai.DoOaiHarvest");
	// private static boolean busy = false;
	public final static String validationWorkFolder = PropertiesManager.getInstance().getProperty("log.logDirectory") + File.separator + "validation" + File.separator;

	public final static int NOT_UPDATED = -100;
	public final static int N_A_ = -50;
	public final static int NONEW = -50;
	public final static int OK = 0;
	public final static int LIMIT_MODE = 2;
	public final static int INTERRUPTED = 5;
	public final static int VALID_ERR = 10;
	public final static int ERR = 50;
	public final static int FAILED = 100;

	public final static HashMap<Integer, String> harvestStatus = new HashMap<Integer, String>();

	static {
		harvestStatus.put(NOT_UPDATED, "Not Updated");
		harvestStatus.put(N_A_, "N.A.");
		harvestStatus.put(NONEW, "No New Metadata");
		harvestStatus.put(OK, "OK");
		harvestStatus.put(LIMIT_MODE, "Limit Mode Was On");
		harvestStatus.put(INTERRUPTED, "Harvesting Interrupted");
		harvestStatus.put(VALID_ERR, "Validation Errors Present");
		harvestStatus.put(ERR, "Some Error(s) Occured");
		harvestStatus.put(FAILED, "Harvesting Failed");

	}

	private static Validator validator = Validator.getValidator();

	// public static boolean busy() {
	// return busy;
	// }

	// public static void setBusy(boolean busy) {
	// OAIHarvester.busy = busy;
	// }

	/**
	 * 
	 * @param records
	 * @param mdPrefix
	 * @return a Vector with LOM objects, and at the first position the
	 *         OAIRecordList for resumption
	 * @throws OAIException
	 */
	@SuppressWarnings("unchecked")
	private static HarvestResult parse(OAIRecordList records, ReposProperties repoProperties, HarvestSessionProperties sessionProperties) throws OAIException {
		HarvestResult result = new HarvestResult();

		// records.getCurrentItem().getMetadataPrefix();

		harvestlogger.debug("Parsing records...");
		int counter = 0;

		InvalidRecords invalidRecords = sessionProperties.getInvalidRecords();

		//		OAIRepository repository = records.getOAIRepository();
		//		reposIdent = repository.getRepositoryIdentifier();
		while (counter < sessionProperties.getBatchSize() && (records.getCurrentItem() != null || invalidRecords.moreItems())) {
			Record record = new Record();
			int recordHarvestStatus = NONEW;

			if(invalidRecords.moreItems()){
				OAIRecord invalidRecord = invalidRecords.getCurrentRecord();
				record.setOaiRecord(invalidRecord);
				invalidRecords.moveNext();
			}
			else {
				OAIRecord oaiRecord = records.getCurrentItem();

				if (oaiRecord != null) {
					try {
						if (oaiRecord.isIdentifierOnly()) {

							oaiRecord = sessionProperties.getRepository().getRecord(oaiRecord.getIdentifier(),
									repoProperties.getMetadataPrefix());
						}
						record.setOaiRecord(oaiRecord);
					} catch (OAIException e) {
						harvestlogger.error("Error ODE Identifier:" + oaiRecord.getIdentifier());
					}
				}else {
					harvestlogger.error("ARCHIVING ERROR : there is no CurrentItem at " + counter);
					result.updateHarvestStatus(ERR);
				}
				records.moveNext();
			}
			if(record.getMetadata() != null) {
				recordHarvestStatus = processRecord(repoProperties, sessionProperties, record);
				result.updateHarvestStatus(recordHarvestStatus);
			}
			if(recordHarvestStatus > OK || record.getMetadata() == null) {
				if (record.getOaiRecord() != null && record.getOaiRecord().deleted()) {
					result.addDeletedRecord(record);
				}
				result.addMetadataRecord(null);
			}else {
				result.addMetadataRecord(record);
			}
			counter++;

		}
		harvestlogger.info("Harvested " + counter + " records");

		if (counter != 0) {
			result.setRecordList(records);
		}
		//		metadataRecords.add(0, harvestStatus);
		return result;
	}

	@SuppressWarnings("unchecked")
	private static Integer processRecord(ReposProperties reposProperties, HarvestSessionProperties sessionProperties, Record record) throws OAIException {// returns a Vector with first the harvestStatus and second the record in form of a JDOM element
		int harvestStatus = OK;
		try {
			transform(sessionProperties, record);
			//			 if(metadataFormat.equals(OAI_DC)){
			//			 lom =
			//			 DublinCoreConvertor.createLOMfromDC(n,record.getIdentifier());
			//			 }
			//			 if(metadataFormat.equals(OAI_LOM)){
			//			 }
			//			 else{
			//			 logger.error("ARCHIVING ERROR : " + metadataFormat +
			//			 " not supported");
			//			 }

			//			String metadataFormat = reposProperties.getMetadataFormat();
			//			if(metadataFormat.equalsIgnoreCase(OAIHarvester.OAI_LOM) || metadataFormat.equalsIgnoreCase(OAIHarvester.OAI_LRE3) || metadataFormat.equalsIgnoreCase(OAIHarvester.OAI_LRE4)) {
			addHarvestingMetadata(record, reposProperties, sessionProperties);
			//			}

			if (sessionProperties.validate()) {
				String xmlstring = JDomUtils.parseXml2string(record.getMetadata(),null);
				validator.validateMetadata(xmlstring, reposProperties.getValidationUri());

			}

		} catch (ValidationException e) {
			harvestStatus = VALID_ERR;
			handleValidationError(sessionProperties.trackValidationErrors(), reposProperties.getRepositoryIdentifier(), record, e);
		} catch (Exception e) {
			harvestStatus = ERR;
			String msg = "An unknown exception was thrown. Error is : (" + e.getClass().getSimpleName() + ")" + e.getMessage();
			harvestlogger.error(msg);
			harvestlogger.debug(e.getStackTrace()[0].toString() + e.getStackTrace()[1].toString() + e.getStackTrace()[2].toString());
		}

		return new Integer(harvestStatus);
	}

	private static void transform(HarvestSessionProperties sessionProps, Record record) throws ClassNotFoundException, InstantiationException,
	IllegalAccessException {
		String classname = null;
		classname = sessionProps.getTransformationID();
		if(classname != null && !classname.trim().equalsIgnoreCase("")) {
			Class mapperClass = Class.forName(classname);
			GenericMapper mapper = (GenericMapper) mapperClass.newInstance();
			record.setMetadata(mapper.map(record));
		}
	}

	private static void initTransformation(ReposProperties reposProperties, HarvestSessionProperties sessionProps){
		String transformationID = null;
		transformationID = reposProperties.getTransformationID();
		if (transformationID.trim().equalsIgnoreCase("")) transformationID = null;
		if(transformationID == null){
			transformationID = PropertiesManager.getInstance().getProperty("mapper.mapperClassName." + reposProperties.getRepositoryIdentifier());
		}if(transformationID == null){
			transformationID = PropertiesManager.getInstance().getProperty("mapper.mapperClassName." + reposProperties.getMetadataFormat());
		}
		sessionProps.setTransformationID(transformationID);
	}

	private static void handleValidationError(boolean trackValidationErrors, String reposIdent, Record record, Exception e) throws OAIException {
		try {

			if (trackValidationErrors) {
				String outputFileName = validationWorkFolder + reposIdent + ".log.new";
				File file = new File(outputFileName);
				if (!file.exists()) {
					file.createNewFile();
					IOUtilsv2.writeStringToFileInEncodingUTF8(record.getOaiIdentifier(), outputFileName);
				} else {
					File result = new File(outputFileName);
					FileOutputStream fos = new FileOutputStream(result, true);
					Writer out = new OutputStreamWriter(fos, "UTF-8");
					out.write("\n" + record.getOaiIdentifier());
					out.close();
					fos.close();
				}
			}		
		} catch (IOException e1) {
			short i = 15;
			throw new OAIException(i, e1.getMessage());
		}
		String msg = "Invalid metadata found. OAI Identifier was " + record.getOaiIdentifier() + ". Error is : (" + e.getClass().getSimpleName() + ")"
		+ e.getMessage();
		harvestlogger.error(msg);
		if (record.getMetadata() != null) {
			harvestlogger.debug("metadatastring involved was : " + JDomUtils.parseXml2stringNoXmlDeclaration(record.getMetadata()));
		}
	}

	private static void addHarvestingMetadata(Record record, ReposProperties reposProperties, HarvestSessionProperties sessionProperties)
	throws IllegalStateException {
		if (sessionProperties.addGlobalMetadataID())
			try {
				HarvesterUtils.addGlobalMetadataIdentifier(record, reposProperties.getRepositoryIdentifier());
			} catch (JDOMException e) {
				harvestlogger.error("An error has occured while adding the global LO identifier : " + e.getMessage());
			}
			if (sessionProperties.addGlobalLOID())
				try {
					HarvesterUtils.addGlobalLOIdentifier(record, reposProperties.getRepositoryIdentifier());
				} catch (JDOMException e) {
					harvestlogger.error("An error has occured while adding the global LO identifier : " + e.getMessage());
				}
				if (!reposProperties.getProviderName().equals("")) {
					try {
						HarvesterUtils.addReposContributor(record, reposProperties.getProviderName());
					} catch (JDOMException e) {
						harvestlogger.error("An error has occured while adding the repository contributor : " + e.getMessage());
					}
				}
	}

	/**
	 * 
	 * @param baseURL
	 * @param mdPrefix
	 * @param from
	 * @param until
	 * @param set
	 * @param resumptionRecords
	 *            : when u start the harvesting this must be null
	 * @return a Vector with LOM objects, and at the first position the
	 *         OAIRecordList for resumption
	 * @throws OAIException
	 */
	@SuppressWarnings("unchecked")
	public static HarvestResult harvest(ReposProperties repoProperties, HarvestSessionProperties sessionProps, OAIRecordList resumptionRecords) throws OAIException {

		if (sessionProps.getJob() == null || !sessionProps.getJob().interruptFlag) {
			OAIRecordList records = null;
			if (resumptionRecords != null) {
				records = resumptionRecords;
			} else {
				harvestlogger.debug("Harvesting...");
				OAIRepository repository = new OAIRepository();
				repository.setBaseURL(repoProperties.getBaseURL());
				repository.setMaxRetryMinutes(Integer.parseInt(PropertiesManager.getInstance().getProperty("Harvest.maxRetryMinutes")));
				repository.setRetryLimit(Integer.parseInt(PropertiesManager.getInstance().getProperty("Harvest.retryLimit")));
				sessionProps.setRepository(repository);
				String from = repoProperties.getLatestHarvestedDatestamp();
				if (repoProperties.getAutoReset())	from = repository.getEarliestDatestamp();
				if(repoProperties.getHarvestByGetRecord()) {
					records = repository.listIdentifiers(sessionProps.getUntil(), from, sessionProps.getSet(), repoProperties.getMetadataPrefix());
				}else {
					records = repository.listRecords(repoProperties.getMetadataPrefix(), sessionProps.getUntil(), from, sessionProps.getSet());
				}
				harvestlogger.debug("Done");
			}
			int length = records.getCompleteSize();
			if (length > 0 & resumptionRecords == null)
				harvestlogger.info("Total number of records to parse : " + length);
			return parse(records, repoProperties, sessionProps);
		} else {
			String msg = "-- HARVESTING INTERRUPTED --";
			short i = 15;
			throw new OAIException(i, msg);
		}
	}

	public static int harvestSet(ReposProperties repoProperties, HarvestSessionProperties sessionProps) throws OAIException {
		int harvestStatus = NOT_UPDATED;

		String batchSize = PropertiesManager.getInstance().getProperty("Harvest.batchSize");
		if(batchSize == null)batchSize = "100";
		sessionProps.setBatchSize(Integer.parseInt(batchSize));
		String addGlobalLOId = PropertiesManager.getInstance().getProperty("Harvest.addGlobalLOIdentifier");
		if(addGlobalLOId == null)addGlobalLOId = "false";
		sessionProps.setAddGlobalLOID(new Boolean(addGlobalLOId).booleanValue());
		String addGlobalMetId = PropertiesManager.getInstance().getProperty("Harvest.addGlobalMetadataIdentifier");
		if(addGlobalMetId == null)addGlobalMetId = "false";
		sessionProps.setAddGlobalMetadataID(new Boolean(addGlobalMetId).booleanValue());
		String trackValidationErrors = PropertiesManager.getInstance().getProperty("Harvest.validation.trackErrors");
		if(trackValidationErrors == null)trackValidationErrors = "false";
		sessionProps.setTrackValidationErrors(new Boolean(trackValidationErrors).booleanValue());

		initTransformation(repoProperties,sessionProps);

		InvalidRecords invalidRecords = new InvalidRecords(repoProperties);
		sessionProps.setInvalidRecords(invalidRecords);
		//		String repositoryIdentifier = repoProperties.getRepositoryIdentifierInteral();

		initInvalidRecordsFile(repoProperties.getRepositoryIdentifierInteral());
		//		Vector lomVector = harvest(repoProperties, sessionProps, null);
		HarvestResult result = harvest(repoProperties, sessionProps, null);
		if (result != null) {
			harvestStatus = result.getHarvestStatus();
			// harvestStatus is
			// always in there, and
			// resumptionrecords or
			// some records should
			// be in there
			Vector<Record> records = result.getMetadataRecords();
			if(records.size() > 0) {
				try {

					// collect writerclasses
					Vector<GenericWriter> writers = new Vector<GenericWriter>();
					String whereToStore = PropertiesManager.getInstance().getProperty("Harvest.storeTo");
					StringTokenizer st = new StringTokenizer(whereToStore, ";");
					while (st.hasMoreTokens()) {
						String storeTo = st.nextToken();
						Class writerClass = Class.forName(PropertiesManager.getInstance().getProperty(storeTo + ".writerClassName"));
						GenericWriter writer = (GenericWriter) writerClass.newInstance();
						writer.CreateTarget(PropertiesManager.getInstance().getProperty(storeTo + ".URI"));
						writers.add(writer);
					}

					OAIRecordList resumptionRecords = result.getRecordList();
					//				// get ReposIdentifier
					//				if (repositoryIdentifier.equals("")) {
					//					repositoryIdentifier = resumptionRecords.getOAIRepository().getBaseURL();
					//				}

					boolean limitMode = Boolean.valueOf(PropertiesManager.getInstance().getProperty("Harvest.limitMode.on")).booleanValue();
					int limit = Integer.parseInt(PropertiesManager.getInstance().getProperty("Harvest.limitMode.limit"));
					int count = 0;
					int harvestedCount = 0;
					int size = records.size();
					boolean stop = false;
					while (!stop) {
						// calculate limit
						if (limitMode) {
							int nextBatch = count + size + 1;
							if (nextBatch > limit) {
								stop = true;
								records.subList(limit - count, size).clear();
							}
						}
						// remove null entries from invalid metadata
						boolean moreNulls = true;
						while (moreNulls) {
							moreNulls = records.remove(null);
						}
						int tempCount = records.size();
						// write metadata
						for (int i = 0; i < writers.size(); i++) {
							GenericWriter writer = ((GenericWriter) writers.elementAt(i));
							writer.connect();
							writer.pushAway(records, repoProperties.getRepositoryIdentifier(), sessionProps.getSet());
							writer.disconnect();
						}
						// update metadataCount
						count += size;
						// update harvestedCount
						harvestedCount += tempCount;
						if (stop) {
							harvestStatus = Math.max(harvestStatus, LIMIT_MODE);
							harvestlogger.info("LIMIT MODE ON : Harvesting of " + repoProperties.getRepositoryName() + " stopped at " + limit + " records.");
						} else if (resumptionRecords.moreItems() || sessionProps.getInvalidRecords().moreItems()) {
							result = harvest(repoProperties, sessionProps, resumptionRecords);
							harvestStatus = Math.max(harvestStatus, result.getHarvestStatus());
							records = result.getMetadataRecords();
							if (records.size() > 0) {
								resumptionRecords = result.getRecordList();
								size = records.size();
							} else {
								harvestlogger.info("Stopping on records.size<=0....");
								stop = true;
							}
						} else{
							harvestlogger.info("Stopping on no more items....CompleteSize:"
	              + (resumptionRecords!=null ? resumptionRecords.getCompleteSize() : "") + "-CurrentIndex:"
	              + (resumptionRecords!=null ? resumptionRecords.getCurrentIndex() : "");
							stop = true;
						}
					}
					moveFileInvalidRecords(repoProperties.getRepositoryIdentifierInteral());
					String msg = "Successfully harvested " + harvestedCount + " records.";
					harvestlogger.info(msg);
				} catch (ClassNotFoundException e) {
					short i = 15;
					throw new OAIException(i, e.getMessage());
				} catch (InstantiationException e) {
					short i = 15;
					throw new OAIException(i, e.getMessage());
				} catch (IllegalAccessException e) {
					short i = 15;
					throw new OAIException(i, e.getMessage());
				}
			}
		}
		return harvestStatus;
	}

	private static void initInvalidRecordsFile(String repositoryIdentifier) {

		File dir = new File(validationWorkFolder);
		if(!dir.exists()) {
			dir.mkdir();
		}

		String outputFileName = validationWorkFolder + repositoryIdentifier + ".log.new";
		File file = new File(outputFileName);
		if(file.exists()) {
			file.delete();
		}
	}

	private static void moveFileInvalidRecords(String repositoryIdentifier) {
		String outputFileName = validationWorkFolder + repositoryIdentifier + ".log";

		File fileOut = new File(outputFileName + ".new");
		if(fileOut.exists()) {
			File file = new File(outputFileName);
			fileOut.renameTo(file);
		}
	}

	public static String checkHarvesting(ReposProperties repoProps, HarvestSessionProperties sessionProps, Calendar untilDate) {
		String status = "";
		String reposName = repoProps.getRepositoryName();
		String internalId = repoProps.getRepositoryIdentifierInteral();
		String baseUrl = repoProps.getBaseURL();
		String from = repoProps.getLatestHarvestedDatestamp();
		String until = "";
		if (repoProps.getHarvestInterval() != null && new Integer(repoProps.getHarvestInterval()) > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date fromDate = null;
			try {
				fromDate = sdf.parse(from);
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDate);
				cal.add(Calendar.DAY_OF_MONTH, new Integer(repoProps.getHarvestInterval()));
				until = sdf.format(cal.getTime());
			} catch (Exception e1) {
				until = OaiUtils.calcUntil(untilDate, repoProps.getGranularity());
			}
		} else {
			until = OaiUtils.calcUntil(untilDate, repoProps.getGranularity());
		}
		sessionProps.setUntil(until);
		Vector<String> sets = OaiUtils.getSets(repoProps.getHarvestingSet());

		if (sessionProps.repositoryLogs())	Log4jInit.reloadLive(reposName);

		int finishStatus = NOT_UPDATED;
		TestOaiTarget test = null;
		if(!repoProps.getSkipPreOaiTest())test = new TestOaiTarget(repoProps, sets);
		if (test == null || !test.hasError()) {
			try {
				OAIRepository oaiRepository = new OAIRepository();
				oaiRepository.setBaseURL(baseUrl);
				if (repoProps.getAutoReset() || !from.equals(until)) {
					harvestlogger.info("Start harvesting from " + reposName);
					initValidation(repoProps, sessionProps);

					for (String set : sets) {
						sessionProps.setSet(set);
						int endStatus = OAIHarvester.harvestSet( repoProps, sessionProps);
						finishStatus = Math.max(finishStatus, endStatus);
					}
					PropertiesManager.getInstance().saveProperty(internalId + ".latestHarvestedDatestamp", until);
					harvestlogger.info("Harvesting from " + reposName + " finished");
					status = "Harvesting finished";

				} else {
					harvestlogger.info("Repository " + reposName + " up to date, no harvesting needed");
					status = "Repository up to date, no harvesting needed";
				}
			} catch (OAIException e) {
				// OAIHarvester.setBusy(false);
				finishStatus = Math.max(finishStatus, ERR);
				status = "Harvesting from " + reposName + " did not complete because of a harvesting error, the error was : " + e.getMessage();
				harvestlogger.error(status);
			}
		} else {
			finishStatus = Math.max(finishStatus, FAILED);
			harvestlogger.info("Harvesting from " + baseUrl + " failed");
			status = "Harvesting failed : Please check <a href=\"../configuration/testConfiguration.jsp\">Test Configuration</a> for details";// +
			// test.getError();
		}
		if (finishStatus > NOT_UPDATED)
			PropertiesManager.getInstance().saveProperty(internalId + ".statusLastHarvest", Integer.toString(finishStatus));
		return status;
	}

	private static void initValidation(ReposProperties repoProps, HarvestSessionProperties sessionProps) throws OAIException {
		// init validator
		if(sessionProps.validate()) {
			String repoValidationUri = repoProps.getValidationUri();
			if (repoValidationUri == null || repoValidationUri.equals("")) {
				repoValidationUri = sessionProps.getValidationUri();
			}
			repoProps.setValidationUri(repoValidationUri);
			if(repoValidationUri == null || repoValidationUri.equals("") || repoValidationUri.equalsIgnoreCase("none")) {
				sessionProps.setValidate(false);
				harvestlogger.info("Validation is off");
				return;
			}
			try {
				Validator.updatePropertiesFileFromRemote();
				validator.initFromPropertiesManager();
				if (!validator.canValidateScheme(repoValidationUri)) {
					short i = 15;
					throw new OAIException(i, "Validator has no information about the given validationScheme (given schemeURI was : "
							+ repoValidationUri + ")");
				}
			} catch (ClassNotFoundException e1) {
				short i = 15;
				throw new OAIException(i, "Validator did not initialize correctly. (Error was : " + e1.getMessage());
			} catch (InstantiationException e1) {
				short i = 15;
				throw new OAIException(i, "Validator did not initialize correctly. (Error was : " + e1.getMessage());
			} catch (IllegalAccessException e1) {
				short i = 15;
				throw new OAIException(i, "Validator did not initialize correctly. (Error was : " + e1.getMessage());
			} catch (InitialisationException e) {
				short i = 15;
				throw new OAIException(i, "Validator did not initialize correctly. (Error was : " + e.getMessage());
			}
		}else {
			harvestlogger.info("Validation is off");
		}
	}

	public static void main(String[] args) throws Exception {
		// http://arxiv.org/oai2
		// 1900 http://aerialphotos.grainger.uiuc.edu/oai.asp
		// 145543 http://memory.loc.gov/cgi-bin/oai2_0
		// 10 http://alcme.oclc.org/bookmarks/servlet/OAIHandler
		// 71 http://alcme.oclc.org/openurl/servlet/OAIHandler
		// 2173 http://digital.library.ucla.edu/oai/sheetmusicdp
		// citeseer http://cs1.ist.psu.edu/cgi-bin/oai.cgi
		// http://www.nla.gov.au/apps/oaicat/servlet/OAIHandler
		// http://re.cs.uct.ac.za/

		// NOTE : HAVE TO INIT PROP FILE OR PUT IT IN ROOT DIR

		PropertiesManager.getInstance().init("install/ariadneV4.properties");

		String dir = PropertiesManager.getInstance().getProperty("log.logDirectory");
		if (!dir.equals("")) {
			System.setProperty("logdir", dir);
		}
		String prefix = "install";
		String file = "oai_log.properties";
		// if the log4j-init-file is not set, then no point in trying
		if (file != null) {
			PropertyConfigurator.configure(prefix + File.separator + file);
		}

		// performHarvest("harvestToDisk",
		// "http://klascement.isource.be/oai/index.php", "oai_lom",
		// "2006-02-15", "2006-02-17", "");

		// performHarvest("harvestToLucene",
		// "http://memory.loc.gov/cgi-bin/oai2_0", "oai_dc", "2006-11-01",
		// "2006-12-01");

		// performHarvest("harvestToDisk",
		// "http://localhost:8080/oaicat/OAIHandler", "oai_lom", "2000-11-01",
		// "2007-03-30");

		// performHarvest("harvestToDisk", "http://www.melt.fwu.de/oai2.php",
		// "oai_lom", "1000-11-01", "2007-11-15", "");

		// performHarvest("harvestToDisk",
		// "http://sdt.sulinet.hu:8082/MELT/OAI.aspx", "oai_lom", "1000-11-01",
		// "2007-12-18", "");

		// performHarvest("harvestToLucene",
		// "http://ariadne.cs.kuleuven.be/oaitarget/OAIHandler", "oai_lom_melt",
		// "1000-11-01", "2007-11-15", "MELT");

		//		performHarvest("harvestToDisk", "http://localhost:12181/mace-oaitarget/OAIHandler", "oai_lom", "1000-11-01", "2008-11-24", null, null, null);

	}
}
