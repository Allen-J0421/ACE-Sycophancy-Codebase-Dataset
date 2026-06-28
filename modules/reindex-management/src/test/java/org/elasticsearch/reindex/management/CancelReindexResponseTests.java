/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v 3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.tasks.TaskResult;
import org.elasticsearch.test.ESTestCase;

import static org.hamcrest.Matchers.is;

public class CancelReindexResponseTests extends ESTestCase {

    public void testAcknowledgedFactoryProducesAcknowledgedResponse() {
        CancelReindexResponse response = CancelReindexResponse.acknowledged();

        assertThat(response.isAcknowledged(), is(true));
        assertThat(response.getCompletedReindexResponse().isEmpty(), is(true));
    }

    public void testCompletedFactoryProducesCompletedResponse() {
        TaskId taskId = new TaskId(randomAlphaOfLength(10), randomNonNegativeLong());
        GetReindexResponse getResponse = new GetReindexResponse(new TaskResult(true, ReindexManagementTestUtils.reindexTaskInfo(taskId)));

        CancelReindexResponse response = CancelReindexResponse.completed(getResponse);

        assertThat(response.isAcknowledged(), is(false));
        assertThat(response.getCompletedReindexResponse().orElseThrow(), is(getResponse));
    }
}
