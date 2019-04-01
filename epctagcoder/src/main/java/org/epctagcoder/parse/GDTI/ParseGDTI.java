package org.epctagcoder.parse.GDTI;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.epctagcoder.option.PrefixLength;
import org.epctagcoder.option.TableItem;
import org.epctagcoder.option.GDTI.GDTIFilterValue;
import org.epctagcoder.option.GDTI.GDTIHeader;
import org.epctagcoder.option.GDTI.GDTITagSize;
import org.epctagcoder.option.GDTI.partitionTable.GDTIPartitionTableList;
import org.epctagcoder.result.GDTI;
import org.epctagcoder.util.Converter;


public class ParseGDTI {
	private GDTI gdti = new GDTI();
	private String companyPrefix;
	private PrefixLength prefixLength;
	private GDTITagSize tagSize;
	private GDTIFilterValue filterValue;
	private String docType;
	private String serial;
	private String rfidTag;
	private String epcTagURI;
	private String epcPureIdentityURI;
	private TableItem tableItem;
	private int remainder;
	
    public static ChoiceStep Builder() throws Exception {
        return new Steps();
    }

	private ParseGDTI(Steps steps) {
		this.companyPrefix = steps.companyPrefix;
		this.tagSize = steps.tagSize;
		this.filterValue = steps.filterValue;
		this.docType = steps.docType;
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
			GDTIPartitionTableList graiPartitionTableList = new GDTIPartitionTableList();			

			tagSize = GDTITagSize.forCode(GDTIHeader.forCode(headerBin).getTagSize());
			tableItem = graiPartitionTableList.getPartitionByValue( Integer.parseInt(partitionBin, 2) );
			
			String filterDec = Long.toString( Long.parseLong(filterBin, 2) );
			String companyPrefixBin = inputBin.substring(14,14+tableItem.getM());
			String docTypeBin = inputBin.substring(14+tableItem.getM(),14+tableItem.getM()+tableItem.getN());
			String serialBin = inputBin.substring(14+tableItem.getM()+tableItem.getN()  );
			
			String companyPrefixDec = Converter.binToDec(companyPrefixBin);
			String docTypeDec = Converter.binToDec(docTypeBin);
			
			docType = Converter.strZero(docTypeDec, tableItem.getDigits() ) ;
			
			if (tagSize.getSerialBitCount()==119) {
				serialBin  = Converter.convertBinToBit(serialBin, 7, 8);
				serial = Converter.binToString(serialBin);
			} else if (tagSize.getSerialBitCount()==41) {
				serial = Converter.binToDec(serialBin);
			}

			companyPrefix = Converter.strZero(companyPrefixDec, tableItem.getL()); // strzero aqui
			filterValue = GDTIFilterValue.forCode( Integer.parseInt(filterDec) );
			prefixLength = PrefixLength.forCode(tableItem.getL());
			
		} else {
		
			if ( optionalCompanyPrefix.isPresent() ) {
				GDTIPartitionTableList sgtinPartitionTableList = new GDTIPartitionTableList();
				prefixLength = PrefixLength.forCode( companyPrefix.length() );
				
				validateCompanyPrefix();
				
				tableItem = sgtinPartitionTableList.getPartitionByL( prefixLength.getValue() );

				validateDocType();
				validateSerial();
			
			} else {
				
				if ( optionalEpcTagURI.isPresent() ) {
					Pattern pattern = Pattern.compile("(urn:epc:tag:gdti-)(96|174)\\:([0-7])\\.(\\d+)\\.(\\d+)\\.(\\w+)");
					Matcher matcher = pattern.matcher(epcTagURI);

					if ( matcher.matches() ) {
						tagSize = GDTITagSize.forCode( Integer.parseInt(matcher.group(2)) );
						filterValue = GDTIFilterValue.forCode( Integer.parseInt(matcher.group(3)) );
						companyPrefix = matcher.group(4);
						prefixLength = PrefixLength.forCode( matcher.group(4).length() );
						docType = matcher.group(5);
						serial = matcher.group(6);					
					} else {
						throw new IllegalArgumentException("EPC Tag URI is invalid");
					}

				} else if ( optionalEpcPureIdentityURI.isPresent() ) {
					Pattern pattern = Pattern.compile("(urn:epc:id:gdti)\\:(\\d+)\\.(\\d+)\\.(\\w+)");
					Matcher matcher = pattern.matcher(epcPureIdentityURI);
					
					if ( matcher.matches() ) {
						companyPrefix = matcher.group(2);
						prefixLength = PrefixLength.forCode( matcher.group(2).length() );
						docType = matcher.group(3);
						serial = matcher.group(4);
					} else {
						throw new IllegalArgumentException("EPC Pure Identity is invalid");
					}
				}				
				
			}
				
			GDTIPartitionTableList sgtinPartitionTableList = new GDTIPartitionTableList();
			tableItem = sgtinPartitionTableList.getPartitionByL( prefixLength.getValue() );
			
		}
		
		String outputBin = getBinary();
		String outputHex = Converter.binToHex( outputBin );
		
