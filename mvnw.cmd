@echo off
setlocal
cd /d "%~dp0backend\budgetbuddy"
call ".\mvnw.cmd" %*
exit /b %ERRORLEVEL%
