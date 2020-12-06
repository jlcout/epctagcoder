package org.epctagcoder.parse.CPI;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.epctagcoder.option.PrefixLength;
import org.epctagcoder.option.TableItem;
import org.epctagcoder.option.CPI.CPIFilterValue;
import org.epctagcoder.option.CPI.CPIHeader;
import org.epctagcoder.option.CPI.CPITagSize;
import org.epctagcoder.option.CPI.partitionTable.CPIPartitionTableList;
import org.epctagcoder.result.CPI;
import org.epctagcoder.util.Converter;


public class ParseCPI {
	private CPI cpi = new CPI();
	private String companyPrefix;
	private PrefixLength prefixLength;
	private CPITagSize tagSize;
	private CPIFilterValue filterValue;
	private String componentPartReference;
	private String serial;
	private String rfidTag;
	private String epcTagURI;
	private String epcPureIdentityURI;
	private TableItem tableItem;
	private int remainder;
	
    public static ChoiceStep Builder() throws Exception {
        return new Steps();
    }

	private ParseCPI(Steps steps) {
		this.companyPrefix = steps.companyPrefix;
		this.tagSize = steps.tagSize;
		this.filterValue = steps.filterValue;
		this.componentPartReference = steps.componentPartReference;
		this.serial = steps.serial;
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

			tagSize = CPITagSize.forCode(CPIHeader.forCode(headerBin).getTagSize());
			CPIPartitionTableList cpiPartitionTableList = new CPIPartitionTableList(tagSize);			
			tableItem = cpiPartitionTableList.getPartitionByValue( Integer.parseInt(partitionBin, 2) );
			
			String filterDec = Long.toString( Long.parseLong(filterBin, 2) );
			String companyPrefixBin = inputBin.substring(14, 14+tableItem.getM());
			String componentPartReferenceBin = null;
			String serialBin = null;
			
			if (tagSize.getValue()==0) {  // variable
				String componentPartReferenceAndSerialBin = inputBin.substring(14+tableItem.getM()  );

				StringBuilder decodeComponentPartReference = new StringBuilder();
				List<String> splitCPR = Converter.splitEqually(componentPartReferenceAndSerialBin, 6);
				for (String item : splitCPR) {
					if ( item.equals("000000") ) {
						break;
					}
					decodeComponentPartReference.append(item);
				}
				
				componentPartReferenceBin = decodeComponentPartReference.toString();
				int posSerial = 14+tableItem.getM()+componentPartReferenceBin.length()+6;
				componentPartReferenceBin = Converter.convertBinToBit(componentPartReferenceBin, 6, 8);
				componentPartReference = Converter.binToString(componentPartReferenceBin);
				serialBin = inputBin.substring(posSerial, posSerial+tagSize.getSerialBitCount());
			} else if (tagSize.getValue()==96) {
				componentPartReferenceBin = inputBin.substring(14+tableItem.getM(),14+tableItem.getM()+tableItem.getN());
				componentPartReference = Converter.binToDec(componentPartReferenceBin);
				serialBin = inputBin.substring(14+tableItem.getM()+tableItem.getN()  );
			}
			
			
			String companyPrefixDec = Converter.binToDec(companyPrefixBin);
			serial = Converter.binToDec(serialBin);
			companyPrefix = Converter.strZero(companyPrefixDec, tableItem.getL()); // strzero aqui
			filterValue = CPIFilterValue.forCode( Integer.parseInt(filterDec) );
			prefixLength = PrefixLength.forCode(tableItem.getL());
		} else {
		
			if ( optionalCompanyPrefix.isPresent() ) {
				CPIPartitionTableList sgtinPartitionTableList = new CPIPartitionTableList(tagSize);
				prefixLength = PrefixLength.forCode( companyPrefix.length() );
				
				validateCompanyPrefix();
				
				tableItem = sgtinPartitionTableList.getPartitionByL( prefixLength.getValue() );

				validateComponentPartReference();
				validateSerial();
			
			} else {
				
				if ( optionalEpcTagURI.isPresent() ) {
					Pattern pattern = Pattern.compile("(urn:epc:tag:cpi-)(96|var)\\:([0-7])\\.(\\d+)\\.(\\d+)\\.(\\w+)");
					
					Matcher matcher = pattern.matcher(epcTagURI);

					if ( matcher.matches() ) {
						if ( matcher.group(2).equals("var") ) {
							tagSize = CPITagSize.forCode( 0 );
						} else {
							tagSize = CPITagSize.forCode( Integer.parseInt(matcher.group(2)) );	
						}
						
						filterValue = CPIFilterValue.forCode( Integer.parseInt(matcher.group(3)) );
						companyPrefix = matcher.group(4);
						prefixLength = PrefixLength.forCode( matcher.group(4).length() );
						componentPartReference = matcher.group(5);
						serial = matcher.group(6);					
					} else {
						throw new IllegalArgumentException("EPC Tag URI is invalid");
					}

				} else if ( optionalEpcPureIdentityURI.isPresent() ) {
					Pattern pattern = Pattern.compile("(urn:epc:id:cpi)\\:(\\d+)\\.(\\d+)\\.(\\w+)");
					Matcher matcher = pattern.matcher(epcPureIdentityURI);
					
					if ( matcher.matches() ) {
						companyPrefix = matcher.group(2);
						prefixLength = PrefixLength.forCode( matcher.group(2).length() );
						componentPartReference = matcher.group(3);;
						serial = matcher.group(4);
					} else {
						throw new IllegalArgumentException("EPC Pure Identity is invalid");
					}
				}				
				
			}
				
			CPIPartitionTableList sgtinPartitionTableList = new CPIPartitionTableList(tagSize);
			tableItem = sgtinPartitionTableList.getPartitionByL( prefixLength.getValue() );
			
		}
		
