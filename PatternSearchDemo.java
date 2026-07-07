import java.util.List;

public class PatternSearchDemo {

    public static void main(String[] args) {
        String txt = "aabaacaadaabaaba";
        String pat = "aaba";

        PatternSearcher searcher = PatternSearchFactory.create(pat);
        List<Integer> res = searcher.search(txt);

        for (int it : res) {
            System.out.print(it + " ");
        }
    }
}
