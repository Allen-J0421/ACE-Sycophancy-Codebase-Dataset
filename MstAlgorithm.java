interface MstAlgorithm {
    MstResult computeMST(Graph graph, int startVertex);

    default MstResult computeMST(Graph graph) {
        return computeMST(graph, 0);
    }
}
