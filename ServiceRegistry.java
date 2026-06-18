import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {
  private final Map<String, ServiceDescriptor> services;

  public ServiceRegistry() {
    this.services = new HashMap<>();
  }

  public void register(ServiceDescriptor descriptor) {
    services.put(descriptor.getName(), descriptor);
  }

  public ServiceDescriptor lookup(String name) {
    return services.get(name);
  }

  public java.util.List<ServiceDescriptor> listServices() {
    return new java.util.ArrayList<>(services.values());
  }

  public int getServiceCount() {
    return services.size();
  }

  public String generateRegistry() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== Service Registry ===\n");

    for (ServiceDescriptor service : listServices()) {
      sb.append("  Name: ").append(service.getName()).append("\n");
      sb.append("  Version: ").append(service.getVersion()).append("\n");
      sb.append("  Description: ").append(service.getDescription()).append("\n");
      sb.append("  Capabilities: ").append(service.getCapabilities()).append("\n");
      sb.append("\n");
    }

    return sb.toString();
  }

  @Override
  public String toString() {
    return String.format("ServiceRegistry{services=%d}", services.size());
  }
}
