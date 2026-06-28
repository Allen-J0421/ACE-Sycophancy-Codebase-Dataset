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

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sql.SQLCompletionEngineDecider;
import org.jkiss.dbeaver.model.sql.semantics.completion.SQLQueryCompletionContextProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public final class SQLCompletionProposalProvider {

    private SQLCompletionProposalProvider() {
    }

    @NotNull
    public static List<CompletionProposalBase> collectProposals(
        @NotNull DBRProgressMonitor monitor,
        @NotNull SQLCompletionRequest request,
        boolean checkNavigatorNodes
    ) throws DBException, InvocationTargetException, InterruptedException {
        List<CompletionProposalBase> proposals = new ArrayList<>();
        SQLCompletionEngineDecider.Decision engineDecision = SQLCompletionEngineDecider.resolve(
            request.getContext().getPreferenceStore(),
            request.getContext().getDataSource()
        );
        if (engineDecision.semanticEnabled()) {
            proposals.addAll(SQLCompletionAnalyzerSupport.collectSemanticProposals(
                monitor,
                progressMonitor -> SQLQueryCompletionContextProvider.prepareCompletionContext(progressMonitor, request),
                request,
                request::getDocumentOffset
            ));
        }
        if (engineDecision.legacyEnabled() && (!engineDecision.legacyRequiresWordPart() || request.getWordPart() != null)) {
            proposals.addAll(SQLCompletionAnalyzerSupport.collectLegacyProposals(
                monitor,
                request,
                checkNavigatorNodes
            ));
        }
        return proposals;
    }
}
