package org.epctagcoder.option.GSRN;

import java.util.LinkedHashMap;
import java.util.Map;


public enum GSRNHeader {
	HEADER_00101101("00101101") {
		public Integer getTagSize() {
			return 96;
		}
	};
	
	private String value;
	public abstract Integer getTagSize();
	
	
	private GSRNHeader(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

    private static final Map<String, GSRNHeader> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GSRNHeader rae : GSRNHeader.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GSRNHeader forCode(String code) {
    	GSRNHeader header = BY_CODE_MAP.get(code);
    	
    	if (header==null) {
    		throw new IllegalArgumentException(String.format("GSRN header [%s] is invalid. Allowed only 00101101", code));
    	}
    	
        return header; 
    } 	
	
	
}
