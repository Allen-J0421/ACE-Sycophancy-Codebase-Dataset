import java.util.Optional;

public interface AugmentingPathFinder {
    Optional<AugmentingPath> find(ResidualNetwork network, FlowProblem problem);
}
