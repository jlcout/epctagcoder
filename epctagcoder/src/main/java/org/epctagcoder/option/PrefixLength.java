package org.epctagcoder.option;

import java.util.LinkedHashMap;
import java.util.Map;

public enum PrefixLength {
	DIGIT_6(6), 
	DIGIT_7(7),
	DIGIT_8(8),
	DIGIT_9(9),
	DIGIT_10(10),
	DIGIT_11(11),
	DIGIT_12(12);
	
	private int value;
	
	private PrefixLength(int value) {
		this.value = value;
	}	
	
	public int getValue() {
		return value;
	}	
	
	
    private static final Map<Integer, PrefixLength> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (PrefixLength rae : PrefixLength.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static PrefixLength forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	

}
