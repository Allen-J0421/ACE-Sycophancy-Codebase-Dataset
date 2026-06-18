import java.util.ArrayList;
import java.util.List;

public class MiddlewareChain {
    private final List<Middleware> middlewares = new ArrayList<>();

    public interface Middleware {
        void execute(MiddlewareContext context);
    }

    public static class MiddlewareContext {
        public final GraphRequest request;
        public final java.util.Map<String, Object> metadata;
        public Object result;
        public boolean abort = false;

        public MiddlewareContext(GraphRequest request) {
            this.request = request;
            this.metadata = new java.util.HashMap<>();
        }

        public void abortWithError(String error) {
            this.abort = true;
            this.metadata.put("error", error);
        }
    }

    public MiddlewareChain add(Middleware middleware) {
        middlewares.add(middleware);
        return this;
    }

    public MiddlewareContext execute(GraphRequest request, MiddlewareChain.Handler handler) {
        MiddlewareContext context = new MiddlewareContext(request);
        executeMiddlewares(context, 0, handler);
        return context;
    }

    private void executeMiddlewares(MiddlewareContext context, int index, Handler handler) {
        if (context.abort) {
            return;
        }

        if (index >= middlewares.size()) {
            try {
                context.result = handler.handle(context.request);
            } catch (Exception e) {
                Logger.error("Handler failed", e);
                context.abortWithError(e.getMessage());
            }
            return;
        }

        Middleware middleware = middlewares.get(index);
        try {
            middleware.execute(context);
            if (!context.abort) {
                executeMiddlewares(context, index + 1, handler);
            }
        } catch (Exception e) {
            Logger.error("Middleware failed at index " + index, e);
            context.abortWithError("Middleware error: " + e.getMessage());
        }
    }

    public interface Handler {
        Object handle(GraphRequest request) throws Exception;
    }
}
