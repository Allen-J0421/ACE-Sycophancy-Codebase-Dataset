/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2025 DBeaver Corp and others
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
package org.jkiss.dbeaver.model.sql.semantics.completion;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.sql.semantics.SQLQuerySymbol;
import org.jkiss.dbeaver.model.sql.semantics.SQLQuerySymbolClass;
import org.jkiss.dbeaver.model.sql.semantics.context.SQLQueryExprType;
import org.jkiss.dbeaver.model.sql.semantics.context.SQLQueryResultColumn;
import org.jkiss.dbeaver.model.sql.semantics.context.SQLQueryResultPseudoColumn;
import org.jkiss.dbeaver.model.sql.semantics.context.SourceResolutionResult;
import org.jkiss.dbeaver.model.struct.DBSEntity;
import org.jkiss.dbeaver.model.struct.DBSEntityAttribute;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.struct.DBSStructContainer;
import org.jkiss.dbeaver.model.struct.rdb.DBSProcedure;

import java.util.LinkedList;
import java.util.List;

public abstract class SQLQueryCompletionItem {

    @NotNull
    private static final SQLQueryCompletionItemFactory FACTORY = new DefaultSQLQueryCompletionItemFactory();

    private final int score;

    @NotNull
    private final SQLQueryWordEntry filterKey;

    @NotNull
    private final SQLQueryCompletionItemKind kind;
    
    private SQLQueryCompletionItem(int score, @NotNull SQLQueryWordEntry filterKey, @NotNull SQLQueryCompletionItemKind kind) {
        this.score = score;
        this.filterKey = filterKey;
        this.kind = kind;
    }

    public int getScore() {
        return this.score;
    }

    @NotNull
    public SQLQueryWordEntry getFilterInfo() {
        return this.filterKey;
    }

    @NotNull
    public final SQLQueryCompletionItemKind getKind() {
        return this.kind;
    }

    @Nullable
    public DBSObject getObject() {
        return null;
    }

    public final <R> R apply(SQLQueryCompletionItemVisitor<R> visitor) {
        return this.applyImpl(visitor);
    }

    protected abstract <R> R applyImpl(SQLQueryCompletionItemVisitor<R> visitor);

    /**
     * Prepare completion item for reserved word
     */
    @NotNull
    public static SQLQueryCompletionItem forReservedWord(int score, @NotNull SQLQueryWordEntry filterKey, @NotNull String text) {
        return FACTORY.createReservedWord(score, filterKey, text);
    }

    /**
     * Build completion item for columns expansion
     */
    @NotNull
    public static SQLQueryCompletionItem forSpecialText(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @NotNull String text,
        @Nullable String description
    ) {
        return FACTORY.createSpecialText(score, filterKey, text, description);
    }

    @NotNull
    public static SQLQueryCompletionItem forRowsSourceAlias(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @NotNull SQLQuerySymbol aliasSymbol,
        @NotNull SourceResolutionResult source,
        boolean isRelated
    ) {
        return FACTORY.createRowsSourceAlias(score, filterKey, aliasSymbol, source, isRelated);
    }

    @NotNull
    public static SQLQueryCompletionItem forRealTable(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @Nullable ContextObjectInfo resolvedContext,
        @NotNull DBSEntity table, boolean isUsed, boolean isRelated
    ) {
        return FACTORY.createRealTable(score, filterKey, resolvedContext, table, isUsed, isRelated);
    }

    @NotNull
    public static SQLColumnNameCompletionItem forSubsetColumn(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @NotNull SQLQueryResultColumn columnInfo,
        @Nullable SourceResolutionResult sourceInfo,
        boolean absolute
    ) {
        return FACTORY.createSubsetColumn(score, filterKey, columnInfo, sourceInfo, absolute);
    }

    @NotNull
    public static SQLQueryCompletionItem forGlobalPseudoColumn(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @NotNull SQLQueryResultPseudoColumn columnInfo
    ) {
        return FACTORY.createGlobalPseudoColumn(score, filterKey, columnInfo);
    }

    @NotNull
    public static SQLQueryCompletionItem forDbObject(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @Nullable ContextObjectInfo resolvedContext,
        @NotNull DBSObject object
    ) {
        return FACTORY.createDbObject(score, filterKey, resolvedContext, object, SQLQueryCompletionItemKind.UNKNOWN);
    }

