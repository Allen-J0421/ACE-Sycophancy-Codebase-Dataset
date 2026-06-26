JAVA := java
JAVAC := javac
OUT_DIR := out
SRC_FILES := $(shell find src -name '*.java' | sort)
MAIN_CLASS := naivepatternsearch.NaivePatternSearchDemo
TEST_CLASS := naivepatternsearch.NaivePatternSearchTest

.PHONY: all build test run clean

all: test

build:
	rm -rf $(OUT_DIR)
	mkdir -p $(OUT_DIR)
	$(JAVAC) -d $(OUT_DIR) $(SRC_FILES)

test: build
	$(JAVA) -cp $(OUT_DIR) $(TEST_CLASS)

run: build
	$(JAVA) -cp $(OUT_DIR) $(MAIN_CLASS)

clean:
	rm -rf $(OUT_DIR)
