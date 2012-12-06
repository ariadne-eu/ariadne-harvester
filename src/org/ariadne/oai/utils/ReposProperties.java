package org.ariadne.oai.utils;

import java.util.HashMap;

public class ReposProperties {

	public static String repositoryName = "repositoryName";
	public static String repositoryIdentifier = "repositoryIdentifier";
	public static String providerName = "providerName";
	public static String latestHarvestedDatestamp = "latestHarvestedDatestamp";
	public static String active = "active";
	public static String metadataPrefix = "metadataPrefix";
	public static String metadataFormat = "metadataFormat";
	public static String harvestingSet = "harvestingSet";
	public static String granularity = "granularity";
	public static String autoReset = "autoReset";
	public static String validationUri = "validationUri";
	public static String statusLastHarvest = "statusLastHarvest";
	public static String baseURL = "baseURL";
	public static String transformationID = "transformationID";
	public static String registryIdentifierCatalog = "registryIdentifier.catalog";
	public static String registryIdentifierEntry = "registryIdentifier.entry";
	public static String skipPreOaiTest = "skipPreOaiTest";
	public static String harvestByGetRecord = "harvestByGetRecord";

	protected String repositoryIdentifierInteral;
//	protected String repositoryIdentifier;
	
	protected HashMap<String, String> properties = new HashMap<String, String>();
	
	protected final static HashMap<String, String> defaultProperties = new HashMap<String, String>();

	static {
		defaultProperties.put(providerName, "");
		defaultProperties.put(repositoryName, "");
		defaultProperties.put(repositoryIdentifier, "");
		defaultProperties.put(latestHarvestedDatestamp, "1000-01-01T00:00:00Z");
		defaultProperties.put(active, "Yes");
		defaultProperties.put(metadataPrefix, "oai_lom");
		defaultProperties.put(metadataFormat, "LOM");
		defaultProperties.put(harvestingSet, "");
		defaultProperties.put(granularity, "YYYY-MM-DDThh:mm:ssZ");
		defaultProperties.put(autoReset, "true");
		defaultProperties.put(validationUri, "");
		defaultProperties.put(statusLastHarvest, "-1");
		defaultProperties.put(transformationID, "");
		defaultProperties.put(registryIdentifierCatalog, "");
		defaultProperties.put(registryIdentifierEntry, "");
		defaultProperties.put(skipPreOaiTest, "false");
		defaultProperties.put(harvestByGetRecord, "false");
	}

	public String getProviderName() {
		return properties.get(providerName);
	}

	public void setProviderName(String providerName) {
		properties.put(ReposProperties.providerName, providerName);
	}

	public String getLatestHarvestedDatestamp() {
		return properties.get(latestHarvestedDatestamp);
	}

	public void setLatestHarvestedDatestamp(String latestHarvestedDatestamp) {
		properties.put(ReposProperties.latestHarvestedDatestamp, latestHarvestedDatestamp);
	}

	public String getActive() {
		return properties.get(active);
	}

	public void setActive(String active) {
		properties.put(ReposProperties.active, active);
	}

	public String getMetadataPrefix() {
		return properties.get(metadataPrefix);
	}

	public void setMetadataPrefix(String metadataPrefix) {
		properties.put(ReposProperties.metadataPrefix, metadataPrefix);
	}

	public String getMetadataFormat() {
		return properties.get(metadataFormat);
	}

	public void setMetadataFormat(String metadataFormat) {
		properties.put(ReposProperties.metadataFormat, metadataFormat);
	}

	public String getHarvestingSet() {
		return properties.get(harvestingSet);
	}

	public void setHarvestingSet(String harvestingSet) {
		properties.put(ReposProperties.harvestingSet, harvestingSet);
	}

	public String getGranularity() {
		return properties.get(granularity);
	}

	public void setGranularity(String granularity) {
		properties.put(ReposProperties.granularity, granularity);
	}

	public boolean getAutoReset() {
		return Boolean.valueOf(properties.get(autoReset)).booleanValue();
	}

	public void setAutoReset(boolean autoReset) {
		properties.put(ReposProperties.autoReset, String.valueOf(autoReset));
	}
	
	public void setAutoReset(String autoReset) {
		properties.put(ReposProperties.autoReset, autoReset);
	}

	public String getValidationUri() {
		return properties.get(validationUri);
	}

	public void setValidationUri(String validationUri) {
		properties.put(ReposProperties.validationUri, validationUri);
	}

	public String getStatusLastHarvest() {
		return properties.get(statusLastHarvest);
	}

	public void setStatusLastHarvest(String statusLastHarvest) {
		properties.put(ReposProperties.statusLastHarvest, statusLastHarvest);
	}

	public String getBaseURL() {
		return properties.get(baseURL);
	}

	public void setBaseURL(String baseURL) {
		properties.put(ReposProperties.baseURL, baseURL);
	}

	public static HashMap<String, String> getDefaultProperties() {
		return defaultProperties;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

	public String getRepositoryIdentifierInteral() {
		return repositoryIdentifierInteral;
	}

	public void setRepositoryIdentifierInteral(String repositoryIdentifierInteral) {
		this.repositoryIdentifierInteral = repositoryIdentifierInteral;
	}

	public String getRepositoryName() {
		return properties.get(repositoryName);
	}

	public void setRepositoryName(String repositoryName) {
		properties.put(ReposProperties.repositoryName, repositoryName);
	}

	public String getRepositoryIdentifier() {
		return properties.get(repositoryIdentifier);
	}

	public void setRepositoryIdentifier(String repositoryIdentifier) {
		properties.put(ReposProperties.repositoryIdentifier, repositoryIdentifier);
	} 

	public String getTransformationID() {
		return properties.get(transformationID);
	}

	public void setTransformationID(String transformationID) {
		properties.put(ReposProperties.transformationID, transformationID);
	}

	public String getRegistryIdentifierCatalog() {
		return properties.get(registryIdentifierCatalog);
	}

	public void setRegistryIdentifierCatalog(String registryIdentifierCatalog) {
		properties.put(ReposProperties.registryIdentifierCatalog,registryIdentifierCatalog);
	}

	public String getRegistryIdentifierEntry() {
		return properties.get(registryIdentifierEntry);
	}

	public void setRegistryIdentifierEntry(String registryIdentifierEntry) {
		properties.put(ReposProperties.registryIdentifierEntry, registryIdentifierEntry);
	}
	
	public boolean getSkipPreOaiTest() {
		return Boolean.valueOf(properties.get(skipPreOaiTest)).booleanValue();
	}

	public void setSkipPreOaiTest(boolean skipPreOaiTest) {
		properties.put(ReposProperties.skipPreOaiTest, String.valueOf(skipPreOaiTest));
	}
	
	public void setSkipPreOaiTest(String skipPreOaiTest) {
		properties.put(ReposProperties.skipPreOaiTest, skipPreOaiTest);
	}
	
	public boolean getHarvestByGetRecord() {
		return Boolean.valueOf(properties.get(harvestByGetRecord)).booleanValue();
	}

	public void setHarvestByGetRecord(boolean harvestByGetRecord) {
		properties.put(ReposProperties.harvestByGetRecord, String.valueOf(harvestByGetRecord));
	}
	
	public void setHarvestByGetRecord(String harvestByGetRecord) {
		properties.put(ReposProperties.harvestByGetRecord, harvestByGetRecord);
	}
}
