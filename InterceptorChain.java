import java.util.ArrayList;
import java.util.List;

public class InterceptorChain implements Interceptor.InterceptorChain {
  private final List<Interceptor> interceptors;
  private int index = 0;

  public InterceptorChain(List<Interceptor> interceptors) {
    this.interceptors = new ArrayList<>(interceptors);
  }

  @Override
  public Object proceed(Object request) {
    if (index >= interceptors.size()) {
      return request;
    }
    Interceptor interceptor = interceptors.get(index++);
    return interceptor.intercept(this, request);
  }

  public static Object executeChain(List<Interceptor> interceptors, Object request) {
    return new InterceptorChain(interceptors).proceed(request);
  }
}
