package org.epctagcoder.option.GSRN;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GSRNTagSize {
	BITS_96(96) {
		public Integer getHeader() {
			return 45;
		}
	};
	
	private int value;
	public abstract Integer getHeader();
	
	private GSRNTagSize(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

    private static final Map<Integer, GSRNTagSize> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GSRNTagSize rae : GSRNTagSize.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GSRNTagSize forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	
	
}
