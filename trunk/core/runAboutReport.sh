mvn -Dsurefire.useFile=false package && \
java -jar target/core-1.3.1-SNAPSHOT-with-deps.jar -print about \
-cp target/core-1.3.1-SNAPSHOT.jar:src/main/java com.google.test.metric.example > about.html && \
open about.html
