public interface SolveMiddleware {
  CoinChangeResult process(SolveContext context, SolveChain chain);

  interface SolveChain {
    CoinChangeResult execute(SolveContext context);
  }
}
