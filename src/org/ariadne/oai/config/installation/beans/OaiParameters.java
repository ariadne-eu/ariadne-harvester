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

package org.ariadne.oai.config.installation.beans;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import org.ariadne.config.PropertiesManager;

public class OaiParameters {

	String spiTargetUrl = "";
	String logFilesDir = "";
	TreeSet<String> databaseTypes = new TreeSet<String>();
	String sessionManagementUrl = "";
	String username = "";
	String password = "";
	String fileSystemDir = "";
	boolean addGlobalLOIdentifier;
	boolean addGlobalMetadataIdentifier;
	boolean validation;
	String metadataProviderSource = "";
	String metadataProviderValue = "";
	String validationScheme = "";
	String registryUrl = "";
	String cenSoapSpiTargetUrl = "";
	String cenSoapSessionManagementUrl = "";
	String cenSoapUsername = "";
	String cenSoapPassword = "";

	public OaiParameters() {
		addGlobalLOIdentifier = Boolean.valueOf(PropertiesManager.getInstance().getProperty("Harvest.addGlobalLOIdentifier")).booleanValue();
		addGlobalMetadataIdentifier = Boolean.valueOf(PropertiesManager.getInstance().getProperty("Harvest.addGlobalMetadataIdentifier")).booleanValue();
		validation = Boolean.valueOf(PropertiesManager.getInstance().getProperty("Harvest.validation")).booleanValue();
	}

	public String getValidationScheme() {
		return validationScheme;
	}

	public void setValidationScheme(String validationScheme) {
		this.validationScheme = validationScheme;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSessionManagementUrl() {
		return sessionManagementUrl;
	}

	public void setSessionManagementUrl(String sessionManagementUrl) {
		this.sessionManagementUrl = sessionManagementUrl;
	}

	public String getLogFilesDir() {
		return logFilesDir;
	}

	public void setLogFilesDir(String logFilesDir) {
		this.logFilesDir = logFilesDir;
	}

	public String getSpiTargetUrl() {
		return spiTargetUrl;
	}

	public void setSpiTargetUrl(String spiTargetUrl) {
		this.spiTargetUrl = spiTargetUrl;
	}

	public String getFileSystemDir() {
		return fileSystemDir;
	}

	public void setFileSystemDir(String fileSystemDir) {
		this.fileSystemDir = fileSystemDir;
	}

	public boolean getAddGlobalLOIdentifier() {
		return addGlobalLOIdentifier;
	}

	public void setAddGlobalLOIdentifier(boolean addGlobalLOIdentifier) {
		this.addGlobalLOIdentifier = addGlobalLOIdentifier;
	}

	public boolean getAddGlobalMetadataIdentifier() {
		return addGlobalMetadataIdentifier;
	}

	public void setAddGlobalMetadataIdentifier(boolean addGlobalMetadataIdentifier) {
		this.addGlobalMetadataIdentifier = addGlobalMetadataIdentifier;
	}

	public String getMetadataProviderSource() {
		return metadataProviderSource;
	}

	public void setMetadataProviderSource(String metadataProviderSource) {
		this.metadataProviderSource = metadataProviderSource;
	}

	public String getMetadataProviderValue() {
		return metadataProviderValue;
	}

	public void setMetadataProviderValue(String metadataProviderValue) {
		this.metadataProviderValue = metadataProviderValue;
	}

	public boolean getValidation() {
		return validation;
	}

	public void setValidation(boolean validation) {
		this.validation = validation;
	}

	public TreeSet<String> getDatabaseTypes() {
		return databaseTypes;
	}

	public String getDatabaseList() {
		String databaseList = "";
		Iterator<String> iter = databaseTypes.iterator();
		if (iter.hasNext()) {
			databaseList = iter.next();
		}
		while (iter.hasNext()) {
			databaseList += ";" + iter.next();
		}
		return databaseList;
	}

	public void setDatabaseTypes(TreeSet<String> databaseTypes) {
		this.databaseTypes = databaseTypes;
	}

	public String getCenSoapSpiTargetUrl() {
		return cenSoapSpiTargetUrl;
	}

	public void setCenSoapSpiTargetUrl(String cenSoapSpiTargetUrl) {
		this.cenSoapSpiTargetUrl = cenSoapSpiTargetUrl;
	}

	public String getCenSoapSessionManagementUrl() {
		return cenSoapSessionManagementUrl;
	}

	public void setCenSoapSessionManagementUrl(String cenSoapSessionManagementUrl) {
		this.cenSoapSessionManagementUrl = cenSoapSessionManagementUrl;
	}

	public String getCenSoapUsername() {
		return cenSoapUsername;
	}

	public void setCenSoapUsername(String cenSoapUsername) {
		this.cenSoapUsername = cenSoapUsername;
	}

	public String getCenSoapPassword() {
		return cenSoapPassword;
	}

	public void setCenSoapPassword(String cenSoapPassword) {
		this.cenSoapPassword = cenSoapPassword;
	}

	public String getRegistryUrl() {
		return registryUrl;
	}

	public void setRegistryUrl(String registryUrl) {
		this.registryUrl = registryUrl;
	}

}
