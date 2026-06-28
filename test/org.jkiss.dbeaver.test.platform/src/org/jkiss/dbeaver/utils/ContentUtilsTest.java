/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2026 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.utils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.runtime.DBRBlockingObject;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContentUtilsTest {

    private static final int COPY_BUFFER_SIZE = 10000;

    @Test
    public void copyBinaryStreamCopiesContentAndReportsProgress() throws Exception {
        byte[] content = "database bytes".getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        RecordingProgressMonitor monitor = new RecordingProgressMonitor();

        ContentUtils.copyStreams(new ByteArrayInputStream(content), content.length, output, monitor);

        Assertions.assertArrayEquals(content, output.toByteArray());
        Assertions.assertEquals("Copy binary content", monitor.taskName);
        Assertions.assertEquals(content.length, monitor.totalWork);
        Assertions.assertEquals(1, monitor.doneCount);
        Assertions.assertEquals(1, monitor.subTasks.size());
    }

    @Test
    public void copyCharacterStreamCopiesContentAndReportsProgress() throws Exception {
        String content = "select *\nfrom sample";
        StringWriter output = new StringWriter();
        RecordingProgressMonitor monitor = new RecordingProgressMonitor();

        ContentUtils.copyStreams(new StringReader(content), content.length(), output, monitor);

        Assertions.assertEquals(content, output.toString());
        Assertions.assertEquals("Copy character content", monitor.taskName);
        Assertions.assertEquals(content.length(), monitor.totalWork);
        Assertions.assertEquals(1, monitor.doneCount);
        Assertions.assertTrue(monitor.subTasks.isEmpty());
    }

    @Test
    public void copyBinaryStreamStopsWhenMonitorIsCanceled() throws Exception {
        byte[] content = new byte[COPY_BUFFER_SIZE * 2];
        Arrays.fill(content, (byte) 1);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        RecordingProgressMonitor monitor = new RecordingProgressMonitor();
        monitor.cancelAfterFirstWork = true;

        ContentUtils.copyStreams(new ByteArrayInputStream(content), content.length, output, monitor);

        Assertions.assertEquals(COPY_BUFFER_SIZE, output.size());
        Assertions.assertEquals(1, monitor.workedCalls);
        Assertions.assertEquals(1, monitor.doneCount);
    }

    private static class RecordingProgressMonitor implements DBRProgressMonitor {
        private final IProgressMonitor nestedMonitor = new NullProgressMonitor();
        private final List<String> subTasks = new ArrayList<>();
        private String taskName;
        private int totalWork;
        private int doneCount;
        private int workedCalls;
        private boolean canceled;
        private boolean cancelAfterFirstWork;

        @NotNull
        @Override
        public IProgressMonitor getNestedMonitor() {
            return nestedMonitor;
        }

        @Override
        public void beginTask(@NotNull String name, int totalWork) {
            this.taskName = name;
            this.totalWork = totalWork;
        }

        @Override
        public void done() {
            doneCount++;
        }

        @Override
        public void subTask(@NotNull String name) {
            subTasks.add(name);
        }

        @Override
        public void worked(int work) {
            workedCalls++;
            if (cancelAfterFirstWork) {
                canceled = true;
            }
        }

        @Override
        public boolean isCanceled() {
            return canceled;
        }

        @Override
        public void startBlock(@NotNull DBRBlockingObject object, @Nullable String taskName) {
            // no-op
        }

        @Override
        public void endBlock() {
            // no-op
        }

        @Nullable
        @Override
        public List<DBRBlockingObject> getActiveBlocks() {
            return null;
        }
    }
}
