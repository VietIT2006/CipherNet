@echo off
setlocal
echo ==========================================
echo  Du an De tai 10 - Khoi chay Server UDP
echo ==========================================

cd /d "%~dp0\.."

set JAVA_BIN=java
if exist "%USERPROFILE%\.jdks\ms-21.0.10\bin\java.exe" set JAVA_BIN=%USERPROFILE%\.jdks\ms-21.0.10\bin\java.exe
if exist "%USERPROFILE%\.jdks\jbr-21.0.11\bin\java.exe" set JAVA_BIN=%USERPROFILE%\.jdks\jbr-21.0.11\bin\java.exe

if exist "app\build\libs\udp-server.jar" (
    "%JAVA_BIN%" -jar app\build\libs\udp-server.jar
) else if exist "build\libs\udp-server.jar" (
    "%JAVA_BIN%" -jar build\libs\udp-server.jar
) else (
    echo Khong tim thay file udp-server.jar! Dang tien hanh bien dich...
    call .\gradlew.bat shadowJar --no-daemon
    "%JAVA_BIN%" -jar app\build\libs\udp-server.jar
)
pause
