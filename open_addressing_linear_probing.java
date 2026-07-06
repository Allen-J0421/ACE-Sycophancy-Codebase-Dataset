class Demo {
    public static void main(String[] args) {
        HashMapOperations h = new HashMap();
        h.insertNode(1, 1);
        h.insertNode(2, 2);
        h.insertNode(2, 3);
        h.display();
        System.out.println(h.getSize());
        System.out.println(h.deleteNode(2));
        System.out.println(h.getSize());
        System.out.println(h.isEmpty());
        System.out.println(h.get(2));
    }
}
