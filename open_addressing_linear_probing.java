class Demo {
    public static void main(String[] args) {
        HashMapOperations<String, Integer> h = new HashMap<>();
        h.insertNode("one", 1);
        h.insertNode("two", 2);
        h.insertNode("two", 3);
        h.display();
        System.out.println(h.getSize());
        System.out.println(h.deleteNode("two"));
        System.out.println(h.getSize());
        System.out.println(h.isEmpty());
        System.out.println(h.get("two"));
    }
}
