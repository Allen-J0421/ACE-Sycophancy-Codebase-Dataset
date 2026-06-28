/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.action.search;

import org.elasticsearch.search.internal.ShardSearchRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Immutable bundle of search telemetry attributes and the request's absolute start time.
 * This keeps phase listeners from rebuilding or mutating attribute maps as telemetry flows
 * through the search pipeline.
 */
public record SearchTelemetryContext(Map<String, Object> attributes, long absoluteStartMillis) {
    SearchTelemetryContext {
        attributes = Map.copyOf(attributes);
    }

    /**
     * Creates telemetry from precomputed attributes and the request start time.
     */
    public static SearchTelemetryContext of(
        Map<String, Object> attributes,
        long absoluteStartMillis
    ) {
        return new SearchTelemetryContext(attributes, absoluteStartMillis);
    }

    /**
     * Returns a mutable copy of the telemetry attributes for callers that need to add derived values.
     */
    public Map<String, Object> mutableAttributes() {
        return new HashMap<>(attributes);
    }

    /**
     * Extracts telemetry from a top-level search request using the resolved local indices.
     */
    public static SearchTelemetryContext from(
        SearchRequest searchRequest,
        String[] localIndices,
        TransportSearchAction.SearchTimeProvider timeProvider
    ) {
        return of(
            SearchRequestAttributesExtractor.extractAttributes(searchRequest, localIndices),
            timeProvider.absoluteStartMillis()
        );
    }

    /**
     * Extracts telemetry from a shard-level search request and adds the current
     * shard timing metadata.
     */
    public static SearchTelemetryContext from(
        ShardSearchRequest shardSearchRequest,
        Long timeRangeFilterFromMillis
    ) {
        long nowInMillis = shardSearchRequest.nowInMillis();
        return of(
            SearchRequestAttributesExtractor.extractAttributes(shardSearchRequest, timeRangeFilterFromMillis, nowInMillis),
            nowInMillis
        );
    }
}
