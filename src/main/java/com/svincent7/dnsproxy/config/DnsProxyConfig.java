package com.svincent7.dnsproxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * Configuration properties for the DNS proxy application.
 * <p>
 * Properties are loaded from configuration files with the prefix {@code dnsproxy}.
 * </p>
 */
@Configuration
@ConfigurationProperties(prefix = "dnsproxy")
@Data
public class DnsProxyConfig {

    private int port;
    private int threadPoolSize;
    private Set<String> upstreamServers;
}
