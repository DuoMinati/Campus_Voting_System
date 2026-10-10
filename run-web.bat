@echo off
title Online Voting System - Phase 3 Web Portal
cls
echo ========================================================
echo   Starting Phase 3 Web Portal (HTTP Server + Browser)
echo ========================================================
echo.
java -cp "bin;lib/*" Main --web
pause
