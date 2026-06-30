final class ConsoleSearchObserver implements SearchObserver {
    @Override
    public void onSearchStarted(String strategyName, int itemCount) {
        System.err.println(strategyName + " started for " + itemCount + " items");
    }

    @Override
    public void onSearchCompleted(SearchExecutionMetrics metrics) {
        System.err.println(
                metrics.strategyName()
                        + " completed in "
                        + metrics.elapsedNanos()
                        + " ns; found="
                        + metrics.found()
                        + "; index="
                        + metrics.result().index());
    }

    @Override
    public void onSearchFailed(
            String strategyName,
            int itemCount,
            long elapsedNanos,
            RuntimeException exception) {
        System.err.println(
                strategyName
                        + " failed in "
                        + elapsedNanos
                        + " ns for "
                        + itemCount
                        + " items: "
                        + exception.getClass().getSimpleName());
    }
}
