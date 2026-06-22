JAVAC ?= javac
JAVA ?= java
BUILD_DIR ?= /tmp/naive-pattern-searching-classes

SOURCES := \
	NaivePatternSearch.java \
	NaivePatternSearchDemo.java \
	NaivePatternSearchTest.java

.PHONY: build test run clean

build:
	mkdir -p $(BUILD_DIR)
	$(JAVAC) -d $(BUILD_DIR) $(SOURCES)

test: build
	$(JAVA) -cp $(BUILD_DIR) NaivePatternSearchTest

run: build
	$(JAVA) -cp $(BUILD_DIR) NaivePatternSearchDemo

clean:
	rm -rf $(BUILD_DIR)
