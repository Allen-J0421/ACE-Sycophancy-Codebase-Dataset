/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v 3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.ActionType;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.client.internal.OriginSettingClient;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.transport.TransportService;

import java.util.Objects;
import java.util.concurrent.Executor;

import static org.elasticsearch.action.admin.cluster.node.tasks.get.TransportGetTaskAction.TASKS_ORIGIN;

/**
 * Shared transport-action base for reindex-management APIs that execute via the tasks origin.
 */
abstract class AbstractReindexTransportAction<Request extends ActionRequest, Response extends ActionResponse> extends HandledTransportAction<
    Request,
    Response> {

    protected final Client client;

    AbstractReindexTransportAction(
        final ActionType<Response> actionType,
        final TransportService transportService,
        final ActionFilters actionFilters,
        final Writeable.Reader<Request> reader,
        final Executor executor,
        final Client client
    ) {
        super(actionType.name(), transportService, actionFilters, reader, executor);
        this.client = new OriginSettingClient(Objects.requireNonNull(client), TASKS_ORIGIN);
    }
}
