package org.epctagcoder.result;

public abstract class Base {
	private String epcScheme;
	private String applicationIdentifier;
	private String tagSize;
	private String filterValue;
	private String partitionValue;
	private String prefixLength;
	private String companyPrefix;
	private String epcPureIdentityURI;
	private String epcTagURI;
	private String epcRawURI;
	private String binary;
	private String rfidTag;
	private String exception;
	

	public Base() {

	}
	
	
	public String getEpcScheme() {
		return epcScheme;
	}


	public void setEpcScheme(String epcScheme) {
		this.epcScheme = epcScheme;
	}


	public String getApplicationIdentifier() {
		return applicationIdentifier;
	}


	public void setApplicationIdentifier(String applicationIdentifier) {
		this.applicationIdentifier = applicationIdentifier;
	}


	public String getTagSize() {
		return tagSize;
	}


	public void setTagSize(String tagSize) {
		this.tagSize = tagSize;
	}


	public String getFilterValue() {
		return filterValue;
	}


	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}


	public String getPartitionValue() {
		return partitionValue;
	}


	public void setPartitionValue(String partitionValue) {
		this.partitionValue = partitionValue;
	}


	public String getPrefixLength() {
		return prefixLength;
	}


	public void setPrefixLength(String prefixLength) {
		this.prefixLength = prefixLength;
	}


	public String getCompanyPrefix() {
		return companyPrefix;
	}


	public void setCompanyPrefix(String companyPrefix) {
		this.companyPrefix = companyPrefix;
	}


	public String getEpcPureIdentityURI() {
		return epcPureIdentityURI;
	}


	public void setEpcPureIdentityURI(String epcPureIdentityURI) {
		this.epcPureIdentityURI = epcPureIdentityURI;
	}


	public String getEpcTagURI() {
		return epcTagURI;
	}


	public void setEpcTagURI(String epcTagURI) {
		this.epcTagURI = epcTagURI;
	}


	public String getEpcRawURI() {
		return epcRawURI;
	}


	public void setEpcRawURI(String epcRawURI) {
		this.epcRawURI = epcRawURI;
	}


	public String getBinary() {
		return binary;
	}


	public void setBinary(String binary) {
		this.binary = binary;
	}


	public String getRfidTag() {
		return rfidTag;
	}


	public void setRfidTag(String rfidTag) {
		this.rfidTag = rfidTag;
	}


	public String getException() {
		return exception;
	}


	public void setException(String exception) {
		this.exception = exception;
	}



	
	

}
