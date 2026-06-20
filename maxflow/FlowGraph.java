package maxflow;

public interface FlowGraph {
    int vertexCount();

    ResidualGraph createResidualGraph();
}
