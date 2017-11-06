package org.epctagcoder.option.SGTIN;

import java.util.LinkedHashMap;
import java.util.Map;

public enum SGTINTagSize {
	BITS_96(96) {
		public Integer getHeader() {
			return 48;
		}
		public Integer getSerialBitCount() {
			return 38;
		}
		public Integer getSerialMaxLenght() {
			return 11;
		}
		public Long getSerialMaxValue() {
			return 274_877_906_943L;   
		}		
	},
	BITS_198(198) {
		public Integer getHeader() {
			return 54; 
		}
		public Integer getSerialBitCount() {
			return 140;
		}
		public Integer getSerialMaxLenght() {
			return 20; 
		}
		public Long getSerialMaxValue() {
			return null;  // not used
		}		
	};
	
	private int value;
	public abstract Integer getHeader();
	public abstract Integer getSerialBitCount();
	public abstract Integer getSerialMaxLenght();
	public abstract Long getSerialMaxValue();
	
	private SGTINTagSize(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

    private static final Map<Integer, SGTINTagSize> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (SGTINTagSize rae : SGTINTagSize.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static SGTINTagSize forCode(int code) {
    	SGTINTagSize bits = BY_CODE_MAP.get(code);
    	
    	if (bits==null) {
    		throw new IllegalArgumentException(String.format("SGTIN tag size %d is invalid. Only 96 bits or 198 bits supported.", code));
    	}
    	
        return bits;
    } 	
	
	
}
