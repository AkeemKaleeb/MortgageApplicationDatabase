@echo off

rem Compile each Java file in the src directory
for %%f in (src\*.java) do (
    javac -cp ".;lib/postgresql-42.7.4.jar" -d bin %%f
    if %errorlevel% neq 0 (
        echo Compilation failed.
        exit /b %errorlevel%
    )
)

java -cp "lib/postgresql-42.7.4.jar;bin" MortgageCLI
