package org.ariadne.oai;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import org.ariadne.oai.utils.ReposProperties;
import org.jdom.Element;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRepository;

public class InvalidRecords {

	protected OAIRepository repository;
	
	protected ReposProperties repoProperties;
	
	protected int pointer = 0;
	
	protected Vector<String> recordIds;
	
	public InvalidRecords(ReposProperties repoProperties) {
		OAIRepository repo = new OAIRepository();
		this.repoProperties = repoProperties;
		try {
			repo.setBaseURL(repoProperties.getBaseURL());
			setRepository(repo);
			getItems();
		} catch (OAIException e) {
			//
		}
	}
	
	private void getItems() throws OAIException {
		recordIds = new Vector<String>();
		Vector<String> temp = new Vector<String>();
        try {
			FileInputStream file = new FileInputStream(OAIHarvester.validationWorkFolder + java.io.File.separator + repoProperties.getRepositoryIdentifierInteral() + ".log");
			byte[] b = new byte[file.available()];
			file.read(b);
			file.close ();
			String recordString = new String (b);
			StringTokenizer tokenizer = new StringTokenizer(recordString,"\n");
			while(tokenizer.hasMoreTokens()) {
				temp.add(tokenizer.nextToken());
			}
			TreeSet<String> treeset = new TreeSet<String>(temp);
			recordIds.addAll(treeset);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void moveNext() throws OAIException {
		if(pointer <= recordIds.size()) {
			pointer++;
		}
		else {
			short i = 15;
			throw new OAIException(i,"No more invalid records available");
		}
	}
	
	public OAIRecord getCurrentRecord() throws OAIException {
		return repository.getRecord(getRecordIds().elementAt(pointer), repoProperties.getMetadataPrefix());
	}
	
//	public String getCurrentIdentifier() throws OAIException {
//		return getRecordIds().elementAt(pointer);
//	}

	public boolean moreItems() {
		return pointer < getRecordIds().size();
	}

	public Vector<String> getRecordIds() {
		return recordIds;
	}

	public void setRecordIds(Vector<String> recordIds) {
		this.recordIds = recordIds;
	}

	public OAIRepository getRepository() {
		return repository;
	}

	public void setRepository(OAIRepository repository) {
		this.repository = repository;
	}
}
