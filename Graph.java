import java.util.List;

interface Graph {

    int vertices();

    List<? extends Edge> edges();
}
