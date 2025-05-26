package com.svincent7.dnsproxy.util;

import com.svincent7.dnsproxy.model.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AddressUtilsTest {

    @Test
    public void testDetectType_withValidIPv4() {
        Assertions.assertEquals(Type.A, AddressUtils.detectType("192.168.0.1"));
        Assertions.assertEquals(Type.A, AddressUtils.detectType("255.255.255.255"));
        Assertions.assertEquals(Type.A, AddressUtils.detectType("0.0.0.0"));
    }

    @Test
    public void testDetectType_withInvalidIPv4() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> AddressUtils.detectType("256.100.100.100"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> AddressUtils.detectType("192.168.1"));
    }

    @Test
    public void testDetectType_withValidIPv6() {
        Assertions.assertEquals(Type.AAAA, AddressUtils.detectType("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
        Assertions.assertEquals(Type.AAAA, AddressUtils.detectType("::1"));
        Assertions.assertEquals(Type.AAAA, AddressUtils.detectType("2001:db8::ff00:42:8329"));
    }

    @Test
    public void testDetectType_withInvalidIPv6() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> AddressUtils.detectType("2001:::7334"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> AddressUtils.detectType("12345::abcd"));
    }

    @Test
    public void testDetectType_withValidDomain() {
        Assertions.assertEquals(Type.CNAME, AddressUtils.detectType("example.com"));
        Assertions.assertEquals(Type.CNAME, AddressUtils.detectType("sub.domain.co.uk"));
    }

    @Test
    public void testDetectType_withInvalidInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> AddressUtils.detectType(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> AddressUtils.detectType(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> AddressUtils.detectType("invalid_domain@.com"));
    }
}
