package com.svincent7.dnsproxy.service;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import com.svincent7.dnsproxy.service.alllowlist.AllowlistDictionary;
import com.svincent7.dnsproxy.service.blocklist.BlocklistDictionary;
import com.svincent7.dnsproxy.service.cache.CacheFactory;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProviderFactory;
import com.svincent7.dnsproxy.service.resolver.DNSResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

public class DnsProxyTest {
    private DnsProxy dnsProxy;

    @BeforeEach
    public void setUp() throws Exception {
        final BlocklistDictionary blocklistDictionary = Mockito.mock(BlocklistDictionary.class);
        final AllowlistDictionary allowlistDictionary = Mockito.mock(AllowlistDictionary.class);
        final DnsProxyConfig config = Mockito.mock(DnsProxyConfig.class);
        final CacheFactory cacheFactory = Mockito.mock(CacheFactory.class);
        final DNSResolverFactory dnsResolverFactory = Mockito.mock(DNSResolverFactory.class);
        final DNSRewritesProviderFactory dnsRewritesProviderFactory = Mockito.mock(DNSRewritesProviderFactory.class);
    }
}
