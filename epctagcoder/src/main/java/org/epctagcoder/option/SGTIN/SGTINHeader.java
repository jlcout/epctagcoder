package org.epctagcoder.option.SGTIN;

import org.epctagcoder.exception.EPCParseException;

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
	
	private final String value;
	public abstract Integer getTagSize();
	
	
	SGTINHeader(String value) {
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
    
    public static SGTINHeader forCode(String code) throws EPCParseException {
    	SGTINHeader header = BY_CODE_MAP.get(code);
    	
    	if (header==null) {
    		throw new EPCParseException(String.format("SGTIN header [%s] is invalid. Allowed only 00110000 or 00110110", code));
    	}

    	return header;
    } 	
	
	
}
