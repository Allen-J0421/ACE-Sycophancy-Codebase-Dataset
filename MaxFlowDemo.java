final class MaxFlowDemo {
    private MaxFlowDemo() {
    }

    public static void main(String[] args) {
        System.out.println("The maximum possible flow is "
                           + new MaxFlow().maximumFlow(
                               SampleNetworks.sixVertexNetwork(),
                               SampleNetworks.SIX_VERTEX_SOURCE,
                               SampleNetworks.SIX_VERTEX_SINK
                           ));
    }
}
