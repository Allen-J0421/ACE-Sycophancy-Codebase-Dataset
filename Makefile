JAVAC ?= javac
JAVA ?= java
SOURCES := $(wildcard *.java)

.PHONY: all run test clean

all:
	$(JAVAC) $(SOURCES)

run: all
	$(JAVA) Dijkstra

test: all
	$(JAVA) DijkstraTest

clean:
	rm -f *.class
