@echo off
chcp 65001 >nul
echo ========================================================
echo  KHỞI TẠO CƠ SỞ DỮ LIỆU MYSQL LOCAL (DỰ ÁN ĐỀ TÀI 10)
echo ========================================================
echo.
echo Dịch vụ MySQL80 đang chạy cục bộ tại: localhost:3306
echo Vui lòng nhập mật khẩu tài khoản 'root' của MySQL trên máy:
set /p MYSQL_PWD=Mật khẩu root: 

echo.
echo [1/2] Đang tạo database và bảng dữ liệu từ schema.sql...
mysql -u root -p%MYSQL_PWD% < "architecture\database\schema.sql"
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Lỗi khi thực thi schema.sql! Vui lòng kiểm tra lại mật khẩu.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Đang nạp dữ liệu mẫu từ sample_data.sql...
mysql -u root -p%MYSQL_PWD% < "architecture\database\sample_data.sql"
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Lỗi khi nạp sample_data.sql!
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ========================================================
echo [SUCCESS] Khởi tạo CSDL MySQL cục bộ thành công!
echo Database: detai10_db
echo Bảng dữ liệu: lich_su_xu_ly, sinh_vien
echo ========================================================
pause
