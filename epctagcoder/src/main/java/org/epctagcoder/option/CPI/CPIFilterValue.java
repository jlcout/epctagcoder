package org.epctagcoder.option.CPI;

import java.util.LinkedHashMap;
import java.util.Map;

public enum CPIFilterValue {
	ALL_OTHERS_0(0), 
	RESERVED_1(1), 
	RESERVED_2(2),
	RESERVED_3(3),
	RESERVED_4(4),
	RESERVED_5(5),
	RESERVED_6(6),
	RESERVED_7(7);
	
	private int value;
	
	private CPIFilterValue(int value) {
		this.value = value;
	}	
	
	public int getValue() {
		return value;
	}
	
    private static final Map<Integer, CPIFilterValue> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (CPIFilterValue rae : CPIFilterValue.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static CPIFilterValue forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	

}
