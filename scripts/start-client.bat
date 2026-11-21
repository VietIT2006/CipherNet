@echo off
echo ==========================================
echo  Dự án Đề tài 10 - Khởi chạy Client UDP
echo ==========================================
if exist "app\build\libs\udp-client.jar" (
    java -jar app\build\libs\udp-client.jar
) else if exist "build\libs\udp-client.jar" (
    java -jar build\libs\udp-client.jar
) else (
    echo Khong tim thay file udp-client.jar! Dang tien hanh bien dich...
    call .\gradlew.bat :app:clientJar --no-daemon
    java -jar app\build\libs\udp-client.jar
)
pause
