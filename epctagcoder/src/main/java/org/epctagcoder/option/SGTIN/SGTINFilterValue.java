package org.epctagcoder.option.SGTIN;

import java.util.LinkedHashMap;
import java.util.Map;

public enum SGTINFilterValue {
	ALL_OTHERS_0(0), 
	POS_ITEM_1(1), 
	CASE_2(2),
	RESERVED_3(3),
	INNER_PACK_4(4),
	RESERVED_5(5),
	UNIT_LOAD_6(6),
	COMPONENT_7(7);
	
	private int value;
	
	private SGTINFilterValue(int value) {
		this.value = value;
	}	
	
	public int getValue() {
		return value;
	}
	
    private static final Map<Integer, SGTINFilterValue> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (SGTINFilterValue rae : SGTINFilterValue.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static SGTINFilterValue forCode(int code) {
        return BY_CODE_MAP.get(code);
    } 	
	

}
