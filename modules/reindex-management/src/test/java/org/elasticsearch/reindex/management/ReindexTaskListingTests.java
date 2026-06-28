/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v 3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksRequest;
import org.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksResponse;
import org.elasticsearch.index.reindex.ReindexAction;
import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.tasks.TaskInfo;
import org.elasticsearch.test.ESTestCase;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class ReindexTaskListingTests extends ESTestCase {

    public void testListTasksRequestUsesReindexActionAndDetailedFlag() {
        boolean detailed = randomBoolean();

        ListTasksRequest request = ReindexTaskListing.listTasksRequest(new ListReindexRequest(detailed));

        assertThat(request.getActions(), equalTo(new String[] { ReindexAction.NAME }));
        assertThat(request.getDetailed(), is(detailed));
    }

    public void testReindexTasksFiltersChildrenAndPreservesNonRelocatedTask() {
        TaskId parentId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());
        TaskInfo parentTask = taskInfo(parentId, TaskId.EMPTY_TASK_ID);
        TaskInfo childTask = taskInfo(new TaskId(randomAlphaOfLength(10), randomNonNegativeLong()), parentId);

        List<TaskInfo> tasks = ReindexTaskListing.reindexTasks(List.of(parentTask, childTask));

        assertThat(tasks, equalTo(List.of(parentTask)));
    }

    public void testReindexTasksRewritesRelocatedTaskToOriginalIdentity() {
        TaskId originalId = new TaskId("original-node", randomNonNegativeLong());
        TaskId relocatedId = new TaskId("relocated-node", randomNonNegativeLong());
        long originalStartMillis = randomLongBetween(0, 100);
        long relocatedStartMillis = randomLongBetween(originalStartMillis, originalStartMillis + randomLongBetween(0, 100));
        long relocatedRunningNanos = randomNonNegativeLong();
        TaskInfo relocatedTask = new TaskInfo(
            relocatedId,
            "transport",
            relocatedId.getNodeId(),
            ReindexAction.NAME,
            "reindex",
            null,
            relocatedStartMillis,
            relocatedRunningNanos,
            false,
            false,
            TaskId.EMPTY_TASK_ID,
            Map.of(),
            originalId,
            originalStartMillis
        );

        List<TaskInfo> tasks = ReindexTaskListing.reindexTasks(List.of(relocatedTask));

        assertThat(tasks, hasSize(1));
        TaskInfo rewritten = tasks.getFirst();
        assertThat(rewritten.taskId(), equalTo(originalId));
        assertThat(rewritten.taskId(), equalTo(rewritten.originalTaskId()));
        assertThat(rewritten.node(), equalTo(originalId.getNodeId()));
        assertThat(rewritten.startTime(), equalTo(originalStartMillis));
        assertThat(rewritten.startTime(), equalTo(rewritten.originalStartTimeMillis()));
        long expectedRunningNanos = relocatedRunningNanos + TimeUnit.MILLISECONDS.toNanos(relocatedStartMillis - originalStartMillis);
        assertThat(rewritten.runningTimeNanos(), equalTo(expectedRunningNanos));
    }

    public void testResponseUsesProjectedTasks() {
        TaskId taskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());
        TaskInfo parentTask = taskInfo(taskId, TaskId.EMPTY_TASK_ID);

        ListReindexResponse response = ReindexTaskListing.response(new ListTasksResponse(List.of(parentTask), List.of(), List.of()));

        assertThat(response.getTasks(), equalTo(List.of(parentTask)));
    }

    private TaskInfo taskInfo(TaskId taskId, TaskId parentTaskId) {
        return new TaskInfo(taskId, "transport", taskId.getNodeId(), ReindexAction.NAME, "reindex", null, 0L, 0L, false, false, parentTaskId, Map.of());
    }
}
