import java.util.ArrayList;
import java.util.List;

final class CutChoiceTable {
    private final LengthIndexedValues firstCutByLength;

    CutChoiceTable(int maxLength) {
        this.firstCutByLength = LengthIndexedValues.initializedToZero(0, maxLength);
    }

    void recordFirstCut(int rodLength, int firstCutLength) {
        firstCutByLength.setValueAt(rodLength, firstCutLength);
    }

    List<Integer> reconstructCuts(int rodLength) {
        List<Integer> cuts = new ArrayList<>();
        int remainingLength = rodLength;

        while (remainingLength > 0) {
            int firstCutLength = firstCutByLength.valueAt(remainingLength);

            if (firstCutLength <= 0) {
                throw new IllegalStateException(
                        "Missing first cut for rod length: " + remainingLength
                );
            }

            cuts.add(firstCutLength);
            remainingLength -= firstCutLength;
        }

        return List.copyOf(cuts);
    }
}
