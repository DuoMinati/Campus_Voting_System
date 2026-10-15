@echo off
title Campus Election Commission - Web Portal
cls
echo ========================================================
echo   CAMPUS ELECTION COMMISSION - WEB PORTAL
echo   Starting Local HTTP Server on Port 8080...
echo ========================================================
echo.
java -cp "bin;lib/*" Main --web
pause
