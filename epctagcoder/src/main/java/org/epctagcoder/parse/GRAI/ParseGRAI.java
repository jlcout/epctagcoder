package org.epctagcoder.parse.GRAI;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.epctagcoder.option.PrefixLength;
import org.epctagcoder.option.TableItem;
import org.epctagcoder.option.GRAI.GRAIFilterValue;
import org.epctagcoder.option.GRAI.GRAIHeader;
import org.epctagcoder.option.GRAI.GRAITagSize;
import org.epctagcoder.option.GRAI.partitionTable.GRAIPartitionTableList;
import org.epctagcoder.result.GRAI;
import org.epctagcoder.util.Converter;


public class ParseGRAI {
	private GRAI grai = new GRAI();
	private String companyPrefix;
	private PrefixLength prefixLength;
	private GRAITagSize tagSize;
	private GRAIFilterValue filterValue;
	private String assetType;
	private String serial;
	private String rfidTag;
	private String epcTagURI;
	private String epcPureIdentityURI;
	private TableItem tableItem;
	private int remainder;
	
    public static ChoiceStep Builder() throws Exception {
        return new Steps();
    }

	private ParseGRAI(Steps steps) {
		this.companyPrefix = steps.companyPrefix;
		this.tagSize = steps.tagSize;
		this.filterValue = steps.filterValue;
		this.assetType = steps.assetType;
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
			GRAIPartitionTableList graiPartitionTableList = new GRAIPartitionTableList();			

			tagSize = GRAITagSize.forCode(GRAIHeader.forCode(headerBin).getTagSize());
			tableItem = graiPartitionTableList.getPartitionByValue( Integer.parseInt(partitionBin, 2) );
			
			String filterDec = Long.toString( Long.parseLong(filterBin, 2) );
			String companyPrefixBin = inputBin.substring(14,14+tableItem.getM());
			String assetTypeBin = inputBin.substring(14+tableItem.getM(),14+tableItem.getM()+tableItem.getN());
			String serialBin = inputBin.substring(14+tableItem.getM()+tableItem.getN()  );
			
			String companyPrefixDec = Converter.binToDec(companyPrefixBin);
			String assetTypeDec = Converter.binToDec(assetTypeBin);
			
			assetType = Converter.strZero(assetTypeDec, tableItem.getDigits() ) ;
			
			if (tagSize.getSerialBitCount()==112) {
				serialBin  = Converter.convertBinToBit(serialBin, 7, 8);
				serial = Converter.binToString(serialBin);
			} else if (tagSize.getSerialBitCount()==38) {
				serial = Converter.binToDec(serialBin);
			}

			companyPrefix = Converter.strZero(companyPrefixDec, tableItem.getL()); // strzero aqui
			filterValue = GRAIFilterValue.forCode( Integer.parseInt(filterDec) );
			prefixLength = PrefixLength.forCode(tableItem.getL());
			
		} else {
		
			if ( optionalCompanyPrefix.isPresent() ) {
				GRAIPartitionTableList sgtinPartitionTableList = new GRAIPartitionTableList();
				prefixLength = PrefixLength.forCode( companyPrefix.length() );
				
				validateCompanyPrefix();
				
				tableItem = sgtinPartitionTableList.getPartitionByL( prefixLength.getValue() );

				validateAssetType();
				validateSerial();
			
			} else {
				
				if ( optionalEpcTagURI.isPresent() ) {
					Pattern pattern = Pattern.compile("(urn:epc:tag:grai-)(96|170)\\:([0-7])\\.(\\d+)\\.(\\d+)\\.(\\w+)");
					Matcher matcher = pattern.matcher(epcTagURI);

					if ( matcher.matches() ) {
						tagSize = GRAITagSize.forCode( Integer.parseInt(matcher.group(2)) );
						filterValue = GRAIFilterValue.forCode( Integer.parseInt(matcher.group(3)) );
						companyPrefix = matcher.group(4);
						prefixLength = PrefixLength.forCode( matcher.group(4).length() );
						assetType = matcher.group(5);
						serial = matcher.group(6);					
					} else {
						throw new IllegalArgumentException("EPC Tag URI is invalid");
					}

				} else if ( optionalEpcPureIdentityURI.isPresent() ) {
					Pattern pattern = Pattern.compile("(urn:epc:id:grai)\\:(\\d+)\\.(\\d+)\\.(\\w+)");
					Matcher matcher = pattern.matcher(epcPureIdentityURI);
					
					if ( matcher.matches() ) {
						companyPrefix = matcher.group(2);
						prefixLength = PrefixLength.forCode( matcher.group(2).length() );
						assetType = matcher.group(3);;
						serial = matcher.group(4);
					} else {
						throw new IllegalArgumentException("EPC Pure Identity is invalid");
					}
				}				
				
			}
				
			GRAIPartitionTableList sgtinPartitionTableList = new GRAIPartitionTableList();
			tableItem = sgtinPartitionTableList.getPartitionByL( prefixLength.getValue() );
			
		}
		
