public class Main {
    public static void main(String[] args) {
        new CacheScenarioRunner(new LRUCacheFactory()).runAll();
        System.out.println("All cache checks passed.");
    }
}