    @NotNull
    public static SQLQueryCompletionItem forDbCatalogObject(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @Nullable ContextObjectInfo resolvedContext,
        @NotNull DBSObject object
    ) {
        return FACTORY.createDbObject(score, filterKey, resolvedContext, object, SQLQueryCompletionItemKind.CATALOG);
    }

    @NotNull
    public static SQLQueryCompletionItem forDbSchemaObject(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @Nullable ContextObjectInfo resolvedContext,
        @NotNull DBSObject object
    ) {
        return FACTORY.createDbObject(score, filterKey, resolvedContext, object, SQLQueryCompletionItemKind.SCHEMA);
    }

    @NotNull
    public static SQLQueryCompletionItem forCompositeField(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @NotNull DBSEntityAttribute attribute,
        @NotNull SQLQueryExprType.SQLQueryExprTypeMemberInfo memberInfo
    ) {
        return FACTORY.createCompositeField(score, filterKey, attribute, memberInfo);
    }

    /**
     * Returns completion item that describes field of the composite entity, which is not backed by the database metadata
     */
    @NotNull
    public static SQLQueryCompletionItem forSpecialCompositeField(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @NotNull SQLQueryExprType.SQLQueryExprTypeMemberInfo memberInfo
    ) {
        return FACTORY.createSpecialCompositeField(score, filterKey, memberInfo);
    }

    public static SQLQueryCompletionItem forJoinCondition(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @NotNull SQLColumnNameCompletionItem first,
        @NotNull SQLColumnNameCompletionItem second) {
        return FACTORY.createJoinCondition(score, filterKey, first, second);
    }

    /**
     * Returns completion item that describes functions that comes from the dialect
     */
    public static SQLQueryCompletionItem forBuiltinFunction(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @NotNull String name
    ) {
        return FACTORY.createBuiltinFunction(score, filterKey, name);
    }

    /**
     * Returns completion item that describes database user-created functions
     */
    public static SQLQueryCompletionItem forProcedureObject(
        int score,
        @NotNull SQLQueryWordEntry filterKey,
        @Nullable ContextObjectInfo resolvedContext,
        @NotNull DBSProcedure object
    ) {
        return FACTORY.createProcedureObject(score, filterKey, resolvedContext, object);
    }

    private abstract static class SQLQueryCompletionItemFactory {

        @NotNull
        final SQLQueryCompletionItem createReservedWord(int score, @NotNull SQLQueryWordEntry filterKey, @NotNull String text) {
            return this.visitReservedWord(score, filterKey, text);
        }

        @NotNull
        protected abstract SQLQueryCompletionItem visitReservedWord(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull String text
        );

        @NotNull
        final SQLQueryCompletionItem createSpecialText(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull String text,
            @Nullable String description
        ) {
            return this.visitSpecialText(score, filterKey, text, description);
        }

        @NotNull
        protected abstract SQLQueryCompletionItem visitSpecialText(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull String text,
            @Nullable String description
        );

        @NotNull
        final SQLQueryCompletionItem createRowsSourceAlias(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQuerySymbol aliasSymbol,
            @NotNull SourceResolutionResult source,
            boolean isRelated
        ) {
            return this.visitRowsSourceAlias(score, filterKey, aliasSymbol, source, isRelated);
        }

        @NotNull
        protected abstract SQLQueryCompletionItem visitRowsSourceAlias(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQuerySymbol aliasSymbol,
            @NotNull SourceResolutionResult source,
            boolean isRelated
        );

        @NotNull
        final SQLQueryCompletionItem createRealTable(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull DBSEntity table,
            boolean isUsed,
            boolean isRelated
        ) {
            return this.visitRealTable(score, filterKey, resolvedContext, table, isUsed, isRelated);
        }

        @NotNull
        protected abstract SQLQueryCompletionItem visitRealTable(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull DBSEntity table,
            boolean isUsed,
            boolean isRelated
        );

        @NotNull
        final SQLColumnNameCompletionItem createSubsetColumn(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryResultColumn columnInfo,
            @Nullable SourceResolutionResult sourceInfo,
            boolean absolute
        ) {
            return this.visitSubsetColumn(score, filterKey, columnInfo, sourceInfo, absolute);
        }

