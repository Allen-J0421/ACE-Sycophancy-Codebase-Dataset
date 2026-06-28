JAVAC := javac
JAVA := java
SOURCES := $(wildcard *.java)

.PHONY: build demo test clean

build:
	$(JAVAC) $(SOURCES)

demo: build
	$(JAVA) BinarySearchDemo

test: build
	$(JAVA) BinarySearchTest

clean:
	rm -f *.class
