package com.svincent7.dnsproxy.model.records.parametersvcb;

public class ParameterFactoryImpl implements ParameterFactory {
    @Override
    public ParameterSvcBinding getParameterSvcBinding(final short key) {
        return switch (ParameterSVCB.fromValue(key)) {
            case MANDATORY -> new ParameterMandatory();
            case ALPN -> new ParameterAlpn();
            case NO_DEFAULT_ALPN -> new ParameterNoDefaultAlpn();
            case PORT -> new ParameterPort();
            case IPV4HINT -> new ParameterIpv4Hint();
            case ECH -> new ParameterEch();
            case IPV6HINT -> new ParameterIpv6Hint();
            default -> new ParameterUnknown(key);
        };
    }
}
