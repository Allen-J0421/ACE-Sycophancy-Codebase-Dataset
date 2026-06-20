JAVAC ?= javac
JAVA ?= java
ARGS ?=

SOURCES = Radix.java RadixDemo.java RadixTest.java

.PHONY: test run clean

test:
	$(JAVAC) $(SOURCES)
	$(JAVA) RadixTest

run:
	$(JAVAC) Radix.java RadixDemo.java
	$(JAVA) RadixDemo $(ARGS)

clean:
	find . -maxdepth 1 -name '*.class' -delete
