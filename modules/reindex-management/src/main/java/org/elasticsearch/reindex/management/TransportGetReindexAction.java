/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionType;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.injection.guice.Inject;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.transport.TransportService;

/**
 * Transport action for getting a reindex task. Validates that the requested task is a reindex parent task,
 * then delegates to the relocation-aware Get Task API which transparently follows any relocation chain.
 */
public class TransportGetReindexAction extends AbstractReindexTransportAction<GetReindexRequest, GetReindexResponse> {
    public static final ActionType<GetReindexResponse> TYPE = new ActionType<>("cluster:monitor/reindex/get");

    private final ReindexTaskLookup taskLookup;

    @Inject
    public TransportGetReindexAction(TransportService transportService, ActionFilters actionFilters, Client client) {
        super(TYPE, transportService, actionFilters, GetReindexRequest::new, EsExecutors.DIRECT_EXECUTOR_SERVICE, client);
        this.taskLookup = new ReindexTaskLookup(this.client);
    }

    @Override
    protected void doExecute(Task thisTask, GetReindexRequest request, ActionListener<GetReindexResponse> listener) {
        taskLookup.execute(request, listener);
    }
}
