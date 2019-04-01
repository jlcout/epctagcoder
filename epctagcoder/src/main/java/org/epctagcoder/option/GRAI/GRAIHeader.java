package org.epctagcoder.option.GRAI;

import java.util.LinkedHashMap;
import java.util.Map;


public enum GRAIHeader {
	HEADER_00110011("00110011") {
		public Integer getTagSize() {
			return 96;
		}
	},
	HEADER_00110111("00110111") {
		public Integer getTagSize() {
			return 170; 
		}
	};
	
	private String value;
	public abstract Integer getTagSize();
	
	
	private GRAIHeader(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

    private static final Map<String, GRAIHeader> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GRAIHeader rae : GRAIHeader.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GRAIHeader forCode(String code) {
    	GRAIHeader header = BY_CODE_MAP.get(code);
    	
    	if (header==null) {
    		throw new IllegalArgumentException(String.format("GRAI header [%s] is invalid. Allowed only 00110011 or 00110111", code));
    	}
    	
        return header;     	
    } 	
	
	
}
