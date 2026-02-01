@echo off
title AUTO MySQL / MariaDB Port Switcher (XAMPP)
color 0A

:: ==== ADMIN CHECK ====
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo [X] Hay chay file bang quyen ADMIN!
    pause
    exit
)

set MYSQL_INI=C:\xampp\mysql\bin\my.ini
set PORT_LIST=3306 3307 3308 3310 3320

echo ================================================
echo   AUTO MYSQL / MARIADB PORT SWITCHER (XAMPP)
echo ================================================
echo.

:: ==== STOP MYSQL SERVICE ====
echo [*] Dang dung MySQL / MariaDB...
taskkill /F /IM mysqld.exe >nul 2>&1

:: ==== FIND FREE PORT ====
echo [*] Dang tim port trong...
set NEWPORT=

for %%P in (%PORT_LIST%) do (
    netstat -aon | findstr :%%P >nul
    if errorlevel 1 (
        set NEWPORT=%%P
        goto PORT_FOUND
    )
)

echo [X] Khong tim duoc port trong!
pause
exit

:PORT_FOUND
echo [OK] Port trong: %NEWPORT%

:: ==== UPDATE my.ini PORT ====
echo [*] Dang cap nhat my.ini...

powershell -Command ^
"(Get-Content '%MYSQL_INI%') ^
-replace 'port\s*=\s*\d+', 'port=%NEWPORT%' ^
| Set-Content '%MYSQL_INI%'"

echo [OK] Da doi port sang %NEWPORT%

:: ==== RESTART MYSQL ====
echo [*] Dang khoi dong lai MariaDB / MySQL...
start "" "C:\xampp\mysql\bin\mysqld.exe" --defaults-file="C:\xampp\mysql\bin\my.ini"

timeout /t 2 >nul

:: ==== VERIFY ====
echo.
echo [*] Kiem tra trang thai port...

netst
