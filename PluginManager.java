import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager {
  private final Map<String, Plugin> plugins;
  private final List<String> loadOrder;

  public PluginManager() {
    this.plugins = new HashMap<>();
    this.loadOrder = new ArrayList<>();
  }

  public void load(Plugin plugin) {
    plugins.put(plugin.getName(), plugin);
    loadOrder.add(plugin.getName());
    plugin.initialize();
  }

  public void unload(String pluginName) {
    Plugin plugin = plugins.remove(pluginName);
    if (plugin != null) {
      loadOrder.remove(pluginName);
      plugin.shutdown();
    }
  }

  public Plugin getPlugin(String name) {
    return plugins.get(name);
  }

  public List<Plugin> getAllPlugins() {
    return new ArrayList<>(plugins.values());
  }

  public int getPluginCount() {
    return plugins.size();
  }

  public String generatePluginReport() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== Plugin Report ===\n");
    sb.append("Total Plugins: ").append(plugins.size()).append("\n\n");

    for (String name : loadOrder) {
      Plugin plugin = plugins.get(name);
      sb.append("Plugin: ").append(plugin.getName()).append("\n");
      sb.append("  Version: ").append(plugin.getVersion()).append("\n");
      sb.append("  Metadata: ").append(plugin.getMetadata()).append("\n\n");
    }

    return sb.toString();
  }

  @Override
  public String toString() {
    return String.format("PluginManager{plugins=%d}", plugins.size());
  }
}
