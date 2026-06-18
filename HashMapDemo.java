public class HashMapDemo {
    public static void main(String[] args) {
        LinearProbingHashMap map = new LinearProbingHashMap();
        runDemo(map);
    }

    private static void runDemo(LinearProbingHashMap map) {
        map.put(1, 1);
        map.put(2, 2);
        map.put(2, 3);
        map.printEntries();
        System.out.println(map.size());
        System.out.println(map.remove(2));
        System.out.println(map.size());
        System.out.println(map.isEmpty());
        System.out.println(map.get(2));
    }
}
