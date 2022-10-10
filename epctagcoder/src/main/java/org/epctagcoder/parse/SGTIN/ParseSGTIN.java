package org.epctagcoder.parse.SGTIN;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.epctagcoder.exception.EPCParseException;
import org.epctagcoder.option.PrefixLength;
import org.epctagcoder.option.TableItem;
import org.epctagcoder.option.SGTIN.SGTINExtensionDigit;
import org.epctagcoder.option.SGTIN.SGTINFilterValue;
import org.epctagcoder.option.SGTIN.SGTINHeader;
import org.epctagcoder.option.SGTIN.SGTINTagSize;
import org.epctagcoder.option.SGTIN.partitionTable.SGTINPartitionTableList;
import org.epctagcoder.result.SGTIN;
import org.epctagcoder.util.Converter;

public class ParseSGTIN {
	
	private final SGTIN sgtin = new SGTIN();
	private final String rfidTag;
	private final String epcTagURI;
	private final String epcPureIdentityURI;
	
	private SGTINExtensionDigit extensionDigit;
	private String companyPrefix;
	private PrefixLength prefixLength;
	private SGTINTagSize tagSize;
	private SGTINFilterValue filterValue;
	private String itemReference;
	private String serial;
	private TableItem tableItem;
	private int remainder;
	
	public static ChoiceStep Builder() {
		return new Steps();
	}

	private ParseSGTIN(Steps steps) throws EPCParseException {
		this.extensionDigit = steps.extensionDigit;
		this.companyPrefix = steps.companyPrefix;
		this.tagSize = steps.tagSize;
		this.filterValue = steps.filterValue;
		this.itemReference = steps.itemReference;
		this.serial = steps.serial;
		this.rfidTag = steps.rfidTag;
		this.epcTagURI = steps.epcTagURI;
		this.epcPureIdentityURI = steps.epcPureIdentityURI;
		parse();
	}
	
	private void parseRfidTag() throws EPCParseException {
		String inputBin = Converter.hexToBin(rfidTag);
		String headerBin = inputBin.substring(0, 8);
		String filterBin = inputBin.substring(8, 11);
		String partitionBin = inputBin.substring(11, 14);
		SGTINPartitionTableList sgtinPartitionTableList = new SGTINPartitionTableList();
		
		tagSize = SGTINTagSize.forCode(SGTINHeader.forCode(headerBin).getTagSize());
		tableItem = sgtinPartitionTableList.getPartitionByValue( Integer.parseInt(partitionBin, 2) );
		
		String filterDec = Long.toString( Long.parseLong(filterBin, 2) );
		String companyPrefixBin = inputBin.substring(14, 14+tableItem.getM());
		String itemReferenceWithExtensionBin = inputBin.substring(14+tableItem.getM(), 14+tableItem.getM()+tableItem.getN());
		
		String serialBin = inputBin.substring(14+tableItem.getM()+tableItem.getN()  )
				.substring(0, tagSize.getSerialBitCount());
		
		String companyPrefixDec = Converter.binToDec(companyPrefixBin);
		String itemReferenceWithExtensionDec = Converter.strZero(Converter.binToDec(itemReferenceWithExtensionBin), tableItem.getDigits());
		String extensionDec = itemReferenceWithExtensionDec.substring(0,1);
		
		itemReference = itemReferenceWithExtensionDec.substring(1);
		
		if (tagSize.getSerialBitCount()==140) {
			serialBin  = Converter.convertBinToBit(serialBin, 7, 8);
			serial = Converter.binToString(serialBin);
		} else if (tagSize.getSerialBitCount()==38) {
			serial = Converter.binToDec(serialBin);
		}
		
		companyPrefix = Converter.strZero(companyPrefixDec, tableItem.getL());
		extensionDigit = SGTINExtensionDigit.forCode( Integer.parseInt(extensionDec) );
		filterValue = SGTINFilterValue.forCode( Integer.parseInt(filterDec) );
		prefixLength = PrefixLength.forCode(tableItem.getL());
	}
	
	private void parseCompanyPrefix() throws EPCParseException {
		SGTINPartitionTableList sgtinPartitionTableList = new SGTINPartitionTableList();
		prefixLength = PrefixLength.forCode(companyPrefix.length());
		
		validateCompanyPrefix();
		
		tableItem = sgtinPartitionTableList.getPartitionByL(prefixLength.getValue());
		
		validateExtensionDigitAndItemReference();
		validateSerial();
	}
	
