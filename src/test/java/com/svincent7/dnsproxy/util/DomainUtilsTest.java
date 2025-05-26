package com.svincent7.dnsproxy.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DomainUtilsTest {

    @Test
    void testValidDomainName() {
        Assertions.assertTrue(DomainUtils.isValidDomainName("example.com"));
        Assertions.assertTrue(DomainUtils.isValidDomainName("www.google.com"));
        Assertions.assertTrue(DomainUtils.isValidDomainName("subdomain3.svincent7.com"));
        Assertions.assertTrue(DomainUtils.isValidDomainName("*"));
    }

    @Test
    void testInvalidDomainName() {
        Assertions.assertFalse(DomainUtils.isValidDomainName(null));
        Assertions.assertFalse(DomainUtils.isValidDomainName("192.168.10.1"));
        Assertions.assertFalse(DomainUtils.isValidDomainName("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
        Assertions.assertFalse(DomainUtils.isValidDomainName("goog!e.com"));
        Assertions.assertFalse(DomainUtils.isValidDomainName("*.com"));
        Assertions.assertFalse(DomainUtils.isValidDomainName("*."));
    }

    @Test
    void testWildDomain() {
        Assertions.assertTrue(DomainUtils.isWildcard("*"));

        Assertions.assertFalse(DomainUtils.isWildcard(null));
        Assertions.assertFalse(DomainUtils.isWildcard("192.168.10.1"));
        Assertions.assertFalse(DomainUtils.isWildcard("google.com"));
    }

    @Test
    void testEnsureFqdn() {
        Assertions.assertEquals("*", DomainUtils.ensureFqdnName("*"));
        Assertions.assertEquals("example.com.", DomainUtils.ensureFqdnName("example.com"));
        Assertions.assertEquals("example.com.", DomainUtils.ensureFqdnName("example.com."));
    }

    @Test
    void testEnsureFqdn_Invalid_ThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DomainUtils.ensureFqdnName("192.168.10.1"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DomainUtils.ensureFqdnName("g.com1"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DomainUtils.ensureFqdnName("*.com"));
    }
}
