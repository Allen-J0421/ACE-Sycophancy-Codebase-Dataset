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
import org.elasticsearch.action.ActionType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.reindex.ReindexAction;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.tasks.TaskInfo;
import org.elasticsearch.tasks.TaskResult;
import org.elasticsearch.test.rest.FakeRestRequest;
import org.elasticsearch.test.rest.RestActionTestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

public class ReindexManagementRestActionsTests extends RestActionTestCase {

    public void testHandlersRejectUnsupportedClusters() {
        IllegalArgumentException listFailure = expectUnsupportedEndpoint(new RestListReindexAction(feature -> false), listRequest(Map.of()));
        IllegalArgumentException getFailure = expectUnsupportedEndpoint(
            new RestGetReindexAction(feature -> false),
            getRequest(validTaskId().toString(), Map.of())
        );
        IllegalArgumentException cancelFailure = expectUnsupportedEndpoint(
            new RestCancelReindexAction(feature -> false),
            cancelRequest(validTaskId().toString(), Map.of())
        );

        assertThat(listFailure.getMessage(), equalTo("endpoint not supported on all nodes in the cluster"));
        assertThat(getFailure.getMessage(), equalTo("endpoint not supported on all nodes in the cluster"));
        assertThat(cancelFailure.getMessage(), equalTo("endpoint not supported on all nodes in the cluster"));
    }

    public void testListRequestDispatchesDetailedFlag() throws Exception {
        RestListReindexAction action = new RestListReindexAction(feature -> true);
        controller().registerHandler(action);
        verifyingClient.setExecuteVerifier((ActionType<ListReindexResponse> actionType, ActionRequest actionRequest) -> {
            assertSame(TransportListReindexAction.TYPE, actionType);
            assertThat(actionRequest, instanceOf(ListReindexRequest.class));
            ListReindexRequest request = (ListReindexRequest) actionRequest;
            assertTrue(request.getDetailed());
            return new ListReindexResponse(List.of(), List.of(), List.of());
        });

        dispatchRequest(listRequest(Map.of("detailed", "true")));
    }

    public void testGetRequestDispatchesTaskIdWaitForCompletionAndTimeout() throws Exception {
        TaskId taskId = validTaskId();
        RestGetReindexAction action = new RestGetReindexAction(feature -> true);
        controller().registerHandler(action);
        verifyingClient.setExecuteVerifier((ActionType<GetReindexResponse> actionType, ActionRequest actionRequest) -> {
            assertSame(TransportGetReindexAction.TYPE, actionType);
            assertThat(actionRequest, instanceOf(GetReindexRequest.class));
            GetReindexRequest request = (GetReindexRequest) actionRequest;
            assertEquals(taskId, request.getTaskId());
            assertTrue(request.getWaitForCompletion());
            assertEquals(TimeValue.timeValueSeconds(15), request.getTimeout());
            return new GetReindexResponse(new TaskResult(false, reindexTaskInfo(taskId)));
        });

        dispatchRequest(getRequest(taskId.toString(), Map.of("wait_for_completion", "true", "timeout", "15s")));
    }

    public void testCancelRejectsInvalidTaskIdBeforeDispatch() {
        RestCancelReindexAction action = new RestCancelReindexAction(feature -> true);
        controller().registerHandler(action);

        IllegalArgumentException error = expectThrows(IllegalArgumentException.class, () -> dispatchRequest(cancelRequest("unset", Map.of())));

        assertEquals("invalid taskId provided: unset", error.getMessage());
    }

    public void testCancelRequestDefaultsWaitForCompletionToTrue() throws Exception {
        TaskId taskId = validTaskId();
        RestCancelReindexAction action = new RestCancelReindexAction(feature -> true);
        controller().registerHandler(action);
        verifyingClient.setExecuteVerifier((ActionType<CancelReindexResponse> actionType, ActionRequest actionRequest) -> {
            assertSame(TransportCancelReindexAction.TYPE, actionType);
            assertThat(actionRequest, instanceOf(CancelReindexRequest.class));
            CancelReindexRequest request = (CancelReindexRequest) actionRequest;
            assertEquals(taskId, request.getTaskId());
            assertTrue(request.waitForCompletion());
            return new CancelReindexResponse(null);
        });

        dispatchRequest(cancelRequest(taskId.toString(), Map.of()));
    }

    private IllegalArgumentException expectUnsupportedEndpoint(AbstractReindexManagementRestHandler action, RestRequest request) {
        controller().registerHandler(action);
        return expectThrows(IllegalArgumentException.class, () -> dispatchRequest(request));
    }

    private FakeRestRequest listRequest(Map<String, String> params) {
        return new FakeRestRequest.Builder(xContentRegistry()).withMethod(RestRequest.Method.GET)
            .withPath("/_reindex")
            .withParams(params)
            .build();
    }

    private FakeRestRequest getRequest(String taskId, Map<String, String> params) {
        return request(RestRequest.Method.GET, "/_reindex/" + taskId, taskId, params);
    }

    private FakeRestRequest cancelRequest(String taskId, Map<String, String> params) {
        return request(RestRequest.Method.POST, "/_reindex/" + taskId + "/_cancel", taskId, params);
    }

    private FakeRestRequest request(RestRequest.Method method, String path, String taskId, Map<String, String> params) {
        Map<String, String> allParams = new HashMap<>(params);
        allParams.put("task_id", taskId);
        return new FakeRestRequest.Builder(xContentRegistry()).withMethod(method).withPath(path).withParams(allParams).build();
    }

    private TaskId validTaskId() {
        return new TaskId(randomAlphaOfLength(8), randomLongBetween(1, 1_000));
    }

    private TaskInfo reindexTaskInfo(TaskId taskId) {
        return new TaskInfo(taskId, "transport", taskId.getNodeId(), ReindexAction.NAME, "reindex", null, 0L, 0L, true, false, TaskId.EMPTY_TASK_ID, Map.of());
    }
}
