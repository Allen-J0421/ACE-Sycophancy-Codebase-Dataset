import java.util.ArrayList;

public class RabinKarp {

    static ArrayList<Integer> search(String pat, String txt)
    {

        int d = 256;

        int q = 101;

        int m = pat.length();

        int n = txt.length();

        int p = 0;

        int t = 0;

        int h = 1;

        ArrayList<Integer> ans = new ArrayList<>();

        for (int i = 0; i < m - 1; i++)
            h = (h * d) % q;

        for (int i = 0; i < m; i++) {
            p = (d * p + pat.charAt(i)) % q;
            t = (d * t + txt.charAt(i)) % q;
        }

        for (int i = 0; i <= n - m; i++) {

            if (p == t) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (txt.charAt(i + j)
                        != pat.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match)
                    ans.add(i);
            }

            if (i < n - m) {
                t = (d * (t - txt.charAt(i) * h)
                     + txt.charAt(i + m))
                    % q;
                if (t < 0)
                    t += q;
            }
        }
        return ans;
    }

    public static void main(String[] args)
    {
        String txt = "geeksforgeeks";
        String pat = "geeks";
        ArrayList<Integer> res = search(pat, txt);
        for (int idx : res)
            System.out.print(idx + " ");
        System.out.println();
    }
}
