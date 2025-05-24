package com.svincent7.dnsproxy.service.blocklist;

import com.svincent7.dnsproxy.util.DomainUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
@Slf4j
public class BlocklistDictionaryImpl implements BlocklistDictionary {
    private volatile Set<String> blocklist = new ConcurrentSkipListSet<>();
    private final List<BlocklistProvider> providers;

    public BlocklistDictionaryImpl(final List<BlocklistProvider> providers) {
        this.providers = providers;
    }

    @PostConstruct
    void init() {
        reloadBlocklist();
    }

    @Override
    public void reloadBlocklist() {
        Set<String> newBlocklist = new ConcurrentSkipListSet<>();
        for (BlocklistProvider provider : providers) {
            provider.getBlocklists().stream()
                    .filter(DomainUtils::isValidDomainName)
                    .map(DomainUtils::ensureFqdnName)
                    .forEach(newBlocklist::add);
        }
        blocklist = newBlocklist;
        log.debug("blocklist: {}", blocklist);
    }

    @Override
    public boolean isBlocked(final String hostname) {
        String domain = DomainUtils.ensureFqdnName(hostname);
        if (blocklist.contains(domain)) {
            return true;
        }
        int dotIndex = domain.indexOf('.');
        while (dotIndex != -1) {
            String suffix = domain.substring(dotIndex + 1);
            if (blocklist.contains("*." + suffix)) {
                return true;
            }
            dotIndex = domain.indexOf('.', dotIndex + 1);
        }
        return false;
    }
}
