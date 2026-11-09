@echo off
rem Compile and start the Smart Student Survival System (needs JDK 8 or newer).
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
if errorlevel 1 goto end
java -cp out gui.Main
:end
del sources.txt 2>nul
pause
