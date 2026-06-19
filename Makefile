.PHONY: compile test clean

compile:
	javac MinHeap.java MinHeapTest.java

test: compile
	java MinHeapTest

clean:
	rm -f *.class
