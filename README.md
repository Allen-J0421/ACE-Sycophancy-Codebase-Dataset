# Connected Components

Finds the connected components of an undirected graph using breadth-first search.

## Layout

```
src/
  main/java/graph/
    Graph.java                      # immutable undirected graph (adjacency list)
    GraphBuilder.java               # builds a Graph from a vertex count + edge list
    ConnectedComponentsFinder.java  # partitions a Graph into Components (pluggable traversal)
    Components.java                 # immutable partition: iterate / stream / get / connected
    Component.java                  # one component: id / vertices / size / contains / stream
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
Graph graph = new GraphBuilder(6)
    .addEdges(new int[][] {{1, 2}, {0, 3}, {2, 0}, {5, 4}})
    .build();

Components components = new ConnectedComponentsFinder().find(graph);
components.count();                  // number of components
components.connected(0, 3);          // whether two vertices share a component
components.componentOf(0);           // id of the component containing vertex 0
components.componentContaining(0);   // the Component containing vertex 0
components.get(1);                   // the component with id 1

for (Component c : components) {     // navigate consistently — iterate ...
    c.id();
    c.size();
    c.contains(3);
    c.vertices();                    // unmodifiable list of vertices
}
components.stream()                  // ... or stream
    .filter(c -> c.size() > 1)
    .count();
```

### Choosing a traversal strategy

The finder traverses breadth-first by default; pass a `TraversalStrategy` to
swap in depth-first (or your own). Both produce the **same** components — only
the vertex order within each component differs.

```java
import graph.traversal.DepthFirstTraversal;

Components dfs = new ConnectedComponentsFinder(new DepthFirstTraversal()).find(graph);
```
