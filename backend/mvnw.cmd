@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper Batch Script
@REM ----------------------------------------------------------------------------

@echo off
@setlocal EnableExtensions EnableDelayedExpansion

if "%HOME%" == "" (set "HOME=%USERPROFILE%")

set ERROR_CODE=0

@REM Determine base directory
set "MAVEN_PROJECTBASEDIR=%~dp0"
:stripSlash
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set "MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%"
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" goto stripSlash

@REM Find java.exe
if not "%JAVA_HOME%" == "" (
    set "JAVACMD=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVACMD=java.exe"
)

set "WRAPPER_JAR=%~dp0.mvn\wrapper\maven-wrapper.jar"
set "WRAPPER_PROPERTIES=%~dp0.mvn\wrapper\maven-wrapper.properties"

if exist "%WRAPPER_JAR%" goto runWrapper

@REM Download wrapper jar if not present
echo Downloading Maven Wrapper...
powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar', '%WRAPPER_JAR%')"
if errorlevel 1 goto error

:runWrapper
"%JAVACMD%" %MAVEN_OPTS% -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
if errorlevel 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
exit /B %ERROR_CODE%
