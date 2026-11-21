@echo off
echo ==========================================
echo  Dự án Đề tài 10 - Khởi chạy Server UDP
echo ==========================================
if exist "app\build\libs\udp-server.jar" (
    java -jar app\build\libs\udp-server.jar
) else if exist "build\libs\udp-server.jar" (
    java -jar build\libs\udp-server.jar
) else (
    echo Khong tim thay file udp-server.jar! Dang tien hanh bien dich...
    call .\gradlew.bat :app:serverJar --no-daemon
    java -jar app\build\libs\udp-server.jar
)
pause
