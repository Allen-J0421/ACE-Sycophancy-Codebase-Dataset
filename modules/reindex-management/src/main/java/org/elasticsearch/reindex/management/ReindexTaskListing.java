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
import org.elasticsearch.tasks.TaskInfo;

import java.util.List;

/**
 * Encapsulates the reindex-specific request shaping and task projection used by the list API.
 */
final class ReindexTaskListing {

    private ReindexTaskListing() {}

    static ListTasksRequest listTasksRequest(final ListReindexRequest request) {
        final ListTasksRequest listTasksRequest = new ListTasksRequest();
        listTasksRequest.setActions(ReindexAction.NAME);
        listTasksRequest.setDetailed(request.getDetailed());
        return listTasksRequest;
    }

    static List<TaskInfo> reindexTasks(final List<TaskInfo> tasks) {
        return tasks.stream().filter(task -> task.parentTaskId().isSet() == false).map(TaskInfo::withOriginalRelocationIdentity).toList();
    }

    static ListReindexResponse response(final ListTasksResponse response) {
        return new ListReindexResponse(reindexTasks(response.getTasks()), response.getTaskFailures(), response.getNodeFailures());
    }
}
