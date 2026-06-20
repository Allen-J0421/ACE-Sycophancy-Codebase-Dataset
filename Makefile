BUILD_DIR ?= /tmp/connected-components-build
SOURCES := ConnectedComponents.java ConnectedComponentsDemo.java UndirectedGraph.java connected_components.java

.PHONY: build run clean

build:
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) $(SOURCES)

run: build
	java -cp $(BUILD_DIR) ConnectedComponentsDemo

clean:
	rm -rf $(BUILD_DIR)
