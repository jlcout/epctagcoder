package org.epctagcoder.option.SGLN;

import java.util.LinkedHashMap;
import java.util.Map;

public enum SGLNFilterValue {

    ALL_OTHERS_0(0),
    RESERVED_1(1),
    RESERVED_2(2),
    RESERVED_3(3),
    RESERVED_4(4),
    RESERVED_5(5),
    RESERVED_6(6),
    RESERVED_7(7);

    private static final Map<Integer, SGLNFilterValue> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (SGLNFilterValue rae : SGLNFilterValue.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    private int value;

    private SGLNFilterValue(int value) {
        this.value = value;
    }

    public static SGLNFilterValue forCode(int code) {
        return BY_CODE_MAP.get(code);
    }

    public int getValue() {
        return this.value;
    }
}
