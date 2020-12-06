package org.epctagcoder.option.GSRNP;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GSRNPHeader {
	HEADER_00101110("00101110") {
		public Integer getTagSize() {
			return 96;
		}
	};
	
	private String value;
	public abstract Integer getTagSize();
	
	
	private GSRNPHeader(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

    private static final Map<String, GSRNPHeader> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GSRNPHeader rae : GSRNPHeader.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GSRNPHeader forCode(String code) {
        return BY_CODE_MAP.get(code);
    } 	
	
	
}
