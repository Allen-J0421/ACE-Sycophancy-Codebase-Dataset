BUILD_DIR ?= build/classes
JAVA ?= java
JAVAC ?= javac
MAIN_SRC_DIR := src/main/java
TEST_SRC_DIR := src/test/java

MAIN_SOURCES := $(shell find $(MAIN_SRC_DIR) -name '*.java' | sort)
TEST_SOURCES := $(shell find $(TEST_SRC_DIR) -name '*.java' | sort)

.PHONY: all compile demo legacy test clean

all: test

compile:
	mkdir -p $(BUILD_DIR)
	$(JAVAC) -d $(BUILD_DIR) $(MAIN_SOURCES)

demo: compile
	$(JAVA) -cp $(BUILD_DIR) kmp.KmpPatternSearchingDemo

legacy: compile
	$(JAVA) -cp $(BUILD_DIR) kmp_pattern_searching

test: compile
	$(JAVAC) -cp $(BUILD_DIR) -d $(BUILD_DIR) $(TEST_SOURCES)
	$(JAVA) -cp $(BUILD_DIR) kmp.KMPSearchTest

clean:
	rm -rf build
