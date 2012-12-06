package org.ariadne.oai;

import org.jdom.Document;
import org.jdom.Element;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;

public class Record {

	protected Element metadata;
	protected String oaiIdentifier;
	protected OAIRecord oaiRecord;
	
	public Element getMetadata() {
		return metadata;
	}
	public void setMetadata(Element metadata) {
		this.metadata = metadata;
	}
	public String getOaiIdentifier() {
		return oaiIdentifier;
	}
	public void setOaiIdentifier(String oaiIdentifier) {
		if(oaiIdentifier != null)this.oaiIdentifier = oaiIdentifier.trim();
	}
	public OAIRecord getOaiRecord() {
		return oaiRecord;
	}
	public void setOaiRecord(OAIRecord oaiRecord) throws OAIException {
			this.oaiRecord = oaiRecord;
			setMetadata(oaiRecord.getMetadata());
			setOaiIdentifier(oaiRecord.getIdentifier());
	}
	
	
}
