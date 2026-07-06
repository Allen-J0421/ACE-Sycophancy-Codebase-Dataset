import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Radix {

    public static void main(String[] args) {
        // named constant — zero configuration at the call site
        Integer[] arr1 = { 170, 45, 75, 90, 802, 24, 2, 66 };
        RadixSorterFactory.DECIMAL.create(Integer::intValue).sort(Sortable.of(arr1));
        System.out.println("Named constant:  " + Arrays.toString(arr1));

        // full builder — compose digits and strategy from scratch
        Integer[] arr2 = { 170, 45, 75, 90, 802, 24, 2, 66 };
        RadixSorterFactory.builder()
            .digits(DigitExtractor.HEX)
            .strategy(CountSortStrategy.BUCKET_LIST)
            .build()
            .create(Integer::intValue)
            .sort(Sortable.of(arr2));
        System.out.println("Full builder:    " + Arrays.toString(arr2));

        // toBuilder — copy an existing config and override one field
        Integer[] arr3 = { 170, 45, 75, 90, 802, 24, 2, 66 };
        RadixSorterFactory.DECIMAL.toBuilder()
            .strategy(CountSortStrategy.BUCKET_LIST)
            .build()
            .create(Integer::intValue)
            .sort(Sortable.of(arr3));
        System.out.println("toBuilder:       " + Arrays.toString(arr3));

        List<String> words = new ArrayList<>(Arrays.asList("banana", "fig", "apple", "kiwi", "date"));
        RadixSorterFactory.HEX.create(String::length).sort(Sortable.of(words));
        System.out.println("By length:       " + words);
    }
}
