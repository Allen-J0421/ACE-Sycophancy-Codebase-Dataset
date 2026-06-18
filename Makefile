SRC_DIR := src/two_pointers
OUT_DIR := out
SOURCES := $(wildcard $(SRC_DIR)/*.java)

.PHONY: build test run clean

build:
	mkdir -p $(OUT_DIR)
	javac -d $(OUT_DIR) $(SOURCES)

test: build
	java -cp $(OUT_DIR) two_pointers.TwoPointersTechniqueTest

run: build
	java -cp $(OUT_DIR) two_pointers.TwoPointersTechniqueDemo

clean:
	rm -rf $(OUT_DIR)
