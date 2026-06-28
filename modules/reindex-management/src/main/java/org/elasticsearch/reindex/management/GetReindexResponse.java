/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.reindex.management;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.tasks.TaskResult;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Objects;

public class GetReindexResponse extends ActionResponse implements ToXContentObject {

    private final TaskResult taskResult;

    public GetReindexResponse(final TaskResult taskResult) {
        this.taskResult = Objects.requireNonNull(taskResult, "taskResult is required");
    }

    public GetReindexResponse(StreamInput in) throws IOException {
        this(new TaskResult(in));
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        taskResult.writeTo(out);
    }

    public TaskResult getTaskResult() {
        return taskResult;
    }

    /**
     * Only selected fields are exposed, to hide task related implementation details.
     * If relocation occurred, the Get Task API already merged timing so the result reflects the full duration.
     */
    @Override
    public XContentBuilder toXContent(final XContentBuilder builder, final Params params) throws IOException {
        builder.startObject();
        ReindexTaskXContent.taskResultToXContent(builder, params, taskResult);
        builder.endObject();
        return builder;
    }

    @Override
    public String toString() {
        return "GetReindexResponse{result=" + taskResult + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetReindexResponse that = (GetReindexResponse) o;
        return Objects.equals(taskResult, that.taskResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskResult);
    }
}
