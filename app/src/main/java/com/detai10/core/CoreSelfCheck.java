package com.detai10.core;

import com.detai10.core.cipher.VigenereCipher;
import com.detai10.core.db.DatabaseHelper;
import com.detai10.core.db.HistoryRecord;
import com.detai10.core.io.FileHelper;
import com.detai10.core.model.FileMode;
import com.detai10.core.protocol.UdpProtocol;
import com.detai10.core.search.KeywordFinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Lớp tự kiểm tra độc lập nhanh toàn bộ các mô-đun trong tầng Core Logic (Headless).
 * <p>
 * Yêu cầu & Quy chuẩn kỹ thuật:
 * - Chạy trực tiếp từ dòng lệnh {@code java com.detai10.core.CoreSelfCheck} hoặc Gradle {@code ./gradlew coreSelfCheck}.
 * - Tự kiểm tra độc lập 5 thành phần cốt lõi:
 *   1. UdpProtocol (Đóng gói và bóc tách gói tin an toàn, bóc tách chuỗi có ký tự pipe '|').
 *   2. VigenereCipher (Mã hóa, giải mã, bảo toàn số, khoảng trắng, ký hiệu và không tăng bước con trỏ khóa).
 *   3. KeywordFinder (Quét vị trí từ khóa HELLO không phân biệt hoa/thường, overlapping).
 *   4. FileHelper (Đọc/ghi file TEXT UTF-8 và BINARY DataStream).
 *   5. DatabaseHelper (Kết nối H2 In-Memory, khởi tạo bảng, lưu trữ PreparedStatement, Zero SQL Injection).
 * - Tuân thủ nghiêm ngặt Quy tắc 1 (Headless First): Tuyệt đối không import thư viện giao diện AWT/Swing.
 * - In kết quả rõ ràng [PASS] / [FAIL] cho từng mục và tổng kết toàn bộ kiểm thử.
 */
public final class CoreSelfCheck {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_BOLD = "\u001B[1m";

    private CoreSelfCheck() {
        // Ngăn chặn khởi tạo thể hiện
    }

    /**
     * Phương thức khởi chạy chính từ terminal hoặc script.
     *
     * @param args Tham số dòng lệnh (không sử dụng)
     */
    public static void main(String[] args) {
        boolean allPassed = runAllChecks();
        if (!allPassed) {
            System.exit(1);
        }
    }

    /**
     * Thực thi toàn bộ chuỗi kiểm tra tự động của tầng Core và in kết quả ra console.
     *
     * @return {@code true} nếu 100% bài kiểm tra đạt [PASS], ngược lại {@code false}.
     */
    public static boolean runAllChecks() {
        printHeader();

        int totalChecks = 5;
        int passedChecks = 0;

        // 1. Kiểm tra UdpProtocol
        if (checkUdpProtocol()) {
            printCheckResult(1, "UdpProtocol", "Đóng gói & bóc tách an toàn (Safe Payload Parsing)", true, null);
            passedChecks++;
        } else {
            printCheckResult(1, "UdpProtocol", "Đóng gói & bóc tách an toàn", false, "Sai cấu trúc gói tin hoặc lỗi bóc tách");
        }

        // 2. Kiểm tra VigenereCipher
        if (checkVigenereCipher()) {
            printCheckResult(2, "VigenereCipher", "Mã hóa/giải mã & bảo toàn ký tự ngoài A-Z (Rule 4)", true, null);
            passedChecks++;
        } else {
            printCheckResult(2, "VigenereCipher", "Mã hóa/giải mã Vigenère", false, "Dữ liệu mã hóa/giải mã không đối xứng hoặc vi phạm bảo toàn");
        }

        // 3. Kiểm tra KeywordFinder
        if (checkKeywordFinder()) {
            printCheckResult(3, "KeywordFinder", "Tìm kiếm từ khóa HELLO case-insensitive & overlapping", true, null);
            passedChecks++;
        } else {
            printCheckResult(3, "KeywordFinder", "Tìm kiếm từ khóa HELLO", false, "Bỏ sót vị trí hoặc sai định dạng kết quả");
        }

        // 4. Kiểm tra FileHelper
        if (checkFileHelper()) {
            printCheckResult(4, "FileHelper", "Đọc/ghi File TEXT (UTF-8) & BINARY (DataStream) (Rule 5)", true, null);
            passedChecks++;
        } else {
            printCheckResult(4, "FileHelper", "Thao tác File I/O", false, "Mất mát nội dung hoặc sai lệch bảng mã UTF-8");
        }

        // 5. Kiểm tra DatabaseHelper
        if (checkDatabaseHelper()) {
            printCheckResult(5, "DatabaseHelper", "Thao tác CSDL H2 In-Memory & Zero SQL Injection (Rule 6)", true, null);
            passedChecks++;
        } else {
            printCheckResult(5, "DatabaseHelper", "Thao tác CSDL JDBC", false, "Lỗi kết nối H2 hoặc lỗi truy vấn CSDL");
        }

        printFooter(passedChecks, totalChecks);
        return passedChecks == totalChecks;
    }

