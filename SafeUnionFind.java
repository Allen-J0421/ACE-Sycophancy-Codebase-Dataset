public class SafeUnionFind {
    private UnionFind unionFind;

    public SafeUnionFind(UnionFind unionFind) {
        this.unionFind = unionFind;
    }

    public Result<Integer> find(int i) {
        try {
            int result = unionFind.find(i);
            return Result.ok(result);
        } catch (UnionFindException e) {
            return Result.error(e);
        }
    }

    public Result<Void> union(int i, int j) {
        try {
            unionFind.union(i, j);
            return Result.ok(null);
        } catch (UnionFindException e) {
            return Result.error(e);
        }
    }

    public Result<Boolean> isConnected(int i, int j) {
        try {
            boolean result = unionFind.isConnected(i, j);
            return Result.ok(result);
        } catch (UnionFindException e) {
            return Result.error(e);
        }
    }

    public static SafeUnionFind wrap(UnionFind unionFind) {
        return new SafeUnionFind(unionFind);
    }
}
