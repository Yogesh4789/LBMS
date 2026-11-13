@REM ----------------------------------------------------------------------------
@REM Maven Wrapper Batch Script for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set "DIR=%~dp0"
if "%DIR%"=="" set "DIR=."

set "MAVEN_PROJECTBASEDIR=%DIR%"

if not exist "%DIR%\.mvn\wrapper\maven-wrapper.jar" (
    echo Downloading Maven Wrapper JAR...
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar', '%DIR%\.mvn\wrapper\maven-wrapper.jar')"
)

java -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -jar "%DIR%\.mvn\wrapper\maven-wrapper.jar" %*
if ERRORLEVEL 1 exit /b %ERRORLEVEL%

