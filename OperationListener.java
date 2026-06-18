public interface OperationListener {
    void onFindOperation(int index, long totalFinds);
    void onUnionOperation(int index1, int index2, long totalUnions);
}
