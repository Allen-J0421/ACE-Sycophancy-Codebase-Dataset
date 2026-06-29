BUILD_DIR := out
MAIN_SOURCES := $(wildcard src/main/java/*.java)
TEST_SOURCES := $(wildcard src/test/java/*.java)

.PHONY: compile demo test clean

compile:
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) $(MAIN_SOURCES)

demo: compile
	java -cp $(BUILD_DIR) BinarySearchDemo

test:
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) $(MAIN_SOURCES) $(TEST_SOURCES)
	java -cp $(BUILD_DIR) BinarySearchTest

clean:
	rm -rf $(BUILD_DIR)
