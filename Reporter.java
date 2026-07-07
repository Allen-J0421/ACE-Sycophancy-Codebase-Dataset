interface Reporter {
    void reportSuccess(String label, String result);
    void reportError(String label, String errorMessage);
}
