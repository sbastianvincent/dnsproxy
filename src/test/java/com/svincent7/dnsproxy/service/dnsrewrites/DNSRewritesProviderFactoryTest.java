package com.svincent7.dnsproxy.service.dnsrewrites;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DNSRewritesProviderFactoryTest {
    private DNSRewritesProviderFactory dnsRewritesProviderFactory;
    private DnsProxyConfig dnsProxyConfig;

    @BeforeEach
    void setup() {
        dnsProxyConfig = Mockito.mock(DnsProxyConfig.class);
        dnsRewritesProviderFactory = new DNSRewritesProviderFactoryImpl(dnsProxyConfig);
    }

    @Test
    void testDefaultGetDNSRewritesProvider_IsConfigFile() {
        Mockito.when(dnsProxyConfig.getDnsRewritesProvider()).thenReturn("default");

        DNSRewritesProvider dnsRewritesProvider = dnsRewritesProviderFactory.getDNSRewritesProvider();

        Assertions.assertTrue(dnsRewritesProvider instanceof ConfigFileDnsRewritesProvider);
    }
}
