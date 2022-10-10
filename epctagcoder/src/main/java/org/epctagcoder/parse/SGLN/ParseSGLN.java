package org.epctagcoder.parse.SGLN;

import org.epctagcoder.option.PrefixLength;
import org.epctagcoder.option.SGLN.SGLNFilterValue;
import org.epctagcoder.option.SGLN.SGLNHeader;
import org.epctagcoder.option.SGLN.SGLNTagSize;
import org.epctagcoder.option.SGLN.partitionTable.SGLNPartitionTableList;
import org.epctagcoder.option.TableItem;
import org.epctagcoder.result.SGLN;
import org.epctagcoder.util.Converter;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseSGLN {

    private SGLN sgln = new SGLN();
    private String companyPrefix;
    private PrefixLength prefixLength;
    private SGLNTagSize tagSize;
    private SGLNFilterValue filterValue;
    private String locationReference;
    private String extension;
    private String rfidTag;
    private String epcTagURI;
    private String epcPureIdentityURI;
    private TableItem tableItem;
    private int remainder;

    private ParseSGLN(Steps steps) {
        companyPrefix = steps.getCompanyPrefix();
        tagSize = steps.getTagSize();
        filterValue = steps.getFilterValue();
        locationReference = steps.getLocationReference();
        extension = steps.getExtension();
        rfidTag = steps.getRfidTag();
        epcTagURI = steps.getEpcTagURI();
        epcPureIdentityURI = steps.getEpcPureIdentityURI();
        parse();
    }

    public static ChoiceStep Builder() throws Exception {
        return new Steps();
    }

    private void parse() {
        Optional<String> optionalCompanyPrefix = Optional.ofNullable(companyPrefix);
        Optional<String> optionalRfidTag = Optional.ofNullable(rfidTag);
        Optional<String> optionalEpcTagURI = Optional.ofNullable(epcTagURI);
        Optional<String> optionalEpcPureIdentityURI = Optional.ofNullable(epcPureIdentityURI);

        if (optionalRfidTag.isPresent()) {
            parseRfidTag();
        } else {
            if (optionalCompanyPrefix.isPresent()) {
                SGLNPartitionTableList partitionTableList = new SGLNPartitionTableList();
                prefixLength = PrefixLength.forCode(companyPrefix.length());
                validateCompanyPrefix();
                tableItem = partitionTableList.getPartitionByL(prefixLength.getValue());
                validateLocationReference();
                validateExtension();
            } else {
                if (optionalEpcTagURI.isPresent()) {
                    parseEpcTagURI();
                } else if (optionalEpcPureIdentityURI.isPresent()) {
                    parseEpcPureIdentityURI();
                }
            }
            SGLNPartitionTableList partitionTableList = new SGLNPartitionTableList();
            tableItem = partitionTableList.getPartitionByL(prefixLength.getValue());
        }

        String outputBin = getBinary();
        String outputHex = Converter.binToHex(outputBin);
        sgln.setEpcScheme("sgln");
        sgln.setApplicationIdentifier("AI 254");
        sgln.setTagSize(Integer.toString(tagSize.getValue()));
        sgln.setFilterValue(Integer.toString(filterValue.getValue()));
        sgln.setPartitionValue(Integer.toString(tableItem.getPartitionValue()));
        sgln.setPrefixLength(Integer.toString(prefixLength.getValue()));
        sgln.setCompanyPrefix(companyPrefix);
        sgln.setLocationReference(locationReference);
        sgln.setExtension(extension);
        sgln.setCheckDigit(Integer.toString(getCheckDigit()));
        sgln.setEpcPureIdentityURI(String.format("urn:epc:id:sgln:%s.%s.%s", companyPrefix, locationReference, extension));
        sgln.setEpcTagURI(String.format("urn:epc:tag:sgln-%s:%s.%s.%s.%s", tagSize.getValue(), filterValue.getValue(), companyPrefix, locationReference, extension));
        sgln.setEpcRawURI(String.format("urn:epc:raw:%s.x%s", tagSize.getValue() + remainder, outputHex));
        sgln.setBinary(outputBin);
        sgln.setRfidTag(outputHex);

    }

    private void parseRfidTag() {
        String inputBin = Converter.hexToBin(rfidTag);
        String headerBin = inputBin.substring(0, 8);
        String filterBin = inputBin.substring(8, 11);
        String partitionBin = inputBin.substring(11, 14);
        SGLNPartitionTableList partitionTableList = new SGLNPartitionTableList();

        tagSize = SGLNTagSize.forCode(SGLNHeader.forCode(headerBin).getTagSize());
        tableItem = partitionTableList.getPartitionByValue(Integer.parseInt(partitionBin, 2));
        String filterDec = Long.toString(Long.parseLong(filterBin, 2));
        String companyPrefixBin = inputBin.substring(14, 14 + tableItem.getM());
        String locationReferenceBin = inputBin.substring(14 + tableItem.getM(), 14 + tableItem.getM() + tableItem.getN());
        String extensionBin = inputBin.substring(14 + tableItem.getM() + tableItem.getN());

        String companyPrefixDec = Converter.binToDec(companyPrefixBin);
        String locationReferenceDec = Converter.binToDec(locationReferenceBin);

        locationReference = Converter.strZero(locationReferenceDec, tableItem.getDigits());

        if (tagSize.getSerialBitCount() == 140) {
            extensionBin = Converter.convertBinToBit(extensionBin, 7, 8);
            extension = Converter.binToString(extensionBin);
        } else if (tagSize.getSerialBitCount() == 41) {
            extension = Converter.binToDec(extensionBin);
        }
        companyPrefix = Converter.strZero(companyPrefixDec, tableItem.getL()); // strzero aqui
        filterValue = SGLNFilterValue.forCode(Integer.parseInt(filterDec));
        prefixLength = PrefixLength.forCode(tableItem.getL());


    }

    private void parseEpcTagURI() {
        Pattern pattern = Pattern.compile("(urn:epc:tag:sgln-)(96|195)\\:([0-7])\\.(\\d+)\\.(\\d+)\\.((\\w|/)+)");
        Matcher matcher = pattern.matcher(epcTagURI);

        if (matcher.matches()) {
            tagSize = SGLNTagSize.forCode(Integer.parseInt(matcher.group(2)));
            filterValue = SGLNFilterValue.forCode(Integer.parseInt(matcher.group(3)));
            companyPrefix = matcher.group(4);
            prefixLength = PrefixLength.forCode(matcher.group(4).length());
            locationReference = matcher.group(5);
            extension = matcher.group(6);
        } else {
            throw new IllegalArgumentException("EPC Tag URI is invalid");
        }
    }

    private void parseEpcPureIdentityURI() {
        Pattern pattern = Pattern.compile("(urn:epc:id:sgln)\\:(\\d+)\\.(\\d+)\\.(\\w+)");
        Matcher matcher = pattern.matcher(epcPureIdentityURI);

        if (matcher.matches()) {
            companyPrefix = matcher.group(2);
            prefixLength = PrefixLength.forCode(matcher.group(2).length());
            locationReference = matcher.group(3);
            extension = matcher.group(4);
        } else {
            throw new IllegalArgumentException("EPC Pure Identity is invalid");
        }
    }

    private String getBinary() {
        StringBuilder bin = new StringBuilder();

        remainder = (int) (Math.ceil((tagSize.getValue() / 16.0)) * 16) - tagSize.getValue();
        bin.append(Converter.decToBin(tagSize.getHeader(), 8));
        bin.append(Converter.decToBin(filterValue.getValue(), 3));
        bin.append(Converter.decToBin(tableItem.getPartitionValue(), 3));
        bin.append(Converter.decToBin(Integer.parseInt(companyPrefix), tableItem.getM()));
        bin.append(Converter.decToBin(Integer.parseInt(locationReference), tableItem.getN()));

        if (tagSize.getValue() == 195) {
            bin.append(Converter.fill(Converter.StringToBinary(extension, 7), tagSize.getSerialBitCount() + remainder));
        } else if (tagSize.getValue() == 96) {
            bin.append(Converter.decToBin(extension, tagSize.getSerialBitCount() + remainder));
        }

        return bin.toString();
    }

    private Integer getCheckDigit() {
        String value = companyPrefix +
                locationReference;

        return (10 - ((3
                * (Character.getNumericValue(value.charAt(1)) + Character.getNumericValue(value.charAt(3))
                + Character.getNumericValue(value.charAt(5))
                + Character.getNumericValue(value.charAt(7)) + Character.getNumericValue(value.charAt(9))
                + Character.getNumericValue(value.charAt(11)))
                + (Character.getNumericValue(value.charAt(0)) + Character.getNumericValue(value.charAt(2))
                + Character.getNumericValue(value.charAt(4)) + Character.getNumericValue(value.charAt(6))
                + Character.getNumericValue(value.charAt(8)) + Character.getNumericValue(value.charAt(10))))
                % 10)) % 10;
    }
 
    public SGLN getSGLN() {
        return sgln;
    }

    public String getRfidTag() {
        return Converter.binToHex(getBinary());
    }


    private void validateCompanyPrefix() {
        Optional<PrefixLength> optionalPrefixLength = Optional.ofNullable(prefixLength);
        if (optionalPrefixLength.isPresent()) {
            throw new IllegalArgumentException("Company Prefix is invalid. Length not found in the partition table");
        }
    }

    private void validateLocationReference() {
        if (locationReference.length() != tableItem.getDigits()) {
            throw new IllegalArgumentException(String.format("Location Reference \"%s\" has %d length and should have %d length",
                    locationReference, locationReference.length(), tableItem.getDigits()));
        }
    }

    private void validateExtension() {
        if (tagSize.getValue() == 195) {
            if (extension.length() > tagSize.getSerialMaxLenght()) {
                throw new IllegalArgumentException(String.format("Extension value is out of range. Should be up to %d alphanumeric characters",
                        tagSize.getSerialMaxLenght()));
            }
        } else if (tagSize.getValue() == 96) {
            if (Long.parseLong(extension) > tagSize.getSerialMaxValue()) {
                throw new IllegalArgumentException(String.format("Extension value is out of range. Should be less than or equal %d",
                        tagSize.getSerialMaxValue()));
            }
        }
    }

    public static interface ChoiceStep {
        LocationReferenceStep withCompanyPrefix(String companyPrefix);

        BuildStep withRFIDTag(String rfidTag);

        BuildStep withEPCTagURI(String epcTagURI);

        TagSizeStep withEPCPureIdentityURI(String epcPureIdentityURI);
    }

    public static interface BuildStep {
        ParseSGLN build();
    }

    public static interface TagSizeStep {
        FilterValueStep withTagSize(SGLNTagSize tagSize);
    }

    public static interface LocationReferenceStep {
        ExtensionStep withLocationReference(String locationReference);
    }

    public static interface ExtensionStep {
        TagSizeStep withExtension(String extension);
    }

    public static interface FilterValueStep {
        BuildStep withFilterValue(SGLNFilterValue filterValue);
    }

    private static class Steps implements ChoiceStep, LocationReferenceStep, ExtensionStep, TagSizeStep, FilterValueStep, BuildStep {

        private String companyPrefix;
        private SGLNTagSize tagSize;
        private SGLNFilterValue filterValue;
        private String locationReference;
        private String extension;

        private String rfidTag;
        private String epcTagURI;
        private String epcPureIdentityURI;

        @Override
        public ParseSGLN build() {
            return new ParseSGLN(this);
        }

        @Override
        public LocationReferenceStep withCompanyPrefix(String companyPrefix) {
            this.companyPrefix = companyPrefix;
            return this;
        }

        @Override
        public BuildStep withRFIDTag(String rfidTag) {
            this.rfidTag = rfidTag;
            return this;
        }

        @Override
        public BuildStep withEPCTagURI(String epcTagURI) {
            this.epcTagURI = epcTagURI;
            return this;
        }

        @Override
        public TagSizeStep withEPCPureIdentityURI(String epcPureIdentityURI) {
            this.epcPureIdentityURI = epcPureIdentityURI;
            return this;
        }

        @Override
        public TagSizeStep withExtension(String extension) {
            this.extension = extension;
            return this;
        }

        @Override
        public BuildStep withFilterValue(SGLNFilterValue filterValue) {
            this.filterValue = filterValue;
            return this;
        }

        @Override
        public ExtensionStep withLocationReference(String locationReference) {
            this.locationReference = locationReference;
            return this;
        }

        @Override
        public FilterValueStep withTagSize(SGLNTagSize tagSize) {
            this.tagSize = tagSize;
            return this;
        }

        public String getCompanyPrefix() {
            return companyPrefix;
        }

        public SGLNTagSize getTagSize() {
            return tagSize;
        }

        public SGLNFilterValue getFilterValue() {
            return filterValue;
        }

        public String getLocationReference() {
            return locationReference;
        }

        public String getExtension() {
            return extension;
        }

        public String getRfidTag() {
            return rfidTag;
        }

        public String getEpcTagURI() {
            return epcTagURI;
        }

        public String getEpcPureIdentityURI() {
            return epcPureIdentityURI;
        }
    }


}


