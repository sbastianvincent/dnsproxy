package com.svincent7.dnsproxy.util;

import java.util.regex.Pattern;

public final class DomainUtils {
    private static final Pattern DOMAIN_NAME_REGEX = Pattern.compile(
            "^(?=.{1,253}$)(?!-)[A-Za-z0-9-]{1,63}(?<!-)(\\.(?!-)[A-Za-z0-9-]{1,63}(?<!-))*\\."
                    + "(com|net|org|edu|gov|io|[a-z]{2,})\\.?$");

    private DomainUtils() {

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

        if (isWildcard(domain)) {
            return true;
        }

        return DOMAIN_NAME_REGEX.matcher(domain).matches();
    }

    public static boolean isWildcard(final String domain) {
        if (domain == null) {
            return false;
        }
        return domain.equals("*");
    }

    public static String ensureFqdnName(final String domainName) {
        if (isWildcard(domainName)) {
            return domainName;
        }

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
