import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortPipeline<T extends Comparable<T>> {
    private final List<Sorter<T>> stages;
    private final String name;

    public SortPipeline(String name) {
        this.name = name;
        this.stages = new ArrayList<>();
    }

    public SortPipeline<T> addStage(Sorter<T> sorter) {
        stages.add(sorter);
        return this;
    }

    public void execute(T[] array) {
        execute(array, Comparable::compareTo);
    }

    public void execute(T[] array, Comparator<T> comparator) {
        if (array == null || array.length == 0) {
            return;
        }

        System.out.println("Executing pipeline: " + name);
        System.out.println("Pipeline stages: " + stages.size());

        for (int i = 0; i < stages.size(); i++) {
            Sorter<T> sorter = stages.get(i);
            System.out.println("  Stage " + (i + 1) + ": " + sorter.getClass().getSimpleName());
            sorter.sort(array, comparator);

            if (sorter.getStatistics() != null) {
                System.out.println("    " + sorter.getStatistics());
            }
        }

        System.out.println("Pipeline completed: " + name);
    }

    public List<SortStatistics> executeAndCollectStats(T[] array) {
        List<SortStatistics> stats = new ArrayList<>();

        for (Sorter<T> sorter : stages) {
            sorter.sort(array);
            if (sorter.getStatistics() != null) {
                stats.add(sorter.getStatistics());
            }
        }

        return stats;
    }

    public int getStageCount() {
        return stages.size();
    }
}
