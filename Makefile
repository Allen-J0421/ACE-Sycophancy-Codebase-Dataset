JAVAC ?= javac
JAVA ?= java
BUILD_DIR := build
SOURCES := BinarySearch.java BinarySearchDemo.java BinarySearchTest.java

.PHONY: build test demo clean

build:
	@mkdir -p $(BUILD_DIR)
	$(JAVAC) -d $(BUILD_DIR) $(SOURCES)

test: build
	$(JAVA) -cp $(BUILD_DIR) BinarySearchTest

demo: build
	$(JAVA) -cp $(BUILD_DIR) BinarySearchDemo

clean:
	rm -rf $(BUILD_DIR)
