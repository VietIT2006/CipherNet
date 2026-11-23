package com.detai10.core.protocol;

import com.detai10.core.model.FileMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @DisplayName("Bóc tách DATA packet an toàn khi content chứa ký tự pipe |")
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
    @DisplayName("Đóng gói thông điệp RESULT và ERROR")
    void testResultAndError() {
        assertEquals("RESULT|FOUND|0, 15", UdpProtocol.createResultFoundMessage("0, 15"));
        assertEquals("RESULT|NOT_FOUND|Khong tim thay tu khoa HELLO", UdpProtocol.createResultNotFoundMessage());
        assertEquals("ERROR|Loi ket noi", UdpProtocol.createErrorMessage("Loi ket noi"));
    }
}
