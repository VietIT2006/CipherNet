package com.detai10.core.protocol;

import com.detai10.core.model.FileMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bộ kiểm thử đơn vị toàn diện cho giao thức UDP (CORE-01 / CORE-07).
 * Tuân thủ Quy tắc 3: Bóc tách gói tin an toàn (Safe Payload Parsing) với split("\\|", 4).
 */
class UdpProtocolTest {

    @Test
    @DisplayName("Kiểm tra thông điệp PING và PONG không phân biệt hoa/thường")
    void testPingPong() {
        assertTrue(UdpProtocol.isPing("PING"));
        assertTrue(UdpProtocol.isPing("ping "));
        assertTrue(UdpProtocol.isPing("  PiNg  "));
        assertFalse(UdpProtocol.isPing("PONG"));
        assertFalse(UdpProtocol.isPing(null));
        assertFalse(UdpProtocol.isPing(""));

        assertTrue(UdpProtocol.isPong("PONG"));
        assertTrue(UdpProtocol.isPong("pong"));
        assertTrue(UdpProtocol.isPong("  PoNg  "));
        assertFalse(UdpProtocol.isPong("PING"));
        assertFalse(UdpProtocol.isPong(null));
        assertFalse(UdpProtocol.isPong(""));

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
    @DisplayName("Bóc tách DATA packet an toàn khi content chứa ký tự pipe | (Quy tắc 3)")
    void testDataPacketWithPipeInContent() {
        String msg = "DATA|BINARY|data.bin|GiaTri1|GiaTri2|GiaTri3|GiaTri4";
        UdpProtocol.DataPayload payload = UdpProtocol.parseDataPacket(msg);

        assertEquals(FileMode.BINARY, payload.getMode());
        assertEquals("data.bin", payload.getFileName());
        assertEquals("GiaTri1|GiaTri2|GiaTri3|GiaTri4", payload.getContent(),
                "Nội dung chứa ký tự pipe '|' phải được bảo toàn nguyên vẹn");
    }

    @Test
    @DisplayName("Đóng gói DATA packet xử lý an toàn giá trị tham số null")
    void testCreateDataMessageNullParams() {
        // null mode mặc định TEXT, null fileName mặc định unnamed.dat, null content mặc định rỗng
        String msg = UdpProtocol.createDataMessage(null, null, null);
        assertEquals("DATA|TEXT|unnamed.dat|", msg);

        UdpProtocol.DataPayload payload = UdpProtocol.parseDataPacket(msg);
        assertEquals(FileMode.TEXT, payload.getMode());
        assertEquals("unnamed.dat", payload.getFileName());
        assertEquals("", payload.getContent());
    }

    @Test
    @DisplayName("Ném ngoại lệ IllegalArgumentException khi gói DATA sai định dạng hoặc thiếu trường")
    void testInvalidDataPacket() {
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.parseDataPacket(null));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.parseDataPacket(""));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.parseDataPacket("   "));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.parseDataPacket("DATA|TEXT"));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.parseDataPacket("DATA|TEXT|file.txt"));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.parseDataPacket("INFO|TEXT|file.txt|content"));
        assertThrows(IllegalArgumentException.class, () -> UdpProtocol.parseDataPacket("DATA|TEXT|  |content"));
    }

    @Test
    @DisplayName("Đóng gói thông điệp RESULT và ERROR")
    void testResultAndError() {
        assertEquals("RESULT|FOUND|0, 15", UdpProtocol.createResultFoundMessage("0, 15"));
        assertEquals("RESULT|FOUND|", UdpProtocol.createResultFoundMessage(null));
        assertEquals("RESULT|NOT_FOUND|Khong tim thay tu khoa HELLO", UdpProtocol.createResultNotFoundMessage());
        assertEquals("ERROR|Loi ket noi", UdpProtocol.createErrorMessage("Loi ket noi"));
        assertEquals("ERROR|Loi khong xac dinh", UdpProtocol.createErrorMessage(null));
    }

    @Test
    @DisplayName("Kiểm tra tính đúng đắn của DataPayload equals và hashCode")
    void testDataPayloadEqualsAndHashCode() {
        UdpProtocol.DataPayload p1 = new UdpProtocol.DataPayload(FileMode.TEXT, "a.txt", "abc");
        UdpProtocol.DataPayload p2 = new UdpProtocol.DataPayload(FileMode.TEXT, "a.txt", "abc");
        UdpProtocol.DataPayload p3 = new UdpProtocol.DataPayload(FileMode.BINARY, "a.txt", "abc");

        assertEquals(p1, p1);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
        assertNotEquals(p1, null);
        assertNotEquals(p1, "khong phai DataPayload");
    }

    @Test
    @DisplayName("Khởi tạo thể hiện tiện ích qua reflection phải ném ngoại lệ UnsupportedOperationException")
    void testPrivateConstructor() throws Exception {
        Constructor<UdpProtocol> constructor = UdpProtocol.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertNotNull(thrown.getCause());
        assertTrue(thrown.getCause() instanceof UnsupportedOperationException);
    }
}
