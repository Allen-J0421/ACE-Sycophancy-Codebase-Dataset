/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2025 DBeaver Corp and others
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
package org.jkiss.dbeaver.model.lsp.context;

import org.eclipse.jface.text.Document;
import org.eclipse.lsp4j.CompletionItem;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.runtime.VoidProgressMonitor;
import org.jkiss.dbeaver.model.sql.completion.CompletionProposalBase;
import org.jkiss.dbeaver.model.sql.completion.SQLCompletionContext;
import org.jkiss.dbeaver.model.sql.completion.SQLCompletionProposalProvider;
import org.jkiss.dbeaver.model.sql.completion.SQLCompletionRequestFactory;
import org.jkiss.dbeaver.runtime.DBWorkbench;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class LspSQLCompletionContextParser {
    private static final int MAX_RESULTS = 200;

    @NotNull
    public static List<CompletionItem> createCompletionsList(
        @NotNull ContextAwareDocument document,
        int offset,
        @NotNull SQLCompletionContext completionContext
    ) throws InterruptedException, InvocationTargetException, DBException {
        Document doc = new Document(document.getText());
        var request = SQLCompletionRequestFactory.create(completionContext, doc, offset, false);
        List<CompletionProposalBase> proposals = SQLCompletionProposalProvider.collectProposals(
            new VoidProgressMonitor(),
            DBWorkbench.getPlatform().getPreferenceStore(),
            request,
            document.getText(),
            offset,
            false
        );

        return proposals.stream()
            .limit(MAX_RESULTS)
            .map(p -> new CompletionItem(p.getReplacementString()))
            .toList();
    }
}
