package com.svincent7.dnsproxy.service.allowlist;

import com.svincent7.dnsproxy.service.alllowlist.AllowlistDictionary;
import com.svincent7.dnsproxy.service.alllowlist.AllowlistDictionaryImpl;
import com.svincent7.dnsproxy.service.alllowlist.AllowlistProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

public class AllowlistDictionaryImplTest {
    private AllowlistDictionary allowlistDictionary;
    private AllowlistProvider allowlistProvider;

    @BeforeEach
    void setup() {
        allowlistProvider = Mockito.mock(AllowlistProvider.class);
        allowlistDictionary = new AllowlistDictionaryImpl(List.of(allowlistProvider));
    }

    @Test
    void testAllowedAll() {
        Mockito.when(allowlistProvider.getAllowlist()).thenReturn(Set.of("*"));
        allowlistDictionary.reloadAllowlist();

        Assertions.assertTrue(allowlistDictionary.isAllowed("example.com"));
    }

    @Test
    void testAllowedDomain() {
        Mockito.when(allowlistProvider.getAllowlist()).thenReturn(Set.of("*.example.com", "allow.com"));
        allowlistDictionary.reloadAllowlist();

        Assertions.assertFalse(allowlistDictionary.isAllowed("example.com"));
        Assertions.assertFalse(allowlistDictionary.isAllowed("1example.com."));
        Assertions.assertTrue(allowlistDictionary.isAllowed("allow.com"));
        Assertions.assertTrue(allowlistDictionary.isAllowed("subdomain.example.com"));
        Assertions.assertTrue(allowlistDictionary.isAllowed("1.example.com."));
    }
}
