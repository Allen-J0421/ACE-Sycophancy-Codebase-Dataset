package countingsort;

@FunctionalInterface
interface SortStateFactory {
    SortState create(int[] arr);
}