        @NotNull
        protected abstract SQLColumnNameCompletionItem visitSubsetColumn(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryResultColumn columnInfo,
            @Nullable SourceResolutionResult sourceInfo,
            boolean absolute
        );

        @NotNull
        final SQLQueryCompletionItem createGlobalPseudoColumn(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryResultPseudoColumn columnInfo
        ) {
            return this.visitGlobalPseudoColumn(score, filterKey, columnInfo);
        }

        @NotNull
        protected abstract SQLQueryCompletionItem visitGlobalPseudoColumn(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryResultPseudoColumn columnInfo
        );

        @NotNull
        final SQLQueryCompletionItem createDbObject(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull DBSObject object,
            @NotNull SQLQueryCompletionItemKind itemKind
        ) {
            return this.visitDbObject(score, filterKey, resolvedContext, object, itemKind);
        }

        @NotNull
        protected abstract SQLQueryCompletionItem visitDbObject(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull DBSObject object,
            @NotNull SQLQueryCompletionItemKind itemKind
        );

        @NotNull
        final SQLQueryCompletionItem createCompositeField(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull DBSEntityAttribute attribute,
            @NotNull SQLQueryExprType.SQLQueryExprTypeMemberInfo memberInfo
        ) {
            return this.visitCompositeField(score, filterKey, attribute, memberInfo);
        }

        @NotNull
        protected abstract SQLQueryCompletionItem visitCompositeField(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull DBSEntityAttribute attribute,
            @NotNull SQLQueryExprType.SQLQueryExprTypeMemberInfo memberInfo
        );

        @NotNull
        final SQLQueryCompletionItem createSpecialCompositeField(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryExprType.SQLQueryExprTypeMemberInfo memberInfo
        ) {
            return this.visitSpecialCompositeField(score, filterKey, memberInfo);
        }

        @NotNull
        protected abstract SQLQueryCompletionItem visitSpecialCompositeField(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryExprType.SQLQueryExprTypeMemberInfo memberInfo
        );

        @NotNull
        final SQLQueryCompletionItem createJoinCondition(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLColumnNameCompletionItem first,
            @NotNull SQLColumnNameCompletionItem second
        ) {
            return this.visitJoinCondition(score, filterKey, first, second);
        }

        @NotNull
        protected abstract SQLQueryCompletionItem visitJoinCondition(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLColumnNameCompletionItem first,
            @NotNull SQLColumnNameCompletionItem second
        );

        @NotNull
        final SQLQueryCompletionItem createBuiltinFunction(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull String name
        ) {
            return this.visitBuiltinFunction(score, filterKey, name);
        }

        @NotNull
        protected abstract SQLQueryCompletionItem visitBuiltinFunction(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull String name
        );

        @NotNull
        final SQLQueryCompletionItem createProcedureObject(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull DBSProcedure object
        ) {
            return this.visitProcedureObject(score, filterKey, resolvedContext, object);
        }

        @NotNull
        protected abstract SQLQueryCompletionItem visitProcedureObject(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull DBSProcedure object
        );
    }

    private static final class DefaultSQLQueryCompletionItemFactory extends SQLQueryCompletionItemFactory {

        @NotNull
        @Override
        protected SQLQueryCompletionItem visitReservedWord(int score, @NotNull SQLQueryWordEntry filterKey, @NotNull String text) {
            return new SQLReservedWordCompletionItem(score, filterKey, text);
        }

        @NotNull
        @Override
        protected SQLQueryCompletionItem visitSpecialText(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull String text,
            @Nullable String description
        ) {
            return new SQLSpecialTextCompletionItem(score, filterKey, text, description);
        }

        @NotNull
        @Override
        protected SQLQueryCompletionItem visitRowsSourceAlias(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQuerySymbol aliasSymbol,
            @NotNull SourceResolutionResult source,
            boolean isRelated
        ) {
            return new SQLRowsSourceAliasCompletionItem(score, filterKey, aliasSymbol, source, isRelated);
        }

