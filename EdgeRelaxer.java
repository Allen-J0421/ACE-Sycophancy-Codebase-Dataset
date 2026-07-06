final class EdgeRelaxer {

    private final DistanceMap distanceMap;
    private final PredecessorMap predecessorMap;

    EdgeRelaxer(int vertices, int source) {
        distanceMap = new ArrayDistanceMap(vertices);
        distanceMap.set(source, 0);
        predecessorMap = new ArrayPredecessorMap(vertices);
    }

    boolean relax(Edge e) {
        int u = e.from(), v = e.to(), w = e.weight();
        if (distanceMap.get(u) != Distances.UNREACHABLE
                && (long) distanceMap.get(u) + w < distanceMap.get(v)) {
            distanceMap.set(v, distanceMap.get(u) + w);
            predecessorMap.set(v, u);
            return true;
        }
        return false;
    }

    boolean canRelax(Edge e) {
        int u = e.from();
        return distanceMap.get(u) != Distances.UNREACHABLE
            && (long) distanceMap.get(u) + e.weight() < distanceMap.get(e.to());
    }

    // Records e.from() as the predecessor of e.to() without changing distances.
    // Used during negative-cycle detection when the distance arrays must not be updated further.
    void linkPredecessor(Edge e) {
        predecessorMap.set(e.to(), e.from());
    }

    DistanceMap distances() { return distanceMap; }

    PredecessorMap predecessors() { return predecessorMap; }
}
