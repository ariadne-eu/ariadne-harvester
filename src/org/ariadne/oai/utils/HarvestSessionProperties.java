package org.ariadne.oai.utils;

import org.ariadne.config.PropertiesManager;
import org.ariadne.oai.InvalidRecords;
import org.ariadne.oai.config.servlet.cron.SingleHarvestingJob;

import uiuc.oai.OAIRepository;

public class HarvestSessionProperties {

	protected String until;
	protected String set;
	protected boolean validate;
	protected String validationUri;
	protected String transformationID;
	protected SingleHarvestingJob job;
	protected boolean repositoryLogs;
	protected InvalidRecords invalidRecords;
	protected int batchSize;
	protected boolean addGlobalLOID;
	protected boolean addGlobalMetadataID;
	protected boolean trackValidationErrors = false; 
	protected OAIRepository repository;

	public String getUntil() {
		return until;
	}
	public void setUntil(String until) {
		this.until = until;
	}
	public String getSet() {
		return set;
	}
	public void setSet(String set) {
		this.set = set;
	}
	public String getValidationUri() {
		return validationUri;
	}
	public void setValidationUri(String validationUri) {
		this.validationUri = validationUri;
	}
	public SingleHarvestingJob getJob() {
		return job;
	}
	public void setJob(SingleHarvestingJob job) {
		this.job = job;
	}
	public void setRepositoryLogs(boolean repositoryLogs) {
		this.repositoryLogs = repositoryLogs;
	}
	public boolean repositoryLogs() {
		return repositoryLogs;
	}
	public boolean validate() {
		return validate;
	}
	public void setValidate(boolean validate) {
		this.validate = validate;
	}
	public InvalidRecords getInvalidRecords() {
		return invalidRecords;
	}
	public void setInvalidRecords(InvalidRecords invalidRecords) {
		this.invalidRecords = invalidRecords;
	}
	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	public boolean addGlobalLOID() {
		return addGlobalLOID;
	}
	public void setAddGlobalLOID(boolean addGlobalLOID) {
		this.addGlobalLOID = addGlobalLOID;
	}
	public boolean addGlobalMetadataID() {
		return addGlobalMetadataID;
	}
	public void setAddGlobalMetadataID(boolean addGlobalMetadataID) {
		this.addGlobalMetadataID = addGlobalMetadataID;
	}
	public boolean trackValidationErrors() {
		return trackValidationErrors;
	}
	public void setTrackValidationErrors(boolean trackValidationErrors) {
		this.trackValidationErrors = trackValidationErrors;
	}
	public String getTransformationID() {
		return transformationID;
	}
	public void setTransformationID(String transformationID) {
		this.transformationID = transformationID;
	}
	public OAIRepository getRepository() {
		return repository;
	}
	public void setRepository(OAIRepository repository) {
		this.repository = repository;
	}

	
}
