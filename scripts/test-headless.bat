@echo off
echo ==========================================
echo  Dự án Đề tài 10 - Chạy Unit Test Core Logic
echo ==========================================
call .\gradlew.bat test --no-daemon
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] 100%% Test pass thanh cong!
) else (
    echo [ERROR] Test that bai!
)
pause
