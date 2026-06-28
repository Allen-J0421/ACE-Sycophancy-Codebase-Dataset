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
package org.jkiss.dbeaver.model.sql.semantics;

import org.antlr.v4.runtime.misc.Interval;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.stm.STMTreeNode;
import org.jkiss.junit.DBeaverUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class SQLQueryLexicalScopeTest extends DBeaverUnitTest {

    @Test
    public void getIntervalShouldIncludeRegisteredItemsAndSyntaxNodes() {
        SQLQueryLexicalScope scope = new SQLQueryLexicalScope();
        scope.registerItem(new TestItem(syntaxNode(10, 12), SQLQuerySymbolClass.COLUMN));
        scope.registerSyntaxNode(syntaxNode(2, 4));

        Interval interval = scope.getInterval();

        assertEquals(2, interval.a);
        assertEquals(15, interval.b);
    }

    @Test
    public void findItemShouldReturnTheContainingItemWithSmallestStart() {
        SQLQueryLexicalScope scope = new SQLQueryLexicalScope();
        TestItem outer = new TestItem(syntaxNode(2, 20), SQLQuerySymbolClass.COLUMN);
        TestItem inner = new TestItem(syntaxNode(8, 12), SQLQuerySymbolClass.COLUMN);
        scope.registerItem(outer);
        scope.registerItem(inner);

        assertSame(inner, scope.findItem(10));
    }

    @Test
    public void findNearestItemShouldPreferTheTightestMatchingItem() {
        SQLQueryLexicalScope scope = new SQLQueryLexicalScope();
        TestItem wide = new TestItem(syntaxNode(2, 20), SQLQuerySymbolClass.COLUMN);
        TestItem narrow = new TestItem(syntaxNode(8, 12), SQLQuerySymbolClass.COLUMN);
        scope.registerItem(wide);
        scope.registerItem(narrow);

        assertSame(narrow, scope.findNearestItem(10));
    }

    @NotNull
    private static STMTreeNode syntaxNode(int start, int end) {
        STMTreeNode node = Mockito.mock(STMTreeNode.class);
        Mockito.when(node.getRealInterval()).thenReturn(Interval.of(start, end));
        return node;
    }

    private static final class TestItem extends SQLQueryLexicalScopeItem {
        private final SQLQuerySymbolClass symbolClass;

        private TestItem(@NotNull STMTreeNode syntaxNode, @NotNull SQLQuerySymbolClass symbolClass) {
            super(syntaxNode);
            this.symbolClass = symbolClass;
        }

        @Nullable
        @Override
        public SQLQuerySymbolClass getSymbolClass() {
            return this.symbolClass;
        }
    }
}
