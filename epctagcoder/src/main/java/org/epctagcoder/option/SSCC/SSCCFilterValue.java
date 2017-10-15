package org.epctagcoder.option.SSCC;

import java.util.LinkedHashMap;
import java.util.Map;

public enum SSCCFilterValue {
	ALL_OTHERS_0(0), 
	RESERVED_1(1), 
	CASE_2(2),
	RESERVED_3(3),
	RESERVED_4(4),
	RESERVED_5(5),
	UNIT_LOAD_6(6),
	RESERVED_7(7);
	
	private int value;
	
	private SSCCFilterValue(int value) {
		this.value = value;
	}	
	
	public int getValue() {
		return value;
	}
	
    private static final Map<Integer, SSCCFilterValue> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (SSCCFilterValue rae : SSCCFilterValue.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static SSCCFilterValue forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	

}
