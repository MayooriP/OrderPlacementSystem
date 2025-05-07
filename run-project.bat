@echo off
echo ===================================================
echo    Restaurant Order System - Project Runner
echo ===================================================
echo.

REM Simple batch file to run the Restaurant Order System

echo Running application without tests...
echo.
call mvn clean spring-boot:run -DskipTests

echo.
echo Application has stopped. Press any key to exit...
pause > nul
