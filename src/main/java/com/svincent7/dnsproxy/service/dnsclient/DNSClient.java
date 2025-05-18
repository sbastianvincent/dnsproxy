package com.svincent7.dnsproxy.service.dnsclient;

import java.io.IOException;

public interface DNSClient {

    byte[] send(byte[] data) throws IOException;
}
