package org.epctagcoder.option.GDTI;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GDTIFilterValue {
	ALL_OTHERS_0(0), 
	RESERVED_1(1), 
	RESERVED_2(2),
	RESERVED_3(3),
	RESERVED_4(4),
	RESERVED_5(5),
	RESERVED_6(6),
	RESERVED_7(7);
	
	private int value;
	
	private GDTIFilterValue(int value) {
		this.value = value;
	}	
	
	public int getValue() {
		return value;
	}
	
    private static final Map<Integer, GDTIFilterValue> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GDTIFilterValue rae : GDTIFilterValue.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GDTIFilterValue forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	

}
