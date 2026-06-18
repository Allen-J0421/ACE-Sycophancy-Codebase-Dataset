public interface Visitor {
  void visit(CoinChangeResult result);
  void visit(Observation observation);
  void visit(SolveContext context);
}
