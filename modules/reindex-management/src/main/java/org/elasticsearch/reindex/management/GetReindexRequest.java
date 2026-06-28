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
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.core.Nullable;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.tasks.TaskId;

import java.io.IOException;

public class GetReindexRequest extends AbstractReindexTaskRequest {
    private final boolean waitForCompletion;
    @Nullable
    private final TimeValue timeout;

    public GetReindexRequest(TaskId taskId, boolean waitForCompletion, @Nullable TimeValue timeout) {
        super(taskId, "id cannot be null");
        this.waitForCompletion = waitForCompletion;
        this.timeout = timeout;
    }

    public GetReindexRequest(StreamInput in) throws IOException {
        super(in);
        waitForCompletion = in.readBoolean();
        timeout = in.readOptionalTimeValue();
    }

    public boolean getWaitForCompletion() {
        return waitForCompletion;
    }

    @Nullable
    public TimeValue getTimeout() {
        return timeout;
    }

    @Override
    public ActionRequestValidationException validate() {
        return validateTaskId("id is required");
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeBoolean(waitForCompletion);
        out.writeOptionalTimeValue(timeout);
    }
}
