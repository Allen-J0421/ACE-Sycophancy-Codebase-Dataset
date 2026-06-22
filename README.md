# Connected Components

Finds the connected components of an undirected graph using breadth-first search.

## Layout

```
src/
  main/java/graph/
    Graph.java                      # undirected graph (adjacency list, validated)
    ConnectedComponentsFinder.java  # partitions a Graph into Components (pluggable traversal)
    Components.java                 # immutable result: count / componentOf / connected
    Demo.java                       # runnable sample (graph.Demo)
    traversal/
      TraversalStrategy.java        # strategy interface for visiting a component
      BreadthFirstTraversal.java    # queue-based (default)
      DepthFirstTraversal.java      # explicit-stack, iterative
  test/java/graph/                  # JUnit 5 suite mirroring the main packages
junit-platform-console-standalone.jar
run-tests.sh
```

## Build, run, and test

```sh
./run-tests.sh
```

Or manually:

```sh
javac -d out/main $(find src/main/java -name '*.java')
java -cp out/main graph.Demo

javac -cp "junit-platform-console-standalone.jar:out/main" -d out/test \
    $(find src/test/java -name '*.java')
java -jar junit-platform-console-standalone.jar execute \
    -cp "out/main:out/test" --scan-classpath
```

## API sketch

```java
Graph graph = new Graph(6);
graph.addEdge(1, 2);
graph.addEdge(0, 3);

Components components = new ConnectedComponentsFinder().find(graph);
components.count();           // number of components
components.componentOf(0);    // id of the component containing vertex 0
components.connected(0, 3);   // whether two vertices share a component
components.asList();          // unmodifiable view of the components
```

### Choosing a traversal strategy

The finder traverses breadth-first by default; pass a `TraversalStrategy` to
swap in depth-first (or your own). Both produce the **same** components — only
the vertex order within each component differs.

```java
import graph.traversal.DepthFirstTraversal;

Components dfs = new ConnectedComponentsFinder(new DepthFirstTraversal()).find(graph);
```
