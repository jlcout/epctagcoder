package org.epctagcoder.parse.GSRNP;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.epctagcoder.option.PrefixLength;
import org.epctagcoder.option.TableItem;
import org.epctagcoder.option.GSRNP.GSRNPFilterValue;
import org.epctagcoder.option.GSRNP.GSRNPHeader;
import org.epctagcoder.option.GSRNP.GSRNPTagSize;
import org.epctagcoder.option.GSRNP.partitionTable.GSRNPPartitionTableList;
import org.epctagcoder.result.GSRNP;
import org.epctagcoder.util.Converter;

public class ParseGSRNP {
	private static final Integer RESERVED = 0; // 24 zero bits
	private GSRNP gsrnp = new GSRNP();
	private String companyPrefix;
	private PrefixLength prefixLength;
	private GSRNPTagSize tagSize;
	private GSRNPFilterValue filterValue;
	private String serviceReference;
	private String rfidTag;
	private String epcTagURI;
	private String epcPureIdentityURI;
	private TableItem tableItem;
	
    public static ChoiceStep Builder() {
        return new Steps();
    }

	private ParseGSRNP(Steps steps) {
		this.companyPrefix = steps.companyPrefix;
		this.tagSize = steps.tagSize;
		this.filterValue = steps.filterValue;
		this.serviceReference = steps.serviceReference;
		this.rfidTag = steps.rfidTag;
		this.epcTagURI = steps.epcTagURI;
		this.epcPureIdentityURI = steps.epcPureIdentityURI;
    	parse();
	}
	
	
	private void parse() {
		Optional<String> optionalCompanyPrefix = Optional.ofNullable(companyPrefix);
		Optional<String> optionalRfidTag = Optional.ofNullable(rfidTag);
		Optional<String> optionalEpcTagURI = Optional.ofNullable(epcTagURI);
		Optional<String> optionalEpcPureIdentityURI = Optional.ofNullable(epcPureIdentityURI);

		if ( optionalRfidTag.isPresent() ) {
			String inputBin = Converter.hexToBin(rfidTag);
			String headerBin = inputBin.substring(0, 8);
			String filterBin = inputBin.substring(8,11);
			String partitionBin = inputBin.substring(11,14);
			GSRNPPartitionTableList GSRNPPartitionTableList = new GSRNPPartitionTableList();

			tableItem = GSRNPPartitionTableList.getPartitionByValue( Integer.parseInt(partitionBin, 2) );
			
			String companyPrefixBin = inputBin.substring(14,14+tableItem.getM());
			String serialWithExtensionBin = inputBin.substring(14+tableItem.getM(),14+tableItem.getM()+tableItem.getN());
			String filterDec = Long.toString( Long.parseLong(filterBin, 2) );
			String companyPrefixDec = Converter.binToDec(companyPrefixBin); //Long.toString( Long.parseLong(companyPrefixBin, 2) );

			serviceReference = Converter.strZero(Converter.binToDec(serialWithExtensionBin), tableItem.getDigits() ); 
			companyPrefix = Converter.strZero(companyPrefixDec, tableItem.getL());
			filterValue = GSRNPFilterValue.forCode( Integer.parseInt(filterDec) );
			tagSize = GSRNPTagSize.forCode( GSRNPHeader.forCode(headerBin).getTagSize() );
			prefixLength = PrefixLength.forCode(tableItem.getL());
			
		} else {
			
			if ( optionalCompanyPrefix.isPresent() ) {
				GSRNPPartitionTableList GSRNPPartitionTableList = new GSRNPPartitionTableList();
				
				prefixLength = PrefixLength.forCode( companyPrefix.length() );
				
				validateCompanyPrefix();
				
				tableItem = GSRNPPartitionTableList.getPartitionByL( prefixLength.getValue() );
				
				validateServiceReference();
				
			} else {
				
				if ( optionalEpcTagURI.isPresent() ) {
					Pattern pattern = Pattern.compile("(urn:epc:tag:gsrnp-)(96)\\:([0-7])\\.(\\d+)\\.(\\d+)");
					Matcher matcher = pattern.matcher(epcTagURI);
				
					if ( matcher.matches() ) {
						tagSize = GSRNPTagSize.forCode( Integer.parseInt(matcher.group(2)) );
						filterValue = GSRNPFilterValue.forCode( Integer.parseInt(matcher.group(3)) );
						companyPrefix = matcher.group(4);
						prefixLength = PrefixLength.forCode( matcher.group(4).length() );
						serviceReference = matcher.group(5);					
					} else {
						throw new IllegalArgumentException("EPC Tag URI is invalid");
					}
					
				} else if ( optionalEpcPureIdentityURI.isPresent() ) {
					Pattern pattern = Pattern.compile("(urn:epc:id:gsrnp)\\:(\\d+)\\.(\\d+)");
					Matcher matcher = pattern.matcher(epcPureIdentityURI);
					
					if ( matcher.matches() ) {
						companyPrefix = matcher.group(2);
						prefixLength = PrefixLength.forCode( matcher.group(2).length() );
						serviceReference = matcher.group(3);
					} else {
						throw new IllegalArgumentException("EPC Pure Identity is invalid");
					}

					
				}

				GSRNPPartitionTableList GSRNPPartitionTableList = new GSRNPPartitionTableList();
				tableItem = GSRNPPartitionTableList.getPartitionByL( prefixLength.getValue() );
				
			}
			
		}
		

		String outputBin = getBinary();
		String outputHex = Converter.binToHex( outputBin );
		
		gsrnp.setEpcScheme("gsrnp");
		gsrnp.setApplicationIdentifier("AI 8017");
		gsrnp.setTagSize(Integer.toString(tagSize.getValue()));
		gsrnp.setFilterValue(Integer.toString(filterValue.getValue()) );
		gsrnp.setPartitionValue(Integer.toString(tableItem.getPartitionValue()));
		gsrnp.setPrefixLength(Integer.toString(prefixLength.getValue()));
		gsrnp.setCompanyPrefix(companyPrefix);
		gsrnp.setServiceReference(serviceReference);
		gsrnp.setCheckDigit(Integer.toString(getCheckDigit()));
		gsrnp.setEpcPureIdentityURI(String.format("urn:epc:id:gsrnp:%s.%s", companyPrefix, serviceReference));
		gsrnp.setEpcTagURI(String.format("urn:epc:tag:gsrnp-%s:%s.%s.%s", tagSize.getValue(),
				filterValue.getValue(), companyPrefix, serviceReference));
		gsrnp.setEpcRawURI(String.format("urn:epc:raw:%s.x%s", tagSize.getValue(), outputHex ));
		gsrnp.setBinary(outputBin);
		gsrnp.setRfidTag(outputHex);		
		
	}
	
