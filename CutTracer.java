import java.util.ArrayList;
import java.util.List;

class CutTracer {

    static List<Integer> trace(int[] bestCut, int n) {
        List<Integer> cuts = new ArrayList<>();
        int remaining = n;
        while (remaining > 0) {
            cuts.add(bestCut[remaining]);
            remaining -= bestCut[remaining];
        }
        return cuts;
    }
}
