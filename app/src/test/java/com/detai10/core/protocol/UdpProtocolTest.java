package com.detai10.core.protocol;

import com.detai10.core.model.FileMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UdpProtocolTest {

    @Test
    @DisplayName("Kiểm tra thông điệp PING và PONG")
    void testPingPong() {
        assertTrue(UdpProtocol.isPing("PING"));
        assertTrue(UdpProtocol.isPing("ping "));
        assertTrue(UdpProtocol.isPong("PONG"));
        assertTrue(UdpProtocol.isPong("pong"));

        assertEquals("PING", UdpProtocol.createPingMessage());
        assertEquals("PONG", UdpProtocol.createPongMessage());
    }

    @Test
    @DisplayName("Đóng gói và bóc tách DATA packet chuẩn xác")
    void testDataPacketCreationAndParsing() {
        String msg = UdpProtocol.createDataMessage(FileMode.TEXT, "test.txt", "Noi dung text");
        assertEquals("DATA|TEXT|test.txt|Noi dung text", msg);

        UdpProtocol.DataPayload payload = UdpProtocol.parseDataPacket(msg);
        assertEquals(FileMode.TEXT, payload.getMode());
        assertEquals("test.txt", payload.getFileName());
        assertEquals("Noi dung text", payload.getContent());
    }

    @Test
    @DisplayName("EX-02: Bóc tách DATA packet an toàn khi content chứa ký tự pipe |")
    void testDataPacketWithPipeInContent() {
        String msg = "DATA|BINARY|data.bin|GiaTri1|GiaTri2|GiaTri3";
        UdpProtocol.DataPayload payload = UdpProtocol.parseDataPacket(msg);

        assertEquals(FileMode.BINARY, payload.getMode());
        assertEquals("data.bin", payload.getFileName());
        assertEquals("GiaTri1|GiaTri2|GiaTri3", payload.getContent());
    }

    @Test
    @DisplayName("Ném ngoại lệ khi gói DATA sai định dạng hoặc null")
    void testInvalidDataPacket() {
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.parseDataPacket(null));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.parseDataPacket(""));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.parseDataPacket("DATA|TEXT"));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.parseDataPacket("DATA|TEXT|  |content"));
    }

    @Test
    @DisplayName("NFR-02: Chặn tên file chứa Path Traversal hoặc ký tự cấm")
    void testSanitizeFileNameBlocksTraversal() {
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.sanitizeFileName("../secret.txt"));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.sanitizeFileName("sub/file.txt"));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.sanitizeFileName("sub\\file.txt"));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.sanitizeFileName("bad|name.txt"));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.parseDataPacket("DATA|TEXT|../evil.txt|HELLO"));
        assertEquals("demo_01.txt", UdpProtocol.sanitizeFileName("  demo_01.txt  "));
    }

    @Test
    @DisplayName("EX-03: Từ chối gói DATA > 30.000 bytes và DatagramPacket > 65.507 bytes UTF-8")
    void testPayloadSizeLimit() throws Exception {
        String oversizedData = "A".repeat(30001);
        assertThrows(IllegalArgumentException.class,
                () -> UdpProtocol.createDataMessage(FileMode.TEXT, "big.txt", oversizedData));

        InetAddress localhost = InetAddress.getByName("127.0.0.1");
        String oversizedUdp = "X".repeat(UdpProtocol.BUFFER_SIZE + 1);
        assertThrows(IllegalArgumentException.class,
                () -> UdpProtocol.createUdpPacket(oversizedUdp, localhost, 9876));
    }

    @Test
    @DisplayName("CORE-01: Helper tạo và trích xuất DatagramPacket UTF-8 đúng offset và length")
    void testDatagramPacketHelpersWithOffsetAndLength() throws Exception {
        InetAddress localhost = InetAddress.getByName("127.0.0.1");
        String text = "Xin chào Việt Nam! HELLO|UDP";

        DatagramPacket sendPacket = UdpProtocol.createUdpPacket(text, localhost, 9876);
        assertEquals(text, UdpProtocol.extractPacketMessage(sendPacket));
        assertEquals(9876, sendPacket.getPort());

        // Giả lập buffer lớn có offset != 0 và nhiều byte rác phía sau
        byte[] payloadBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] paddedBuffer = new byte[1024];
        int offset = 16;
        System.arraycopy(payloadBytes, 0, paddedBuffer, offset, payloadBytes.length);
        paddedBuffer[offset + payloadBytes.length] = 'Z'; // byte rác ngoài length

        DatagramPacket slicedPacket = new DatagramPacket(paddedBuffer, offset, payloadBytes.length);
        assertEquals(text, UdpProtocol.extractPacketMessage(slicedPacket));

        DatagramPacket receiveBuffer = UdpProtocol.createReceiveBufferPacket();
        assertEquals(UdpProtocol.BUFFER_SIZE, receiveBuffer.getData().length);

        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.extractPacketMessage(null));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.createUdpPacket("PING", null, 9876));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.createUdpPacket("PING", localhost, 0));
    }

    @Test
    @DisplayName("Đóng gói và bóc tách RESULT (SRS 3.1) và ERROR")
    void testResultAndError() {
        String foundMsg = UdpProtocol.createResultMessage(List.of(0, 15));
        assertEquals("RESULT|OK|Vi tri HELLO: [0, 15]", foundMsg);

        UdpProtocol.ResponsePayload parsedFound = UdpProtocol.parseResponsePacket(foundMsg);
        assertTrue(parsedFound.isFound());
        assertFalse(parsedFound.isError());
        assertEquals("Vi tri HELLO: [0, 15]", parsedFound.getMessage());

        String notFoundMsg = UdpProtocol.createResultMessage(Collections.emptyList());
        assertEquals("RESULT|NOT_FOUND|Khong tim thay tu khoa HELLO", notFoundMsg);

        UdpProtocol.ResponsePayload parsedNotFound = UdpProtocol.parseResponsePacket(notFoundMsg);
        assertFalse(parsedNotFound.isFound());
        assertFalse(parsedNotFound.isError());

        String errorMsg = UdpProtocol.createErrorMessage("Loi ket noi");
        assertEquals("ERROR|Loi ket noi", errorMsg);
        UdpProtocol.ResponsePayload parsedError = UdpProtocol.parseResponsePacket(errorMsg);
        assertTrue(parsedError.isError());
        assertEquals("Loi ket noi", parsedError.getMessage());
    }
}