		String outputBin = getBinary();
		String outputHex = Converter.binToHex( outputBin );
		
		grai.setEpcScheme("grai");
		grai.setApplicationIdentifier("AI 8003");
		grai.setTagSize(Integer.toString(tagSize.getValue()));
		grai.setFilterValue(Integer.toString(filterValue.getValue()));
		grai.setPartitionValue(Integer.toString(tableItem.getPartitionValue()));
		grai.setPrefixLength(Integer.toString(prefixLength.getValue()));
		grai.setCompanyPrefix(companyPrefix);
		grai.setAssetType(assetType);
		grai.setSerial(serial);
		grai.setCheckDigit(Integer.toString(getCheckDigit()));
		grai.setEpcPureIdentityURI(String.format("urn:epc:id:grai:%s.%s.%s", companyPrefix, assetType, serial));
		grai.setEpcTagURI(String.format("urn:epc:tag:grai-%s:%s.%s.%s.%s", tagSize.getValue(), filterValue.getValue(), companyPrefix, assetType, serial));
		grai.setEpcRawURI(String.format("urn:epc:raw:%s.x%s", tagSize.getValue()+remainder, outputHex ));
		grai.setBinary(outputBin);
		grai.setRfidTag(outputHex);		
	}	
	
	
	private String getBinary() {
		StringBuilder bin = new StringBuilder();
		
		remainder =  (int) (Math.ceil((tagSize.getValue()/16.0))*16)-tagSize.getValue();
		
		bin.append( Converter.decToBin(tagSize.getHeader(), 8) );
		bin.append( Converter.decToBin(filterValue.getValue(), 3) );
		bin.append( Converter.decToBin(tableItem.getPartitionValue(), 3) );
		bin.append( Converter.decToBin(Integer.parseInt(companyPrefix), tableItem.getM()) );
		bin.append( Converter.decToBin(Integer.parseInt(assetType), tableItem.getN()) );

		if (tagSize.getValue()==170) {		
			bin.append( Converter.fill(Converter.StringtoBinary(serial, 7), tagSize.getSerialBitCount()+remainder) );   
		} else if (tagSize.getValue()==96) {
			bin.append( Converter.decToBin(serial, tagSize.getSerialBitCount()+remainder ) );
		}
		
		return bin.toString();
	}	
	

	
	private Integer getCheckDigit() {
		String value = new StringBuilder()
				.append(companyPrefix)
				.append(assetType)
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

	
	public GRAI getGRAI() {
		return grai;
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
	
	private void validateAssetType() {
		if ( assetType.length()!=tableItem.getDigits() ) {
			throw new IllegalArgumentException(String.format("Asset Type \"%s\" has %d length and should have %d length",
					assetType, assetType.length(), tableItem.getDigits()));
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
    	assetTypeStep withCompanyPrefix(String companyPrefix);    	
        BuildStep withRFIDTag(String rfidTag);
        BuildStep withEPCTagURI(String epcTagURI);
        TagSizeStep withEPCPureIdentityURI(String epcPureIdentityURI);
    }

    
    public static interface assetTypeStep {
    	serialStep withAssetType(String assetType);
    }
    
    public static interface serialStep {
    	TagSizeStep withserial(String serial);
    }   
    
    public static interface TagSizeStep {
    	FilterValueStep withTagSize( GRAITagSize tagSize );
    }
    
    public static interface FilterValueStep {
    	BuildStep withFilterValue( GRAIFilterValue filterValue );	
    }
    
    public static interface BuildStep {
    	ParseGRAI build();
    }

    
    private static class Steps implements ChoiceStep, assetTypeStep, serialStep, TagSizeStep, FilterValueStep, BuildStep {
    	private String companyPrefix;
    	private GRAITagSize tagSize;
    	private GRAIFilterValue filterValue;
    	private String assetType;
    	private String serial;
    	private String rfidTag;
    	private String epcTagURI;
    	private String epcPureIdentityURI;

		@Override
		public ParseGRAI build() {
			return new ParseGRAI(this);
		}

		@Override
		public BuildStep withFilterValue(GRAIFilterValue filterValue) {
			this.filterValue = filterValue;
			return this;
		}

		@Override
		public FilterValueStep withTagSize(GRAITagSize tagSize) {
			this.tagSize = tagSize;
			return this;
		}

		@Override
		public TagSizeStep withserial(String serial) {
			this.serial = serial;
			return this;
		}

		@Override
		public serialStep withAssetType(String assetType) {
			this.assetType = assetType;
			return this;
		}

		@Override
		public assetTypeStep withCompanyPrefix(String companyPrefix) {
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


