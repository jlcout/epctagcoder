package org.epctagcoder.option.GRAI;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GRAITagSize {
	BITS_96(96) {
		public Integer getHeader() {
			return 51;
		}
		public Integer getSerialBitCount() {
			return 38;
		}
		public Integer getSerialMaxLenght() {
			return 13;
		}		
		public Long getSerialMaxValue() {
			return 274_877_906_943L;   
		}			
	},
	BITS_170(170) {
		public Integer getHeader() {
			return 55; 
		}
		public Integer getSerialBitCount() {
			return 112;
		}
		public Integer getSerialMaxLenght() {
			return 16;  
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
	
	private GRAITagSize(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

    private static final Map<Integer, GRAITagSize> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GRAITagSize rae : GRAITagSize.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GRAITagSize forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	
	
}
