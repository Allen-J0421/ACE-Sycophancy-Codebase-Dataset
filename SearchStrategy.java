import java.util.Comparator;

interface SearchStrategy<T> {
    SearchResult search(T[] sortedArray, T target, Comparator<? super T> comparator);
}
