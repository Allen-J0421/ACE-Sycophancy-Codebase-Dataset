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

import org.eclipse.jface.text.Document;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.DBPImage;
import org.jkiss.dbeaver.model.DBPKeywordType;
import org.jkiss.dbeaver.model.DBPNamedObject;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;
import org.jkiss.dbeaver.model.impl.sql.BasicSQLDialect;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.model.runtime.VoidProgressMonitor;
import org.jkiss.dbeaver.model.sql.SQLCompletionMode;
import org.jkiss.dbeaver.model.sql.SQLModelPreferences;
import org.jkiss.dbeaver.model.sql.SQLSyntaxManager;
import org.jkiss.dbeaver.model.sql.parser.SQLRuleManager;
import org.jkiss.junit.DBeaverUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SQLCompletionProposalProviderTest extends DBeaverUnitTest {

    @Test
    public void legacyModeShouldProduceLegacyCompletionProposals()
        throws DBException, InvocationTargetException, InterruptedException {
        TestCompletionContext context = createCompletionContext();
        DBPPreferenceStore preferenceStore = mockPreferenceStore(SQLCompletionMode.DEFAULT, false, false);

        String sql = "SEL|";
        int offset = sql.indexOf('|');
        Document document = new Document(sql.replace("|", ""));
        SQLCompletionRequest request = SQLCompletionRequestFactory.create(context, document, offset, false);

        List<CompletionProposalBase> proposals = SQLCompletionProposalProvider.collectProposals(
            new VoidProgressMonitor(),
            preferenceStore,
            request,
            false
        );

        Assertions.assertFalse(proposals.isEmpty());
        Assertions.assertEquals("SELECT", proposals.get(0).getReplacementString());
    }

    private static DBPPreferenceStore mockPreferenceStore(
        SQLCompletionMode mode,
        boolean advancedHighlightingEnabled,
        boolean metadataReadEnabled
    ) {
        DBPPreferenceStore store = mock(DBPPreferenceStore.class);
        when(store.getString(SQLModelPreferences.AUTOCOMPLETION_MODE)).thenReturn(mode.getPreferenceValue());
        when(store.getBoolean(SQLModelPreferences.ADVANCED_HIGHLIGHTING_ENABLE)).thenReturn(advancedHighlightingEnabled);
        when(store.getBoolean(SQLModelPreferences.READ_METADATA_FOR_SEMANTIC_ANALYSIS)).thenReturn(metadataReadEnabled);
        return store;
    }

    private static TestCompletionContext createCompletionContext() {
        DBPPreferenceStore preferenceStore = mock(DBPPreferenceStore.class);

        DBPDataSourceContainer dataSourceContainer = mock(DBPDataSourceContainer.class);
        when(dataSourceContainer.getPreferenceStore()).thenReturn(preferenceStore);

        DBPDataSource dataSource = mock(DBPDataSource.class);
        when(dataSource.getSQLDialect()).thenReturn(BasicSQLDialect.INSTANCE);
        when(dataSource.getContainer()).thenReturn(dataSourceContainer);

        DBCExecutionContext executionContext = mock(DBCExecutionContext.class);
        when(executionContext.getDataSource()).thenReturn(dataSource);

        SQLSyntaxManager syntaxManager = new SQLSyntaxManager();
        syntaxManager.init(BasicSQLDialect.INSTANCE, preferenceStore);

        SQLRuleManager ruleManager = new SQLRuleManager(syntaxManager);
        ruleManager.loadRules(dataSource, false);

        return new TestCompletionContext(dataSource, executionContext, syntaxManager, ruleManager);
    }

    private record TestCompletionContext(
        DBPDataSource dataSource,
        DBCExecutionContext executionContext,
        SQLSyntaxManager syntaxManager,
        SQLRuleManager ruleManager
    ) implements SQLCompletionContext {

        @Override
        public DBPDataSource getDataSource() {
            return dataSource;
        }

        @Override
        public DBCExecutionContext getExecutionContext() {
            return executionContext;
        }

        @Override
        public SQLSyntaxManager getSyntaxManager() {
            return syntaxManager;
        }

        @Override
        public SQLRuleManager getRuleManager() {
            return ruleManager;
        }

        @Override
        public boolean isUseFQNames() {
            return false;
        }

        @Override
        public boolean isReplaceWords() {
            return false;
        }

        @Override
        public boolean isShowServerHelp() {
            return false;
        }

        @Override
        public boolean isUseShortNames() {
            return false;
        }

        @Override
        public int getInsertCase() {
            return PROPOSAL_CASE_DEFAULT;
        }

        @Override
        public boolean isSearchProcedures() {
            return false;
        }

        @Override
        public boolean isSearchInsideNames() {
            return false;
        }

        @Override
        public boolean isSortAlphabetically() {
            return false;
        }

        @Override
        public boolean isSearchGlobally() {
            return false;
        }

        @Override
        public boolean isHideDuplicates() {
            return false;
        }

        @Override
        public boolean isShowValues() {
            return false;
        }

        @Override
        public boolean isForceQualifiedColumnNames() {
            return false;
        }

        @Override
        public SQLCompletionProposalBase createProposal(
            @NotNull SQLCompletionRequest request,
            @NotNull String displayString,
            @NotNull String replacementString,
            int cursorPosition,
            @Nullable DBPImage image,
            @NotNull DBPKeywordType proposalType,
            @Nullable String description,
            @Nullable DBPNamedObject object,
            @NotNull Map<String, Object> params
        ) {
            return new SQLCompletionProposalBase(
                request,
                displayString,
                replacementString,
                cursorPosition,
                image,
                proposalType,
                description,
                object,
                params
            );
        }
    }
}