	private void parseTagUri() throws EPCParseException {
		Pattern pattern = Pattern.compile("(urn:epc:tag:sgtin-)(96|198):([0-7])\\.(\\d+)\\.([0-8])(\\d+)\\.(\\w+)");
		Matcher matcher = pattern.matcher(epcTagURI);
		
		if (matcher.matches()) {
			tagSize = SGTINTagSize.forCode(Integer.parseInt(matcher.group(2)));
			filterValue = SGTINFilterValue.forCode(Integer.parseInt(matcher.group(3)));
			companyPrefix = matcher.group(4);
			prefixLength = PrefixLength.forCode(matcher.group(4).length());
			extensionDigit = SGTINExtensionDigit.forCode(Integer.parseInt(matcher.group(5)));
			itemReference = matcher.group(6);
			serial = matcher.group(7);
		} else {
			throw new EPCParseException("EPC Tag URI is invalid");
		}
	}
	
	private void parsePureIdentityUri() throws EPCParseException {
		Pattern pattern = Pattern.compile("(urn:epc:id:sgtin):(\\d+)\\.([0-8])(\\d+)\\.(\\w+)");
		Matcher matcher = pattern.matcher(epcPureIdentityURI);
		
		if (matcher.matches()) {
			companyPrefix = matcher.group(2);
			prefixLength = PrefixLength.forCode(matcher.group(2).length());
			extensionDigit = SGTINExtensionDigit.forCode(Integer.parseInt(matcher.group(3)));
			itemReference = matcher.group(4);
			serial = matcher.group(5);
		} else {
			throw new EPCParseException("EPC Pure Identity is invalid");
		}
	}
	
	private void parse() throws EPCParseException {
		
		if(rfidTag != null) {
			parseRfidTag();
		} else if (extensionDigit != null) {
			parseCompanyPrefix();
		} else {
			if (epcTagURI != null) {
				parseTagUri();
			} else if (epcPureIdentityURI != null) {
				parsePureIdentityUri();
			}
			
			SGTINPartitionTableList sgtinPartitionTableList = new SGTINPartitionTableList();
			tableItem = sgtinPartitionTableList.getPartitionByL(prefixLength.getValue());
		}
		
		String outputBin = getBinary();
		String outputHex = Converter.binToHex( outputBin );
		
		sgtin.setEpcScheme("sgtin");
		sgtin.setApplicationIdentifier("AI 414 + AI 254");
		sgtin.setTagSize(Integer.toString(tagSize.getValue()));
		sgtin.setFilterValue(Integer.toString(filterValue.getValue()));
		sgtin.setPartitionValue(Integer.toString(tableItem.getPartitionValue()));
		sgtin.setPrefixLength(Integer.toString(prefixLength.getValue()));
		sgtin.setCompanyPrefix(companyPrefix);
		sgtin.setItemReference(itemReference);
		sgtin.setExtensionDigit(Integer.toString(extensionDigit.getValue()));
		sgtin.setSerial(serial);
		sgtin.setCheckDigit(Integer.toString(getCheckDigit()));
		sgtin.setEpcPureIdentityURI(String.format("urn:epc:id:sgtin:%s.%s%s.%s", companyPrefix, extensionDigit.getValue(), itemReference, serial));
		sgtin.setEpcTagURI(String.format("urn:epc:tag:sgtin-%s:%s.%s.%s%s.%s", tagSize.getValue(), filterValue.getValue(), companyPrefix, extensionDigit.getValue(), itemReference, serial));
		sgtin.setEpcRawURI(String.format("urn:epc:raw:%s.x%s", tagSize.getValue()+remainder, outputHex ));
		sgtin.setBinary(outputBin);
		sgtin.setRfidTag(outputHex);
	}	
	
	private String getBinary() {
		StringBuilder bin = new StringBuilder();
		
		remainder =  (int) (Math.ceil((tagSize.getValue()/16.0))*16)-tagSize.getValue();
		
		bin.append( Converter.decToBin(tagSize.getHeader(), 8) );
		bin.append( Converter.decToBin(filterValue.getValue(), 3) );
		bin.append( Converter.decToBin(tableItem.getPartitionValue(), 3) );
		bin.append( Converter.decToBin(companyPrefix, tableItem.getM()) );
		bin.append( Converter.decToBin(Integer.parseInt(extensionDigit.getValue() +itemReference), tableItem.getN()) );
		
		if (tagSize.getValue()==198) {		
			bin.append( Converter.fill(Converter.StringToBinary(serial, 7), tagSize.getSerialBitCount()+remainder ) );
		} else if (tagSize.getValue()==96) {
			bin.append( Converter.decToBin(serial, tagSize.getSerialBitCount()+remainder ) );
		}
		
		return bin.toString();
	}	
	
