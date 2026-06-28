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

import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.struct.rdb.DBSProcedure;
import org.jkiss.junit.DBeaverUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SQLQueryCompletionItemFactoryTest extends DBeaverUnitTest {

    @Test
    public void shouldCreateNamedDbObjectItemsWithExpectedKinds() {
        DBSObject object = Mockito.mock(DBSObject.class);
        SQLQueryWordEntry filterKey = new SQLQueryWordEntry(0, "obj");

        SQLQueryCompletionItem dbObjectItem = SQLQueryCompletionItem.forDbObject(1, filterKey, null, object);
        SQLQueryCompletionItem catalogItem = SQLQueryCompletionItem.forDbCatalogObject(2, filterKey, null, object);
        SQLQueryCompletionItem schemaItem = SQLQueryCompletionItem.forDbSchemaObject(3, filterKey, null, object);

        assertSame(object, dbObjectItem.getObject());
        assertSame(object, catalogItem.getObject());
        assertSame(object, schemaItem.getObject());
        assertEquals(SQLQueryCompletionItemKind.UNKNOWN, dbObjectItem.getKind());
        assertEquals(SQLQueryCompletionItemKind.CATALOG, catalogItem.getKind());
        assertEquals(SQLQueryCompletionItemKind.SCHEMA, schemaItem.getKind());
        assertTrue(dbObjectItem instanceof SQLQueryCompletionItem.SQLDbNamedObjectCompletionItem);
        assertTrue(catalogItem instanceof SQLQueryCompletionItem.SQLDbNamedObjectCompletionItem);
        assertTrue(schemaItem instanceof SQLQueryCompletionItem.SQLDbNamedObjectCompletionItem);
    }

    @Test
    public void shouldCreateProcedureItemWithWrappedObject() {
        DBSProcedure procedure = Mockito.mock(DBSProcedure.class);
        SQLQueryWordEntry filterKey = new SQLQueryWordEntry(0, "proc");
        SQLQueryCompletionItem.ContextObjectInfo context = new SQLQueryCompletionItem.ContextObjectInfo(
            "ctx",
            Mockito.mock(DBSObject.class),
            false
        );

        SQLQueryCompletionItem item = SQLQueryCompletionItem.forProcedureObject(1, filterKey, context, procedure);

        assertSame(procedure, item.getObject());
        assertEquals(SQLQueryCompletionItemKind.PROCEDURE, item.getKind());
        assertTrue(item instanceof SQLQueryCompletionItem.SQLProcedureCompletionItem);
    }
}
