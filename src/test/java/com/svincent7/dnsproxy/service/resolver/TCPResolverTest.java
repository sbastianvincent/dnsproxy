package com.svincent7.dnsproxy.service.resolver;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPResolverTest {
    private static final int TEST_ECHO_PORT = 59545;
    private static final int TEST_NULL_PORT = 58545;
    private static Thread echoServerThread;
    private static Thread nullServerThread;
    private static volatile boolean running = true;

    @BeforeAll
    static void startEchoServer() {
        echoServerThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TEST_ECHO_PORT)) {
                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleClient(clientSocket)).start();
                }
            } catch (IOException e) {
                if (running) e.printStackTrace();
            }
        });
        echoServerThread.start();
        nullServerThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TEST_NULL_PORT)) {
                while (running) {
                    serverSocket.accept();
                    new Thread(() -> {}).start();
                }
            } catch (IOException e) {
                if (running) e.printStackTrace();
            }
        });
        nullServerThread.start();
    }

    static void handleClient(Socket socket) {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            int length = in.readUnsignedShort();
            // Timeout Test Mock
            if (length == 10) {
                return;
            }
            byte[] data = new byte[length];
            in.readFully(data);
            if (length == 11) {
                out.writeShort(100);
                out.write(data);
                out.flush();
                return;
            }

            // Echo response: send same data back with 2-byte length prefix
            out.writeShort(data.length);
            out.write(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void stopEchoServer() {
        running = false;
        try {
            try (Socket socket = new Socket("localhost", TEST_ECHO_PORT)) {}
            try (Socket socket = new Socket("localhost", TEST_NULL_PORT)) {}
            echoServerThread.interrupt();
            echoServerThread.join();
            nullServerThread.interrupt();
            nullServerThread.join();
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSend() throws IOException {
        TCPResolver tcpResolver = new TCPResolver("127.0.0.1", TEST_ECHO_PORT);
        byte[] request = new byte[] {1, 2, 3, 4};
        byte[] response = tcpResolver.send(request);

        Assertions.assertArrayEquals(request, response);
    }

    @Test
    void testReceivedBufferNotCompleted() {
        TCPResolver tcpResolver = new TCPResolver("127.0.0.1", TEST_ECHO_PORT);
        byte[] request = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        Assertions.assertThrows(IOException.class, () -> tcpResolver.send(request));
    }

    @Test
    void testConnectionClosed() {
        TCPResolver tcpResolver = new TCPResolver("127.0.0.1", TEST_ECHO_PORT);
        byte[] request = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Assertions.assertThrows(IOException.class, () -> tcpResolver.send(request));
    }

    @Test
    void testTimeout() {
        TCPResolver tcpResolver = new TCPResolver("127.0.0.1", TEST_NULL_PORT);
        byte[] request = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Assertions.assertThrows(IOException.class, () -> tcpResolver.send(request));
    }

    @Test
    void testGetMaxPacketSize() {
        TCPResolver tcpResolver = new TCPResolver("127.0.0.1", TEST_ECHO_PORT);
        Assertions.assertEquals(65535, tcpResolver.getMaxPacketSize());
    }
}
