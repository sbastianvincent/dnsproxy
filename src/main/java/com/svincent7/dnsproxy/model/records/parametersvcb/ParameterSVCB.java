package com.svincent7.dnsproxy.model.records.parametersvcb;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public enum ParameterSVCB {
    MANDATORY(0),
    ALPN(1),
    NO_DEFAULT_ALPN(2),
    PORT(3),
    IPV4HINT(4),
    ECH(5),
    IPV6HINT(6),
    UNKNOWN(null);

    private final Integer value;
    ParameterSVCB(final Integer value) {
        this.value = value;
    }

    private static final Map<Integer, ParameterSVCB> LOOKUP = new HashMap<>();

    static {
        for (ParameterSVCB t : ParameterSVCB.values()) {
            if (t.getValue() != null) {
                LOOKUP.put(t.getValue(), t);
            }
        }
    }

    public static ParameterSVCB fromValue(final int value) {
        return LOOKUP.getOrDefault(value, UNKNOWN);
    }
}
