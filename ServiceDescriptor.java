public class ServiceDescriptor {
  private final String name;
  private final String version;
  private final String description;
  private final java.util.List<String> capabilities;
  private final java.util.Map<String, String> metadata;

  public ServiceDescriptor(String name, String version, String description) {
    this.name = name;
    this.version = version;
    this.description = description;
    this.capabilities = new java.util.ArrayList<>();
    this.metadata = new java.util.HashMap<>();
  }

  public void addCapability(String capability) {
    capabilities.add(capability);
  }

  public void addMetadata(String key, String value) {
    metadata.put(key, value);
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public String getDescription() {
    return description;
  }

  public java.util.List<String> getCapabilities() {
    return new java.util.ArrayList<>(capabilities);
  }

  public java.util.Map<String, String> getMetadata() {
    return new java.util.HashMap<>(metadata);
  }

  @Override
  public String toString() {
    return String.format("ServiceDescriptor{name=%s, version=%s, capabilities=%d}",
        name, version, capabilities.size());
  }
}
