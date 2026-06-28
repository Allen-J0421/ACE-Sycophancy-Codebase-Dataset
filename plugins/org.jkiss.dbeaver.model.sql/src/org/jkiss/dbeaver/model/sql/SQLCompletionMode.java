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
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.utils.CommonUtils;

public enum SQLCompletionMode {
    DEFAULT(true, false),
    NEW(false, true),
    COMBINED(true, true);

    private final boolean useLegacyAnalyzer;
    private final boolean useSemanticAnalyzer;

    SQLCompletionMode(boolean useLegacyAnalyzer, boolean useSemanticAnalyzer) {
        this.useLegacyAnalyzer = useLegacyAnalyzer;
        this.useSemanticAnalyzer = useSemanticAnalyzer;
    }

    public boolean usesLegacyAnalyzer() {
        return useLegacyAnalyzer;
    }

    public boolean usesSemanticAnalyzer() {
        return useSemanticAnalyzer;
    }

    @NotNull
    public String getPreferenceValue() {
        return this.name();
    }

    @NotNull
    public static SQLCompletionMode valueByName(String name) {
        return CommonUtils.valueOf(SQLCompletionMode.class, name, DEFAULT);
    }

    @NotNull
    public static SQLCompletionMode fromPreferences(@NotNull DBPPreferenceStore preferenceStore) {
        return valueByName(preferenceStore.getString(SQLModelPreferences.AUTOCOMPLETION_MODE));
    }
}
