package com.svincent7.dnsproxy.service.resolver;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface Resolver {

    byte[] send(byte[] data) throws IOException;
    int getMaxPacketSize();
    InetSocketAddress getAddress();
}
