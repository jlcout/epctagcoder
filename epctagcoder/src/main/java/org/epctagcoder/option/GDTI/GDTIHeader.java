package org.epctagcoder.option.GDTI;

import java.util.LinkedHashMap;
import java.util.Map;


public enum GDTIHeader {
	HEADER_00101100("00101100") {
		public Integer getTagSize() {
			return 96;
		}
	},
	HEADER_00111110("00111110") {
		public Integer getTagSize() {
			return 174; 
		}
	};
	
	private String value;
	public abstract Integer getTagSize();
	
	
	private GDTIHeader(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

    private static final Map<String, GDTIHeader> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GDTIHeader rae : GDTIHeader.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GDTIHeader forCode(String code) {
    	GDTIHeader header = BY_CODE_MAP.get(code);
    	
    	if (header==null) {
    		throw new IllegalArgumentException(String.format("GDTI header [%s] is invalid. Allowed only 00101100 or 00111110", code));
    	}
    	
        return header;    	    	
    } 	
	
	
}
