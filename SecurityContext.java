import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SecurityContext {
    private final String principalId;
    private final Set<String> roles;
    private final Map<String, Object> attributes;
    private final long issuedAt;
    private final long expiresAt;

    public SecurityContext(String principalId, Set<String> roles, long ttlMillis) {
        this.principalId = principalId;
        this.roles = new HashSet<>(roles);
        this.attributes = new HashMap<>();
        this.issuedAt = System.currentTimeMillis();
        this.expiresAt = issuedAt + ttlMillis;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public Set<String> getRoles() {
        return new HashSet<>(roles);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public boolean isAuthenticated() {
        return !isExpired() && principalId != null;
    }

    @Override
    public String toString() {
        return "SecurityContext{" +
                "principal=" + principalId +
                ", roles=" + roles +
                ", authenticated=" + isAuthenticated() +
                '}';
    }
}
