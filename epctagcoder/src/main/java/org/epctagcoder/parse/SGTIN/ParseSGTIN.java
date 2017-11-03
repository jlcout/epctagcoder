package org.epctagcoder.parse.SGTIN;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private SGTIN sgtin = new SGTIN();
	private SGTINExtensionDigit extensionDigit;
	private String companyPrefix;
	private PrefixLength prefixLength;
	private SGTINTagSize tagSize;
	private SGTINFilterValue filterValue;
	private String itemReference;
	private String serial;
	private String rfidTag;
	private String epcTagURI;
	private String epcPureIdentityURI;
	private TableItem tableItem;
	private int remainder;
	
    public static ChoiceStep Builder() {
        return new Steps();
    }

	private ParseSGTIN(Steps steps) {
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
	
	
	
	private void parse() {
		Optional<SGTINExtensionDigit> optionalCompanyPrefix = Optional.ofNullable(extensionDigit);
		Optional<String> optionalRfidTag = Optional.ofNullable(rfidTag);
		Optional<String> optionalEpcTagURI = Optional.ofNullable(epcTagURI);
		Optional<String> optionalEpcPureIdentityURI = Optional.ofNullable(epcPureIdentityURI);
		
		if ( optionalRfidTag.isPresent() ) {
			String inputBin = Converter.hexToBin(rfidTag);
			String headerBin = inputBin.substring(0, 8);
			String filterBin = inputBin.substring(8,11);
			String partitionBin = inputBin.substring(11,14);
			SGTINPartitionTableList sgtinPartitionTableList = new SGTINPartitionTableList();			

			tagSize = SGTINTagSize.forCode(SGTINHeader.forCode(headerBin).getTagSize());
			tableItem = sgtinPartitionTableList.getPartitionByValue( Integer.parseInt(partitionBin, 2) );
			
			String filterDec = Long.toString( Long.parseLong(filterBin, 2) );
			String companyPrefixBin = inputBin.substring(14,14+tableItem.getM());
			String itemReferenceWithExtensionBin = inputBin.substring(14+tableItem.getM(),14+tableItem.getM()+tableItem.getN());
			String serialBin = inputBin.substring(14+tableItem.getM()+tableItem.getN()  );
			String companyPrefixDec = Converter.binToDec(companyPrefixBin);
			String itemReferenceWithExtensionDec = Converter.binToDec(itemReferenceWithExtensionBin);
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
			
		} else {
		
			if ( optionalCompanyPrefix.isPresent() ) {
				SGTINPartitionTableList sgtinPartitionTableList = new SGTINPartitionTableList();				
				prefixLength = PrefixLength.forCode( companyPrefix.length() );

				validateCompanyPrefix();

				tableItem = sgtinPartitionTableList.getPartitionByL( prefixLength.getValue() );
				
				validateExtensionDigitAndItemReference();
				validateSerial();
				
			} else {
				
				if ( optionalEpcTagURI.isPresent() ) {
					Pattern pattern = Pattern.compile("(urn:epc:tag:sgtin-)(96|198)\\:([0-7])\\.(\\d+)\\.([0-8])(\\d+)\\.(\\w+)");
					Matcher matcher = pattern.matcher(epcTagURI);

					if ( matcher.matches() ) {
						tagSize = SGTINTagSize.forCode( Integer.parseInt(matcher.group(2)) );
						filterValue = SGTINFilterValue.forCode( Integer.parseInt(matcher.group(3)) );
						companyPrefix = matcher.group(4);
						prefixLength = PrefixLength.forCode( matcher.group(4).length() );
						extensionDigit = SGTINExtensionDigit.forCode( Integer.parseInt(matcher.group(5)) );
						itemReference = matcher.group(6);
						serial = matcher.group(7);					
					} else {
						throw new IllegalArgumentException("EPC Tag URI is invalid");
					}

				} else if ( optionalEpcPureIdentityURI.isPresent() ) {
					Pattern pattern = Pattern.compile("(urn:epc:id:sgtin)\\:(\\d+)\\.([0-8])(\\d+)\\.(\\w+)");
					Matcher matcher = pattern.matcher(epcPureIdentityURI);
					
					if ( matcher.matches() ) {
						companyPrefix = matcher.group(2);
						prefixLength = PrefixLength.forCode( matcher.group(2).length() );
						extensionDigit = SGTINExtensionDigit.forCode( Integer.parseInt(matcher.group(3)) );
						itemReference = matcher.group(4);;
						serial = matcher.group(5);
					} else {
						throw new IllegalArgumentException("EPC Pure Identity is invalid");
					}
					

				}			

				SGTINPartitionTableList sgtinPartitionTableList = new SGTINPartitionTableList();
				tableItem = sgtinPartitionTableList.getPartitionByL( prefixLength.getValue() );				
				
			}
			
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
		bin.append( Converter.decToBin(Integer.parseInt(companyPrefix), tableItem.getM()) );
		bin.append( Converter.decToBin(Integer.parseInt(Integer.toString(extensionDigit.getValue())+itemReference), tableItem.getN()) );
		
		if (tagSize.getValue()==198) {		
			bin.append( Converter.fill(Converter.StringtoBinary(serial, 7), tagSize.getSerialBitCount()+remainder ) );
		} else if (tagSize.getValue()==96) {
			bin.append( Converter.decToBin(serial, tagSize.getSerialBitCount()+remainder ) );
		}
		
		return bin.toString();
	}	
	
	
	private Integer getCheckDigit() {
		String value = new StringBuilder()
				.append(extensionDigit.getValue())
				.append(companyPrefix)
				.append(itemReference)
				.toString();

		Integer d14 = (10 - ((3
				* (Character.getNumericValue(value.charAt(0)) + Character.getNumericValue(value.charAt(2))
						+ Character.getNumericValue(value.charAt(4))
						+ Character.getNumericValue(value.charAt(6)) + Character.getNumericValue(value.charAt(8))
						+ Character.getNumericValue(value.charAt(10)) + Character.getNumericValue(value.charAt(12)))
				+ (Character.getNumericValue(value.charAt(1)) + Character.getNumericValue(value.charAt(3))
						+ Character.getNumericValue(value.charAt(5)) + Character.getNumericValue(value.charAt(7))
						+ Character.getNumericValue(value.charAt(9)) + Character.getNumericValue(value.charAt(11))))
				% 10)) % 10;
				
		return d14;
	}

	
	public SGTIN getSGTIN() {
		return sgtin;
	}
	
	public String getRfidTag() {
		return Converter.binToHex( getBinary() );
	}
	
	
	
	
	private void validateExtensionDigitAndItemReference() { 
		StringBuilder value = new StringBuilder()
				.append(extensionDigit.getValue())
				.append(itemReference);
		
		if ( value.length()!=tableItem.getDigits() ) {
			throw new IllegalArgumentException(String.format("Concatenation between Extension Digit \"%d\" and Item Reference \"%s\" has %d length and should have %d length",
					extensionDigit.getValue(), itemReference, value.length(), tableItem.getDigits()));
		}
	}
	
	private void validateCompanyPrefix() { 
		Optional<PrefixLength> optionalPefixLength = Optional.ofNullable(prefixLength);
		if ( !optionalPefixLength.isPresent() ) {
			throw new IllegalArgumentException("Company Prefix is invalid. Length not found in the partition table");
		}
		
	}
	
	private void validateSerial() { 
		if (tagSize.getValue()==198 ) {
			if ( serial.length()>tagSize.getSerialMaxLenght() ) {
				throw new IllegalArgumentException("Serial value is out of range. Should be up to 20 alphanumeric characters");
			}
		} else if (tagSize.getValue()==96 ) {
			if ( Long.parseLong(serial) >tagSize.getSerialMaxValue() ) {                                            
				throw new IllegalArgumentException("Serial value is out of range. Should be less than or equal 274,877,906,943");
			}
			if ( serial.startsWith("0") ) {
				throw new IllegalArgumentException("Serial with leading zeros is not allowed");
			}
		}
		
	}

	

	
    public static interface ChoiceStep {
    	ExtensionDigiStep withCompanyPrefix(String companyPrefix);    	
        BuildStep withRFIDTag(String rfidTag);
        BuildStep withEPCTagURI(String epcTagURI);
        TagSizeStep withEPCPureIdentityURI(String epcPureIdentityURI);
    }

    public static interface ExtensionDigiStep {
    	ItemReferenceStep withExtensionDigit(SGTINExtensionDigit extensionDigit);
    }   
    
    public static interface ItemReferenceStep {
    	SerialStep withItemReference(String itemReference);
    }
    
    public static interface SerialStep {
    	TagSizeStep withSerial(String serial);
    }   
    
    public static interface TagSizeStep {
    	FilterValueStep withTagSize( SGTINTagSize tagSize );
    }
    
    public static interface FilterValueStep {
    	BuildStep withFilterValue( SGTINFilterValue filterValue );	
    }
    
    public static interface BuildStep {
    	ParseSGTIN build();
    }

    
    private static class Steps implements ChoiceStep, ExtensionDigiStep, ItemReferenceStep, SerialStep, TagSizeStep, FilterValueStep, BuildStep {
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
		public ParseSGTIN build() {
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
		public ExtensionDigiStep withCompanyPrefix(String companyPrefix) {
			this.companyPrefix = companyPrefix;
			return this;
		}
		
    	
    }
    

}



