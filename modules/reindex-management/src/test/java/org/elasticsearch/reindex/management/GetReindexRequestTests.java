/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v 3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.test.ESTestCase;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class GetReindexRequestTests extends ESTestCase {

    public void testValidationFailsWithoutTaskId() {
        final GetReindexRequest request = new GetReindexRequest(TaskId.EMPTY_TASK_ID, randomBoolean(), randomBoolean() ? null : TimeValue.ZERO);
        final ActionRequestValidationException validation = request.validate();
        assertThat(validation, notNullValue());
        assertThat(validation.getMessage(), containsString("id is required"));
    }

    public void testValidationPassesWithTaskId() {
        final GetReindexRequest request = new GetReindexRequest(
            new TaskId("node", randomNonNegativeLong()),
            randomBoolean(),
            randomBoolean() ? null : TimeValue.timeValueSeconds(randomIntBetween(1, 60))
        );
        assertThat(request.validate(), is(nullValue()));
    }
}
