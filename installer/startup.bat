@echo off

rem will be set by the installer
if not "%EXIST_HOME%" == "" goto gotExistHome
set EXIST_HOME=$INSTALL_PATH

:gotExistHome
cd %EXIST_HOME%

if not "%JAVA_HOME%" == "" goto gotJavaHome
set JAVA_HOME=$JAVA_HOME

:gotJavaHome
set JAVA_CMD="%JAVA_HOME%\bin\java"

set JAVA_ENDORSED_DIRS="%EXIST_HOME%"\lib\endorsed
set JAVA_OPTS="-Xms16000k -Xmx128000k -Dfile.encoding=UTF-8 -Djava.endorsed.dirs=%JAVA_ENDORSED_DIRS%"

%JAVA_CMD% "%JAVA_OPTS%" -Dexist.home="%EXIST_HOME%" -jar "%EXIST_HOME%\start.jar" jetty %1

:eof
