# CLAUDE.md — Agent & Assistant Guide for Vigen (Đề tài 10)

## Build & Test Commands
- **Run tests:** `./gradlew test` (or `.\scripts\test-headless.bat`)
- **Package Fat JARs:** `./gradlew shadowJar` (or `.\scripts\build-all.bat`)
- **Run Server:** `java -jar app/build/libs/udp-server.jar` (or `.\scripts\start-server.bat`)
- **Run Client:** `java -jar app/build/libs/udp-client.jar` (or `.\scripts\start-client.bat`)

## Key Architecture & Code Conventions
- **Language:** Java 17 LTS (`--release 17`, tương thích JDK 17+), Gradle 9.7.1 wrapper.
- **Headless Core & Client Net:** Packages `com.detai10.core.*` and `com.detai10.client.net.*` contain pure logic with ZERO Swing/AWT imports.
- **UDP Protocol:**
  - Handshake: `PING` <-> `PONG` (3s timeout).
  - Data transfer: `DATA|<mode>|<fileName>|<content>`.
  - Parse with `split("\\|", 4)` for pipe safety.
- **Vigenère Cipher:** Shifts `[A-Za-z]`; preserves digits, whitespace, symbols; key pointer advances only on letters.
- **I/O:**
  - `TEXT`: UTF-8 `BufferedWriter`/`BufferedReader`.
  - `BINARY`: `DataOutputStream(BufferedOutputStream)` / `DataInputStream(BufferedInputStream)` with `writeUTF()`/`readUTF()`.
- **Database:** MySQL JDBC via `PreparedStatement` on table `lich_su_xu_ly`.
- **Threading:** Datagram socket listener runs in `UdpServerWorker` thread. GUI updates dispatched via `SwingUtilities.invokeLater()`.

## Reference Documents
- High-level Agent Constitution: `AGENTS.md`
- Context & Domain Language: `CONTEXT.md`
- Task Specifications: `architecture/tasks/<TASK_ID>.md`
- Technical Specs: `architecture/specs/`
- Coding Standards: `architecture/docs/CODING_STANDARDS.md`
- Testing Guide: `architecture/docs/TESTING_GUIDE.md`
