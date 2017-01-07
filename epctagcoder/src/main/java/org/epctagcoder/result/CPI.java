package org.epctagcoder.result;

public class CPI extends Base {
	private String componentPartReference;
	private String serial;



	public CPI() {

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
				.append(String.format(", \"componentPartReference\": \"%s\"", getComponentPartReference()))
				.append(String.format(", \"serial\": \"%s\"", getSerial()))
				.append(String.format(", \"epcPureIdentityURI\": \"%s\"", getEpcPureIdentityURI()))
				.append(String.format(", \"epcTagURI\": \"%s\"", getEpcTagURI()))
				.append(String.format(", \"epcRawURI\": \"%s\"", getEpcRawURI()))
				.append(String.format(", \"binary\": \"%s\"", getBinary()))
				.append(String.format(", \"rfidTag\": \"%s\"", getRfidTag())).append(" }");
		
		return json.toString();
	}




	public String getComponentPartReference() {
		return componentPartReference;
	}




	public void setComponentPartReference(String componentPartReference) {
		this.componentPartReference = componentPartReference;
	}




	public String getSerial() {
		return serial;
	}




	public void setSerial(String serial) {
		this.serial = serial;
	}





	

}
