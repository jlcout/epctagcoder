package org.epctagcoder.option.SGTIN;

import java.util.LinkedHashMap;
import java.util.Map;

public enum SGTINHeader {
	HEADER_00110000("00110000") {
		public Integer getTagSize() {
			return 96;
		}
	},
	HEADER_00110110("00110110") {
		public Integer getTagSize() {
			return 198; 
		}
	};
	
	private String value;
	public abstract Integer getTagSize();
	
	
	private SGTINHeader(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

    private static final Map<String, SGTINHeader> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (SGTINHeader rae : SGTINHeader.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static SGTINHeader forCode(String code) {
    	SGTINHeader header = BY_CODE_MAP.get(code);
    	
    	if (header==null) {
    		throw new IllegalArgumentException(String.format("SGTIN header %s is invalid. Allowed only 00110000 and 00110110", code));
    	}

    	return header;
    } 	
	
	
}
