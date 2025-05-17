package com.svincent7.dnsproxy.model.records.parametersvcb;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public enum ParameterSVCB {
    MANDATORY((short) 0),
    ALPN((short) 1),
    NO_DEFAULT_ALPN((short) 2),
    PORT((short) 3),
    IPV4HINT((short) 4),
    ECH((short) 5),
    IPV6HINT((short) 6),
    UNKNOWN(null);

    private final Short value;
    ParameterSVCB(final Short value) {
        this.value = value;
    }

    private static final Map<Short, ParameterSVCB> LOOKUP = new HashMap<>();

    static {
        for (ParameterSVCB t : ParameterSVCB.values()) {
            LOOKUP.put(t.getValue(), t);
        }
    }

    public static ParameterSVCB fromValue(final short value) {
        return LOOKUP.get(value);
    }
}
