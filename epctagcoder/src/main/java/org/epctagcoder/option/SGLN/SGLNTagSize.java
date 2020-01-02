package org.epctagcoder.option.SGLN;

import java.util.LinkedHashMap;
import java.util.Map;

public enum SGLNTagSize {

    BITS_96(96) {
        @Override
        public Integer getHeader() {
            return 51;
        }

        @Override
        public Integer getSerialBitCount() {
            return 41;
        }

        @Override
        public Integer getSerialMaxLenght() {
            return 13;
        }

        @Override
        public Long getSerialMaxValue() {
            return 2_199_023_255_551L;
        }
    },
    BITS_195(195) {
        @Override
        public Integer getHeader() {
            return 55;
        }

        @Override
        public Integer getSerialBitCount() {
            return 140;
        }

        @Override
        public Integer getSerialMaxLenght() {
            return 20;
        }

        @Override
        public Long getSerialMaxValue() {
            return null;  // not used
        }
    };

    private static final Map<Integer, SGLNTagSize> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (SGLNTagSize rae : SGLNTagSize.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }


    private int value;

    private SGLNTagSize(int value) {
        this.value = value;
    }

    public static SGLNTagSize forCode(int code) {
        return BY_CODE_MAP.get(code);
    }

    public abstract Integer getHeader();

    public abstract Integer getSerialBitCount();

    public abstract Integer getSerialMaxLenght();

    public abstract Long getSerialMaxValue();

    public int getValue() {
        return value;
    }
}
