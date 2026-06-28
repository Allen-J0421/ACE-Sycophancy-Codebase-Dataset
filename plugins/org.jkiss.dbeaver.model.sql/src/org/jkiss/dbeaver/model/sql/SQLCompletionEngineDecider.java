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
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.impl.sql.BasicSQLDialect;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;

public final class SQLCompletionEngineDecider {

    private SQLCompletionEngineDecider() {
    }

    @NotNull
    public static Decision resolve(@NotNull DBPPreferenceStore preferenceStore, @Nullable DBPDataSource dataSource) {
        SQLCompletionMode mode = SQLCompletionMode.fromPreferences(preferenceStore);
        boolean semanticEnabled = mode.usesSemanticAnalyzer()
            && preferenceStore.getBoolean(SQLModelPreferences.ADVANCED_HIGHLIGHTING_ENABLE)
            && preferenceStore.getBoolean(SQLModelPreferences.READ_METADATA_FOR_SEMANTIC_ANALYSIS)
            && dataSource != null
            && dataSource.getSQLDialect() instanceof BasicSQLDialect;
        boolean legacyEnabled = dataSource != null && (mode.usesLegacyAnalyzer() || !semanticEnabled);
        boolean legacyRequiresWordPart = semanticEnabled && mode.usesLegacyAnalyzer();
        return new Decision(legacyEnabled, semanticEnabled, legacyRequiresWordPart);
    }

    public record Decision(boolean legacyEnabled, boolean semanticEnabled, boolean legacyRequiresWordPart) {
    }
}
