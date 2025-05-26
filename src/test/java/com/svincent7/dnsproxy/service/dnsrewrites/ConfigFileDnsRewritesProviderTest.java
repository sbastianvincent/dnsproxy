package com.svincent7.dnsproxy.service.dnsrewrites;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import com.svincent7.dnsproxy.model.records.ARecord;
import com.svincent7.dnsproxy.model.records.QRecord;
import com.svincent7.dnsproxy.model.records.Record;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigFileDnsRewritesProviderTest {
    private DNSRewritesProvider configFileDnsRewritesProvider;
    private DnsProxyConfig config;

    @BeforeEach
    void setup() {
        config = Mockito.mock(DnsProxyConfig.class);

        Mockito.when(config.getAllowlistedDnsRewritesTypes()).thenReturn(List.of("A", "CNAME"));
        Map<String, List<String>> dnsRewrites = new HashMap<>();
        dnsRewrites.put("invalid.domain.1", List.of("127.0.0.1"));
        dnsRewrites.put("example.com", List.of("192.168.1.1", "example.redirect.com", "2001:db8::ff00:42:8329"));
        Mockito.when(config.getDnsRewrites()).thenReturn(dnsRewrites);

        configFileDnsRewritesProvider = new ConfigFileDnsRewritesProvider(config);
    }

    @Test
    void testDNSRewrites() {
        QRecord question = new QRecord(new Name("example.com."), Type.A, DNSClass.IN);
        QRecord question2 = new QRecord(new Name("invalid.domain.1"), Type.A, DNSClass.IN);
        QRecord question3 = new QRecord(new Name("example.com."), Type.AAAA, DNSClass.IN);

        List<Record> records = configFileDnsRewritesProvider.getDNSRewritesAnswer(question);
        List<Record> records2 = configFileDnsRewritesProvider.getDNSRewritesAnswer(question2);
        List<Record> records3 = configFileDnsRewritesProvider.getDNSRewritesAnswer(question3);

        Assertions.assertFalse(records.isEmpty());
        Assertions.assertTrue(records.get(0) instanceof ARecord);
        Assertions.assertEquals(((ARecord) records.get(0)).getIpAddress(), "192.168.1.1");

        Assertions.assertNull(records2);
        Assertions.assertNull(records3);
    }

    @Test
    void testInitRecordDNSRewrite_InvalidType_ThrowException() {
        DnsProxyConfig config = Mockito.mock(DnsProxyConfig.class);

        Map<String, List<String>> dnsRewrites = new HashMap<>();
        dnsRewrites.put("example.com", List.of(""));
        Mockito.when(config.getDnsRewrites()).thenReturn(dnsRewrites);

        Assertions.assertThrows(IllegalArgumentException.class, () -> new ConfigFileDnsRewritesProvider(config));
    }

    @Test
    void testEmptyDNSRewrites() {
        DnsProxyConfig config = Mockito.mock(DnsProxyConfig.class);

        Map<String, List<String>> dnsRewrites = new HashMap<>();
        Mockito.when(config.getDnsRewrites()).thenReturn(dnsRewrites);

        QRecord question = new QRecord(new Name("example.com."), Type.A, DNSClass.IN);

        DNSRewritesProvider dnsRewritesProvider = new ConfigFileDnsRewritesProvider(config);

        List<Record> records = dnsRewritesProvider.getDNSRewritesAnswer(question);

        Assertions.assertNull(records);
    }
}
