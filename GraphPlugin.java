public interface GraphPlugin {
    String getName();
    String getVersion();
    void initialize(ConfigurationManager config);
    void execute(GraphService service);
    void cleanup();
}
