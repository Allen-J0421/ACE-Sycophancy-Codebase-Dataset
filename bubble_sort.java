class Main {

    public static void main(String[] args) {
        Sorter<Integer> intSorter = SorterFactory.create(SorterFactory.Algorithm.BUBBLE);
        Sorter<String> strSorter = SorterFactory.create(SorterFactory.Algorithm.BUBBLE);
        Reporter reporter = new ConsoleReporter();

        TestRegistry registry = new TestRegistry();
        registry.register(SortTestCase.of("Random order",   new Integer[]{ 64, 34, 25, 12, 22, 11, 90 }, intSorter, reporter));
        registry.register(SortTestCase.of("Already sorted", new Integer[]{ 1, 2, 3, 4, 5 },              intSorter, reporter));
        registry.register(SortTestCase.of("Reverse order",  new Integer[]{ 5, 4, 3, 2, 1 },              intSorter, reporter));
        registry.register(SortTestCase.of("Single element", new Integer[]{ 42 },                          intSorter, reporter));
        registry.register(SortTestCase.of("Strings",        new String[]{ "banana", "apple", "cherry" },  strSorter, reporter));
        registry.register(SortTestCase.of("Null array",     null,                                          intSorter, reporter));

        registry.runAll();
    }
}
