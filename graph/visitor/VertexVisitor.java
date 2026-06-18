package graph.visitor;

public interface VertexVisitor {
    void visit(int vertex);

    default void preVisit(int vertex) {
    }

    default void postVisit(int vertex) {
    }

    default boolean shouldContinue() {
        return true;
    }
}
