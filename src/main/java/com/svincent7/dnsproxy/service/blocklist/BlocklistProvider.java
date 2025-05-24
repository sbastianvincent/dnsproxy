package com.svincent7.dnsproxy.service.blocklist;

import java.util.Set;

public interface BlocklistProvider {
    Set<String> getBlocklists();
}
