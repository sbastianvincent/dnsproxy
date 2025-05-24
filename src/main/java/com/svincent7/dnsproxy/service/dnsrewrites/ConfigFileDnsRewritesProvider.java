package com.svincent7.dnsproxy.service.dnsrewrites;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import com.svincent7.dnsproxy.model.records.AAAARecord;
import com.svincent7.dnsproxy.model.records.ARecord;
import com.svincent7.dnsproxy.model.records.CNAMERecord;
import com.svincent7.dnsproxy.util.AddressUtils;
import com.svincent7.dnsproxy.util.DomainUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class ConfigFileDnsRewritesProvider extends AbstractDNSRewritesProvider {

    public ConfigFileDnsRewritesProvider(final DnsProxyConfig config) {
        super(config);
    }

    @Override
    protected void initRecordDNSRewrites() {
        Map<String, List<String>> dnsRewrites = getConfig().getDnsRewrites();
        for (Map.Entry<String, List<String>> entry : dnsRewrites.entrySet()) {
            String domain = entry.getKey();
            if (!DomainUtils.isValidDomainName(domain)) {
                log.warn("Skipping DNS Rewrites because of Invalid domain name: " + domain);
                continue;
            }

            domain = DomainUtils.ensureFqdnName(domain);

            List<String> rewrites = entry.getValue();
            for (String rewrite : rewrites) {
                Type type = AddressUtils.detectType(rewrite);
                switch (type) {
                    case A -> addRecordDNSRewrites(domain,
                            new ARecord(domain, getConfig().getDefaultDnsRewritesTimeout(), rewrite));
                    case AAAA -> addRecordDNSRewrites(domain,
                            new AAAARecord(domain, getConfig().getDefaultDnsRewritesTimeout(), rewrite));
                    case CNAME -> addRecordDNSRewrites(domain,
                            new CNAMERecord(domain, getConfig().getDefaultDnsRewritesTimeout(), new Name(rewrite)));
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                }
            }
        }
    }
}
