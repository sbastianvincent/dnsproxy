package com.svincent7.dnsproxy.model.records.parametersvcb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParameterFactoryImplTest {
    private ParameterFactoryImpl impl;

    @BeforeEach
    public void setUp() {
        impl = new ParameterFactoryImpl();
    }

    @Test
    void testParameterMandatory() {
        ParameterSvcBinding param = impl.getParameterSvcBinding(0);

        Assertions.assertTrue(param instanceof ParameterMandatory);
    }

    @Test
    void testParameterAlpn() {
        ParameterSvcBinding param = impl.getParameterSvcBinding(1);

        Assertions.assertTrue(param instanceof ParameterAlpn);
    }

    @Test
    void testParameterNoDefaultAlpn() {
        ParameterSvcBinding param = impl.getParameterSvcBinding(2);

        Assertions.assertTrue(param instanceof ParameterNoDefaultAlpn);
    }

    @Test
    void testParameterPort() {
        ParameterSvcBinding param = impl.getParameterSvcBinding(3);

        Assertions.assertTrue(param instanceof ParameterPort);
    }

    @Test
    void testParameterIpv4Hint() {
        ParameterSvcBinding param = impl.getParameterSvcBinding(4);

        Assertions.assertTrue(param instanceof ParameterIpv4Hint);
    }

    @Test
    void testParameterEch() {
        ParameterSvcBinding param = impl.getParameterSvcBinding(5);

        Assertions.assertTrue(param instanceof ParameterEch);
    }

    @Test
    void testParameterIpv6Hint() {
        ParameterSvcBinding param = impl.getParameterSvcBinding(6);

        Assertions.assertTrue(param instanceof ParameterIpv6Hint);
    }

    @Test
    void testParameterUnknown() {
        ParameterSvcBinding param = impl.getParameterSvcBinding(-1);

        Assertions.assertTrue(param instanceof ParameterUnknown);
    }
}
