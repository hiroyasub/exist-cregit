@echo off
rem $Id$
rem
rem In addition to the other parameter options for the interactive client 
rem pass -j or --jmx to enable JMX agent.  The port for it can be specified 
rem with optional port number e.g. -j1099 or --jmx=1099.
rem

set JMX_ENABLED=0
set JMX_PORT=1099
set JAVA_ARGS=

if not "%JAVA_HOME%" == "" goto gotJavaHome

rem will be set by the installer
set JAVA_HOME=$JDKPath

:gotJavaHome
if not "%EXIST_HOME%" == "" goto gotExistHome

rem will be set by the installer
set EXIST_HOME=$INSTALL_PATH

:gotExistHome
set JAVA_ENDORSED_DIRS="%EXIST_HOME%"\lib\endorsed
set JAVA_OPTS="-Xms16000k -Xmx256000k -Dfile.encoding=UTF-8 -Djava.endorsed.dirs=%JAVA_ENDORSED_DIRS%"

set BATCH.D="%EXIST_HOME%\bin\batch.d"
call %BATCH.D%\get_opts.bat %*
call %BATCH.D%\check_jmx_status.bat

"%JAVA_HOME%\bin\java" "%JAVA_OPTS%"  -Dexist.home="%EXIST_HOME%" -jar "%EXIST_HOME%\start.jar" client %JAVA_ARGS%
:eof
