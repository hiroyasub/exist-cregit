@echo off

rem will be set by the installer
if not "%EXIST_HOME%" == "" goto gotExistHome
set EXIST_HOME=$INSTALL_PATH

:gotExistHome
if not "%JAVA_HOME%" == "" goto gotJavaHome
set JAVA_HOME=$JAVA_HOME

:gotJavaHome
set ANT_HOME=%EXIST_HOME%\tools
set _LIBJARS=%CLASSPATH%;%ANT_HOME%\lib\ant-launcher.jar;%ANT_HOME%\lib\junit.jar;%JAVA_HOME%\lib\tools.jar

set JAVA_ENDORSED_DIRS=%EXIST_HOME%\lib\endorsed
set JAVA_OPTS=-Xms32000k -Xmx256000k -Djava.endorsed.dirs=%JAVA_ENDORSED_DIRS% -Dant.home=%ANT_HOME%

echo Starting Ant...
echo

java %JAVA_OPTS% -classpath %_LIBJARS% org.apache.tools.ant.launch.Launcher %1 %2 %3 %4 %5
