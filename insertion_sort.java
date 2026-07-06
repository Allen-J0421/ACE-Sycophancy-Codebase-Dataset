import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class InsertionSort {

    static <T> void sort(List<T> list, Comparator<T> comparator) {
        for (int i = 1; i < list.size(); i++) {
            T key = list.get(i);
            int insertAt = shiftGreaterElementsRight(list, i - 1, key, comparator);
            list.set(insertAt, key);
        }
    }

    static <T> int shiftGreaterElementsRight(List<T> list, int from, T key, Comparator<T> comparator) {
        int j = from;
        while (j >= 0 && comparator.compare(list.get(j), key) > 0) {
            list.set(j + 1, list.get(j));
            j--;
        }
        return j + 1;
    }

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(12, 11, 13, 5, 6);
        sort(numbers, Comparator.naturalOrder());
        System.out.println(numbers);
    }
}
