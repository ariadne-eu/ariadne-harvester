package org.ariadne.oai.config;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.xmlbeans.impl.common.ReaderInputStream;
import org.ariadne.config.PropertiesManager;
import org.ariadne.oai.config.servlet.logging.Log4jInit;
import org.ariadne.oai.utils.HarvesterUtils;
import org.ariadne.oai.utils.ReposProperties;
import org.ariadne.validation.Validator;
import org.ariadne.validation.exception.InitialisationException;
import org.ariadne.validation.utils.ValidationConstants;

public class UploadServletHandler {

	protected static UploadServletHandler handler = null;

	public static UploadServletHandler getInstance() {
		if(handler == null) handler = new UploadServletHandler();
		return handler;
	}

	protected UploadServletHandler() {

	}

	public void handleUpload(HttpServletRequest request, ServletContext application) {
		String contentType = request.getContentType();
		System.out.println("Content type is :: " + contentType);
		if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {
			try {
				DataInputStream in = new DataInputStream(request.getInputStream());
				int formDataLength = request.getContentLength();

				byte dataBytes[] = new byte[formDataLength];
				int byteRead = 0;
				int totalBytesRead = 0;
				while (totalBytesRead < formDataLength) {
					byteRead = in.read(dataBytes, totalBytesRead,formDataLength);
					totalBytesRead += byteRead;
				}

				String file = new String(dataBytes);
				//String saveFile = file.substring(file.indexOf("filename=\"") + 10);
				//saveFile = saveFile.substring(0, saveFile.indexOf("\n"));
				//saveFile = saveFile.substring(saveFile.lastIndexOf("\\") + 1,saveFile.indexOf("\""));
				//String propFile = application.getRealPath("install") + File.separator + "ariadneV4.properties";

				//out.print(dataBytes);

				int lastIndex = contentType.lastIndexOf("=");
				String boundary = contentType.substring(lastIndex + 1,
						contentType.length());
				//out.println(boundary);
				int pos;
				pos = file.indexOf("filename=\"");

				pos = file.indexOf("\n", pos) + 1;

				pos = file.indexOf("\n", pos) + 1;

				pos = file.indexOf("\n", pos) + 1;

				int boundaryLocation = file.indexOf(boundary, pos) - 4;
				int startPos = ((file.substring(0, pos)).getBytes()).length;
				int endPos = ((file.substring(0, boundaryLocation)).getBytes()).length;

				String outputFileString = application.getRealPath("install") + File.separator + "targets.properties";

				FileOutputStream fileOut = new FileOutputStream(outputFileString);

				//fileOut.write(dataBytes);
				fileOut.write(dataBytes, startPos, (endPos - startPos));
				fileOut.flush();
				fileOut.close();

				System.out.println("<center><br>File successfully saved as "
						+ outputFileString + "</center>");

				//PropertiesManager.getInstance().setPropertiesFile(propFile);
				//if (!PropertiesManager.getInstance().getPropertiesFile().exists())
				//	out.println("Could not find ariadneV4.properties template at '"	+ PropertiesManager.getInstance().getPropertiesFile()	+ "'");
				//PropertiesManager.getInstance().init();

				Properties properties = new Properties();
				try {
					FileInputStream fileInputStream = new FileInputStream(outputFileString);
					InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF8");
					properties.load(new ReaderInputStream(inputStreamReader,"UTF8"));
					replaceTargets(properties);
				} catch (IOException e) {
					
				}
				//				Validator.updatePropertiesFileFromRemote();
				//			} catch (InitialisationException e) {
				//				// TODO Auto-generated catch block
				//				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

	}

	public void replaceTargets(Properties properties) {
		HarvesterUtils.removeAllTargets();
		String sourceTargets = properties.getProperty("AllTargets.list");
		StringTokenizer st = new StringTokenizer(sourceTargets, ";");
		while (st.hasMoreTokens()) {
			String internalId = st.nextToken();
			Hashtable repoProperties = PropertiesManager.getInstance().getPropertyStartingWith(internalId + ".", properties);

			ReposProperties reposProps = new ReposProperties();
			reposProps.setRepositoryIdentifier(getProperty(internalId + "." + ReposProperties.repositoryIdentifier, repoProperties));
			reposProps.setRepositoryName(getProperty(internalId + "." + ReposProperties.repositoryName, repoProperties));
			reposProps.setBaseURL(getProperty(internalId + "." + ReposProperties.baseURL, repoProperties));
			reposProps.setProviderName(getProperty(internalId + "." + ReposProperties.providerName, repoProperties));
			reposProps.setLatestHarvestedDatestamp(getProperty(internalId + "." + ReposProperties.latestHarvestedDatestamp, repoProperties));
			reposProps.setActive(getProperty(internalId + "." + ReposProperties.active, repoProperties));
			reposProps.setMetadataPrefix(getProperty(internalId + "." + ReposProperties.metadataPrefix, repoProperties));
			reposProps.setMetadataFormat(getProperty(internalId + "." + ReposProperties.metadataFormat, repoProperties));
			reposProps.setHarvestingSet(getProperty(internalId + "." + ReposProperties.harvestingSet, repoProperties));
			reposProps.setAutoReset(getProperty(internalId + "." + ReposProperties.autoReset, repoProperties));
			reposProps.setValidationUri(getProperty(internalId + "." + ReposProperties.validationUri, repoProperties));
			reposProps.setGranularity(getProperty(internalId + "." + ReposProperties.granularity, repoProperties));
			HarvesterUtils.saveDetails(internalId,reposProps);
		}

		PropertiesManager.getInstance().saveProperty("AllTargets.list",sourceTargets);
	}

	private String getProperty(String propKey, Hashtable repoProperties) {
		Object prop = repoProperties.get(propKey);
		if(prop == null)prop = "";
		return (String)prop;
	}
}
