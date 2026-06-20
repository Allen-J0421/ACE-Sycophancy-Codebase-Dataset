package maxflow;

public interface FlowAugmentor {
    int augment(ResidualGraph residualGraph, AugmentingPath path);
}
