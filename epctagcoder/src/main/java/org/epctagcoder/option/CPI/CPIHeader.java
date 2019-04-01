package org.epctagcoder.option.CPI;

import java.util.LinkedHashMap;
import java.util.Map;



public enum CPIHeader {
	HEADER_00111100("00111100") {
		public Integer getTagSize() {
			return 96;
		}
	},
	HEADER_00111101("00111101") {
		public Integer getTagSize() {
			return 0; //null;  // variable 
		}
	};
	
	private String value;
	public abstract Integer getTagSize();
	
	
	private CPIHeader(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

    private static final Map<String, CPIHeader> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (CPIHeader rae : CPIHeader.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static CPIHeader forCode(String code) {
    	CPIHeader header = BY_CODE_MAP.get(code);
    	
    	if (header==null) {
    		throw new IllegalArgumentException(String.format("CPI header [%s] is invalid. Allowed only 00111100 or 00111101", code));
    	}
    	
        return header;    	
    } 	
	
	
}
