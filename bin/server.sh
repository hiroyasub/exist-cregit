#!/bin/bash
# -----------------------------------------------------------------------------
# startup.sh - Start Script for Jetty + eXist
#
# $Id$
# -----------------------------------------------------------------------------

#
# Pass -j to enable JMX agent. The port for it can be specified with -p
#
usage="server.sh [-j] [-p jmx-port]\n"

JMX_ENABLED=0
JMX_PORT=1099
while getopts ":jp:" option
do 
  case $option in
      j ) JMX_ENABLED=1 ;;
      p ) JMX_PORT=$OPTARG;;
  esac
done

exist_home () {
	case "$0" in
		/*)
			p=$0
		;;
		*)
			p=`/bin/pwd`/$0
		;;
	esac
		(cd `/usr/bin/dirname $p` ; /bin/pwd)
}

if [ -z "$EXIST_HOME" ]; then
	EXIST_HOME_1=`exist_home`
	EXIST_HOME="$EXIST_HOME_1/.."
fi

if [ ! -f "$EXIST_HOME/start.jar" ]; then
	echo "Unable to find start.jar. Please set EXIST_HOME to point to your installation directory."
	exit 1
fi

OPTIONS="-Dexist.home=$EXIST_HOME"

# save LANG
if [ -n "$LANG" ]; then
	OLD_LANG="$LANG"
fi
# set LANG to UTF-8
LANG=en_US.UTF-8

# set java options
if [ -z "$JAVA_OPTIONS" ]; then
	JAVA_OPTIONS="-Xms16000k -Xmx256000k -Dfile.encoding=UTF-8"
fi

JAVA_ENDORSED_DIRS="$EXIST_HOME"/lib/endorsed
JAVA_OPTIONS="$JAVA_OPTIONS -Djava.endorsed.dirs=$JAVA_ENDORSED_DIRS"



$JAVA_HOME/bin/java $JAVA_OPTIONS $OPTIONS -jar "$EXIST_HOME/start.jar" standalone $*

if [ -n "$OLD_LANG" ]; then
	LANG="$OLD_LANG"
fi
