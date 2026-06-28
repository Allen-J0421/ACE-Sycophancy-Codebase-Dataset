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
package org.jkiss.dbeaver.model.sql;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.impl.sql.BasicSQLDialect;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.junit.DBeaverUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SQLCompletionEngineDeciderTest extends DBeaverUnitTest {

    @Test
    public void newModeShouldFallbackToLegacyWhenSemanticAnalyzerUnavailable() {
        DBPPreferenceStore store = mockStore(SQLCompletionMode.NEW, true, false);
        DBPDataSource dataSource = mockDataSource();

        SQLCompletionEngineDecider.Decision decision = SQLCompletionEngineDecider.resolve(store, dataSource);

        Assertions.assertTrue(decision.legacyEnabled());
        Assertions.assertFalse(decision.semanticEnabled());
        Assertions.assertFalse(decision.legacyRequiresWordPart());
    }

    @Test
    public void combinedModeShouldEnableBothAnalyzersWhenSemanticAnalyzerAvailable() {
        DBPPreferenceStore store = mockStore(SQLCompletionMode.COMBINED, true, true);
        DBPDataSource dataSource = mockDataSource();

        SQLCompletionEngineDecider.Decision decision = SQLCompletionEngineDecider.resolve(store, dataSource);

        Assertions.assertTrue(decision.legacyEnabled());
        Assertions.assertTrue(decision.semanticEnabled());
        Assertions.assertTrue(decision.legacyRequiresWordPart());
    }

    @Test
    public void defaultModeShouldUseLegacyAnalyzerOnly() {
        DBPPreferenceStore store = mockStore(SQLCompletionMode.DEFAULT, true, true);
        DBPDataSource dataSource = mockDataSource();

        SQLCompletionEngineDecider.Decision decision = SQLCompletionEngineDecider.resolve(store, dataSource);

        Assertions.assertTrue(decision.legacyEnabled());
        Assertions.assertFalse(decision.semanticEnabled());
        Assertions.assertFalse(decision.legacyRequiresWordPart());
    }

    @NotNull
    private static DBPPreferenceStore mockStore(
        @NotNull SQLCompletionMode mode,
        boolean advancedHighlightingEnabled,
        boolean metadataReadEnabled
    ) {
        DBPPreferenceStore store = mock(DBPPreferenceStore.class);
        when(store.getString(SQLModelPreferences.AUTOCOMPLETION_MODE)).thenReturn(mode.getPreferenceValue());
        when(store.getBoolean(SQLModelPreferences.ADVANCED_HIGHLIGHTING_ENABLE)).thenReturn(advancedHighlightingEnabled);
        when(store.getBoolean(SQLModelPreferences.READ_METADATA_FOR_SEMANTIC_ANALYSIS)).thenReturn(metadataReadEnabled);
        return store;
    }

    @NotNull
    private static DBPDataSource mockDataSource() {
        DBPDataSource dataSource = mock(DBPDataSource.class);
        when(dataSource.getSQLDialect()).thenReturn(BasicSQLDialect.INSTANCE);
        return dataSource;
    }
}
