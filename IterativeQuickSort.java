import java.util.ArrayDeque;
import java.util.Deque;

public class IterativeQuickSort extends AbstractQuickSort {

    public IterativeQuickSort() {
        this(PivotSelector.LAST_ELEMENT);
    }

    public IterativeQuickSort(PivotSelector pivotSelector) {
        super(pivotSelector);
    }

    @Override
    public void sort(int[] arr) {
        if (arr.length < 2) return;

        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);
        stack.push(arr.length - 1);

        while (!stack.isEmpty()) {
            int high = stack.pop();
            int low  = stack.pop();
            int pi   = partition(arr, low, high);
            if (low  < pi - 1) { stack.push(low);    stack.push(pi - 1); }
            if (pi + 1 < high) { stack.push(pi + 1); stack.push(high);   }
        }
    }
}
