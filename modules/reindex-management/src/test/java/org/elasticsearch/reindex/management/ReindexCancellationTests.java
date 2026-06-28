/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v 3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.ResourceNotFoundException;
import org.elasticsearch.action.FailedNodeException;
import org.elasticsearch.action.TaskOperationFailure;
import org.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksResponse;
import org.elasticsearch.index.reindex.ReindexAction;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.tasks.TaskInfo;
import org.elasticsearch.tasks.TaskResult;
import org.elasticsearch.test.ESTestCase;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ReindexCancellationTests extends ESTestCase {

    public void testIsNotFoundForValidationFailuresOnly() {
        TaskId taskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());
        FailedNodeException nodeFailure = new FailedNodeException(
            taskId.getNodeId(),
            "node failed",
            new IllegalArgumentException("task [" + taskId + "] doesn't support cancellation")
        );

        assertThat(ReindexCancellation.isNotFound(new ListTasksResponse(List.of(), List.of(), List.of(nodeFailure))), is(true));
    }

    public void testIsNotFoundFalseWhenTaskFailuresExist() {
        TaskId taskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());
        FailedNodeException nodeFailure = new FailedNodeException(taskId.getNodeId(), "node failed", new ResourceNotFoundException("gone"));
        TaskOperationFailure taskFailure = new TaskOperationFailure(
            taskId.getNodeId(),
            taskId.getId(),
            new ElasticsearchStatusException("relocating", RestStatus.SERVICE_UNAVAILABLE)
        );

        assertThat(ReindexCancellation.isNotFound(new ListTasksResponse(List.of(), List.of(taskFailure), List.of(nodeFailure))), is(false));
    }

    public void testCancellationFailureSkipsValidationNoiseButKeepsRealFailures() {
        TaskId taskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());
        FailedNodeException validation = new FailedNodeException(taskId.getNodeId(), "node failed", new ResourceNotFoundException("gone"));
        RuntimeException transport = new RuntimeException("transport");
        FailedNodeException nodeFailure = new FailedNodeException(taskId.getNodeId(), "node failed", transport);
        ElasticsearchStatusException relocating = new ElasticsearchStatusException("being relocated", RestStatus.SERVICE_UNAVAILABLE);
        TaskOperationFailure taskFailure = new TaskOperationFailure(taskId.getNodeId(), taskId.getId(), relocating);

        Exception failure = ReindexCancellation.cancellationFailure(new ListTasksResponse(List.of(), List.of(taskFailure), List.of(validation, nodeFailure)));

        assertSame(nodeFailure, failure);
        assertThat(failure.getSuppressed(), arrayContaining(relocating));
    }

    public void testCancellationFailureReturnsNullWhenOnlyValidationNoiseExists() {
        TaskId taskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());
        FailedNodeException validation = new FailedNodeException(taskId.getNodeId(), "node failed", new ResourceNotFoundException("gone"));

        assertThat(ReindexCancellation.cancellationFailure(new ListTasksResponse(List.of(), List.of(), List.of(validation))), nullValue());
    }

    public void testCompletedResponseForcesCancelledFlag() {
        TaskId taskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());
        TaskInfo taskInfo = new TaskInfo(
            taskId,
            "transport",
            taskId.getNodeId(),
            ReindexAction.NAME,
            "reindex",
            null,
            randomNonNegativeLong(),
            randomNonNegativeLong(),
            true,
            false,
            TaskId.EMPTY_TASK_ID,
            Map.of()
        );

        CancelReindexResponse response = ReindexCancellation.completedResponse(new GetReindexResponse(new TaskResult(true, taskInfo)));

        assertThat(response.getCompletedReindexResponse().isPresent(), is(true));
        assertThat(response.getCompletedReindexResponse().orElseThrow().getTaskResult().getTask().cancelled(), is(true));
    }
}
