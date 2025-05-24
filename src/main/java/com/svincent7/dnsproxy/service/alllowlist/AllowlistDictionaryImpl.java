package com.svincent7.dnsproxy.service.alllowlist;

import com.svincent7.dnsproxy.util.DomainUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
@Slf4j
public class AllowlistDictionaryImpl implements AllowlistDictionary {
    private volatile Set<String> allowlist = new ConcurrentSkipListSet<>();
    private final List<AllowlistProvider> providers;

    public AllowlistDictionaryImpl(final List<AllowlistProvider> providers) {
        this.providers = providers;
    }

    @PostConstruct
    void init() {
        reloadAllowlist();
    }

    @Override
    public void reloadAllowlist() {
        Set<String> newAllowlist = new ConcurrentSkipListSet<>();
        for (AllowlistProvider provider : providers) {
            for (String domain : provider.getAllowlist()) {
                if (DomainUtils.isWildcard(domain)) {
                    newAllowlist.add(domain);
                } else if (DomainUtils.isValidDomainName(domain)) {
                    newAllowlist.add(DomainUtils.ensureFqdnName(domain));
                } else {
                    log.warn("Invalid allowlist entry: {}", domain);
                }
            }
        }
        allowlist = newAllowlist;
        log.debug("allowlist: {}", allowlist);
    }

    @Override
    public boolean isAllowed(final String hostname) {
        if (allowlist.contains("*")) {
            return true;
        }

        String domain = DomainUtils.ensureFqdnName(hostname);
        if (allowlist.contains(domain)) {
            return true;
        }
        int dotIndex = domain.indexOf('.');
        while (dotIndex != -1) {
            String suffix = domain.substring(dotIndex + 1);
            if (allowlist.contains("*." + suffix)) {
                return true;
            }
            dotIndex = domain.indexOf('.', dotIndex + 1);
        }
        return false;
    }
}
