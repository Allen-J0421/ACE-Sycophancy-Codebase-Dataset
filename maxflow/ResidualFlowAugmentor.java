package maxflow;

public final class ResidualFlowAugmentor implements FlowAugmentor {
    @Override
    public int augment(ResidualGraph residualGraph, AugmentingPath path) {
        return residualGraph.augmentPath(path);
    }
}
