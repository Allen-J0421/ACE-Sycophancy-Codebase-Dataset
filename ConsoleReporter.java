class ConsoleReporter implements Reporter {

    @Override
    public void reportSuccess(String label, String result) {
        System.out.println(label + ": " + result);
    }

    @Override
    public void reportError(String label, String errorMessage) {
        System.out.println(label + " [error]: " + errorMessage);
    }
}
