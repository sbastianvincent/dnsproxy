package com.svincent7.dnsproxy.service.resolver;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class UDPResolverTest {
    private static final int TEST_ECHO_PORT = 53535;
    private static Thread echoServerThread;
    private static volatile boolean running = true;
    private static final int TEST_ECHO_LENGTH = 100;

    @BeforeAll
    static void startEchoServer() {
        echoServerThread = new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(TEST_ECHO_PORT)) {
                byte[] buffer = new byte[512];
                while (running) {
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    socket.receive(request);

                    byte[] data = Arrays.copyOf(request.getData(), request.getLength());
                    DatagramPacket response = new DatagramPacket(
                            data, data.length, request.getAddress(), request.getPort()
                    );
                    socket.send(response);
                }
            } catch (IOException e) {
                if (running) e.printStackTrace();
            }
        });
        echoServerThread.start();
    }

    @AfterAll
    static void stopEchoServer() {
        running = false;
        try {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.send(new DatagramPacket(new byte[]{0}, 1, new InetSocketAddress("127.0.0.1", TEST_ECHO_PORT)));
            }
            echoServerThread.interrupt();
            echoServerThread.join();
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSend() throws IOException {
        UDPResolver udpResolver = new UDPResolver("127.0.0.1", TEST_ECHO_PORT);
        byte[] request = new byte[TEST_ECHO_LENGTH];
        byte[] response = udpResolver.send(request);

        Assertions.assertArrayEquals(request, response);
    }

    @Test
    void testTimeout() {
        UDPResolver udpResolver = new UDPResolver("127.0.0.1", 63535);
        byte[] request = new byte[10];

        Assertions.assertThrows(IOException.class, () -> udpResolver.send(request));
    }

    @Test
    void testGetMaxPacketSize() {
        UDPResolver udpResolver = new UDPResolver("127.0.0.1", TEST_ECHO_PORT);
        Assertions.assertEquals(512, udpResolver.getMaxPacketSize());
    }
}
