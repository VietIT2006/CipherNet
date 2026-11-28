@echo off
setlocal
chcp 65001 >nul
echo ======================================================================
echo    CHUONG TRINH KIEM THU TAI NHE UDP SERVER (VER-04) - DE TAI 10
echo    Phu trach thuc hien: Nguyen Tuan Nhut (Sprint 4 - M4-release)
echo ======================================================================
echo Dang chay kiem thu tu dong 100 goi tin UDP dong thoi qua JUnit 5...
echo.

cd /d "%~dp0\.."
call gradlew.bat test --tests com.detai10.server.UdpServerStressTest

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ======================================================================
    echo [THANH CONG] Toan bo 100 goi tin UDP va CSDL da duoc kiem chung 100%%!
    echo Benchmark do tre phan hoi trung binh dat chuan NFR-01: ^< 100ms.
    echo ======================================================================
) else (
    echo.
    echo [LOI] Kiem thu tai gap su co! Vui long kiem tra log chi tiet.
)
pause
