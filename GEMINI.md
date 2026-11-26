# Antigravity & Gemini Project Rules: Vigen (Đề tài 10 - UDP Java System)

You are an expert AI software engineer developing the **Vigen (Đề tài 10)** project.
You must adhere to the following strict architectural rules:

## 1. Single Source of Truth
- System Specs: `architecture/specs/` (`SRS.md`, `udp-protocol.md`, `vigenere-and-io.md`)
- Task Definitions: `architecture/tasks/<TASK_ID>.md`
- Database Schema: `architecture/database/schema.sql`
- Agent Instructions: `AGENTS.md` and `.agents/rules/*.md`

## 2. Core Logic Isolation (Headless First)
- Classes in `com.detai10.core.*` MUST NOT import `java.awt.*` or `javax.swing.*`.
- Keep core logic 100% testable via JUnit 5 without a display server for Linux CI.

## 3. Safe Delimiter Parsing
- Always split DATA payload with limit 4:
  `rawMessage.split("\\|", 4)`
- This prevents packet corruption when user text content contains pipe `|` characters.

## 4. Vigenère Cipher Invariants
- Only shift English letters `[A-Za-z]`.
- Numbers `0-9`, spaces, newlines, punctuation, and symbols MUST be preserved invariant.
- Key index MUST NOT advance on non-alphabetic characters.

## 5. File I/O Integrity
- TEXT mode: `BufferedWriter`/`BufferedReader` with explicit `StandardCharsets.UTF_8`.
- BINARY mode: `DataOutputStream(BufferedOutputStream)` / `DataInputStream(BufferedInputStream)` with `writeUTF()`/`readUTF()`.
- Use JUnit 5 `@TempDir` for testing FileHelper. Never create garbage files in workspace.

## 6. MySQL Database (Local Only)
- MySQL runs locally on `localhost:3306`, not deployed to cloud.
- Connection config: `app/src/main/resources/database.properties` (ignored by git).
- Prevent SQL injection by strictly using `PreparedStatement` with `?` parameters for table `lich_su_xu_ly`.

## 7. Non-blocking UI Threading & Swing Standards
- Socket listener loop `DatagramSocket.receive()` must run on a background worker thread (`UdpServerWorker`).
- Client UDP operations must run on `SwingWorker` with loading indicators.
- All UI updates must be wrapped in `SwingUtilities.invokeLater()`.
- Do NOT use absolute layout (`setLayout(null)`). See `.agents/rules/07-ui-swing-guidelines.md`.

## 8. Architecture Folder Immutability (Strict Read-Only)
- The entire `architecture/` directory (`specs/`, `tasks/`, `database/`, `docs/`, `verification/`) is the FROZEN Single Source of Truth.
- Agents MUST NEVER modify, add, delete, or rename any file or directory inside `architecture/`. All files inside `architecture/` are strictly READ-ONLY.
