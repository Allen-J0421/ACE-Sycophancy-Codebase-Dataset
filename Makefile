BUILD_DIR ?= build/classes
JAVA ?= java
JAVAC ?= javac
MAIN_SRC_DIR := src/main/java
TEST_SRC_DIR := src/test/java

MAIN_SOURCES := $(wildcard $(MAIN_SRC_DIR)/*.java)
TEST_SOURCES := $(wildcard $(TEST_SRC_DIR)/*.java)

.PHONY: all compile demo legacy test clean

all: test

compile:
	mkdir -p $(BUILD_DIR)
	$(JAVAC) -d $(BUILD_DIR) $(MAIN_SOURCES)

demo: compile
	$(JAVA) -cp $(BUILD_DIR) KmpPatternSearchingDemo

legacy: compile
	$(JAVA) -cp $(BUILD_DIR) kmp_pattern_searching

test: compile
	$(JAVAC) -cp $(BUILD_DIR) -d $(BUILD_DIR) $(TEST_SOURCES)
	$(JAVA) -cp $(BUILD_DIR) KMPSearchTest

clean:
	rm -rf build