        @NotNull
        @Override
        protected SQLQueryCompletionItem visitRealTable(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull DBSEntity table,
            boolean isUsed,
            boolean isRelated
        ) {
            return new SQLTableNameCompletionItem(score, filterKey, resolvedContext, table, isUsed, isRelated);
        }

        @NotNull
        @Override
        protected SQLColumnNameCompletionItem visitSubsetColumn(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryResultColumn columnInfo,
            @Nullable SourceResolutionResult sourceInfo,
            boolean absolute
        ) {
            return new SQLColumnNameCompletionItem(score, filterKey, columnInfo, sourceInfo, absolute);
        }

        @NotNull
        @Override
        protected SQLQueryCompletionItem visitGlobalPseudoColumn(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryResultPseudoColumn columnInfo
        ) {
            return new SQLGlobalPseudoColumnCompletionItem(score, filterKey, columnInfo);
        }

        @NotNull
        @Override
        protected SQLQueryCompletionItem visitDbObject(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull DBSObject object,
            @NotNull SQLQueryCompletionItemKind itemKind
        ) {
            return new SQLDbNamedObjectCompletionItem(score, filterKey, resolvedContext, object, itemKind);
        }

        @NotNull
        @Override
        protected SQLQueryCompletionItem visitCompositeField(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull DBSEntityAttribute attribute,
            @NotNull SQLQueryExprType.SQLQueryExprTypeMemberInfo memberInfo
        ) {
            return new SQLCompositeFieldCompletionItem(score, filterKey, attribute, memberInfo);
        }

        @NotNull
        @Override
        protected SQLQueryCompletionItem visitSpecialCompositeField(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryExprType.SQLQueryExprTypeMemberInfo memberInfo
        ) {
            return new SQLSpecialCompositeFieldCompletionItem(score, filterKey, memberInfo);
        }

        @NotNull
        @Override
        protected SQLQueryCompletionItem visitJoinCondition(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLColumnNameCompletionItem first,
            @NotNull SQLColumnNameCompletionItem second
        ) {
            return new SQLJoinConditionCompletionItem(score, filterKey, first, second);
        }

        @NotNull
        @Override
        protected SQLQueryCompletionItem visitBuiltinFunction(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull String name
        ) {
            return new SQLBuiltinFunctionCompletionItem(score, filterKey, name);
        }

        @NotNull
        @Override
        protected SQLQueryCompletionItem visitProcedureObject(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull DBSProcedure object
        ) {
            return new SQLProcedureCompletionItem(score, filterKey, resolvedContext, object);
        }
    }

    public static class SQLRowsSourceAliasCompletionItem extends SQLQueryCompletionItem {
        @NotNull
        public final SQLQuerySymbol symbol;
        @NotNull
        public final SourceResolutionResult sourceInfo;

        public final boolean isRelated;

        SQLRowsSourceAliasCompletionItem(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQuerySymbol symbol,
            @NotNull SourceResolutionResult sourceInfo,
            boolean isRelated
        ) {
            super(score, filterKey, isRelated ? SQLQueryCompletionItemKind.RELATED_SUBQUERY_ALIAS : SQLQueryCompletionItemKind.SUBQUERY_ALIAS);
            this.symbol = symbol;
            this.sourceInfo = sourceInfo;
            this.isRelated = isRelated;
        }

        @Override
        protected <R> R applyImpl(SQLQueryCompletionItemVisitor<R> visitor) {
            return visitor.visitSubqueryAlias(this);
        }
    }
    
    public static class SQLColumnNameCompletionItem extends SQLQueryCompletionItem {
        @NotNull
        public final SQLQueryResultColumn columnInfo;
        // should be null only for columns provided by the root projection (SELECT clause), because it doesn't serve as a source
        @Nullable
        public final SourceResolutionResult sourceInfo;
        // TODO consider removing this flag in favor of refactoring for explicit formatting mechanism
        public final boolean absolute;

        SQLColumnNameCompletionItem(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryResultColumn columnInfo,
            @Nullable SourceResolutionResult sourceInfo,
            boolean absolute
        ) {
            super(score, filterKey, columnInfo.symbol.getSymbolClass() == SQLQuerySymbolClass.COLUMN_DERIVED
                ? SQLQueryCompletionItemKind.DERIVED_COLUMN_NAME
                : SQLQueryCompletionItemKind.TABLE_COLUMN_NAME);

            if (columnInfo == null) {
                throw new IllegalArgumentException("columnInfo should not be null");
            }

            this.columnInfo = columnInfo;
            this.sourceInfo = sourceInfo;
            this.absolute = absolute;
        }

