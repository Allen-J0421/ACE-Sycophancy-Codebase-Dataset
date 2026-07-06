class GraphView implements ResultVisitor<Partition> {

    @Override
    public void onSuccess(Partition partition) {
        System.out.println(true);
    }

    @Override
    public void onFailure() {
        System.out.println(false);
    }
}
