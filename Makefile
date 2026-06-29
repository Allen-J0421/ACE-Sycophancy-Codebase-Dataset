BUILD_DIR := out
MAIN_BUILD_DIR := $(BUILD_DIR)/main
TEST_BUILD_DIR := $(BUILD_DIR)/test
MAIN_SOURCES := $(shell find src/main/java -name '*.java')
TEST_SOURCES := $(shell find src/test/java -name '*.java')
DEMO_CLASS := com.example.search.BinarySearchDemo
TEST_CLASS := com.example.search.BinarySearchTest

.PHONY: compile demo test clean

compile:
	mkdir -p $(MAIN_BUILD_DIR)
	javac -d $(MAIN_BUILD_DIR) $(MAIN_SOURCES)

demo: compile
	java -cp $(MAIN_BUILD_DIR) $(DEMO_CLASS)

test: compile
	mkdir -p $(TEST_BUILD_DIR)
	javac -cp $(MAIN_BUILD_DIR) -d $(TEST_BUILD_DIR) $(TEST_SOURCES)
	java -cp $(MAIN_BUILD_DIR):$(TEST_BUILD_DIR) $(TEST_CLASS)

clean:
	rm -rf $(BUILD_DIR)
