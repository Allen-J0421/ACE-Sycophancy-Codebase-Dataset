package com.algorithms.scc;

/**
 * Renders a {@link StronglyConnectedComponentsResult} as human-readable text,
 * one component per line with its vertices separated by spaces.
 *
 * @param <V> the vertex label type
 */
public final class StronglyConnectedComponentsFormatter<V> {

    private static final String DEFAULT_HEADER = "Strongly Connected Components:";

    private final String header;

    public StronglyConnectedComponentsFormatter() {
        this(DEFAULT_HEADER);
    }

    public StronglyConnectedComponentsFormatter(String header) {
        this.header = header;
    }

    /**
     * @param result the components to render
     * @return a multi-line string: a header followed by one line per component
     */
    public String format(StronglyConnectedComponentsResult<V> result) {
        StringBuilder sb = new StringBuilder(header);
        for (StronglyConnectedComponent<V> component : result.components()) {
            sb.append(System.lineSeparator()).append(component);
        }
        return sb.toString();
    }
}
