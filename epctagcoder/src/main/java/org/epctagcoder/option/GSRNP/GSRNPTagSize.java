package org.epctagcoder.option.GSRNP;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GSRNPTagSize {
	BITS_96(96) {
		public Integer getHeader() {
			return 46;
		}
	};
	
	private int value;
	public abstract Integer getHeader();
	
	private GSRNPTagSize(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

    private static final Map<Integer, GSRNPTagSize> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GSRNPTagSize rae : GSRNPTagSize.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GSRNPTagSize forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	
	
}
