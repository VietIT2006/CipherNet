package com.detai10.core;

import com.detai10.core.cipher.VigenereCipher;
import com.detai10.core.db.DatabaseHelper;
import com.detai10.core.io.FileHelper;
import com.detai10.core.model.FileMode;
import com.detai10.core.protocol.UdpProtocol;
import com.detai10.core.search.KeywordFinder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Lớp tự kiểm tra nhanh toàn bộ tầng Core Logic trên console (Headless, không cần Swing).
 * Thực hiện theo Task CORE-07.
 */
public class CoreSelfCheck {

    public static void main(String[] args) throws Exception {
        System.out.println("=== CORE SELF-CHECK (HEADLESS) ===");

        // 1. Kiểm tra UdpProtocol
        String packet = UdpProtocol.createDataMessage(FileMode.TEXT, "check.txt", "A|B|HELLO world");
        UdpProtocol.DataPayload parsed = UdpProtocol.parseDataPacket(packet);
        assertCondition("A|B|HELLO world".equals(parsed.getContent()), "UdpProtocol pipe-safe parsing");

        // 2. Kiểm tra VigenereCipher
        String original = "Say HELLO 2026! Tiếng Việt?";
        String key = "KEYSECRET";
        String encrypted = VigenereCipher.encrypt(original, key);
        String decrypted = VigenereCipher.decrypt(encrypted, key);
        assertCondition(original.equals(decrypted), "VigenereCipher encrypt/decrypt round-trip");

        // 3. Kiểm tra FileHelper (Text & Binary) trên thư mục tạm
        Path tempDir = Files.createTempDirectory("detai10-selfcheck-");
        try {
            File textFile = tempDir.resolve("text.txt").toFile();
            File binFile = tempDir.resolve("bin.dat").toFile();

            FileHelper.writeFile(textFile, encrypted, FileMode.TEXT);
            assertCondition(encrypted.equals(FileHelper.readFile(textFile, FileMode.TEXT)), "FileHelper TEXT UTF-8");

            FileHelper.writeFile(binFile, encrypted, FileMode.BINARY);
            assertCondition(encrypted.equals(FileHelper.readFile(binFile, FileMode.BINARY)), "FileHelper BINARY DataStream");
        } finally {
            deleteRecursively(tempDir.toFile());
        }

        // 4. Kiểm tra KeywordFinder
        List<Integer> positions = KeywordFinder.findHelloIndices(decrypted);
        assertCondition(positions.equals(List.of(4)), "KeywordFinder HELLO indices");

        // 5. Kiểm tra DatabaseHelper (H2 In-Memory)
        DatabaseHelper db = DatabaseHelper.createInMemoryHelper();
        db.clearHistory();
        boolean saved = db.insertLog("127.0.0.1", "check.txt", "TEXT", original, encrypted, "[4]");
        assertCondition(saved && db.getAllHistory().size() == 1, "DatabaseHelper insertLog & query");

        System.out.println("[PASS] 5/5 module Core Logic hoat dong chinh xac!");
    }

    private static void assertCondition(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError("Self-check FAILED tai: " + label);
        }
        System.out.println("  [OK] " + label);
    }

    private static void deleteRecursively(File target) {
        if (target.isDirectory()) {
            File[] children = target.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        target.delete();
    }
}
