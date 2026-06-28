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
package org.jkiss.dbeaver.model.sql.semantics.model;

import org.antlr.v4.runtime.misc.Interval;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.sql.semantics.SQLQueryLexicalScope;
import org.jkiss.dbeaver.model.sql.semantics.SQLQueryLexicalScopeItem;
import org.jkiss.dbeaver.model.sql.semantics.SQLQuerySymbolClass;
import org.jkiss.dbeaver.model.sql.semantics.SQLQuerySymbolOrigin;
import org.jkiss.dbeaver.model.stm.STMTreeNode;
import org.jkiss.junit.DBeaverUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SQLQueryModelContextTest extends DBeaverUnitTest {

    @Test
    public void findNodeContainingShouldReturnDeepestNodeAndPreferTailOriginFromIt() {
        STMTreeNode rootSyntax = syntaxNode(0, 30);
        STMTreeNode contentSyntax = syntaxNode(0, 30);
        STMTreeNode outerSyntax = syntaxNode(2, 20);
        STMTreeNode innerSyntax = syntaxNode(12, 18);
        STMTreeNode itemSyntax = syntaxNode(13, 15);

        TestOrigin itemOrigin = new TestOrigin("item");
        TestOrigin outerTailOrigin = new TestOrigin("outer-tail");
        TestOrigin innerTailOrigin = new TestOrigin("inner-tail");

        TestLexicalScopeItem lexicalItem = new TestLexicalScopeItem(itemSyntax, SQLQuerySymbolClass.COLUMN);
        lexicalItem.setOrigin(itemOrigin);

        SQLQueryLexicalScope lexicalScope = new SQLQueryLexicalScope();
        lexicalScope.registerItem(lexicalItem);
        lexicalScope.setSymbolsOrigin(new TestOrigin("scope"));

        TestNode innerNode = new TestNode(innerSyntax);
        innerNode.registerLexicalScope(lexicalScope);
        innerNode.setTailOrigin(innerTailOrigin);

        TestNode outerNode = new TestNode(outerSyntax, innerNode);
        outerNode.setTailOrigin(outerTailOrigin);

        TestContent content = new TestContent(contentSyntax, outerNode);
        SQLQueryModel model = new SQLQueryModel(rootSyntax, content, Set.of(), List.of());

        assertSame(innerNode, model.findNodeContaining(14));

        SQLQueryModel.LexicalContextResolutionResult lexicalContext = model.findLexicalContext(14);
        assertSame(lexicalItem, lexicalContext.lexicalItem());
        assertSame(itemOrigin, lexicalContext.symbolsOrigin());
    }

    @Test
    public void findLexicalContextShouldFallbackToDeepestTailOriginAfterQueryEnd() {
        STMTreeNode rootSyntax = syntaxNode(0, 30);
        STMTreeNode contentSyntax = syntaxNode(0, 30);
        STMTreeNode outerSyntax = syntaxNode(2, 20);
        STMTreeNode innerSyntax = syntaxNode(12, 18);

        TestOrigin outerTailOrigin = new TestOrigin("outer-tail");
        TestOrigin innerTailOrigin = new TestOrigin("inner-tail");

        TestNode innerNode = new TestNode(innerSyntax);
        innerNode.setTailOrigin(innerTailOrigin);

        TestNode outerNode = new TestNode(outerSyntax, innerNode);
        outerNode.setTailOrigin(outerTailOrigin);

        TestContent content = new TestContent(contentSyntax, outerNode);
        SQLQueryModel model = new SQLQueryModel(rootSyntax, content, Set.of(), List.of());

        assertSame(innerNode, model.findNodeContaining(50));

        SQLQueryModel.LexicalContextResolutionResult lexicalContext = model.findLexicalContext(50);
        assertNull(lexicalContext.lexicalItem());
        assertSame(innerTailOrigin, lexicalContext.symbolsOrigin());
    }

    @NotNull
    private static STMTreeNode syntaxNode(int start, int end) {
        STMTreeNode node = Mockito.mock(STMTreeNode.class);
        Mockito.when(node.getRealInterval()).thenReturn(Interval.of(start, end));
        return node;
    }

    private static final class TestContent extends SQLCommandModel {
        private TestContent(@NotNull STMTreeNode syntaxNode, @NotNull SQLQueryNodeModel... children) {
            super(syntaxNode, "test");
            for (SQLQueryNodeModel child : children) {
                registerSubnode(child);
            }
        }
    }

    private static final class TestNode extends SQLQueryNodeModel {
        private TestNode(@NotNull STMTreeNode syntaxNode, @Nullable SQLQueryNodeModel... children) {
            super(syntaxNode.getRealInterval(), syntaxNode, children);
        }

        @Override
        protected <R, T> R applyImpl(@NotNull SQLQueryNodeModelVisitor<T, R> visitor, T arg) {
            return null;
        }
    }

    private static final class TestLexicalScopeItem extends SQLQueryLexicalScopeItem {
        private final SQLQuerySymbolClass symbolClass;

        private TestLexicalScopeItem(@NotNull STMTreeNode syntaxNode, @NotNull SQLQuerySymbolClass symbolClass) {
            super(syntaxNode);
            this.symbolClass = symbolClass;
        }

        @Nullable
        @Override
        public SQLQuerySymbolClass getSymbolClass() {
            return this.symbolClass;
        }
    }

    private static final class TestOrigin extends SQLQuerySymbolOrigin {
        private final String label;

        private TestOrigin(@NotNull String label) {
            this.label = label;
        }

        @Override
        public boolean isChained() {
            return false;
        }

        @Override
        public void apply(@NotNull Visitor visitor) {
            // no-op
        }

        @Override
        public String toString() {
            return this.label;
        }
    }
}
