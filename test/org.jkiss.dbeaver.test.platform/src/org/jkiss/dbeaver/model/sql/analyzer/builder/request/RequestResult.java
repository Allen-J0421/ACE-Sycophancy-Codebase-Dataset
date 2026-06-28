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
package org.jkiss.dbeaver.model.sql.analyzer.builder.request;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.runtime.VoidProgressMonitor;
import org.jkiss.dbeaver.model.sql.completion.SQLCompletionAnalyzerSupport;
import org.jkiss.dbeaver.model.sql.SQLSyntaxManager;
import org.jkiss.dbeaver.model.sql.completion.SQLCompletionProposalBase;
import org.jkiss.dbeaver.model.sql.completion.SQLCompletionRequest;
import org.jkiss.dbeaver.model.sql.completion.SQLCompletionRequestFactory;
import org.jkiss.dbeaver.model.sql.completion.SQLCompletionTestContext;
import org.jkiss.dbeaver.model.sql.parser.SQLRuleManager;
import org.jkiss.dbeaver.model.sql.semantics.completion.SQLQueryCompletionContextProvider;
import org.jkiss.dbeaver.model.sql.semantics.completion.SQLQueryCompletionProposal;
import org.jkiss.utils.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestResult {
    private final DBPDataSource dataSource;

    public RequestResult(@NotNull DBPDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @NotNull
    public List<SQLCompletionProposalBase> request(@NotNull String sql) throws DBException {
        return request(sql, true);
    }

    @NotNull
    public List<SQLCompletionProposalBase> request(@NotNull String sql, boolean simpleMode) throws DBException {
        final SQLCompletionRequest request = prepareCompletionRequest(sql, simpleMode);
        return SQLCompletionAnalyzerSupport.collectLegacyProposals(new VoidProgressMonitor(), request, false);
    }

    /**
     * Returns the list of proposals that semantic autocompletion engine returns for the provided text in simple mode
     */
    public Set<String> requestNewStrings(@NotNull String sql) throws DBException {
        return this.requestNewStrings(sql, true);
    }

    /**
     * Returns the list of proposals that semantic autocompletion engine returns for the provided text
     */
    public Set<String> requestNewStrings(@NotNull String sql, boolean simpleMode) throws DBException {
        var r = this.requestNewInternal(sql, simpleMode);
        return r.getSecond().stream().map(p -> {
            try {
                if (p.getReplacementLength() > 0) {
                    String originalString = r.getFirst().getDocument().get(p.getReplacementOffset(), p.getReplacementLength());
                    String replacementFragment = p.getReplacementString().substring(0, originalString.length());
                    if (originalString.equals(replacementFragment) && replacementFragment.endsWith(".")) {
                        return p.getReplacementString().substring(replacementFragment.length());
                    } else {
                        return p.getReplacementString();
                    }
                } else {
                    return p.getReplacementString();
                }
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
    }

    @NotNull
    private Pair<SQLCompletionRequest, List<? extends SQLQueryCompletionProposal>> requestNewInternal(
        @NotNull String sql,
        boolean simpleMode
    ) throws DBException {
        final SQLCompletionRequest request = prepareCompletionRequest(sql, simpleMode);
        final DBRProgressMonitor monitor = new VoidProgressMonitor();
        try {
            return Pair.of(
                request,
                SQLCompletionAnalyzerSupport.collectSemanticProposals(
                    monitor,
                    progressMonitor -> SQLQueryCompletionContextProvider.prepareCompletionContext(progressMonitor, request),
                    request,
                    request::getDocumentOffset
                )
            );
        } catch (InvocationTargetException | InterruptedException e) {
            throw new DBException("Error while preparing completion proposals", e);
        }
    }

    @NotNull
    private SQLCompletionRequest prepareCompletionRequest(@NotNull String sql, boolean simpleMode) {
        final DBCExecutionContext executionContext = mock(DBCExecutionContext.class);
        when(executionContext.getDataSource()).thenReturn(dataSource);

        final SQLSyntaxManager syntaxManager = new SQLSyntaxManager();
        syntaxManager.init(dataSource.getSQLDialect(), dataSource.getContainer().getPreferenceStore());

        final SQLRuleManager ruleManager = new SQLRuleManager(syntaxManager);
        ruleManager.loadRules(dataSource, false);

        final Pair<String, Integer> cursor = getCursorFromQuery(sql);

        final Document document = new Document();
        document.set(cursor.getFirst());

        final SQLCompletionTestContext context = new SQLCompletionTestContext(
            dataSource,
            executionContext,
            syntaxManager,
            ruleManager,
            dataSource.getContainer().getPreferenceStore(),
            true
        );

        return SQLCompletionRequestFactory.create(
            context,
            document,
            cursor.getSecond(),
            simpleMode
        );
    }

    @NotNull
    private static Pair<String, Integer> getCursorFromQuery(@NotNull String sql) {
        final int cursor = sql.indexOf('|');
        if (cursor < 0) {
            throw new IllegalArgumentException("Can't locate cursor in query");
        }
        return new Pair<>(sql.substring(0, cursor) + sql.substring(cursor + 1), cursor);
    }
}
