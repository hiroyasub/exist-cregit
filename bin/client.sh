#!/bin/bash
# -----------------------------------------------------------------------------
# startup.sh - Start Script for Jetty + eXist
#
# $Id$
# -----------------------------------------------------------------------------

#
# In addition to the other parameter options for the interactive client 
# pass -j or --jmx to enable JMX agent.  The port for it can be specified 
# with optional port number e.g. -j1099 or --jmx=1099.
#

SCRIPTPATH=$(dirname `/bin/pwd`/$0)
# source common functions and settings
. ${SCRIPTPATH}/functions.d/eXist-settings.sh
. ${SCRIPTPATH}/functions.d/jmx-settings.sh
. ${SCRIPTPATH}/functions.d/getopt-settings.sh

get_client_getopts $*;

check_exist_home $0;

set_exist_options;

# set java options
set_client_java_options;

# enable the JMX agent? If so, concat to $JAVA_OPTIONS:
check_jmx_status;

# save LANG
set_locale_lang;

# save LD_LIBRARY_PATH
set_library_path;

${JAVA_HOME}/bin/java ${JAVA_OPTIONS} ${OPTIONS} ${DEBUG_OPTS} -jar "$EXIST_HOME/start.jar" client ${JAVA_OPTS[@]}

restore_library_path;
restore_locale_lang;
