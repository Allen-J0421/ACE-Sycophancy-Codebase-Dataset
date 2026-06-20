import java.util.Arrays;

final class StronglyConnectedComponentsDemo {

    private StronglyConnectedComponentsDemo() {
    }

    public static void main(String[] args) {
        int vertexCount = 6;
        int[][] edges = {
            {0, 1},
            {1, 2},
            {2, 0},
            {1, 3},
            {3, 4},
            {4, 5},
            {5, 3}
        };

        int[][] components = StronglyConnectedComponents.findStronglyConnectedComponents(vertexCount, edges);

        System.out.println("Strongly Connected Components:");
        for (int[] component : components) {
            System.out.println(Arrays.toString(component));
        }
    }
}