	private Integer getCheckDigit() {
		String value = new StringBuilder()
				.append(companyPrefix)
				.append(serviceReference)
				.toString();		

		Integer d18 = (10 - ((3
				* (Character.getNumericValue(value.charAt(0)) + Character.getNumericValue(value.charAt(2))
						+ Character.getNumericValue(value.charAt(4)) + Character.getNumericValue(value.charAt(6))
						+ Character.getNumericValue(value.charAt(8))
						+ Character.getNumericValue(value.charAt(10)) + Character.getNumericValue(value.charAt(12))
						+ Character.getNumericValue(value.charAt(14)) + Character.getNumericValue(value.charAt(16)))
				+ (Character.getNumericValue(value.charAt(1)) + Character.getNumericValue(value.charAt(3))
						+ Character.getNumericValue(value.charAt(5)) + Character.getNumericValue(value.charAt(7))
						+ Character.getNumericValue(value.charAt(9)) + Character.getNumericValue(value.charAt(11))
						+ Character.getNumericValue(value.charAt(13)) + Character.getNumericValue(value.charAt(15))))
				% 10)) % 10;
		return d18;
	}
	
	
	private String getBinary() {
		StringBuilder bin = new StringBuilder();
		
		bin.append( Converter.decToBin(tagSize.getHeader(), 8) );
		bin.append( Converter.decToBin(filterValue.getValue(), 3) );
		bin.append( Converter.decToBin(tableItem.getPartitionValue(), 3) );
		bin.append( Converter.decToBin(Integer.parseInt(companyPrefix), tableItem.getM()) );
		//bin.append( Converter.strZero(BigDec2Bin.dec2bin(serviceReference), tableItem.getN()) );
		bin.append( Converter.decToBin(Integer.parseInt(serviceReference), tableItem.getN()) );
		bin.append( Converter.decToBin(RESERVED, 24) );		

		return bin.toString();
	}
	

	public GSRNP getGSRNP() {
		return gsrnp;
	}
	
	public String getRfidTag() {
		return Converter.binToHex( getBinary() );
	}	
	
	
	
	private void validateServiceReference()  {
		StringBuilder value = new StringBuilder()
				.append(serviceReference);
		
		if ( value.length()!=tableItem.getDigits() ) {
			throw new IllegalArgumentException(String.format("Service Reference \"%s\" has %d length and should have %d length",
					serviceReference, value.length(), tableItem.getDigits()));
		}
	}
	
	
	private void validateCompanyPrefix() { 
		Optional<PrefixLength> optionalpPefixLenght = Optional.ofNullable(prefixLength);
		if ( !optionalpPefixLenght.isPresent() ) {
			throw new IllegalArgumentException("Company Prefix is invalid. Length not found in the partition table");
		}
		
	}	
	
	

	
    public static interface ChoiceStep {
    	ServiceReferenceStep withCompanyPrefix(String companyPrefix);
        BuildStep withRFIDTag(String rfidTag);
        BuildStep withEPCTagURI(String epcTagURI);
        TagSizeStep withEPCPureIdentityURI(String epcPureIdentityURI);
    }

    
    public static interface ServiceReferenceStep {
    	TagSizeStep withServiceReference(String serviceReference);
    }   
    
    public static interface TagSizeStep {
    	FilterValueStep withTagSize( GSRNPTagSize tagSize );
    }
    
    public static interface FilterValueStep {
    	BuildStep withFilterValue( GSRNPFilterValue filterValue );	
    }
    
    public static interface BuildStep {
    	ParseGSRNP build();
    }

    
    
    private static class Steps implements ChoiceStep, ServiceReferenceStep, TagSizeStep, FilterValueStep, BuildStep {
    	private String companyPrefix;
    	private GSRNPTagSize tagSize;
    	private GSRNPFilterValue filterValue;
    	private String serviceReference;
    	private String rfidTag;
    	private String epcTagURI;
    	private String epcPureIdentityURI;

		@Override
		public ParseGSRNP build() {
			return new ParseGSRNP(this);
		}

//		@Override
//		public SerialStep withExtensionDigit(SSCCExtensionDigit extensionDigit) {
//			this.extensionDigit = extensionDigit;
//			return this;
//		}
		
		
		@Override
		public BuildStep withFilterValue(GSRNPFilterValue filterValue) {
			this.filterValue = filterValue;
			return this;
		}

		@Override
		public FilterValueStep withTagSize(GSRNPTagSize tagSize) {
			this.tagSize = tagSize;
			return this;
		}

		@Override
		public TagSizeStep withServiceReference(String serviceReference) {
			this.serviceReference = serviceReference;
			return this;
		}

		@Override
		public ServiceReferenceStep withCompanyPrefix(String companyPrefix) {
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



    	
    }

}
