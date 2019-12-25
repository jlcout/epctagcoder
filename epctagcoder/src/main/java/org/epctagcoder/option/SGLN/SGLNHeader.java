package org.epctagcoder.option.SGLN;


import java.util.LinkedHashMap;
import java.util.Map;

public enum SGLNHeader {

    HEADER_00110010("00110010") {
        public Integer getTagSize() {
            return 96;
        }
    },
    HEADER_00111001("00111001") {
        public Integer getTagSize() {
            return 195;
        }
    };

    private String value;
    public abstract Integer getTagSize();


    private SGLNHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static final Map<String, SGLNHeader> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (SGLNHeader rae : SGLNHeader.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static SGLNHeader forCode(String code) {
        SGLNHeader header = BY_CODE_MAP.get(code);

        if (header==null) {
            throw new IllegalArgumentException(String.format("SGLN header [%s] is invalid. Allowed only 00110010 or 00111001", code));
        }

        return header;
    }
}
