package org.epctagcoder.result;

public class SGLN extends Base {
    private String locationReference;
    private String extension;
    private String checkDigit;


    public SGLN() {

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
                .append(String.format(", \"locationReference\": \"%s\"", getLocationReference()))
                .append(String.format(", \"extension\": \"%s\"", getExtension()))
                .append(String.format(", \"checkDigit\": \"%s\"", getCheckDigit()))
                .append(String.format(", \"epcPureIdentityURI\": \"%s\"", getEpcPureIdentityURI()))
                .append(String.format(", \"epcTagURI\": \"%s\"", getEpcTagURI()))
                .append(String.format(", \"epcRawURI\": \"%s\"", getEpcRawURI()))
                .append(String.format(", \"binary\": \"%s\"", getBinary()))
                .append(String.format(", \"rfidTag\": \"%s\"", getRfidTag())).append(" }");

        return json.toString();
    }


    public String getLocationReference() {
        return locationReference;
    }


    public void setLocationReference(String locationReference) {
        this.locationReference = locationReference;
    }


    public String getExtension() {
        return extension;
    }


    public void setExtension(String extension) {
        this.extension = extension;
    }


    public String getCheckDigit() {
        return checkDigit;
    }


    public void setCheckDigit(String checkDigit) {
        this.checkDigit = checkDigit;
    }


}
