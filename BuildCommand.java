import java.util.List;

interface BuildCommand {
    void execute(List<int[]> edges);
    void undo(List<int[]> edges);
}
