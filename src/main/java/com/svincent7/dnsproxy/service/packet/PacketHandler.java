package com.svincent7.dnsproxy.service.packet;

import java.io.IOException;

public interface PacketHandler {
    void handlePacket() throws IOException;
}
