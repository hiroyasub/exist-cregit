#!/bin/bash
# -----------------------------------------------------------------------------
# startup.sh - Start Script for Jetty + eXist
#
# $Id: server.sh 6208 2007-07-10 21:15:31Z ellefj $
# -----------------------------------------------------------------------------

#
# In addition to the other parameter options for the standalone server 
# pass -j or --jmx to enable JMX agent.  The port for it can be specified 
# with optional port number e.g. -j1099 or --jmx=1099.
#

# will be set by the installer
if [ -z "$EXIST_HOME" ]; then
	EXIST_HOME="%{INSTALL_PATH}"
fi

if [ -z "$JAVA_HOME" ]; then
    JAVA_HOME="%{JAVA_HOME}"
fi

SCRIPTPATH=$(dirname `/bin/pwd`/$0)
# source common functions and settings
. ${SCRIPTPATH}/functions.d/eXist-settings.sh
. ${SCRIPTPATH}/functions.d/jmx-settings.sh
. ${SCRIPTPATH}/functions.d/getopt-settings.sh

get_opts "$*" "${STANDALONESERVER_OPTS}";

check_exist_home $0;

set_exist_options;

# set java options
set_java_options;

# enable the JMX agent? If so, concat to $JAVA_OPTIONS:
check_jmx_status;

# save LANG
set_locale_lang;

$JAVA_HOME/bin/java $JAVA_OPTIONS $OPTIONS -jar "$EXIST_HOME/start.jar" standalone ${JAVA_OPTS[@]}

restore_locale_lang;
