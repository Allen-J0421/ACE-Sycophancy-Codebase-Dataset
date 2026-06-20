import java.util.ArrayList;
import java.util.List;

class ArticulationPoints {

    private static final int NO_PARENT = -1;
    private static final int NO_ARTICULATION_POINT = -1;

    private ArticulationPoints() {
    }

    static List<List<Integer>> constructAdj(int vertexCount, int[][] edges) {
        return constructAdjacencyList(vertexCount, edges);
    }

    private static List<List<Integer>> constructAdjacencyList(int vertexCount, int[][] edges) {
        List<List<Integer>> adj = createEmptyAdjacencyList(vertexCount);
        for (int[] edge : edges) {
            addUndirectedEdge(adj, edge[0], edge[1]);
        }
        return adj;
    }

    private static List<List<Integer>> createEmptyAdjacencyList(int vertexCount) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adj.add(new ArrayList<>());
        }
        return adj;
    }

    private static void addUndirectedEdge(List<List<Integer>> adj, int firstVertex, int secondVertex) {
        adj.get(firstVertex).add(secondVertex);
        adj.get(secondVertex).add(firstVertex);
    }

    static List<Integer> articulationPoints(int vertexCount, List<List<Integer>> adj) {
        return new ArticulationPointFinder(new Graph(vertexCount, adj)).find();
    }

    private static final class Graph {
        private final int vertexCount;
        private final List<List<Integer>> adj;

        private Graph(int vertexCount, List<List<Integer>> adj) {
            this.vertexCount = vertexCount;
            this.adj = adj;
        }

        private int vertexCount() {
            return vertexCount;
        }

        private List<Integer> neighborsOf(int vertex) {
            return adj.get(vertex);
        }
    }

    private static final class ArticulationPointFinder {
        private final Graph graph;
        private final SearchState state;

        private ArticulationPointFinder(Graph graph) {
            this.graph = graph;
            state = new SearchState(graph.vertexCount());
        }

        private List<Integer> find() {
            for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
                if (!state.isVisited(vertex)) {
                    depthFirstSearch(vertex, NO_PARENT);
                }
            }

            return collectArticulationPoints();
        }

        private List<Integer> collectArticulationPoints() {
            List<Integer> articulationPoints = new ArrayList<>();
            for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
                if (state.isArticulationPoint(vertex)) {
                    articulationPoints.add(vertex);
                }
            }

            if (articulationPoints.isEmpty()) {
                articulationPoints.add(NO_ARTICULATION_POINT);
            }
            return articulationPoints;
        }

        private void depthFirstSearch(int vertex, int parent) {
            state.visit(vertex);
            int childCount = 0;

            for (int neighbor : graph.neighborsOf(vertex)) {
                if (!state.isVisited(neighbor)) {
                    childCount++;
                    depthFirstSearch(neighbor, vertex);
                    state.updateLowestReachableFromChild(vertex, neighbor);

                    if (isNonRootArticulationPoint(vertex, neighbor, parent)) {
                        state.markArticulationPoint(vertex);
                    }
                } else if (neighbor != parent) {
                    state.updateLowestReachableFromBackEdge(vertex, neighbor);
                }
            }

            if (isRootArticulationPoint(parent, childCount)) {
                state.markArticulationPoint(vertex);
            }
        }

        private boolean isNonRootArticulationPoint(int vertex, int child, int parent) {
            return parent != NO_PARENT
                    && state.lowestReachableTime(child) >= state.discoveryTime(vertex);
        }

        private boolean isRootArticulationPoint(int parent, int childCount) {
            return parent == NO_PARENT && childCount > 1;
        }

        private static final class SearchState {
            private final boolean[] visited;
            private final int[] discoveryTime;
            private final int[] lowestReachableTime;
            private final boolean[] isArticulationPoint;
            private int time;

            private SearchState(int vertexCount) {
                visited = new boolean[vertexCount];
                discoveryTime = new int[vertexCount];
                lowestReachableTime = new int[vertexCount];
                isArticulationPoint = new boolean[vertexCount];
            }

            private void visit(int vertex) {
                visited[vertex] = true;
                discoveryTime[vertex] = lowestReachableTime[vertex] = ++time;
            }

            private boolean isVisited(int vertex) {
                return visited[vertex];
            }

            private boolean isArticulationPoint(int vertex) {
                return isArticulationPoint[vertex];
            }

            private void markArticulationPoint(int vertex) {
                isArticulationPoint[vertex] = true;
            }

            private int discoveryTime(int vertex) {
                return discoveryTime[vertex];
            }

            private int lowestReachableTime(int vertex) {
                return lowestReachableTime[vertex];
            }

            private void updateLowestReachableFromChild(int vertex, int child) {
                lowestReachableTime[vertex] = Math.min(
                        lowestReachableTime[vertex],
                        lowestReachableTime[child]
                );
            }

            private void updateLowestReachableFromBackEdge(int vertex, int ancestor) {
                lowestReachableTime[vertex] = Math.min(
                        lowestReachableTime[vertex],
                        discoveryTime[ancestor]
                );
            }
        }
    }

    public static void main(String[] args) {
        runSample();
    }

    private static void runSample() {
        int vertexCount = 5;

        List<List<Integer>> adj = constructAdjacencyList(vertexCount, sampleEdges());
        List<Integer> articulationPoints = articulationPoints(vertexCount, adj);

        printVertices(articulationPoints);
    }

    private static int[][] sampleEdges() {
        return new int[][]{{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};
    }

    private static void printVertices(List<Integer> vertices) {
        for (int vertex : vertices) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
