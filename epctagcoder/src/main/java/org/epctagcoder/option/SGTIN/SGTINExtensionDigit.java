package org.epctagcoder.option.SGTIN;

import java.util.LinkedHashMap;
import java.util.Map;

public enum SGTINExtensionDigit {
	EXTENSION_0(0),
	EXTENSION_1(1),
	EXTENSION_2(2),
	EXTENSION_3(3),
	EXTENSION_4(4),
	EXTENSION_5(5),
	EXTENSION_6(6),
	EXTENSION_7(7),
	EXTENSION_8(8),
	EXTENSION_9(9);
	
	private int value;
	
	private SGTINExtensionDigit(int value) {
		this.value = value;
	}	
	
	public int getValue() {
		return value;
	}		
	
    private static final Map<Integer, SGTINExtensionDigit> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (SGTINExtensionDigit rae : SGTINExtensionDigit.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static SGTINExtensionDigit forCode(int code) {
        return BY_CODE_MAP.get(code);
    }    

	
	
	
}
