BUILD_DIR ?= build/classes
JAVA ?= java
JAVAC ?= javac

SOURCES := KMPSearch.java KmpPatternSearchingDemo.java kmp_pattern_searching.java KMPSearchTest.java

.PHONY: all compile demo legacy test clean

all: test

compile:
	mkdir -p $(BUILD_DIR)
	$(JAVAC) -d $(BUILD_DIR) $(SOURCES)

demo: compile
	$(JAVA) -cp $(BUILD_DIR) KmpPatternSearchingDemo

legacy: compile
	$(JAVA) -cp $(BUILD_DIR) kmp_pattern_searching

test: compile
	$(JAVA) -cp $(BUILD_DIR) KMPSearchTest

clean:
	rm -rf build
