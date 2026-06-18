import java.util.ArrayList;
import java.util.List;

public class MiddlewarePipeline {
  private final List<SolveMiddleware> middlewares;
  private final java.util.function.Function<SolveContext, CoinChangeResult> handler;

  public MiddlewarePipeline(java.util.function.Function<SolveContext, CoinChangeResult> handler) {
    this.middlewares = new ArrayList<>();
    this.handler = handler;
  }

  public MiddlewarePipeline use(SolveMiddleware middleware) {
    middlewares.add(middleware);
    return this;
  }

  public CoinChangeResult execute(SolveContext context) {
    return executeMiddleware(context, 0);
  }

  private CoinChangeResult executeMiddleware(SolveContext context, int index) {
    if (index >= middlewares.size()) {
      return handler.apply(context);
    }

    SolveMiddleware middleware = middlewares.get(index);
    int nextIndex = index + 1;

    return middleware.process(context, ctx -> executeMiddleware(ctx, nextIndex));
  }

  public int getMiddlewareCount() {
    return middlewares.size();
  }
}