    /**
     * Kiểm tra logic của {@link UdpProtocol}.
     */
    public static boolean checkUdpProtocol() {
        try {
            // PING & PONG
            if (!UdpProtocol.isPing("PING") || !UdpProtocol.isPong("PONG")) {
                return false;
            }
            if (!"PING".equals(UdpProtocol.createPingMessage()) || !"PONG".equals(UdpProtocol.createPongMessage())) {
                return false;
            }

            // DATA Packet đóng gói & bóc tách
            String dataMsg = UdpProtocol.createDataMessage(FileMode.TEXT, "test.txt", "Noi dung|co chua|nhieu dau pipe");
            UdpProtocol.DataPayload payload = UdpProtocol.parseDataPacket(dataMsg);
            if (payload.getMode() != FileMode.TEXT ||
                    !"test.txt".equals(payload.getFileName()) ||
                    !"Noi dung|co chua|nhieu dau pipe".equals(payload.getContent())) {
                return false;
            }

            // Phản hồi RESULT & ERROR
            if (!"RESULT|FOUND|0, 15".equals(UdpProtocol.createResultFoundMessage("0, 15"))) {
                return false;
            }
            if (!"RESULT|NOT_FOUND|Khong tim thay tu khoa HELLO".equals(UdpProtocol.createResultNotFoundMessage())) {
                return false;
            }
            if (!"ERROR|Loi ket noi".equals(UdpProtocol.createErrorMessage("Loi ket noi"))) {
                return false;
            }

            // Kiểm tra bắt ngoại lệ khi gói tin hỏng
            try {
                UdpProtocol.parseDataPacket("GoiTinKhongHopLe");
                return false; // Phải ném ngoại lệ
            } catch (IllegalArgumentException expected) {
                // Hợp lệ
            }

            return true;
        } catch (Exception e) {
            System.err.println("[ERROR][UdpProtocol] " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra logic của {@link VigenereCipher}.
     */
    public static boolean checkVigenereCipher() {
        try {
            // Vector kiểm thử chuẩn: ATTACKATDAWN với khóa LEMON -> LXFOPVEFRNHR
            String plain = "ATTACKATDAWN";
            String key = "LEMON";
            String cipher = VigenereCipher.encrypt(plain, key);
            if (!"LXFOPVEFRNHR".equals(cipher)) {
                return false;
            }
            String decrypted = VigenereCipher.decrypt(cipher, key);
            if (!plain.equals(decrypted)) {
                return false;
            }

            // Kiểm tra bảo toàn số, dấu câu, khoảng trắng và không tăng bước con trỏ khóa
            String mixedPlain = "Hello 123! How are you?";
            String mixedCipher = VigenereCipher.encrypt(mixedPlain, "KEY");
            if (!" 123! ".equals(mixedCipher.substring(5, 11))) {
                return false;
            }
            if (!mixedPlain.equals(VigenereCipher.decrypt(mixedCipher, "KEY"))) {
                return false;
            }

            // Kiểm tra bảo toàn chữ hoa / chữ thường
            String casePlain = "AbCdEfGh";
            String caseCipher = VigenereCipher.encrypt(casePlain, "XYZ");
            if (!casePlain.equals(VigenereCipher.decrypt(caseCipher, "XYZ"))) {
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("[ERROR][VigenereCipher] " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra logic của {@link KeywordFinder}.
     */
    public static boolean checkKeywordFinder() {
        try {
            // Tìm kiếm HELLO không phân biệt hoa/thường
            String text = "hello World, HELLO Java, hElLo 2026!";
            List<Integer> positions = KeywordFinder.findHelloIndices(text);
            if (positions.size() != 3 || positions.get(0) != 0 || positions.get(1) != 13 || positions.get(2) != 25) {
                return false;
            }

            // Tìm kiếm overlapping: "HELLOHELLO" -> 0, 5
            List<Integer> overlap = KeywordFinder.findHelloIndices("HELLOHELLO");
            if (overlap.size() != 2 || overlap.get(0) != 0 || overlap.get(1) != 5) {
                return false;
            }

            // Không tìm thấy
            List<Integer> notFound = KeywordFinder.findHelloIndices("Chao mung ban den voi De tai 10");
            if (!notFound.isEmpty()) {
                return false;
            }
            if (!"NOT_FOUND".equals(KeywordFinder.formatPositions(notFound))) {
                return false;
            }
            if (!"Không tìm thấy".equals(KeywordFinder.formatPositionsDisplay(notFound))) {
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("[ERROR][KeywordFinder] " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra logic của {@link FileHelper}.
     */
    public static boolean checkFileHelper() {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("detai10_selfcheck_");

            // 1. Chế độ TEXT: UTF-8 tiếng Việt có dấu và ký tự đặc biệt
            File textFile = new File(tempDir.toFile(), "sub/text_utf8.txt");
            String textContent = "Hệ thống truyền nhận UDP - Mã hóa Vigenère Đề tài 10 - Năm học 2026.\nKý tự đặc biệt: @#$%^&*()_+";
            FileHelper.writeFile(textFile, textContent, FileMode.TEXT);
            String readText = FileHelper.readFile(textFile, FileMode.TEXT);
            if (!textContent.equals(readText)) {
                return false;
            }

            // 2. Chế độ BINARY: DataOutputStream / DataInputStream writeUTF / readUTF
            File binFile = new File(tempDir.toFile(), "sub/binary_data.bin");
            String binContent = "Nội dung kiểm tra nhị phân định kiểu UTF: HELLO DE TAI 10 (2026)";
            FileHelper.writeFile(binFile, binContent, FileMode.BINARY);
            String readBin = FileHelper.readFile(binFile, FileMode.BINARY);
            if (!binContent.equals(readBin)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("[ERROR][FileHelper] " + e.getMessage());
            return false;
        } finally {
            if (tempDir != null) {
                try {
                    Files.walk(tempDir)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Kiểm tra logic của {@link DatabaseHelper}.
     */
    public static boolean checkDatabaseHelper() {
        try (DatabaseHelper dbHelper = DatabaseHelper.createInMemoryHelper()) {
            if (!dbHelper.isConnectionAlive()) {
                return false;
            }

            dbHelper.clearHistory();

            // Lưu bản ghi lịch sử chuẩn
            HistoryRecord record = new HistoryRecord(
                    "127.0.0.1",
                    "sample.txt",
                    "TEXT",
                    "HELLO WORLD",
                    "RIJVS UYVJN",
                    "0"
            );
            boolean saved = dbHelper.saveHistory(record);
            if (!saved || record.getId() <= 0) {
                return false;
            }

            // Truy vấn và xác thực tính toàn vẹn
            List<HistoryRecord> historyList = dbHelper.getAllHistory();
            if (historyList.size() != 1) {
                return false;
            }
            HistoryRecord retrieved = historyList.get(0);
            if (!"127.0.0.1".equals(retrieved.getClientIp()) ||
                    !"sample.txt".equals(retrieved.getFileName()) ||
                    !"HELLO WORLD".equals(retrieved.getOriginalContent()) ||
                    !"0".equals(retrieved.getHelloPositions())) {
                return false;
            }

            // Kiểm tra Zero SQL Injection
            String injectionAttack = "'; DROP TABLE lich_su_xu_ly; --";
            HistoryRecord injectionRecord = new HistoryRecord(
                    "192.168.1.99",
                    injectionAttack,
                    "TEXT",
                    injectionAttack,
                    injectionAttack,
                    "NOT_FOUND"
            );
            if (!dbHelper.saveHistory(injectionRecord)) {
                return false;
            }

            // Xác nhận bảng vẫn sống và kết nối hoạt động
            if (!dbHelper.isConnectionAlive()) {
                return false;
            }

            dbHelper.clearHistory();
            return dbHelper.getAllHistory().isEmpty();
        } catch (Exception e) {
            System.err.println("[ERROR][DatabaseHelper] " + e.getMessage());
            return false;
        }
    }

    private static void printHeader() {
        System.out.println(ANSI_CYAN + "============================================================" + ANSI_RESET);
        System.out.println(ANSI_BOLD + "     CORE SELF-CHECK SUITE — HỆ THỐNG VIGEN (ĐỀ TÀI 10)" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "============================================================" + ANSI_RESET);
    }

    private static void printCheckResult(int index, String component, String description, boolean passed, String errorDetails) {
        String statusLabel = passed
                ? ANSI_GREEN + "[PASS]" + ANSI_RESET
                : ANSI_RED + "[FAIL]" + ANSI_RESET;
        System.out.printf("%s %d. %-15s : %s%n", statusLabel, index, component, description);
        if (!passed && errorDetails != null) {
            System.out.println(ANSI_RED + "       -> Nguyên nhân: " + errorDetails + ANSI_RESET);
        }
    }

    private static void printFooter(int passed, int total) {
        System.out.println(ANSI_CYAN + "------------------------------------------------------------" + ANSI_RESET);
        if (passed == total) {
            System.out.println(ANSI_GREEN + ANSI_BOLD +
                    "KẾT QUẢ TỰ KIỂM TRA: " + passed + "/" + total + " KIỂM TRA ĐẠT CHUẨN [ALL PASS]" + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + ANSI_BOLD +
                    "KẾT QUẢ TỰ KIỂM TRA: " + (total - passed) + " KIỂM TRA THẤT BẠI [" + passed + "/" + total + " PASS]" + ANSI_RESET);
        }
        System.out.println(ANSI_CYAN + "============================================================" + ANSI_RESET);
    }
}
