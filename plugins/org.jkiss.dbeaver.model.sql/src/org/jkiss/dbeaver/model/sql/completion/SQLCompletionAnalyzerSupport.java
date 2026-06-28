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
import org.jkiss.dbeaver.model.sql.semantics.completion.SQLQueryCompletionAnalyzer;
import org.jkiss.dbeaver.model.sql.semantics.completion.SQLQueryCompletionContext;
import org.jkiss.dbeaver.model.sql.semantics.completion.SQLQueryCompletionProposal;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class SQLCompletionAnalyzerSupport {

    private SQLCompletionAnalyzerSupport() {
    }

    @NotNull
    public static SQLCompletionAnalyzer createLegacyAnalyzer(
        @NotNull SQLCompletionRequest request,
        boolean checkNavigatorNodes
    ) {
        SQLCompletionAnalyzer analyzer = new SQLCompletionAnalyzer(request);
        analyzer.setCheckNavigatorNodes(checkNavigatorNodes);
        return analyzer;
    }

    @NotNull
    public static List<SQLCompletionProposalBase> collectLegacyProposals(
        @NotNull DBRProgressMonitor monitor,
        @NotNull SQLCompletionRequest request,
        boolean checkNavigatorNodes
    ) throws DBException {
        SQLCompletionAnalyzer analyzer = createLegacyAnalyzer(request, checkNavigatorNodes);
        analyzer.runAnalyzer(monitor);
        return analyzer.getProposals();
    }

    @NotNull
    public static SQLQueryCompletionAnalyzer createSemanticAnalyzer(
        @NotNull Function<DBRProgressMonitor, SQLQueryCompletionContext> completionContextSupplier,
        @NotNull SQLCompletionRequest request,
        @NotNull Supplier<Integer> currentCompletionOffsetSupplier
    ) {
        return new SQLQueryCompletionAnalyzer(
            completionContextSupplier,
            request,
            currentCompletionOffsetSupplier
        );
    }

    @NotNull
    public static List<? extends SQLQueryCompletionProposal> collectSemanticProposals(
        @NotNull DBRProgressMonitor monitor,
        @NotNull Function<DBRProgressMonitor, SQLQueryCompletionContext> completionContextSupplier,
        @NotNull SQLCompletionRequest request,
        @NotNull Supplier<Integer> currentCompletionOffsetSupplier
    ) throws InvocationTargetException, InterruptedException {
        SQLQueryCompletionAnalyzer analyzer = createSemanticAnalyzer(
            completionContextSupplier,
            request,
            currentCompletionOffsetSupplier
        );
        analyzer.run(monitor);
        return analyzer.getResult();
    }
}
