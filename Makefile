JAVAC ?= javac
JAVA ?= java
BUILD_DIR ?= /tmp/naive-pattern-searching-classes
MAIN_CLASS := NaivePatternSearchDemo
TEST_CLASS := NaivePatternSearchTest

SOURCES := \
	NaivePatternSearch.java \
	NaivePatternSearchDemo.java \
	NaivePatternSearchTest.java

.PHONY: all build test run clean

all: test

build:
	@mkdir -p $(BUILD_DIR)
	@$(JAVAC) -d $(BUILD_DIR) $(SOURCES)

test: build
	@$(JAVA) -cp $(BUILD_DIR) $(TEST_CLASS)

run: build
	@$(JAVA) -cp $(BUILD_DIR) $(MAIN_CLASS)

clean:
	@rm -rf $(BUILD_DIR)
