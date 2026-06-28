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
package org.jkiss.dbeaver.model.sql.semantics.context;

import org.antlr.v4.runtime.misc.Interval;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.sql.SQLDialect;
import org.jkiss.dbeaver.model.sql.semantics.SQLQueryComplexName;
import org.jkiss.dbeaver.model.sql.semantics.SQLQueryRecognitionContext;
import org.jkiss.dbeaver.model.sql.semantics.SQLQuerySymbolEntry;
import org.jkiss.dbeaver.model.sql.semantics.model.SQLQueryNodeModelVisitor;
import org.jkiss.dbeaver.model.sql.semantics.model.select.SQLQueryRowsSourceModel;
import org.jkiss.dbeaver.model.stm.STMTreeNode;
import org.jkiss.junit.DBeaverUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SQLQueryRowsSourceContextTest extends DBeaverUnitTest {

    @Test
    public void appendSourceShouldRegisterVariantsAndExposeChildKnownSources() {
        SQLQueryRowsSourceContext parent = new SQLQueryRowsSourceContext(
            new SQLQueryConnectionDummyContext(Mockito.mock(SQLDialect.class), Set.of(), Set.of())
        );

        TestRowsSourceModel source = new TestRowsSourceModel(syntaxNode());
        SQLQueryComplexName qualifiedName = complexName("schema", "table");

        SQLQueryRowsSourceContext child = parent.appendSource(source, qualifiedName, null);

        assertSame(source, child.findReferencedSourceExact(qualifiedName));
        assertSame(source, child.findReferencedSourceExact(complexName("table")));

        SQLQueryRowsSourceContext.SourceResolutionInfo trimmedLookup = child.findReferencedSource(complexName("schema", "table", "column"));
        assertNotNull(trimmedLookup);
        assertEquals(qualifiedName, trimmedLookup.key());
        assertSame(source, trimmedLookup.target().source);

        SQLQuerySourcesInfoCollection directKnownSources = parent.getKnownSources(false);
        SQLQuerySourcesInfoCollection subtreeKnownSources = parent.getKnownSources(true);
        assertTrue(directKnownSources.getResolutionResults().isEmpty());
        assertTrue(subtreeKnownSources.getResolutionResults().containsKey(source));
        assertSame(source, subtreeKnownSources.getResolutionResults().get(source).source);
    }

    @NotNull
    private static STMTreeNode syntaxNode() {
        STMTreeNode node = Mockito.mock(STMTreeNode.class);
        Mockito.when(node.getRealInterval()).thenReturn(Interval.of(0, 0));
        return node;
    }

    @NotNull
    private static SQLQueryComplexName complexName(@NotNull String... parts) {
        STMTreeNode node = syntaxNode();
        List<SQLQuerySymbolEntry> entries = java.util.Arrays.stream(parts)
            .map(part -> new SQLQuerySymbolEntry(node, part, part, null))
            .toList();
        return new SQLQueryComplexName(node, entries, 0, null);
    }

    private static final class TestRowsSourceModel extends SQLQueryRowsSourceModel {
        private TestRowsSourceModel(@NotNull STMTreeNode syntaxNode) {
            super(syntaxNode);
        }

        @Override
        protected SQLQueryRowsSourceContext resolveRowSourcesImpl(
            @NotNull SQLQueryRowsSourceContext context,
            @NotNull SQLQueryRecognitionContext statistics
        ) {
            return context;
        }

        @Override
        protected SQLQueryRowsDataContext resolveRowDataImpl(
            @NotNull SQLQueryRowsDataContext context,
            @NotNull SQLQueryRecognitionContext statistics
        ) {
            return context;
        }

        @Override
        protected <R, T> R applyImpl(@NotNull SQLQueryNodeModelVisitor<T, R> visitor, @Nullable T arg) {
            return null;
        }
    }
}
