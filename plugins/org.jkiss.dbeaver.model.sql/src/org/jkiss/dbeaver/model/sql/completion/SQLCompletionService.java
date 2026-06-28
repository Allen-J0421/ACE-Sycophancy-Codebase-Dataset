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
package org.jkiss.dbeaver.model.sql.completion;

import org.eclipse.jface.text.IDocument;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sql.SQLScriptElement;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class SQLCompletionService {

    private SQLCompletionService() {
    }

    @NotNull
    public static SQLCompletionRequest prepareRequest(
        @NotNull SQLCompletionContext context,
        @NotNull IDocument document,
        int documentOffset,
        boolean simpleMode
    ) {
        return SQLCompletionRequestFactory.create(context, document, documentOffset, simpleMode);
    }

    @NotNull
    public static SQLCompletionRequest prepareRequest(
        @NotNull SQLCompletionContext context,
        @NotNull IDocument document,
        int documentOffset,
        @Nullable SQLScriptElement activeQuery,
        boolean simpleMode
    ) {
        return SQLCompletionRequestFactory.create(context, document, documentOffset, activeQuery, simpleMode);
    }

    @NotNull
    public static List<CompletionProposalBase> collectProposals(
        @NotNull DBRProgressMonitor monitor,
        @NotNull SQLCompletionContext context,
        @NotNull IDocument document,
        int documentOffset,
        boolean simpleMode,
        boolean checkNavigatorNodes
    ) throws DBException, InvocationTargetException, InterruptedException {
        return collectProposals(
            monitor,
            prepareRequest(context, document, documentOffset, simpleMode),
            checkNavigatorNodes
        );
    }

    @NotNull
    public static List<CompletionProposalBase> collectProposals(
        @NotNull DBRProgressMonitor monitor,
        @NotNull SQLCompletionRequest request,
        boolean checkNavigatorNodes
    ) throws DBException, InvocationTargetException, InterruptedException {
        return SQLCompletionProposalProvider.collectProposals(monitor, request, checkNavigatorNodes);
    }
}
