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
import org.elasticsearch.action.admin.cluster.node.tasks.get.GetTaskRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.reindex.ReindexAction;
import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.tasks.TaskInfo;
import org.elasticsearch.tasks.TaskResult;
import org.elasticsearch.test.ESTestCase;

import java.util.Map;

import static org.hamcrest.Matchers.is;

public class ReindexTaskLookupTests extends ESTestCase {

    public void testProbeRequestDisablesWaitingAndRelocationFollowing() {
        TaskId taskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());

        GetTaskRequest request = ReindexTaskLookup.probeRequest(taskId);

        assertThat(request.getTaskId(), is(taskId));
        assertThat(request.getWaitForCompletion(), is(false));
        assertThat(request.getFollowRelocations(), is(false));
    }

    public void testFetchRequestUsesWaitAndTimeoutFromRequest() {
        TaskId taskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());
        TimeValue timeout = TimeValue.timeValueSeconds(randomIntBetween(1, 60));
        GetReindexRequest request = new GetReindexRequest(taskId, true, timeout);

        GetTaskRequest fetchRequest = ReindexTaskLookup.fetchRequest(request);

        assertThat(fetchRequest.getTaskId(), is(taskId));
        assertThat(fetchRequest.getWaitForCompletion(), is(true));
        assertThat(fetchRequest.getFollowRelocations(), is(true));
        assertThat(fetchRequest.getTimeout(), is(timeout));
    }

    public void testValidateProbeResultAcceptsReindexParentTask() {
        TaskId taskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());

        ReindexTaskLookup.validateProbeResult(taskId, new TaskResult(false, taskInfo(taskId, ReindexAction.NAME, TaskId.EMPTY_TASK_ID)));
    }

    public void testValidateProbeResultRejectsNonReindexTask() {
        TaskId taskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());

        ResourceNotFoundException e = expectThrows(
            ResourceNotFoundException.class,
            () -> ReindexTaskLookup.validateProbeResult(taskId, new TaskResult(false, taskInfo(taskId, "other_action", TaskId.EMPTY_TASK_ID)))
        );

        assertEquals(ReindexTaskLookup.notFoundException(taskId).getMessage(), e.getMessage());
    }

    public void testValidateProbeResultRejectsSubtask() {
        TaskId taskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());
        TaskId parentTaskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());

        ResourceNotFoundException e = expectThrows(
            ResourceNotFoundException.class,
            () -> ReindexTaskLookup.validateProbeResult(taskId, new TaskResult(false, taskInfo(taskId, ReindexAction.NAME, parentTaskId)))
        );

        assertEquals(ReindexTaskLookup.notFoundException(taskId).getMessage(), e.getMessage());
    }

    private TaskInfo taskInfo(TaskId taskId, String action, TaskId parentTaskId) {
        return new TaskInfo(taskId, "transport", taskId.getNodeId(), action, "reindex", null, 0L, 0L, false, false, parentTaskId, Map.of());
    }
}
