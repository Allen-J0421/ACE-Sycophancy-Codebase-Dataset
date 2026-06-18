public class FormattingVisitor implements Visitor {
  private final StringBuilder output;

  public FormattingVisitor() {
    this.output = new StringBuilder();
  }

  @Override
  public void visit(CoinChangeResult result) {
    output.append("Result: ").append(result.getWays()).append(" ways\n");
  }

  @Override
  public void visit(Observation observation) {
    output.append("Observation: ").append(observation.getType())
        .append(" - ").append(observation.getDescription()).append("\n");
  }

  @Override
  public void visit(SolveContext context) {
    output.append("Context: ").append(context.getRequestId())
        .append(" (sum=").append(context.getTargetSum()).append(")\n");
  }

  public String getFormattedOutput() {
    return output.toString();
  }

  @Override
  public String toString() {
    return "FormattingVisitor";
  }
}
