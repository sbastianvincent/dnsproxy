package com.svincent7.dnsproxy.util;

public final class DomainUtils {
    private static final int MAX_DOMAIN_NAME_LENGTH = 253;
    private static final int MAX_DOMAIN_LABEL_LENGTH = 63;
    private static final String DOMAIN_NAME_REGEX = "^[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?$";

    private DomainUtils() {
        throw new IllegalArgumentException("Utility class");
    }

    /**
     * Domain Name Rules (RFC 1035 / 1123).
     *     Only letters (a-z), digits (0-9), and hyphens (-)
     *     Labels (parts between dots) must be 1–63 characters
     *     Domain length ≤ 253 characters
     *     Labels must not start or end with -
     *     No consecutive dots
     *     No spaces or special characters
     */
    public static boolean isValidDomainName(final String domain) {
        if (domain == null) {
            return false;
        }

        String d = domain.endsWith(".") ? domain.substring(0, domain.length() - 1) : domain;

        boolean hasWildcard = d.startsWith("*.");
        if (hasWildcard) {
            d = d.substring(2); // remove "*."
        }

        if (d.length() > MAX_DOMAIN_NAME_LENGTH) {
            return false;
        }

        String[] labels = d.split("\\.");
        for (String label : labels) {
            if (label.isEmpty() || label.length() > MAX_DOMAIN_LABEL_LENGTH) {
                return false;
            }
            if (!label.matches(DOMAIN_NAME_REGEX)) {
                return false;
            }
        }

        return true;
    }

    public static String ensureFqdnName(final String domainName) {
        if (!isValidDomainName(domainName)) {
            throw new IllegalArgumentException("Invalid domain name: " + domainName);
        }

        if (domainName.endsWith(".")) {
            return domainName.toLowerCase();
        } else {
            return domainName.toLowerCase() + ".";
        }
    }

}
