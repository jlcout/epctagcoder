package org.epctagcoder.result;

public class GRAI extends Base {
	private String assetType;
	private String serial;
	private String checkDigit;

	public GRAI() {

	}

	
	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getCheckDigit() {
		return checkDigit;
	}

	public void setCheckDigit(String checkDigit) {
		this.checkDigit = checkDigit;
	}


	
	@Override
	public String toString() {
		StringBuilder json = new StringBuilder();
		
		json.append(String.format("{ \"epcScheme\": \"%s\"", getEpcScheme()))
				.append(String.format(", \"applicationIdentifier\": \"%s\"", getApplicationIdentifier()))
				.append(String.format(", \"tagSize\": \"%s\"", getTagSize()))
				.append(String.format(", \"filterValue\": \"%s\"", getFilterValue()))
				.append(String.format(", \"partitionValue\": \"%s\"", getPartitionValue()))
				.append(String.format(", \"prefixLength\": \"%s\"", getPrefixLength()))
				.append(String.format(", \"companyPrefix\": \"%s\"", getCompanyPrefix()))
				.append(String.format(", \"assetType\": \"%s\"", getAssetType()))
				.append(String.format(", \"serial\": \"%s\"", getSerial()))
				.append(String.format(", \"checkDigit\": \"%s\"", getCheckDigit()))
				.append(String.format(", \"epcPureIdentityURI\": \"%s\"", getEpcPureIdentityURI()))
				.append(String.format(", \"epcTagURI\": \"%s\"", getEpcTagURI()))
				.append(String.format(", \"epcRawURI\": \"%s\"", getEpcRawURI()))
				.append(String.format(", \"binary\": \"%s\"", getBinary()))
				.append(String.format(", \"rfidTag\": \"%s\"", getRfidTag())).append(" }");
		
		return json.toString();
	}









	

}
