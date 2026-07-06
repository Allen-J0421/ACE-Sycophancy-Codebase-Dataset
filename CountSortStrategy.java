import java.util.ArrayList;
import java.util.List;

interface CountSortStrategy {
    <T> void apply(Sortable<T> s, Pass<T> pass);

    CountSortStrategy STABLE      = new StableCountSort();
    CountSortStrategy BUCKET_LIST = new BucketListCountSort();
}

class StableCountSort implements CountSortStrategy {

    @SuppressWarnings("unchecked")
    public <T> void apply(Sortable<T> s, Pass<T> pass) {
        int n = s.size();
        // plain Object[] avoids the generic-array restriction; cast back to T on write-through is safe
        Object[] output = new Object[n];
        int[] count = new int[pass.base()];

        for (int i = 0; i < n; i++)
            count[pass.bucket(s.get(i))]++;

        for (int i = 1; i < pass.base(); i++)
            count[i] += count[i - 1];

        for (int i = n - 1; i >= 0; i--) {
            int bucket = pass.bucket(s.get(i));
            output[count[bucket] - 1] = s.get(i);
            count[bucket]--;
        }

        for (int i = 0; i < n; i++)
            s.set(i, (T) output[i]);
    }
}

class BucketListCountSort implements CountSortStrategy {

    @SuppressWarnings("unchecked")
    public <T> void apply(Sortable<T> s, Pass<T> pass) {
        List<T>[] buckets = new List[pass.base()];
        for (int i = 0; i < pass.base(); i++) buckets[i] = new ArrayList<>();

        for (int i = 0; i < s.size(); i++)
            buckets[pass.bucket(s.get(i))].add(s.get(i));

        int pos = 0;
        for (List<T> bucket : buckets)
            for (T item : bucket) s.set(pos++, item);
    }
}
