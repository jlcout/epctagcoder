package org.epctagcoder.option.SSCC;

import java.util.LinkedHashMap;
import java.util.Map;

public enum SSCCHeader {
	HEADER_00110001("00110001") {
		public Integer getTagSize() {
			return 96;
		}
	};
	
	private String value;
	public abstract Integer getTagSize();
	
	
	private SSCCHeader(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

    private static final Map<String, SSCCHeader> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (SSCCHeader rae : SSCCHeader.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static SSCCHeader forCode(String code) {
    	SSCCHeader header = BY_CODE_MAP.get(code);
    	
    	if (header==null) {
    		throw new IllegalArgumentException(String.format("SSCC header [%s] is invalid. Allowed only 00110001", code));
    	}
    	
        return header;
    } 	
	
	
}
