package graph.io;

public enum GraphFormat {
    ADJACENCY_LIST("Adjacency List Format"),
    EDGE_LIST("Edge List Format"),
    MATRIX("Adjacency Matrix Format");

    private final String description;

    GraphFormat(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
