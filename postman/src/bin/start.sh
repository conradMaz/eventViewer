#!/bin/bash

  if [ -z "$JAVA_HOME" ]; then
       echo "Please set your Java home";
       exit 1;
  fi
  
  readonly MAINCLASS=com.conradmaz.messagehub.postman.Postman;
  
  function start() {
    echo "###############################################################################" ;
    echo "Starting postman application javahome=$JAVA" ;
    classpath=lib/*:lib/ext/*:conf/*:
   
    echo "classpath=$classpath" ;
    startScript="$JAVA_HOME/bin/java -Dlog4j.configurationFile=conf/default/log4j2.xml -cp $classpath $MAINCLASS";
    
    echo "startScript=$startScript";
    
    $startScript ; 
  }
  
  start