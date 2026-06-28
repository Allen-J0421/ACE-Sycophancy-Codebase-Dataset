/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v 3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.index.reindex.ReindexAction;
import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.tasks.TaskInfo;

import java.util.Map;

final class ReindexManagementTestUtils {

    private ReindexManagementTestUtils() {}

    static TaskInfo reindexTaskInfo(final TaskId taskId) {
        return taskInfo(taskId, ReindexAction.NAME, TaskId.EMPTY_TASK_ID);
    }

    static TaskInfo taskInfo(final TaskId taskId, final String action, final TaskId parentTaskId) {
        return new TaskInfo(taskId, "transport", taskId.getNodeId(), action, "reindex", null, 0L, 0L, true, false, parentTaskId, Map.of());
    }

    static TaskInfo relocatedReindexTaskInfo(
        final TaskId currentTaskId,
        final TaskId originalTaskId,
        final long currentStartTimeMillis,
        final long runningTimeNanos,
        final boolean cancellable,
        final boolean cancelled
    ) {
        return new TaskInfo(
            currentTaskId,
            "transport",
            currentTaskId.getNodeId(),
            ReindexAction.NAME,
            "reindex",
            null,
            currentStartTimeMillis,
            runningTimeNanos,
            cancellable,
            cancelled,
            TaskId.EMPTY_TASK_ID,
            Map.of(),
            originalTaskId,
            currentStartTimeMillis
        );
    }

    static TaskInfo relocatedReindexTaskInfo(
        final TaskId currentTaskId,
        final TaskId originalTaskId,
        final long currentStartTimeMillis,
        final long runningTimeNanos,
        final boolean cancellable,
        final boolean cancelled,
        final long originalStartTimeMillis
    ) {
        return new TaskInfo(
            currentTaskId,
            "transport",
            currentTaskId.getNodeId(),
            ReindexAction.NAME,
            "reindex",
            null,
            currentStartTimeMillis,
            runningTimeNanos,
            cancellable,
            cancelled,
            TaskId.EMPTY_TASK_ID,
            Map.of(),
            originalTaskId,
            originalStartTimeMillis
        );
    }
}
