/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.client.internal.node.NodeClient;
import org.elasticsearch.features.NodeFeature;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.Scope;
import org.elasticsearch.rest.ServerlessScope;
import org.elasticsearch.rest.action.RestToXContentListener;
import org.elasticsearch.tasks.TaskId;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import static org.elasticsearch.rest.RestRequest.Method.POST;

/** REST handler for cancelling an ongoing reindex task. */
@ServerlessScope(Scope.PUBLIC)
public class RestCancelReindexAction extends AbstractReindexManagementRestHandler {

    public RestCancelReindexAction(final Predicate<NodeFeature> clusterSupportsFeature) {
        super(clusterSupportsFeature);
    }

    @Override
    public List<Route> routes() {
        return List.of(new Route(POST, "/_reindex/{task_id}/_cancel"));
    }

    @Override
    public String getName() {
        return "cancel_reindex_action";
    }

    @Override
    protected RestChannelConsumer innerPrepareRequest(final RestRequest request, final NodeClient client) throws IOException {
        final String taskIdParam = request.param("task_id");
        final TaskId taskId = taskId(request);
        if (taskId.isSet() == false) {
            throw new IllegalArgumentException("invalid taskId provided: " + taskIdParam);
        }

        final boolean waitForCompletion = request.paramAsBoolean("wait_for_completion", true);
        final CancelReindexRequest cancelRequest = new CancelReindexRequest(taskId, waitForCompletion);

        return channel -> client.execute(TransportCancelReindexAction.TYPE, cancelRequest, new RestToXContentListener<>(channel));
    }
}