		String outputBin = getBinary();
		String outputHex = Converter.binToHex( outputBin );
		
		cpi.setEpcScheme("cpi");
		cpi.setApplicationIdentifier("AI 8010 + AI 8011");
		cpi.setTagSize( (tagSize.getValue()==0) ? "var" : Integer.toString(tagSize.getValue()) );		
		cpi.setFilterValue(Integer.toString(filterValue.getValue()));
		cpi.setPartitionValue(Integer.toString(tableItem.getPartitionValue()));
		cpi.setPrefixLength(Integer.toString(prefixLength.getValue()));
		cpi.setCompanyPrefix(companyPrefix);
		cpi.setComponentPartReference(componentPartReference);
		cpi.setSerial(serial);
		cpi.setEpcPureIdentityURI(String.format("urn:epc:id:cpi:%s.%s.%s", companyPrefix, componentPartReference, serial));
		cpi.setEpcTagURI(String.format("urn:epc:tag:cpi-%s:%s.%s.%s.%s", (tagSize.getValue()==0) ? "var" : tagSize.getValue(), filterValue.getValue(), companyPrefix, componentPartReference, serial));
		cpi.setEpcRawURI(String.format("urn:epc:raw:%s.x%s", outputBin.length(), outputHex ));
		cpi.setBinary(outputBin);
		cpi.setRfidTag(outputHex);		
	}	
	

	private String getBinary() {
		StringBuilder bin = new StringBuilder();
		
		bin.append( Converter.decToBin(tagSize.getHeader(), 8) );
		bin.append( Converter.decToBin(filterValue.getValue(), 3) );
		bin.append( Converter.decToBin(tableItem.getPartitionValue(), 3) );
		bin.append( Converter.decToBin(Integer.parseInt(companyPrefix), tableItem.getM()) );
		
		if (tagSize.getValue()==0) {  // variable		
			bin.append( Converter.StringtoBinary(componentPartReference, 6) );
			bin.append("000000");
		} else if (tagSize.getValue()==96) {
			bin.append( Converter.decToBin(Integer.parseInt(componentPartReference), tableItem.getN()) );
		}
		
		bin.append( Converter.decToBin(serial, tagSize.getSerialBitCount() ) );
		remainder =  (int) (Math.ceil((bin.length()/16.0))*16)-bin.length();
		bin.append( Converter.fill("0", remainder) );
		
		return bin.toString();
	}	
	

	
	public CPI getCPI() {
		return cpi;
	}	
	
	public String getRfidTag() {
		return Converter.binToHex( getBinary() );
	}	



	private void validateCompanyPrefix() { 
		Optional<PrefixLength> optionalPefixLenght = Optional.ofNullable(prefixLength);
		if ( !optionalPefixLenght.isPresent() ) {
			throw new IllegalArgumentException("Company Prefix is invalid. Length not found in the partition table");
		}
	}
	

	
	private void validateComponentPartReference() {
		
		if ( !Converter.isNumeric(componentPartReference) ) {
			throw new IllegalArgumentException("Component/Part Reference is allowed with numerical only");
		}
		
		if ( componentPartReference.length()>tableItem.getDigits() ) {				
			throw new IllegalArgumentException("Component/Part Reference is out of range");
		}
		
		
		if (tagSize.getValue()==96 ) {
			if ( componentPartReference.startsWith("0") ) {
				throw new IllegalArgumentException("Component/Part Reference with leading zeros is not allowed");
			}
			
		}
		
	}
	
	private void validateSerial() { 

		if ( serial.startsWith("0") ) {
			throw new IllegalArgumentException("Serial with leading zeros is not allowed");
		}
		
		if (tagSize.getValue()==0 ) { // variable
			if ( serial.length()>tagSize.getSerialMaxLenght() ) { 
				throw new IllegalArgumentException( String.format("Serial value is out of range. Should be up to %d alphanumeric characters",
						tagSize.getSerialMaxLenght() ));
			}
		} else if (tagSize.getValue()==96 ) {
			
			if ( !Converter.isNumeric(serial) ) {
				throw new IllegalArgumentException("Serial value is allowed with numerical only");
			}
			
			if ( Long.parseLong(serial)>tagSize.getSerialMaxValue() ) {                                            
				throw new IllegalArgumentException( String.format("Serial value is out of range. Should be less than or equal %d",
						tagSize.getSerialMaxValue() ));
			}
		}
		
	}	
	

	
    public static interface ChoiceStep {
    	componentPartReferenceStep withCompanyPrefix(String companyPrefix);    	
        BuildStep withRFIDTag(String rfidTag);
        BuildStep withEPCTagURI(String epcTagURI);
        TagSizeStep withEPCPureIdentityURI(String epcPureIdentityURI);
    }

    
    public static interface componentPartReferenceStep {
    	serialStep withComponentPartReference(String componentPartReference);
    }
    
    public static interface serialStep {
    	TagSizeStep withSerial(String serial);
    }   
    
    public static interface TagSizeStep {
    	FilterValueStep withTagSize( CPITagSize tagSize );
    }
    
    public static interface FilterValueStep {
    	BuildStep withFilterValue( CPIFilterValue filterValue );	
    }
    
    public static interface BuildStep {
    	ParseCPI build();
    }

    
    private static class Steps implements ChoiceStep, componentPartReferenceStep, serialStep, TagSizeStep, FilterValueStep, BuildStep {
    	private String companyPrefix;
    	private CPITagSize tagSize;
    	private CPIFilterValue filterValue;
    	private String componentPartReference;
    	private String serial;
    	private String rfidTag;
    	private String epcTagURI;
    	private String epcPureIdentityURI;

		@Override
		public ParseCPI build() {
			return new ParseCPI(this);
		}

		@Override
		public BuildStep withFilterValue(CPIFilterValue filterValue) {
			this.filterValue = filterValue;
			return this;
		}

		@Override
		public FilterValueStep withTagSize(CPITagSize tagSize) {
			this.tagSize = tagSize;
			return this;
		}

		@Override
		public TagSizeStep withSerial(String serial) {
			this.serial = serial;
			return this;
		}

		@Override
		public serialStep withComponentPartReference(String componentPartReference) {
			this.componentPartReference = componentPartReference;
			return this;
		}

		@Override
		public componentPartReferenceStep withCompanyPrefix(String companyPrefix) {
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


