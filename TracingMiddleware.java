public class TracingMiddleware implements SolveMiddleware {

  @Override
  public CoinChangeResult process(SolveContext context, SolveChain chain) {
    String traceId = java.util.UUID.randomUUID().toString();
    String spanId = java.util.UUID.randomUUID().toString();
    TracingContext tracing = new TracingContext(traceId, spanId);

    context.setAttribute("traceId", traceId);
    context.setAttribute("spanId", spanId);
    context.setAttribute("tracing", tracing);

    try {
      tracing.addTag("request_id", context.getRequestId());
      tracing.addTag("target_sum", String.valueOf(context.getTargetSum()));
      tracing.addLog("Solving started");

      CoinChangeResult result = chain.execute(context);

      tracing.addTag("result_ways", String.valueOf(result.getWays()));
      tracing.addLog("Solving completed");

      return result;

    } finally {
      tracing.end();
    }
  }

  public static TracingContext getTracingContext(SolveContext context) {
    return (TracingContext) context.getAttribute("tracing");
  }
}
