package org.epctagcoder.option.GIAI;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GIAIFilterValue {
	ALL_OTHERS_0(0), 
	RESERVED_1(1), 
	RESERVED_2(2),
	RESERVED_3(3),
	RESERVED_4(4),
	RESERVED_5(5),
	RESERVED_6(6),
	RESERVED_7(7);
	
	private int value;
	
	private GIAIFilterValue(int value) {
		this.value = value;
	}	
	
	public int getValue() {
		return value;
	}
	
    private static final Map<Integer, GIAIFilterValue> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GIAIFilterValue rae : GIAIFilterValue.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GIAIFilterValue forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	

}
