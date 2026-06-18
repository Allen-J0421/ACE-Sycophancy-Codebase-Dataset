public class APIVersion {
    private final String version;
    private final int majorVersion;
    private final int minorVersion;

    public APIVersion(String version) {
        this.version = version;
        String[] parts = version.split("\\.");
        this.majorVersion = Integer.parseInt(parts[0]);
        this.minorVersion = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
    }

    public String getVersion() {
        return version;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public boolean isCompatible(APIVersion other) {
        return this.majorVersion == other.majorVersion;
    }

    public boolean isGreaterOrEqual(APIVersion other) {
        if (this.majorVersion > other.majorVersion) return true;
        if (this.majorVersion < other.majorVersion) return false;
        return this.minorVersion >= other.minorVersion;
    }

    @Override
    public String toString() {
        return "APIVersion{" + version + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        APIVersion that = (APIVersion) o;
        return version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return version.hashCode();
    }

    public static final APIVersion V1_0 = new APIVersion("1.0");
    public static final APIVersion V1_1 = new APIVersion("1.1");
    public static final APIVersion V2_0 = new APIVersion("2.0");
    public static final APIVersion LATEST = V2_0;
}
