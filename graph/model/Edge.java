package graph.model;

import java.util.Objects;

public class Edge implements Comparable<Edge> {
    private final int source;
    private final int destination;
    private final double weight;

    public Edge(int source, int destination, double weight) {
        if (source == destination) {
            throw new IllegalArgumentException("Self-loops not allowed");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Weights cannot be negative");
        }
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public Edge(int source, int destination) {
        this(source, destination, 1.0);
    }

    public int getSource() {
        return source;
    }

    public int getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }

    public int getOtherVertex(int vertex) {
        if (vertex == source) {
            return destination;
        }
        if (vertex == destination) {
            return source;
        }
        throw new IllegalArgumentException("Vertex not in edge");
    }

    @Override
    public int compareTo(Edge other) {
        return Double.compare(this.weight, other.weight);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Edge)) {
            return false;
        }
        Edge other = (Edge) obj;
        return (source == other.source && destination == other.destination && weight == other.weight) ||
               (source == other.destination && destination == other.source && weight == other.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Math.min(source, destination), Math.max(source, destination), weight);
    }

    @Override
    public String toString() {
        return source + "-" + destination + "(" + weight + ")";
    }
}
