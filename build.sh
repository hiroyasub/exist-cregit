#!/bin/bash

if [ "$JAVA_HOME" = "" ] ; then
  echo "ERROR: JAVA_HOME not found in your environment."
  echo
  echo "Please, set the JAVA_HOME variable in your environment to match the"
  echo "location of the Java Virtual Machine you want to use."
  exit 1
fi

if [ -z "$EXIST_HOME" ]; then
    P=$(dirname $0)

    if test "$P" = "." 
    then
        EXIST_HOME="`pwd`"
    else
        EXIST_HOME="$P"
    fi
fi

LOCALCLASSPATH=$CLASSPATH:$JAVA_HOME/lib/tools.jar:$EXIST_HOME/lib/core/ant.jar:$EXIST_HOME/lib/optional/ant-optional.jar:$EXIST_HOME/lib/core/junit.jar

JAVA_OPTS='-Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl'

echo Starting Ant...
echo

$JAVA_HOME/bin/java $JAVA_OPTS -classpath $LOCALCLASSPATH org.apache.tools.ant.Main $*
