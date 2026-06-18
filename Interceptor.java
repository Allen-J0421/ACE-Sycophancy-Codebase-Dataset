public interface Interceptor {
  Object intercept(InterceptorChain chain, Object request);

  interface InterceptorChain {
    Object proceed(Object request);
  }
}
