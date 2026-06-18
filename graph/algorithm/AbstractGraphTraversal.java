package graph.algorithm;

import graph.core.IGraph;
import graph.visitor.VertexVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGraphTraversal implements GraphTraversal {

    protected boolean[] visited;
    protected List<Integer> result;
    protected IGraph graph;

    protected AbstractGraphTraversal() {
        this.visited = new boolean[0];
        this.result = new ArrayList<>();
    }

    @Override
    public final List<Integer> traverse(IGraph graph) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.result = new ArrayList<>();

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                traverseFromVertex(i);
            }
        }

        return result;
    }

    @Override
    public final void traverse(IGraph graph, VertexVisitor visitor) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.result = new ArrayList<>();

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i] && visitor.shouldContinue()) {
                traverseFromVertex(i, visitor);
            }
        }
    }

    protected abstract void traverseFromVertex(int startVertex);

    protected void traverseFromVertex(int startVertex, VertexVisitor visitor) {
        traverseFromVertex(startVertex);
        for (Integer v : result) {
            visitor.visit(v);
        }
        result.clear();
    }

    @Override
    public abstract String getAlgorithmName();

    protected void markVisited(int vertex) {
        visited[vertex] = true;
    }

    protected void addToResult(int vertex) {
        result.add(vertex);
    }

    protected boolean isVisited(int vertex) {
        return visited[vertex];
    }
}
