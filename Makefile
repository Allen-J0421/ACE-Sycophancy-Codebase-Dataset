OUT_DIR := out
MAIN_SOURCES := $(shell find src/main/java -name '*.java' | sort)
TEST_SOURCES := $(shell find src/test/java -name '*.java' | sort)
ALL_SOURCES := $(MAIN_SOURCES) $(TEST_SOURCES)

.PHONY: build test run-demo clean

build:
	javac -Xlint:all -d $(OUT_DIR) $(ALL_SOURCES)

test: build
	java -cp $(OUT_DIR) queue.CircularQueueTest

run-demo: build
	java -cp $(OUT_DIR) queue.CircularQueueDemo

clean:
	rm -rf $(OUT_DIR)
