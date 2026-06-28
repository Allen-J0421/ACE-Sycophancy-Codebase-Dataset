/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.tasks.TaskId;

import java.io.IOException;

/** A request to cancel an ongoing reindex task. */
public class CancelReindexRequest extends AbstractReindexTaskRequest {

    private final boolean waitForCompletion;

    public CancelReindexRequest(final TaskId taskId, final boolean waitForCompletion) {
        super(taskId, "task id cannot be null");
        this.waitForCompletion = waitForCompletion;
    }

    public CancelReindexRequest(final StreamInput in) throws IOException {
        super(in);
        this.waitForCompletion = in.readBoolean();
    }

    @Override
    public void writeTo(final StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeBoolean(waitForCompletion);
    }

    @Override
    public ActionRequestValidationException validate() {
        return validateTaskId("task id must be provided");
    }

    @Override
    public String getDescription() {
        return "taskId[" + getTaskId() + "], waitForCompletion[" + waitForCompletion + "]";
    }

    public boolean waitForCompletion() {
        return waitForCompletion;
    }
}
