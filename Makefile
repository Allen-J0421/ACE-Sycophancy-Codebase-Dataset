BUILD_DIR := out
MAIN_SOURCES := $(shell find src/main/java -name '*.java')
TEST_SOURCES := $(shell find src/test/java -name '*.java')

.PHONY: compile demo test clean

compile:
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) $(MAIN_SOURCES)

demo: compile
	java -cp $(BUILD_DIR) com.example.search.BinarySearchDemo

test:
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) $(MAIN_SOURCES) $(TEST_SOURCES)
	java -cp $(BUILD_DIR) com.example.search.BinarySearchTest

clean:
	rm -rf $(BUILD_DIR)
