package sorting;

import java.util.Arrays;
import java.util.stream.Collectors;

public class IntArrayFormatter {

    private IntArrayFormatter() {}

    public static String format(int[] arr) {
        return Arrays.stream(arr)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(" "));
    }
}
