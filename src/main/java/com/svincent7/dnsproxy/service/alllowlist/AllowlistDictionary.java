package com.svincent7.dnsproxy.service.alllowlist;

public interface AllowlistDictionary {
    void reloadAllowlist();
    boolean isAllowed(String hostname);
}
