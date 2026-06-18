public class LoggingPlugin implements GraphPlugin {
    private boolean enabled = true;

    @Override
    public String getName() {
        return "LoggingPlugin";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public void initialize(ConfigurationManager config) {
        enabled = config.getBoolean("logging.enabled", true);
        Logger.setLevel(Logger.Level.valueOf(
            config.getString("logging.level", "INFO")
        ));
        Logger.info("LoggingPlugin initialized");
    }

    @Override
    public void execute(GraphService service) {
        if (enabled) {
            Logger.info("LoggingPlugin: Service operations will be logged");
        }
    }

    @Override
    public void cleanup() {
        Logger.info("LoggingPlugin cleaned up");
    }
}
