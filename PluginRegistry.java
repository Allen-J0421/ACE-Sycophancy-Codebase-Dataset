import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginRegistry {
    private static final PluginRegistry instance = new PluginRegistry();
    private final Map<String, GraphPlugin> plugins = new HashMap<>();
    private final List<GraphPlugin> executionOrder = new ArrayList<>();

    private PluginRegistry() {}

    public static PluginRegistry getInstance() {
        return instance;
    }

    public void register(GraphPlugin plugin) {
        ConfigurationManager config = ConfigurationManager.getInstance();
        plugin.initialize(config);
        plugins.put(plugin.getName(), plugin);
        executionOrder.add(plugin);
        Logger.info("Plugin registered: " + plugin.getName() + " v" + plugin.getVersion());
    }

    public void unregister(String pluginName) {
        GraphPlugin plugin = plugins.remove(pluginName);
        if (plugin != null) {
            executionOrder.remove(plugin);
            plugin.cleanup();
            Logger.info("Plugin unregistered: " + pluginName);
        }
    }

    public void executeAll(GraphService service) {
        for (GraphPlugin plugin : executionOrder) {
            try {
                Logger.debug("Executing plugin: " + plugin.getName());
                plugin.execute(service);
            } catch (Exception e) {
                Logger.error("Plugin execution failed: " + plugin.getName(), e);
            }
        }
    }

    public GraphPlugin getPlugin(String name) {
        return plugins.get(name);
    }

    public List<String> getPluginNames() {
        return new ArrayList<>(plugins.keySet());
    }

    public int getPluginCount() {
        return plugins.size();
    }

    public void unregisterAll() {
        for (GraphPlugin plugin : new ArrayList<>(executionOrder)) {
            unregister(plugin.getName());
        }
    }
}
