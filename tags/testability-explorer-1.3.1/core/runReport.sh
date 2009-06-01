#!/bin/sh

TE_ARGS="-jar target/core-1.3.1-SNAPSHOT-with-deps.jar" 
#TE_ARGS="$TE_ARGS  -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,suspend=y,server=y"
TE_ARGS="$TE_ARGS -cp target/core-1.3.1-SNAPSHOT.jar"
TE_ARGS="$TE_ARGS -print html"
TE_ARGS="$TE_ARGS -srcFileLineUrl http://code.google.com/p/testability-explorer/source/browse/trunk/core/src/main/java/{path}.java#{line}"
TE_ARGS="$TE_ARGS -srcFileUrl http://code.google.com/p/testability-explorer/source/browse/trunk/core/src/main/java/{path}.java"

mvn package -Dsurefire.useFile=false && java ${TE_ARGS} > report.html && open report.html

