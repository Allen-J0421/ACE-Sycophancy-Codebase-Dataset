/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v 3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionType;
import org.elasticsearch.action.admin.cluster.node.tasks.list.TransportListTasksAction;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.injection.guice.Inject;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.transport.TransportService;

/// Transport action for listing all running reindex tasks.
/// Delegates to {@link TransportListTasksAction} to fan out to all nodes (which handles deduplication if we list a non-relocated and
/// relocated task), then filters for reindex parent tasks and rewrites task identity to reflect the original (pre-relocation) task.
public class TransportListReindexAction extends AbstractReindexTransportAction<ListReindexRequest, ListReindexResponse> {

    public static final ActionType<ListReindexResponse> TYPE = new ActionType<>("cluster:monitor/reindex/list");

    @Inject
    public TransportListReindexAction(final TransportService transportService, final ActionFilters actionFilters, final Client client) {
        super(TYPE, transportService, actionFilters, ListReindexRequest::new, EsExecutors.DIRECT_EXECUTOR_SERVICE, client);
    }

    @Override
    protected void doExecute(final Task task, final ListReindexRequest request, final ActionListener<ListReindexResponse> listener) {
        client.execute(
            TransportListTasksAction.TYPE,
            ReindexTaskListing.listTasksRequest(request),
            listener.delegateFailureAndWrap((l, response) -> l.onResponse(ReindexTaskListing.response(response)))
        );
    }
}
