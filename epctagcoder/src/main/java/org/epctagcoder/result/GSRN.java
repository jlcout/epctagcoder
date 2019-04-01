package org.epctagcoder.result;

public class GSRN extends Base {
	private String serviceReference;
	private String checkDigit;

	public GSRN() {

	}


	public String getServiceReference() {
		return serviceReference;
	}

	public void setServiceReference(String serviceReference) {
		this.serviceReference = serviceReference;
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
				.append(String.format(", \"serviceReference\": \"%s\"", getServiceReference()))
				.append(String.format(", \"checkDigit\": \"%s\"", getCheckDigit()))
				.append(String.format(", \"epcPureIdentityURI\": \"%s\"", getEpcPureIdentityURI()))
				.append(String.format(", \"epcTagURI\": \"%s\"", getEpcTagURI()))
				.append(String.format(", \"epcRawURI\": \"%s\"", getEpcRawURI()))
				.append(String.format(", \"binary\": \"%s\"", getBinary()))
				.append(String.format(", \"rfidTag\": \"%s\"", getRfidTag())).append(" }");
		
	
/*		
		json.append("{ \"epcScheme\": \"").append(getEpcScheme()).append("\"") 
				.append(", \"applicationIdentifier\": \"").append(getApplicationIdentifier()).append("\"")
				.append(", \"tagSize\": \"").append(getTagSize()).append("\"")
				.append(", \"filterValue\": \"").append(getFilterValue()).append("\"")
				.append(", \"partitionValue\": \"").append(getPartitionValue()).append("\"")
				.append(", \"prefixLength\": \"").append(getPrefixLength()).append("\"")
				.append(", \"companyPrefix\": \"").append(getCompanyPrefix()).append("\"")
				.append(", \"serviceReference\": \"").append(getServiceReference()).append("\"")
				.append(", \"checkDigit\": \"").append(getCheckDigit()).append("\"")
				.append(", \"epcPureIdentityURI\": \"").append(getEpcPureIdentityURI()).append("\"")
				.append(", \"epcTagURI\": \"").append(getEpcTagURI()).append("\"")
				.append(", \"epcRawURI\": \"").append(getEpcRawURI()).append("\"")
				.append(", \"binary\": \"").append(getBinary()).append("\"")
				.append(", \"rfidTag\": \"").append(getRfidTag()).append("\" }");
		*/
		
		
		return json.toString();
	}




	

}
