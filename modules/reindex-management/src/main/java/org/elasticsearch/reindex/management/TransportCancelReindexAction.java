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
import org.elasticsearch.action.admin.cluster.node.tasks.cancel.CancelTasksRequest;
import org.elasticsearch.action.admin.cluster.node.tasks.cancel.TransportCancelTasksAction;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.client.internal.OriginSettingClient;
import org.elasticsearch.index.reindex.ReindexAction;
import org.elasticsearch.injection.guice.Inject;
import org.elasticsearch.logging.LogManager;
import org.elasticsearch.logging.Logger;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

import java.util.Objects;

import static org.elasticsearch.action.admin.cluster.node.tasks.get.TransportGetTaskAction.TASKS_ORIGIN;

/** Transport action that cancels an in-flight reindex task and its descendants. */
public class TransportCancelReindexAction extends HandledTransportAction<CancelReindexRequest, CancelReindexResponse> {

    public static final ActionType<CancelReindexResponse> TYPE = new ActionType<>("cluster:admin/reindex/cancel");

    private static final Logger logger = LogManager.getLogger(TransportCancelReindexAction.class);

    private final Client client;

    @Inject
    public TransportCancelReindexAction(final TransportService transportService, final ActionFilters actionFilters, final Client client) {
        super(
            TYPE.name(),
            transportService,
            actionFilters,
            CancelReindexRequest::new,
            transportService.getThreadPool().executor(ThreadPool.Names.GENERIC)
        );
        this.client = new OriginSettingClient(Objects.requireNonNull(client), TASKS_ORIGIN);
    }

    @Override
    protected void doExecute(
        final Task thisTask,
        final CancelReindexRequest request,
        final ActionListener<CancelReindexResponse> listener
    ) {
        final TaskId taskId = request.getTaskId();

        final CancelTasksRequest cancelTasksRequest = new CancelTasksRequest();
        cancelTasksRequest.setTargetTaskId(taskId);
        cancelTasksRequest.setActions(ReindexAction.NAME);
        cancelTasksRequest.setExcludeChildTasks(true);
        cancelTasksRequest.setWaitForCompletion(false);

        client.execute(TransportCancelTasksAction.TYPE, cancelTasksRequest, listener.delegateFailureAndWrap((l, cancelResponse) -> {
            // Maps validation and resource not found failures to reindex specific error
            if (ReindexCancellation.isNotFound(cancelResponse)) {
                logger.debug("cancel-tasks rejected task [{}]; reporting as reindex not found", taskId);
                l.onFailure(ReindexCancellation.notFoundException(taskId));
                return;
            }
            // Surface other failures as-is
            final Exception cancelFailure = ReindexCancellation.cancellationFailure(cancelResponse);
            if (cancelFailure != null) {
                l.onFailure(cancelFailure);
                return;
            }
            // No failure was reported but no task was matched either
            if (cancelResponse.getTasks().isEmpty()) {
                l.onFailure(ReindexCancellation.notFoundException(taskId));
                return;
            }

            if (request.waitForCompletion()) {
                // Fetch the completed reindex result with relocation following so the response reflects the original task identity.
                final GetReindexRequest getRequest = new GetReindexRequest(taskId, true, null);
                client.execute(
                    TransportGetReindexAction.TYPE,
                    getRequest,
                    l.delegateFailureAndWrap(
                        (l2, getResp) -> l2.onResponse(ReindexCancellation.completedResponse(getResp))
                    )
                );
            } else {
                l.onResponse(new CancelReindexResponse((GetReindexResponse) null));
            }
        }));
    }
}
