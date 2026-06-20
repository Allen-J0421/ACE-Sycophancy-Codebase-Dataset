package maxflow;

public interface FlowAugmentor {
    int augment(ResidualNetwork residualNetwork, AugmentingPath path);
}
