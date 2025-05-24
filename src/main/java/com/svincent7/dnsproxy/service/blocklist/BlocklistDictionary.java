package com.svincent7.dnsproxy.service.blocklist;

public interface BlocklistDictionary {
    void reloadBlocklist();
    boolean isBlocked(String hostname);
}
