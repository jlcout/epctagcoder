package org.epctagcoder.option.GSRNP;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GSRNPFilterValue {
	ALL_OTHERS_0(0), 
	RESERVED_1(1), 
	RESERVED_2(2),
	RESERVED_3(3),
	RESERVED_4(4),
	RESERVED_5(5),
	RESERVED_6(6),
	RESERVED_7(7);
	
	private int value;
	
	private GSRNPFilterValue(int value) {
		this.value = value;
	}	
	
	public int getValue() {
		return value;
	}
	
    private static final Map<Integer, GSRNPFilterValue> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GSRNPFilterValue rae : GSRNPFilterValue.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GSRNPFilterValue forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	

}
