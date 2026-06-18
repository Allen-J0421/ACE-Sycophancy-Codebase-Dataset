import java.util.ArrayList;
import java.util.List;

public class TransformationPipeline {
  private final List<Transformer> transformers;

  public TransformationPipeline() {
    this.transformers = new ArrayList<>();
  }

  public void addTransformer(Transformer transformer) {
    transformers.add(transformer);
  }

  public SolveContext transform(SolveContext context) {
    SolveContext current = context;
    for (Transformer transformer : transformers) {
      current = transformer.transform(current);
    }
    return current;
  }

  public int getTransformerCount() {
    return transformers.size();
  }

  @Override
  public String toString() {
    return String.format("TransformationPipeline{transformers=%d}", transformers.size());
  }
}
