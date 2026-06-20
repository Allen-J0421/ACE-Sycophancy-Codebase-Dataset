JAVAC ?= javac
JAVA ?= java
SOURCES := $(wildcard *.java)

.PHONY: all test run legacy clean

all: test

test:
	$(JAVAC) $(SOURCES)
	$(JAVA) MaxFlowTest

run:
	$(JAVAC) $(SOURCES)
	$(JAVA) MaxFlowDemo

legacy:
	$(MAKE) clean
	$(JAVAC) ford_fulkerson_max_flow.java
	$(JAVA) ford_fulkerson_max_flow

clean:
	rm -f *.class
