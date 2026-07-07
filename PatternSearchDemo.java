public class PatternSearchDemo {

    public static void main(String[] args) {
        String txt = "aabaacaadaabaaba";
        String pat = "aaba";

        PatternSearcher searcher = PatternSearchFactory.create(pat);
        SearchResult result = searcher.search(txt);

        if (result.hasMatches()) {
            System.out.println("Found " + result.count() + " match(es) at positions:");
            for (int pos : result.getMatches()) {
                System.out.print(pos + " ");
            }
            System.out.println();
        } else {
            System.out.println("No matches found.");
        }
    }
}
