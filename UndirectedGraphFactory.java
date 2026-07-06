class UndirectedGraphFactory implements GraphFactory {

    @Override
    public GraphBuilder newBuilder(int V) {
        return new UndirectedGraphBuilder(V);
    }
}
