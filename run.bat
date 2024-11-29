@echo off

rem Delete existing compiled classes
if exist bin (
    rd /s /q bin
)

rem Create bin directory if it doesn't exist
if not exist bin (
    mkdir bin
)

rem Compile all Java files in the src directory
javac -cp ".;lib/postgresql-42.7.4.jar" -d bin src/java/*.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    exit /b %errorlevel%
)

rem Run the application
java -cp "lib/postgresql-42.7.4.jar;bin" MortgageCLI