	private Integer getCheckDigit() {
		String value = extensionDigit.getValue() + companyPrefix + itemReference;
		
		return (10 - ((3
				* (Character.getNumericValue(value.charAt(0)) + Character.getNumericValue(value.charAt(2))
						+ Character.getNumericValue(value.charAt(4))
						+ Character.getNumericValue(value.charAt(6)) + Character.getNumericValue(value.charAt(8))
						+ Character.getNumericValue(value.charAt(10)) + Character.getNumericValue(value.charAt(12)))
				+ (Character.getNumericValue(value.charAt(1)) + Character.getNumericValue(value.charAt(3))
						+ Character.getNumericValue(value.charAt(5)) + Character.getNumericValue(value.charAt(7))
						+ Character.getNumericValue(value.charAt(9)) + Character.getNumericValue(value.charAt(11))))
				% 10)) % 10;
	}

	
	public SGTIN getSGTIN() {
		return sgtin;
	}
	
	public String getRfidTag() {
		return Converter.binToHex( getBinary() );
	}
	
	private void validateExtensionDigitAndItemReference() throws EPCParseException {
		StringBuilder value = new StringBuilder()
				.append(extensionDigit.getValue())
				.append(itemReference);
		
		if ( value.length()!=tableItem.getDigits() ) {
			String message = "Concatenation between Extension Digit \"%d\" and " +
					"Item Reference \"%s\" has %d length and should have %d length";
			throw new EPCParseException(String.format(message,
					extensionDigit.getValue(), itemReference, value.length(), tableItem.getDigits()));
		}
	}
	
	private void validateCompanyPrefix() throws EPCParseException {
		if(prefixLength == null) {
			throw new EPCParseException("Company Prefix is invalid. Length not found in the partition table");
		}
	}
	
	private void validateSerial() throws EPCParseException {
		if (tagSize.getValue()==198 ) {
			if ( serial.length()>tagSize.getSerialMaxLenght() ) {
				throw new EPCParseException("Serial value is out of range. Should be up to 20 alphanumeric characters");
			}
		} else if (tagSize.getValue()==96 ) {
			if ( Long.parseLong(serial) >tagSize.getSerialMaxValue() ) {                                            
				throw new EPCParseException("Serial value is out of range. Should be less than or equal 274,877,906,943");
			}
			if ( serial.startsWith("0") ) {
				throw new EPCParseException("Serial with leading zeros is not allowed");
			}
		}
		
	}
	
    public interface ChoiceStep {
    	ExtensionDigitStep withCompanyPrefix(String companyPrefix);
        BuildStep withRFIDTag(String rfidTag);
        BuildStep withEPCTagURI(String epcTagURI);
        TagSizeStep withEPCPureIdentityURI(String epcPureIdentityURI);
    }

    public interface ExtensionDigitStep {
    	ItemReferenceStep withExtensionDigit(SGTINExtensionDigit extensionDigit);
    }   
    
    public interface ItemReferenceStep {
    	SerialStep withItemReference(String itemReference);
    }
    
    public interface SerialStep {
    	TagSizeStep withSerial(String serial);
    }   
    
    public interface TagSizeStep {
    	FilterValueStep withTagSize( SGTINTagSize tagSize );
    }
    
    public interface FilterValueStep {
    	BuildStep withFilterValue( SGTINFilterValue filterValue );	
    }
    
    public interface BuildStep {
    	ParseSGTIN build() throws EPCParseException;
    }
	
    private static class Steps implements ChoiceStep, ExtensionDigitStep, ItemReferenceStep, SerialStep, TagSizeStep, FilterValueStep, BuildStep {
    	private SGTINExtensionDigit extensionDigit;
    	private String companyPrefix;
    	private SGTINTagSize tagSize;
    	private SGTINFilterValue filterValue;
    	private String itemReference;
    	private String serial;
    	private String rfidTag;
    	private String epcTagURI;
    	private String epcPureIdentityURI;

		@Override
		public ParseSGTIN build() throws EPCParseException {
			return new ParseSGTIN(this);
		}
		
		@Override
		public ItemReferenceStep withExtensionDigit(SGTINExtensionDigit extensionDigit) {
			this.extensionDigit = extensionDigit;
			return this;		
		}	
		
		@Override
		public SerialStep withItemReference(String itemReference) {
			this.itemReference = itemReference;
			return this;
		}
		
		@Override
		public TagSizeStep withSerial(String serial) {
			this.serial = serial;
			return this;
		}

		@Override
		public FilterValueStep withTagSize(SGTINTagSize tagSize) {
			this.tagSize = tagSize;
			return this;
		}
		
		@Override
		public BuildStep withFilterValue(SGTINFilterValue filterValue) {
			this.filterValue = filterValue;
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
		public ExtensionDigitStep withCompanyPrefix(String companyPrefix) {
			this.companyPrefix = companyPrefix;
			return this;
		}
    }
}



