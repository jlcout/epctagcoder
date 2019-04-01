package org.epctagcoder.parse.GSRN;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.epctagcoder.option.PrefixLength;
import org.epctagcoder.option.TableItem;
import org.epctagcoder.option.GSRN.GSRNFilterValue;
import org.epctagcoder.option.GSRN.GSRNHeader;
import org.epctagcoder.option.GSRN.GSRNTagSize;
import org.epctagcoder.option.GSRN.partitionTable.GSRNPartitionTableList;
import org.epctagcoder.result.GSRN;
import org.epctagcoder.util.Converter;

public class ParseGSRN {
	private static final Integer RESERVED = 0; // 24 zero bits
	private GSRN gsrn = new GSRN();
	private String companyPrefix;
	private PrefixLength prefixLength;
	private GSRNTagSize tagSize;
	private GSRNFilterValue filterValue;
	private String serviceReference;
	private String rfidTag;
	private String epcTagURI;
	private String epcPureIdentityURI;
	private TableItem tableItem;
	
    public static ChoiceStep Builder() throws Exception {
        return new Steps();
    }

	private ParseGSRN(Steps steps) {
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
			GSRNPartitionTableList GSRNPartitionTableList = new GSRNPartitionTableList();

			tableItem = GSRNPartitionTableList.getPartitionByValue( Integer.parseInt(partitionBin, 2) );
			
			String companyPrefixBin = inputBin.substring(14,14+tableItem.getM());
			String serialWithExtensionBin = inputBin.substring(14+tableItem.getM(),14+tableItem.getM()+tableItem.getN());
			String filterDec = Long.toString( Long.parseLong(filterBin, 2) );
			String companyPrefixDec = Converter.binToDec(companyPrefixBin);

			serviceReference = Converter.strZero(Converter.binToDec(serialWithExtensionBin), tableItem.getDigits() ); 
			companyPrefix = Converter.strZero(companyPrefixDec, tableItem.getL());
			filterValue = GSRNFilterValue.forCode( Integer.parseInt(filterDec) );
			tagSize = GSRNTagSize.forCode( GSRNHeader.forCode(headerBin).getTagSize() );
			prefixLength = PrefixLength.forCode(tableItem.getL());
			
		} else {
			
			if ( optionalCompanyPrefix.isPresent() ) {
				GSRNPartitionTableList GSRNPartitionTableList = new GSRNPartitionTableList();
				
				prefixLength = PrefixLength.forCode( companyPrefix.length() );
				
				validateCompanyPrefix();
				
				tableItem = GSRNPartitionTableList.getPartitionByL( prefixLength.getValue() );
				
				validateServiceReference();
				
			} else {
				
				if ( optionalEpcTagURI.isPresent() ) {
					Pattern pattern = Pattern.compile("(urn:epc:tag:gsrn-)(96)\\:([0-7])\\.(\\d+)\\.(\\d+)");
					Matcher matcher = pattern.matcher(epcTagURI);
				
					if ( matcher.matches() ) {
						tagSize = GSRNTagSize.forCode( Integer.parseInt(matcher.group(2)) );
						filterValue = GSRNFilterValue.forCode( Integer.parseInt(matcher.group(3)) );
						companyPrefix = matcher.group(4);
						prefixLength = PrefixLength.forCode( matcher.group(4).length() );
						serviceReference = matcher.group(5);					
					} else {
						throw new IllegalArgumentException("EPC Tag URI is invalid");
					}
					
				} else if ( optionalEpcPureIdentityURI.isPresent() ) {
					Pattern pattern = Pattern.compile("(urn:epc:id:gsrn)\\:(\\d+)\\.(\\d+)");
					Matcher matcher = pattern.matcher(epcPureIdentityURI);
					
					if ( matcher.matches() ) {
						companyPrefix = matcher.group(2);
						prefixLength = PrefixLength.forCode( matcher.group(2).length() );
						serviceReference = matcher.group(3);
					} else {
						throw new IllegalArgumentException("EPC Pure Identity is invalid");
					}

					
				}

				GSRNPartitionTableList GSRNPartitionTableList = new GSRNPartitionTableList();
				tableItem = GSRNPartitionTableList.getPartitionByL( prefixLength.getValue() );
				
			}
			
		}
		

		String outputBin = getBinary();
		String outputHex = Converter.binToHex( outputBin );
		
		gsrn.setEpcScheme("gsrn");
		gsrn.setApplicationIdentifier("AI 8018");
		gsrn.setTagSize(Integer.toString(tagSize.getValue()));
		gsrn.setFilterValue(Integer.toString(filterValue.getValue()) );
		gsrn.setPartitionValue(Integer.toString(tableItem.getPartitionValue()));
		gsrn.setPrefixLength(Integer.toString(prefixLength.getValue()));
		gsrn.setCompanyPrefix(companyPrefix);
		gsrn.setServiceReference(serviceReference);
		gsrn.setCheckDigit(Integer.toString(getCheckDigit()));
		gsrn.setEpcPureIdentityURI(String.format("urn:epc:id:gsrn:%s.%s", companyPrefix, serviceReference));
		gsrn.setEpcTagURI(String.format("urn:epc:tag:gsrn-%s:%s.%s.%s", tagSize.getValue(),
				filterValue.getValue(), companyPrefix, serviceReference));
		gsrn.setEpcRawURI(String.format("urn:epc:raw:%s.x%s", tagSize.getValue(), outputHex ));
		gsrn.setBinary(outputBin);
		gsrn.setRfidTag(outputHex);		
		
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
		bin.append( Converter.decToBin(Integer.parseInt(serviceReference), tableItem.getN()) );
		bin.append( Converter.decToBin(RESERVED, 24) );		

		return bin.toString();
	}
	

	public GSRN getGSRN() {
		return gsrn;
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
    	FilterValueStep withTagSize( GSRNTagSize tagSize );
    }
    
    public static interface FilterValueStep {
    	BuildStep withFilterValue( GSRNFilterValue filterValue );	
    }
    
    public static interface BuildStep {
    	ParseGSRN build();
    }

    
    
    private static class Steps implements ChoiceStep, ServiceReferenceStep, TagSizeStep, FilterValueStep, BuildStep {
    	private String companyPrefix;
    	private GSRNTagSize tagSize;
    	private GSRNFilterValue filterValue;
    	private String serviceReference;
    	private String rfidTag;
    	private String epcTagURI;
    	private String epcPureIdentityURI;

		@Override
		public ParseGSRN build() {
			return new ParseGSRN(this);
		}

		@Override
		public BuildStep withFilterValue(GSRNFilterValue filterValue) {
			this.filterValue = filterValue;
			return this;
		}

		@Override
		public FilterValueStep withTagSize(GSRNTagSize tagSize) {
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
