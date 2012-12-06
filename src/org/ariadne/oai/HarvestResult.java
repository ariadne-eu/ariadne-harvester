package org.ariadne.oai;

import java.util.Vector;

import uiuc.oai.OAIRecordList;

public class HarvestResult {

	protected OAIRecordList recordList;
	protected int harvestStatus = OAIHarvester.NONEW;
	protected Vector<Record> metadataRecords = new Vector<Record>();
	protected Vector<Record> deletedRecords = new Vector<Record>();
	
	public OAIRecordList getRecordList() {
		return recordList;
	}
	public void setRecordList(OAIRecordList recordList) {
		this.recordList = recordList;
	}
	public int getHarvestStatus() {
		return harvestStatus;
	}
	public void setHarvestStatus(int harvestStatus) {
		this.harvestStatus = harvestStatus;
	}
	public Vector<Record> getMetadataRecords() {
		return metadataRecords;
	}
	public void addMetadataRecord(Record record) {
		getMetadataRecords().add(record);
	}
	public Vector<Record> getDeletedRecords() {
		return deletedRecords;
	}
	public void addDeletedRecord(Record deletedRecord) {
		getDeletedRecords().add(deletedRecord);
	}
	public void updateHarvestStatus(int newStatus) {
		setHarvestStatus(Math.max(getHarvestStatus(), newStatus));		
	}
	
}
