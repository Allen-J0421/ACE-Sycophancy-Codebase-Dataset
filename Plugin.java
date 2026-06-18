public interface Plugin {
  String getName();
  String getVersion();
  void initialize();
  void shutdown();
  java.util.Map<String, Object> getMetadata();
}
