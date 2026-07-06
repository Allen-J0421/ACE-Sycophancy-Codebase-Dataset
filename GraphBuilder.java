interface GraphBuilder {
    GraphBuilder addEdge(int u, int v);
    GraphBuilder undo();
    Graph build();
    Graph replay();
}
