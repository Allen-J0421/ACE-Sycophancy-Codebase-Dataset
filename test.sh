#!/bin/sh
set -e
javac -cp junit-platform-console-standalone.jar Graph.java ConnectedComponents.java GraphTest.java ConnectedComponentsTest.java
java -jar junit-platform-console-standalone.jar execute \
  --class-path . \
  --select-class GraphTest \
  --select-class ConnectedComponentsTest \
  --details=tree
