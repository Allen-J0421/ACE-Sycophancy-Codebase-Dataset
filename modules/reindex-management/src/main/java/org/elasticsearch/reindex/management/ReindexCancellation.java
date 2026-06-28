/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v 3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.ResourceNotFoundException;
import org.elasticsearch.action.admin.cluster.node.tasks.cancel.TransportCancelTasksAction;
import org.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksResponse;
import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.tasks.TaskInfo;
import org.elasticsearch.tasks.TaskResult;

/**
 * Encapsulates the reindex-specific interpretation of cancel-tasks responses and the response shaping needed for the cancel API.
 */
final class ReindexCancellation {

    private ReindexCancellation() {}

    /**
     * Returns the real failure to surface from a cancel-tasks response, with any additional real failures attached as suppressed
     * exceptions, or {@code null} when there are none. Validation-style node failures (handled separately by
     * {@link #isNotFound(ListTasksResponse)}) are skipped.
     */
    static Exception cancellationFailure(final ListTasksResponse response) {
        Exception head = null;
        for (var nodeFailure : response.getNodeFailures()) {
            if (isTaskValidationFailure(nodeFailure)) {
                continue;
            }
            head = ExceptionsHelper.useOrSuppress(head, nodeFailure);
        }
        for (var taskFailure : response.getTaskFailures()) {
            head = ExceptionsHelper.useOrSuppress(head, taskFailure.getCause());
        }
        return head;
    }

    /**
     * Returns {@code true} when the cancel-tasks response indicates the target reindex task could not be located or did not qualify
     * (non-reindex action, sub-task, non-cancellable). Such failures arrive as node failures whose cause is
     * {@link ResourceNotFoundException} or {@link IllegalArgumentException} thrown by {@link TransportCancelTasksAction}.
     * <p>
     * We also require that no task failures are present, because a task failure signals a real cancel failure that must be surfaced
     * to the caller.
     */
    static boolean isNotFound(final ListTasksResponse response) {
        if (response.getTaskFailures().isEmpty() == false) {
            return false;
        }
        if (response.getNodeFailures().isEmpty()) {
            return false;
        }
        for (var failure : response.getNodeFailures()) {
            if (isTaskValidationFailure(failure) == false) {
                return false;
            }
        }
        return true;
    }

    static ResourceNotFoundException notFoundException(final TaskId taskId) {
        return new ResourceNotFoundException("reindex task [{}] either not found or completed", taskId);
    }

    /**
     * Forces the completed task view to reflect cancellation, even if the stored task snapshot predates the cancellation flag update.
     */
    static CancelReindexResponse completedResponse(final GetReindexResponse getResponse) {
        return new CancelReindexResponse(new GetReindexResponse(taskResultWithCancelledTrue(getResponse.getTaskResult())));
    }

    /**
     * True iff the given node failure unwraps to one of the validation-style exceptions
     * {@link TransportCancelTasksAction#processTasks} throws when the request targets a single task that doesn't qualify.
     */
    private static boolean isTaskValidationFailure(final ElasticsearchException failure) {
        if (ExceptionsHelper.unwrap(failure, ResourceNotFoundException.class) != null) {
            return true;
        }
        final IllegalArgumentException iae = (IllegalArgumentException) ExceptionsHelper.unwrap(failure, IllegalArgumentException.class);
        if (iae == null) {
            return false;
        }
        final String message = iae.getMessage();
        return message != null && (message.contains("doesn't support this operation") || message.contains("doesn't support cancellation"));
    }

    private static TaskResult taskResultWithCancelledTrue(final TaskResult result) {
        final TaskInfo taskInfo = result.getTask();
        final TaskInfo newTaskInfo = new TaskInfo(
            taskInfo.taskId(),
            taskInfo.type(),
            taskInfo.node(),
            taskInfo.action(),
            taskInfo.description(),
            taskInfo.status(),
            taskInfo.startTime(),
            taskInfo.runningTimeNanos(),
            taskInfo.cancellable(),
            true,
            taskInfo.parentTaskId(),
            taskInfo.headers(),
            taskInfo.originalTaskId(),
            taskInfo.originalStartTimeMillis()
        );
        return new TaskResult(result.isCompleted(), newTaskInfo, result.getError(), result.getResponse());
    }
}
