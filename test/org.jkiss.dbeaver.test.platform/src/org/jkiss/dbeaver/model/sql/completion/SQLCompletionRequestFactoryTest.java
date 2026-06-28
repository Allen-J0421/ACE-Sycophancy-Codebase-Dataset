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
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
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

public class SQLCompletionRequestFactoryTest extends DBeaverUnitTest {

    @Test
    public void createShouldResolveActiveQueryAtCursorPosition() {
        SQLCompletionTestContext context = createCompletionContext();

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
        SQLCompletionTestContext context = createCompletionContext();
        Document document = new Document("SELECT 1");
        SQLScriptElement activeQuery = new SQLQuery(context.getDataSource(), "SELECT 1");

        SQLCompletionRequest request = SQLCompletionRequestFactory.create(context, document, 0, activeQuery, false);

        Assertions.assertSame(activeQuery, request.getActiveQuery());
    }

    private static SQLCompletionTestContext createCompletionContext() {
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

        return new SQLCompletionTestContext(
            dataSource,
            executionContext,
            syntaxManager,
            ruleManager,
            preferenceStore
        );
    }
}
