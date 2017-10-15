package org.epctagcoder.option.GSRN;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GSRNFilterValue {
	ALL_OTHERS_0(0), 
	RESERVED_1(1), 
	RESERVED_2(2),
	RESERVED_3(3),
	RESERVED_4(4),
	RESERVED_5(5),
	RESERVED_6(6),
	RESERVED_7(7);
	
	private int value;
	
	private GSRNFilterValue(int value) {
		this.value = value;
	}	
	
	public int getValue() {
		return value;
	}
	
    private static final Map<Integer, GSRNFilterValue> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GSRNFilterValue rae : GSRNFilterValue.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GSRNFilterValue forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	

}
