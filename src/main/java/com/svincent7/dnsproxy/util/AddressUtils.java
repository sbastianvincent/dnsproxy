package com.svincent7.dnsproxy.util;

import com.svincent7.dnsproxy.model.Type;

import java.util.regex.Pattern;

public final class AddressUtils {
    private AddressUtils() {
        throw new IllegalArgumentException("Utility class");
    }

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.|$)){4}$"
    );

    private static final Pattern IPV6_PATTERN = Pattern.compile(
            "^(?:[\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$|"          // full form
                    + "^((?:[\\da-fA-F]{1,4}:){1,7}:)$|"           // ending with ::
                    + "^(:((?::[\\da-fA-F]{1,4}){1,7}))$|"         // starting with ::
                    + "^(::)$"                                     // just ::
    );

    public static Type detectType(final String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        if (IPV4_PATTERN.matcher(input).matches()) {
            return Type.A;
        }
        if (IPV6_PATTERN.matcher(input).matches()) {
            return Type.AAAA;
        }
        if (DomainUtils.isValidDomainName(input)) {
            return Type.CNAME;
        }
        return null;
    }
}