		gdti.setEpcScheme("gdti");
		gdti.setApplicationIdentifier("AI 253");
		gdti.setTagSize(Integer.toString(tagSize.getValue()));
		gdti.setFilterValue(Integer.toString(filterValue.getValue()));
		gdti.setPartitionValue(Integer.toString(tableItem.getPartitionValue()));
		gdti.setPrefixLength(Integer.toString(prefixLength.getValue()));
		gdti.setCompanyPrefix(companyPrefix);
		gdti.setDocType(docType);
		gdti.setSerial(serial);
		gdti.setCheckDigit(Integer.toString(getCheckDigit()));
		gdti.setEpcPureIdentityURI(String.format("urn:epc:id:gdti:%s.%s.%s", companyPrefix, docType, serial));
		gdti.setEpcTagURI(String.format("urn:epc:tag:gdti-%s:%s.%s.%s.%s", tagSize.getValue(), filterValue.getValue(), companyPrefix, docType, serial));
		gdti.setEpcRawURI(String.format("urn:epc:raw:%s.x%s", tagSize.getValue()+remainder, outputHex ));
		gdti.setBinary(outputBin);
		gdti.setRfidTag(outputHex);		
	}	
	
	
	private String getBinary() {
		StringBuilder bin = new StringBuilder();
		
		remainder =  (int) (Math.ceil((tagSize.getValue()/16.0))*16)-tagSize.getValue();
		
		bin.append( Converter.decToBin(tagSize.getHeader(), 8) );
		bin.append( Converter.decToBin(filterValue.getValue(), 3) );
		bin.append( Converter.decToBin(tableItem.getPartitionValue(), 3) );
		bin.append( Converter.decToBin(Integer.parseInt(companyPrefix), tableItem.getM()) );
		bin.append( Converter.decToBin(Integer.parseInt(docType), tableItem.getN()) );

		if (tagSize.getValue()==174) {		
			bin.append( Converter.fill(Converter.StringtoBinary(serial, 7), tagSize.getSerialBitCount()+remainder) );   
		} else if (tagSize.getValue()==96) {
			bin.append( Converter.decToBin(serial, tagSize.getSerialBitCount()+remainder ) );
		}
		
		return bin.toString();
	}	
	

	
	private Integer getCheckDigit() {
		String value = new StringBuilder()
				.append(companyPrefix)
				.append(docType)
				.toString();

		Integer d13 = (10 - ((3
				* (Character.getNumericValue(value.charAt(1)) + Character.getNumericValue(value.charAt(3))
						+ Character.getNumericValue(value.charAt(5))
						+ Character.getNumericValue(value.charAt(7)) + Character.getNumericValue(value.charAt(9))
						+ Character.getNumericValue(value.charAt(11)) )
				+ (Character.getNumericValue(value.charAt(0)) + Character.getNumericValue(value.charAt(2))
						+ Character.getNumericValue(value.charAt(4)) + Character.getNumericValue(value.charAt(6))
						+ Character.getNumericValue(value.charAt(8)) + Character.getNumericValue(value.charAt(10))))
				% 10)) % 10;
				
		return d13;
	}

	
	public GDTI getGDTI() {
		return gdti;
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
	
	private void validateDocType() {
		if ( docType.length()!=tableItem.getDigits() ) {
			throw new IllegalArgumentException(String.format("Asset Type \"%s\" has %d length and should have %d length",
					docType, docType.length(), tableItem.getDigits()));
		}		
	}
	
	private void validateSerial() { 
		if (tagSize.getValue()==170 ) {
			if ( serial.length()>tagSize.getSerialMaxLenght() ) { 
				throw new IllegalArgumentException( String.format("Serial value is out of range. Should be up to %d alphanumeric characters",
						tagSize.getSerialMaxLenght() ));
			}
		} else if (tagSize.getValue()==96 ) {
			if ( Long.parseLong(serial) >tagSize.getSerialMaxValue() ) {                                            
				throw new IllegalArgumentException( String.format("Serial value is out of range. Should be less than or equal %d",
						tagSize.getSerialMaxValue() ));
			}
			if ( serial.startsWith("0") ) {
				throw new IllegalArgumentException("Serial with leading zeros is not allowed");
			}
		}
		
	}	
	

	
    public static interface ChoiceStep {
    	docTypeStep withCompanyPrefix(String companyPrefix);    	
        BuildStep withRFIDTag(String rfidTag);
        BuildStep withEPCTagURI(String epcTagURI);
        TagSizeStep withEPCPureIdentityURI(String epcPureIdentityURI);
    }

    
    public static interface docTypeStep {
    	serialStep withDocType(String docType);
    }
    
    public static interface serialStep {
    	TagSizeStep withserial(String serial);
    }   
    
    public static interface TagSizeStep {
    	FilterValueStep withTagSize( GDTITagSize tagSize );
    }
    
    public static interface FilterValueStep {
    	BuildStep withFilterValue( GDTIFilterValue filterValue );	
    }
    
    public static interface BuildStep {
    	ParseGDTI build();
    }

    
    private static class Steps implements ChoiceStep, docTypeStep, serialStep, TagSizeStep, FilterValueStep, BuildStep {
    	private String companyPrefix;
    	private GDTITagSize tagSize;
    	private GDTIFilterValue filterValue;
    	private String docType;
    	private String serial;
    	private String rfidTag;
    	private String epcTagURI;
    	private String epcPureIdentityURI;

		@Override
		public ParseGDTI build() {
			return new ParseGDTI(this);
		}

		@Override
		public BuildStep withFilterValue(GDTIFilterValue filterValue) {
			this.filterValue = filterValue;
			return this;
		}

		@Override
		public FilterValueStep withTagSize(GDTITagSize tagSize) {
			this.tagSize = tagSize;
			return this;
		}

		@Override
		public TagSizeStep withserial(String serial) {
			this.serial = serial;
			return this;
		}

		@Override
		public serialStep withDocType(String docType) {
			this.docType = docType;
			return this;
		}

		@Override
		public docTypeStep withCompanyPrefix(String companyPrefix) {
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


