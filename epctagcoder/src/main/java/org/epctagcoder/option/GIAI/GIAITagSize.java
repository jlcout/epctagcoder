package org.epctagcoder.option.GIAI;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GIAITagSize {
	BITS_96(96) {
		public Integer getHeader() {
			return 52;
		}
		public Integer getSerialBitCount() {
			return 38;
		}
		public Integer getSerialMaxLenght() {
			return 13;
		}		
	},
	BITS_202(202) {
		public Integer getHeader() {
			return 56; 
		}
		public Integer getSerialBitCount() {
			return 112;
		}
		public Integer getSerialMaxLenght() {
			return 20;  
		}		
	};
	
	private int value;
	public abstract Integer getHeader();
	public abstract Integer getSerialBitCount();
	public abstract Integer getSerialMaxLenght();
	
	private GIAITagSize(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

    private static final Map<Integer, GIAITagSize> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GIAITagSize rae : GIAITagSize.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GIAITagSize forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	
	
}
