@echo off
echo Starting HelpHub Server...
echo.
echo Make sure you have built the project first:
echo   mvn clean package
echo.
java -jar target\helphub-3.0.0.jar
pause



