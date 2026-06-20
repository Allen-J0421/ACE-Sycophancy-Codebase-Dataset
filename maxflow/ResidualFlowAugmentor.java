package maxflow;

public final class ResidualFlowAugmentor implements FlowAugmentor {
    @Override
    public int augment(ResidualNetwork residualNetwork, AugmentingPath path) {
        return residualNetwork.augmentPath(path);
    }
}
