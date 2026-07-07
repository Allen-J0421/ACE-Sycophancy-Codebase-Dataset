class Main {

    public static void main(String[] args) {
        Sorter<Integer> intSorter = SorterFactory.create(SorterFactory.Algorithm.BUBBLE);
        Sorter<String>  strSorter = SorterFactory.create(SorterFactory.Algorithm.BUBBLE);
        Reporter reporter = new ConsoleReporter();
        TestRegistry registry = new TestRegistry();

        new TestCaseBuilder<>(intSorter, reporter)
                .add("Random order",   new Integer[]{ 64, 34, 25, 12, 22, 11, 90 })
                .add("Already sorted", new Integer[]{ 1, 2, 3, 4, 5 })
                .add("Reverse order",  new Integer[]{ 5, 4, 3, 2, 1 })
                .add("Single element", new Integer[]{ 42 })
                .add("Null array",     null)
                .registerAll(registry);

        new TestCaseBuilder<>(strSorter, reporter)
                .add("Strings", new String[]{ "banana", "apple", "cherry" })
                .registerAll(registry);

        registry.forEach(SortTestCase::run);
    }
}
