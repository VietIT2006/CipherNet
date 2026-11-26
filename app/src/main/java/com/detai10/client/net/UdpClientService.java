package com.detai10.client.net;

import com.detai10.core.model.FileMode;
import com.detai10.core.protocol.UdpProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Tầng giao tiếp mạng UDP phía Client (Headless, không phụ thuộc Swing/AWT).
 * <p>
 * Đóng gói cơ chế gửi/nhận {@link DatagramSocket}, {@link DatagramPacket},
 * kiểm soát timeout theo đặc tả SRS (3 giây cho PING/PONG, 5 giây cho DATA/RESULT)
 * và xác thực kích thước gói tin UTF-8 an toàn trước khi truyền.
 */
public class UdpClientService {

    public static final int PING_TIMEOUT_MS = 3000;
    public static final int DATA_TIMEOUT_MS = 5000;

    /**
     * Gửi gói tin {@code PING} tới Server và chờ phản hồi {@code PONG} trong tối đa 3 giây.
     *
     * @param serverIp Địa chỉ IP hoặc hostname của Server
     * @param port     Cổng UDP lắng nghe của Server (1..65535)
     * @return {@code true} nếu nhận được gói {@code PONG} hợp lệ
     * @throws IOException nếu hết thời gian chờ (SocketTimeoutException) hoặc lỗi mạng
     */
    public boolean ping(String serverIp, int port) throws IOException {
        validateEndpoint(serverIp, port);
        String response = sendAndReceive(serverIp.trim(), port, UdpProtocol.createPingMessage(), PING_TIMEOUT_MS);
        return UdpProtocol.isPong(response);
    }

    /**
     * Đóng gói và gửi gói tin {@code DATA|<mode>|<fileName>|<content>} tới Server,
     * chờ phản hồi {@code RESULT|...} hoặc {@code ERROR|...} trong tối đa 5 giây.
     */
    public UdpProtocol.ResponsePayload sendData(String serverIp, int port, FileMode mode,
                                                 String fileName, String content) throws IOException {
        validateEndpoint(serverIp, port);
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Noi dung van ban khong duoc de trong");
        }
        String packetPayload = UdpProtocol.createDataMessage(mode, fileName, content);
        String rawResponse = sendAndReceive(serverIp.trim(), port, packetPayload, DATA_TIMEOUT_MS);
        return UdpProtocol.parseResponsePacket(rawResponse);
    }

    protected String sendAndReceive(String serverIp, int port, String message, int timeoutMs) throws IOException {
        InetAddress address = InetAddress.getByName(serverIp);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeoutMs);
            DatagramPacket sendPacket = UdpProtocol.createUdpPacket(message, address, port);
            socket.send(sendPacket);

            DatagramPacket receivePacket = UdpProtocol.createReceiveBufferPacket();
            socket.receive(receivePacket);

            return UdpProtocol.extractPacketMessage(receivePacket);
        }
    }

    private void validateEndpoint(String serverIp, int port) {
        if (serverIp == null || serverIp.trim().isEmpty()) {
            throw new IllegalArgumentException("Dia chi IP Server khong duoc de trong");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Cong UDP khong hop le (phai tu 1 den 65535): " + port);
        }
    }
}
