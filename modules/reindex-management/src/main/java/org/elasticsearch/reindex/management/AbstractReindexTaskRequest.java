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
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.tasks.TaskId;

import java.io.IOException;
import java.util.Objects;

import static org.elasticsearch.action.ValidateActions.addValidationError;

/**
 * Base request for reindex-management APIs that target a single task and therefore share the same task-id transport handling.
 */
abstract class AbstractReindexTaskRequest extends ActionRequest {

    private final TaskId taskId;

    AbstractReindexTaskRequest(final TaskId taskId, final String nullTaskIdMessage) {
        this.taskId = Objects.requireNonNull(taskId, nullTaskIdMessage);
    }

    AbstractReindexTaskRequest(final StreamInput in) throws IOException {
        super(in);
        this.taskId = TaskId.readFromStream(in);
    }

    @Override
    public void writeTo(final StreamOutput out) throws IOException {
        super.writeTo(out);
        taskId.writeTo(out);
    }

    public final TaskId getTaskId() {
        return taskId;
    }

    /**
     * Validates that the task id was provided, using the caller's endpoint-specific message.
     */
    protected final ActionRequestValidationException validateTaskId(final String validationMessage) {
        ActionRequestValidationException validationException = null;
        if (taskId.isSet() == false) {
            validationException = addValidationError(validationMessage, validationException);
        }
        return validationException;
    }
}
