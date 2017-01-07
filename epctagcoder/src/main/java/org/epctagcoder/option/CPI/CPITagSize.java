package org.epctagcoder.option.CPI;

import java.util.LinkedHashMap;
import java.util.Map;

public enum CPITagSize {
	BITS_96(96) {
		public Integer getHeader() {
			return 60;
		}
		public Integer getSerialBitCount() {
			return 31;
		}
		public Integer getSerialMaxLenght() {  
			return 0;
		}		
		public Long getSerialMaxValue() {
			return 2_147_483_647L;   
		}			
	},
	BITS_VARIABLE(0) {
		public Integer getHeader() {
			return 61; 
		}
		public Integer getSerialBitCount() {
			return 40;
		}
		public Integer getSerialMaxLenght() {
			return 12;  
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
	
	private CPITagSize(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

    private static final Map<Integer, CPITagSize> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (CPITagSize rae : CPITagSize.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static CPITagSize forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	
	
}
