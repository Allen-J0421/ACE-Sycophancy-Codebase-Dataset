import java.util.List;

public interface RangeSumQuery<T> {
    T rangeSum(int l, int r);
    List<T> toList();
}
