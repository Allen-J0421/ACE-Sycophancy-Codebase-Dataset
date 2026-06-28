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
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.ServerlessScope;
import org.elasticsearch.rest.action.RestChunkedToXContentListener;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.Scope.PUBLIC;

/**
 * REST handler for listing all running reindex tasks.
 */
@ServerlessScope(PUBLIC)
public class RestListReindexAction extends AbstractReindexManagementRestHandler {

    RestListReindexAction(final Predicate<NodeFeature> clusterSupportsFeature) {
        super(clusterSupportsFeature);
    }

    @Override
    public List<Route> routes() {
        return List.of(new Route(GET, "/_reindex"));
    }

    @Override
    public String getName() {
        return "list_reindex_action";
    }

    @Override
    protected RestChannelConsumer innerPrepareRequest(RestRequest request, NodeClient client) throws IOException {
        ListReindexRequest listReindexRequest = new ListReindexRequest(request.paramAsBoolean("detailed", false));
        return channel -> client.execute(TransportListReindexAction.TYPE, listReindexRequest, new RestChunkedToXContentListener<>(channel));
    }
}
