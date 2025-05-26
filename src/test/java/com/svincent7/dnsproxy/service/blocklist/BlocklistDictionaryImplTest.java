package com.svincent7.dnsproxy.service.blocklist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

public class BlocklistDictionaryImplTest {
    private BlocklistDictionary blocklistDictionary;
    private BlocklistProvider blocklistProvider;

    @BeforeEach
    void setup() {
        blocklistProvider = Mockito.mock(BlocklistProvider.class);
        blocklistDictionary = new BlocklistDictionaryImpl(List.of(blocklistProvider));
    }

    @Test
    void testBlockedDomain() {
        Mockito.when(blocklistProvider.getBlocklists()).thenReturn(Set.of("*.example.com", "block.com"));
        blocklistDictionary.reloadBlocklist();

        Assertions.assertFalse(blocklistDictionary.isBlocked("example.com"));
        Assertions.assertFalse(blocklistDictionary.isBlocked("1example.com."));
        Assertions.assertTrue(blocklistDictionary.isBlocked("block.com"));
        Assertions.assertTrue(blocklistDictionary.isBlocked("subdomain.example.com"));
        Assertions.assertTrue(blocklistDictionary.isBlocked("1.example.com."));
    }
}
