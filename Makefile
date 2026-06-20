BUILD_DIR ?= /tmp/connected-components-build
SOURCES := $(wildcard *.java)

.PHONY: build run test clean

build:
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) $(SOURCES)

run: build
	java -cp $(BUILD_DIR) ConnectedComponentsDemo

test: build
	java -cp $(BUILD_DIR) ConnectedComponentsTest

clean:
	rm -rf $(BUILD_DIR)
