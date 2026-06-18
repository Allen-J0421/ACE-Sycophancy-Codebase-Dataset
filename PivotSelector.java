@FunctionalInterface
public interface PivotSelector {

    int selectPivot(int[] arr, int low, int high);

    PivotSelector LAST_ELEMENT = (arr, low, high) -> high;

    // Avoids worst-case O(n²) on already-sorted/reverse-sorted input.
    PivotSelector MEDIAN_OF_THREE = (arr, low, high) -> {
        int mid = low + (high - low) / 2;
        int a = arr[low], b = arr[mid], c = arr[high];
        if ((a <= b && b <= c) || (c <= b && b <= a)) return mid;
        if ((b <= a && a <= c) || (c <= a && a <= b)) return low;
        return high;
    };
}
