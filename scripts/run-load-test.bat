@echo off
chcp 65001 >nul
echo ======================================================================
echo    CHƯƠNG TRÌNH KIỂM THỬ TẢI NHẸ UDP SERVER (VER-04) - ĐỀ TÀI 10
echo ======================================================================
echo Đang chạy kiểm thử tự động 100 gói tin UDP đồng thời qua JUnit 5...
echo.

cd /d "%~dp0\.."
call gradlew.bat test --tests com.detai10.server.UdpServerStressTest

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ======================================================================
    echo [THÀNH CÔNG] Toàn bộ 100 gói tin UDP và CSDL được kiểm chứng 100%%!
    echo ======================================================================
) else (
    echo.
    echo [LỖI] Kiểm thử tải thất bại! Vui lòng kiểm tra log lỗi.
)
pause
