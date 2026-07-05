JAVAC ?= javac
JAVA ?= java
BUILD_DIR := build
BUILD_STAMP := $(BUILD_DIR)/.compiled
SOURCES := BinarySearch.java BinarySearchDemo.java BinarySearchTest.java

.PHONY: build test demo clean

build: $(BUILD_STAMP)

$(BUILD_STAMP): $(SOURCES)
	@mkdir -p $(BUILD_DIR)
	$(JAVAC) -d $(BUILD_DIR) $(SOURCES)
	@touch $(BUILD_STAMP)

test: build
	$(JAVA) -cp $(BUILD_DIR) BinarySearchTest

demo: build
	$(JAVA) -cp $(BUILD_DIR) BinarySearchDemo

clean:
	rm -rf $(BUILD_DIR)