        @Nullable
        @Override
        public DBSObject getObject() {
            return this.columnInfo.realAttr;
        }

        @Override
        protected <R> R applyImpl(SQLQueryCompletionItemVisitor<R> visitor) {
            return visitor.visitColumnName(this);
        }
    }

    public static class SQLGlobalPseudoColumnCompletionItem extends SQLQueryCompletionItem {
        @NotNull
        public final SQLQueryResultPseudoColumn columnInfo;

        SQLGlobalPseudoColumnCompletionItem(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryResultPseudoColumn columnInfo
        ) {
            super(score, filterKey, SQLQueryCompletionItemKind.GLOBAL_PSEUDO_COLUMN);
            this.columnInfo = columnInfo;
        }

        @Override
        protected <R> R applyImpl(SQLQueryCompletionItemVisitor<R> visitor) {
            return visitor.visitGlobalPseudoColumn(this);
        }
    }

    public abstract static class SQLDbObjectCompletionItem<T extends DBSObject> extends SQLQueryCompletionItem {
        @Nullable
        public final ContextObjectInfo resolvedContext;
        @NotNull
        public final T object;

        SQLDbObjectCompletionItem(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull T object,
            @NotNull SQLQueryCompletionItemKind kind
        ) {
            super(score, filterKey, kind);
            this.resolvedContext = resolvedContext;
            this.object = object;
        }

        @NotNull
        @Override
        public T getObject() {
            return this.object;
        }
    }

    public static class SQLTableNameCompletionItem extends SQLDbObjectCompletionItem<DBSEntity> {
        public final boolean isUsed;
        public final boolean isRelated;

        SQLTableNameCompletionItem(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull DBSEntity table,
            boolean isUsed,
            boolean isRelated
        ) {
            super(
                score,
                filterKey,
                resolvedContext,
                table,
                isRelated ? SQLQueryCompletionItemKind.RELATED_TABLE_NAME
                    : isUsed ? SQLQueryCompletionItemKind.USED_TABLE_NAME
                    : SQLQueryCompletionItemKind.NEW_TABLE_NAME
            );
            this.isUsed = isUsed;
            this.isRelated = isRelated;
        }

        @Override
        protected <R> R applyImpl(SQLQueryCompletionItemVisitor<R> visitor) {
            return visitor.visitTableName(this);
        }
    }
    
    public abstract static class SQLTextCompletionItem extends SQLQueryCompletionItem {
        @NotNull
        public final String text;

        SQLTextCompletionItem(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryCompletionItemKind kind,
            @NotNull String text
        ) {
            super(score, filterKey, kind);
            this.text = text;
        }
    }

    public static class SQLReservedWordCompletionItem extends SQLTextCompletionItem {
        SQLReservedWordCompletionItem(int score, @NotNull SQLQueryWordEntry filterKey, @NotNull String text) {
            super(score, filterKey, SQLQueryCompletionItemKind.RESERVED, text);
        }

        @Override
        protected <R> R applyImpl(@NotNull SQLQueryCompletionItemVisitor<R> visitor) {
            return visitor.visitReservedWord(this);
        }
    }

    public static class SQLSpecialTextCompletionItem extends SQLTextCompletionItem {
        public final String description;

        SQLSpecialTextCompletionItem(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull String text,
            @Nullable String description
        ) {
            super(score, filterKey, SQLQueryCompletionItemKind.UNKNOWN, text);
            this.description = description;
        }

        @Override
        protected <R> R applyImpl(@NotNull SQLQueryCompletionItemVisitor<R> visitor) {
            return visitor.visitSpecialText(this);
        }
    }

    public static class SQLDbNamedObjectCompletionItem extends SQLDbObjectCompletionItem<DBSObject>  {

        SQLDbNamedObjectCompletionItem(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull DBSObject object,
            @NotNull SQLQueryCompletionItemKind itemKind
        ) {
            super(score, filterKey, resolvedContext, object, itemKind);
        }

