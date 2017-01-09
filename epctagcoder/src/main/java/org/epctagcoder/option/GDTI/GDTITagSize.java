package org.epctagcoder.option.GDTI;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GDTITagSize {
	BITS_96(96) {
		public Integer getHeader() {
			return 44;
		}
		public Integer getSerialBitCount() {
			return 41;
		}
		public Integer getSerialMaxLenght() {
			return 13;
		}		
		public Long getSerialMaxValue() { // confirmar isso
			return 2_199_023_255_551L;   
			       
		}			
	},
	BITS_174(174) {
		public Integer getHeader() {
			return 62; 
		}
		public Integer getSerialBitCount() {
			return 119;
		}
		public Integer getSerialMaxLenght() {
			return 17;  
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
	
	private GDTITagSize(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

    private static final Map<Integer, GDTITagSize> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GDTITagSize rae : GDTITagSize.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GDTITagSize forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	
	
}
