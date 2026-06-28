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
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.DBPImage;
import org.jkiss.dbeaver.model.DBPKeywordType;
import org.jkiss.dbeaver.model.DBPNamedObject;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;
import org.jkiss.dbeaver.model.impl.sql.BasicSQLDialect;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.model.sql.SQLQuery;
import org.jkiss.dbeaver.model.sql.SQLScriptElement;
import org.jkiss.dbeaver.model.sql.SQLSyntaxManager;
import org.jkiss.dbeaver.model.sql.parser.SQLRuleManager;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.junit.DBeaverUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

public class SQLCompletionRequestFactoryTest extends DBeaverUnitTest {

    @Test
    public void createShouldResolveActiveQueryAtCursorPosition() {
        TestCompletionContext context = createCompletionContext();

        String sql = "SELECT 1;\nSELECT |2";
        int offset = sql.indexOf('|');
        Document document = new Document(sql.replace("|", ""));

        SQLCompletionRequest request = SQLCompletionRequestFactory.create(context, document, offset, false);

        Assertions.assertNotNull(request.getActiveQuery());
        Assertions.assertEquals("SELECT 2", request.getActiveQuery().getText());
        Assertions.assertEquals(document.get().indexOf("SELECT 2"), request.getActiveQuery().getOffset());
    }

    @Test
    public void createWithExplicitActiveQueryShouldPreserveProvidedElement() {
        TestCompletionContext context = createCompletionContext();
        Document document = new Document("SELECT 1");
        SQLScriptElement activeQuery = new SQLQuery(context.getDataSource(), "SELECT 1");

        SQLCompletionRequest request = SQLCompletionRequestFactory.create(context, document, 0, activeQuery, false);

        Assertions.assertSame(activeQuery, request.getActiveQuery());
    }

    private static TestCompletionContext createCompletionContext() {
        DBPPreferenceStore preferenceStore = DBWorkbench.getPlatform().getPreferenceStore();

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
