OUT_DIR := out
MAIN_SOURCES := $(shell find src/main/java -name '*.java')
TEST_SOURCES := $(shell find src/test/java -name '*.java')

.PHONY: build test run clean

build:
	rm -rf $(OUT_DIR)
	mkdir -p $(OUT_DIR)
	javac -d $(OUT_DIR) $(MAIN_SOURCES) $(TEST_SOURCES)

test: build
	java -cp $(OUT_DIR) binarysearch.BinarySearchTest

run: build
	java -cp $(OUT_DIR) binarysearch.BinarySearchDemo

clean:
	rm -rf $(OUT_DIR)
