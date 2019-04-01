package org.epctagcoder.option.GIAI;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GIAIHeader {
	HEADER_00110100("00110100") {
		public Integer getTagSize() {
			return 96;
		}
	},
	HEADER_00111000("00111000") {
		public Integer getTagSize() {
			return 202; 
		}
	};
	
	private String value;
	public abstract Integer getTagSize();
	
	
	private GIAIHeader(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

    private static final Map<String, GIAIHeader> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GIAIHeader rae : GIAIHeader.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }
    
    public static GIAIHeader forCode(String code) {
        return BY_CODE_MAP.get(code);
    } 	
	
	
}
