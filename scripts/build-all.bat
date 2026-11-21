@echo off
echo ==========================================
echo  Dự án Đề tài 10 - Build & Package Fat JARs
echo ==========================================
call .\gradlew.bat shadowJar --no-daemon
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Build thanh cong!
    echo Cac file Fat JAR duoc luu tai: app\build\libs\
) else (
    echo [ERROR] Build that bai! Vui long kiem tra loi.
)
pause
