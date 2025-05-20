package com.svincent7.dnsproxy.service.resolver;

public interface DNSResolverFactory {
    UDPResolver createUDPResolver();
    TCPResolver createTCPResolver();
}
