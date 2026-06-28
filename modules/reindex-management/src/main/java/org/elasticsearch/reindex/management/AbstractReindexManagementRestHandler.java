/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v 3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.client.internal.node.NodeClient;
import org.elasticsearch.features.NodeFeature;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.tasks.TaskId;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import static org.elasticsearch.reindex.management.ReindexManagementPlugin.CAPABILITY_REINDEX_MANAGEMENT_API;

/**
 * Shared REST handler base for reindex-management endpoints that all require the same cluster feature gate and capability declaration.
 */
abstract class AbstractReindexManagementRestHandler extends BaseRestHandler {

    private final Predicate<NodeFeature> clusterSupportsFeature;

    AbstractReindexManagementRestHandler(final Predicate<NodeFeature> clusterSupportsFeature) {
        this.clusterSupportsFeature = Objects.requireNonNull(clusterSupportsFeature);
    }

    @Override
    protected final RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
        ensureEndpointSupported();
        return innerPrepareRequest(request, client);
    }

    /**
     * Prepares the endpoint-specific request once the cluster-wide compatibility checks have passed.
     */
    protected abstract RestChannelConsumer innerPrepareRequest(RestRequest request, NodeClient client) throws IOException;

    protected final TaskId taskId(final RestRequest request) {
        return new TaskId(request.param("task_id"));
    }

    @Override
    public final Set<String> supportedCapabilities() {
        return Set.of(CAPABILITY_REINDEX_MANAGEMENT_API);
    }

    private void ensureEndpointSupported() {
        if (clusterSupportsFeature.test(ReindexManagementFeatures.NEW_ENDPOINTS) == false) {
            throw new IllegalArgumentException("endpoint not supported on all nodes in the cluster");
        }
    }
}
