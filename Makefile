BUILD_DIR := out
MAIN_SOURCES := $(shell find src/main/java -name '*.java')
TEST_SOURCES := $(shell find src/test/java -name '*.java')

.PHONY: clean run test

clean:
	rm -rf $(BUILD_DIR)

$(BUILD_DIR): $(MAIN_SOURCES) $(TEST_SOURCES)
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) $(MAIN_SOURCES) $(TEST_SOURCES)

run: $(BUILD_DIR)
	java -cp $(BUILD_DIR) coinchange.CoinChange

test: $(BUILD_DIR)
	java -cp $(BUILD_DIR) coinchange.CoinChangeSelfTest
