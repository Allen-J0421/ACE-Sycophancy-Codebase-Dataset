public class TraversalProfile {
    public enum Profile {
        PERFORMANCE("Performance", true, false, false),
        MONITORING("Monitoring", true, true, true),
        MINIMAL("Minimal", false, false, false),
        FULL("Full", true, true, true);

        private final String name;
        private final boolean caching;
        private final boolean events;
        private final boolean validation;

        Profile(String name, boolean caching, boolean events, boolean validation) {
            this.name = name;
            this.caching = caching;
            this.events = events;
            this.validation = validation;
        }

        public TraversalConfig toConfig() {
            TraversalConfig.Builder builder = TraversalConfig.builder()
                    .withBFS()
                    .withCache(caching)
                    .withEvents(events);

            return builder.build();
        }

        public String getName() {
            return name;
        }

        public boolean isCachingEnabled() {
            return caching;
        }

        public boolean isEventsEnabled() {
            return events;
        }

        public boolean isValidationEnabled() {
            return validation;
        }
    }

    private final Profile profile;
    private PerformanceMetrics metrics;
    private ResultValidator.ValidationReport validationReport;

    public TraversalProfile(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

    public PerformanceMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(PerformanceMetrics metrics) {
        this.metrics = metrics;
    }

    public ResultValidator.ValidationReport getValidationReport() {
        return validationReport;
    }

    public void setValidationReport(ResultValidator.ValidationReport report) {
        this.validationReport = report;
    }

    public void printReport() {
        System.out.println("\n=== Traversal Profile: " + profile.name + " ===");

        if (metrics != null) {
            metrics.print();
        }

        if (validationReport != null && profile.isValidationEnabled()) {
            validationReport.print();
        }
    }
}