        @Override
        protected <R> R applyImpl(SQLQueryCompletionItemVisitor<R> visitor) {
            return visitor.visitNamedObject(this);
        }
    }

    public static class SQLCompositeFieldCompletionItem extends SQLDbObjectCompletionItem<DBSEntityAttribute>  {
        @NotNull
        public final SQLQueryExprType.SQLQueryExprTypeMemberInfo memberInfo;

        SQLCompositeFieldCompletionItem(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull DBSEntityAttribute attribute,
            @NotNull SQLQueryExprType.SQLQueryExprTypeMemberInfo memberInfo
        ) {
            super(score, filterKey, null, attribute, SQLQueryCompletionItemKind.COMPOSITE_FIELD_NAME);
            this.memberInfo = memberInfo;
        }

        @Override
        protected <R> R applyImpl(SQLQueryCompletionItemVisitor<R> visitor) {
            return visitor.visitCompositeField(this);
        }
    }

    /**
     * Completion item that describes field of the composite entity, which is not backed by the database metadata
     */
    public static class SQLSpecialCompositeFieldCompletionItem extends SQLQueryCompletionItem {
        @NotNull
        public final SQLQueryExprType.SQLQueryExprTypeMemberInfo memberInfo;

        SQLSpecialCompositeFieldCompletionItem(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLQueryExprType.SQLQueryExprTypeMemberInfo memberInfo
        ) {
            super(score, filterKey, SQLQueryCompletionItemKind.COMPOSITE_FIELD_NAME);
            this.memberInfo = memberInfo;
        }

        @Override
        protected <R> R applyImpl(SQLQueryCompletionItemVisitor<R> visitor) {
            return visitor.visitSpecialCompositeField(this);
        }
    }

    public static class SQLJoinConditionCompletionItem extends SQLQueryCompletionItem {
        @NotNull
        public final SQLColumnNameCompletionItem left;
        @NotNull
        public final SQLColumnNameCompletionItem right;

        SQLJoinConditionCompletionItem(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @NotNull SQLColumnNameCompletionItem left,
            @NotNull SQLColumnNameCompletionItem right
        ) {
            super(score, filterKey, SQLQueryCompletionItemKind.JOIN_CONDITION);
            this.left = left;
            this.right = right;
        }

        @Override
        protected <R> R applyImpl(@NotNull SQLQueryCompletionItemVisitor<R> visitor) {
            return visitor.visitJoinCondition(this);
        }
    }

    public static List<String> prepareQualifiedNameParts(@NotNull DBSObject object, @Nullable DBSObject knownSubroot) {
        LinkedList<String> parts = new LinkedList<>();
        parts.addFirst(DBUtils.getQuotedIdentifier(object));
        for (DBSObject o = object.getParentObject(); o != knownSubroot; o = o.getParentObject()) {
            if (o instanceof DBSStructContainer) {
                parts.addFirst(DBUtils.getQuotedIdentifier(o));
            }
        }
        return parts;
    }

    public record ContextObjectInfo(@NotNull String string, @NotNull DBSObject object, boolean preventFullName) {
    }

    public static class SQLBuiltinFunctionCompletionItem extends SQLTextCompletionItem {

        private SQLBuiltinFunctionCompletionItem(int score, @NotNull SQLQueryWordEntry filterKey, @NotNull String name) {
            super(score, filterKey, SQLQueryCompletionItemKind.PROCEDURE, name);
        }

        @Override
        protected <R> R applyImpl(SQLQueryCompletionItemVisitor<R> visitor) {
            return visitor.visitBuiltinFunction(this);
        }
    }

    public static class SQLProcedureCompletionItem extends SQLDbObjectCompletionItem<DBSProcedure> {

        public SQLProcedureCompletionItem(
            int score,
            @NotNull SQLQueryWordEntry filterKey,
            @Nullable ContextObjectInfo resolvedContext,
            @NotNull DBSProcedure object
        ) {
            super(score, filterKey, resolvedContext, object, SQLQueryCompletionItemKind.PROCEDURE);
        }

        @Override
        protected <R> R applyImpl(SQLQueryCompletionItemVisitor<R> visitor) {
            return visitor.visitProcedure(this);
        }
    }
}
