/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v 3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.ResourceNotFoundException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.cluster.node.tasks.get.GetTaskRequest;
import org.elasticsearch.action.admin.cluster.node.tasks.get.GetTaskResponse;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.index.reindex.ReindexAction;
import org.elasticsearch.logging.LogManager;
import org.elasticsearch.logging.Logger;
import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.tasks.TaskResult;

import java.util.Objects;

/**
 * Encapsulates the reindex-specific get-task probe/fetch flow and hides task implementation details from callers.
 */
final class ReindexTaskLookup {

    private static final Logger logger = LogManager.getLogger(ReindexTaskLookup.class);

    private final Client client;

    ReindexTaskLookup(final Client client) {
        this.client = Objects.requireNonNull(client);
    }

    void execute(final GetReindexRequest request, final ActionListener<GetReindexResponse> listener) {
        final TaskId taskId = request.getTaskId();
        getTask(probeRequest(taskId), taskId, listener.delegateFailureAndWrap((l, probeResult) -> {
            try {
                validateProbeResult(taskId, probeResult);
            } catch (ResourceNotFoundException e) {
                l.onFailure(e);
                return;
            }
            getTask(fetchRequest(request), taskId, l.delegateFailureAndWrap((l2, result) -> l2.onResponse(new GetReindexResponse(result))));
        }));
    }

    static GetTaskRequest probeRequest(final TaskId taskId) {
        return new GetTaskRequest().setTaskId(taskId).setWaitForCompletion(false).setFollowRelocations(false);
    }

    static GetTaskRequest fetchRequest(final GetReindexRequest request) {
        return new GetTaskRequest().setTaskId(request.getTaskId())
            .setWaitForCompletion(request.getWaitForCompletion())
            .setTimeout(request.getTimeout());
    }

    static void validateProbeResult(final TaskId taskId, final TaskResult probeResult) {
        // Reject if the specified task is not a reindex parent task, to hide task or slicing implementation details.
        if (ReindexAction.NAME.equals(probeResult.getTask().action()) == false) {
            logger.debug("task [{}] requested as reindex but is [{}], returning not found", taskId, probeResult.getTask().action());
            throw notFoundException(taskId);
        }
        if (probeResult.getTask().parentTaskId().isSet()) {
            logger.debug("reindex subtask [{}] requested directly, returning not found", taskId);
            throw notFoundException(taskId);
        }
    }

    static ResourceNotFoundException notFoundException(final TaskId taskId) {
        return new ResourceNotFoundException("Reindex operation [{}] not found", taskId);
    }

    /** Fetches a task, replacing {@link ResourceNotFoundException} with a reindex-specific message. */
    private void getTask(final GetTaskRequest request, final TaskId originalTaskId, final ActionListener<TaskResult> listener) {
        client.admin().cluster().getTask(request, new ActionListener<>() {
            @Override
            public void onResponse(final GetTaskResponse response) {
                listener.onResponse(response.getTask());
            }

            @Override
            public void onFailure(final Exception e) {
                if (e instanceof ResourceNotFoundException) {
                    logger.debug("task [{}] not found, returning as reindex not found", originalTaskId);
                    // Wrap the task-not-found exception to hide task details.
                    listener.onFailure(notFoundException(originalTaskId));
                } else {
                    listener.onFailure(e);
                }
            }
        });
    }
}
