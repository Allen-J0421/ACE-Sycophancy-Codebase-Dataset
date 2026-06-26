import java.util.ArrayList;
import java.util.List;

class CutChoiceTable extends LengthIndexedValues {
    CutChoiceTable(int size) {
        super(size);
    }

    List<Integer> reconstructCuts(int remaining) {
        List<Integer> cuts = new ArrayList<>();
        while (remaining > 0) {
            int cut = get(remaining);
            cuts.add(cut);
            remaining -= cut;
        }
        return cuts;
    }
}
