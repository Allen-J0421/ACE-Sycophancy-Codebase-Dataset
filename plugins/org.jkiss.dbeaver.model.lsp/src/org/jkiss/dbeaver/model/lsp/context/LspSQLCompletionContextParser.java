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
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.runtime.VoidProgressMonitor;
import org.jkiss.dbeaver.model.sql.SQLCompletionEngineDecider;
import org.jkiss.dbeaver.model.sql.SQLScriptElement;
import org.jkiss.dbeaver.model.sql.completion.CompletionProposalBase;
import org.jkiss.dbeaver.model.sql.completion.SQLCompletionAnalyzer;
import org.jkiss.dbeaver.model.sql.completion.SQLCompletionContext;
import org.jkiss.dbeaver.model.sql.completion.SQLCompletionRequest;
import org.jkiss.dbeaver.model.sql.parser.SQLParserContext;
import org.jkiss.dbeaver.model.sql.parser.SQLScriptParser;
import org.jkiss.dbeaver.model.sql.semantics.completion.SQLQueryCompletionAnalyzer;
import org.jkiss.dbeaver.model.sql.semantics.completion.SQLQueryCompletionContextProvider;
import org.jkiss.dbeaver.runtime.DBWorkbench;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
        SQLParserContext parserContext = new SQLParserContext(
            document.getDataSource(),
            completionContext.getSyntaxManager(),
            completionContext.getRuleManager(),
            doc
        );
        SQLScriptElement activeQuery = SQLScriptParser.extractActiveQuery(parserContext, offset, 0);

        SQLCompletionRequest request = new SQLCompletionRequest(
            completionContext,
            doc,
            offset,
            activeQuery,
            false
        );

        List<CompletionProposalBase> proposals = collectCompletionProposals(document, offset, request);

        return proposals.stream()
            .limit(MAX_RESULTS)
            .map(p -> new CompletionItem(p.getReplacementString()))
            .toList();
    }

    @NotNull
    private static List<CompletionProposalBase> collectCompletionProposals(
        @NotNull ContextAwareDocument document,
        int offset,
        @NotNull SQLCompletionRequest request
    ) throws InterruptedException, InvocationTargetException, DBException {
        List<CompletionProposalBase> proposals = new ArrayList<>();
        SQLCompletionEngineDecider.Decision engineDecision = resolveCompletionEngines(document.getDataSource());
        if (engineDecision.semanticEnabled()) {
            collectSemanticCompletionProposals(document, offset, request, proposals);
        }
        if (engineDecision.legacyEnabled() && (!engineDecision.legacyRequiresWordPart() || request.getWordPart() != null)) {
            collectLegacyCompletionProposals(request, proposals);
        }
        return proposals;
    }

    @NotNull
    private static SQLCompletionEngineDecider.Decision resolveCompletionEngines(@NotNull DBPDataSource dataSource) {
        return SQLCompletionEngineDecider.resolve(
            DBWorkbench.getPlatform().getPreferenceStore(),
            dataSource
        );
    }

    private static void collectSemanticCompletionProposals(
        @NotNull ContextAwareDocument document,
        int offset,
        @NotNull SQLCompletionRequest request,
        @NotNull List<CompletionProposalBase> proposals
    ) throws InterruptedException, InvocationTargetException, DBException {
        SQLQueryCompletionAnalyzer analyzer = new SQLQueryCompletionAnalyzer(
            monitor -> SQLQueryCompletionContextProvider.prepareCompletionContext(
                monitor,
                request,
                document.getText(),
                offset
            ),
            request,
            request::getDocumentOffset
        );
        analyzer.run(new VoidProgressMonitor());
        proposals.addAll(analyzer.getResult());
    }

    private static void collectLegacyCompletionProposals(
        @NotNull SQLCompletionRequest request,
        @NotNull List<CompletionProposalBase> proposals
    ) {
        SQLCompletionAnalyzer analyzer = new SQLCompletionAnalyzer(request);
        analyzer.setCheckNavigatorNodes(false);
        analyzer.runAnalyzer(new VoidProgressMonitor());
        proposals.addAll(analyzer.getProposals());
    }
}
