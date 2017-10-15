package org.epctagcoder.option.SSCC;

import java.util.LinkedHashMap;
import java.util.Map;

public enum SSCCTagSize {
	BITS_96(96) {
		public Integer getHeader() {
			return 49;
		}
	};
	
	private int value;
	public abstract Integer getHeader();
	
	private SSCCTagSize(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

    private static final Map<Integer, SSCCTagSize> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (SSCCTagSize rae : SSCCTagSize.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static SSCCTagSize forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	
	
}
