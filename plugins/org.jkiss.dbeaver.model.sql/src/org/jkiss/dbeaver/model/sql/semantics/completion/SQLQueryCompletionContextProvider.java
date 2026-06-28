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
package org.jkiss.dbeaver.model.sql.semantics.completion;

import org.eclipse.jface.text.Document;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sql.SQLModelPreferences;
import org.jkiss.dbeaver.model.sql.completion.SQLCompletionRequest;
import org.jkiss.dbeaver.model.sql.parser.SQLParserContext;
import org.jkiss.dbeaver.model.sql.parser.SQLScriptParser;
import org.jkiss.dbeaver.model.sql.semantics.SQLDocumentSyntaxContext;
import org.jkiss.dbeaver.model.sql.semantics.SQLQueryModelRecognizer;
import org.jkiss.dbeaver.model.sql.semantics.SQLQueryRecognitionContext;
import org.jkiss.dbeaver.model.sql.semantics.SQLScriptItemAtOffset;
import org.jkiss.dbeaver.runtime.DBWorkbench;

public final class SQLQueryCompletionContextProvider {

    private SQLQueryCompletionContextProvider() {
    }

    @NotNull
    public static SQLQueryCompletionContext prepareCompletionContext(
        @NotNull DBRProgressMonitor monitor,
        @NotNull SQLCompletionRequest request
    ) {
        SQLDocumentSyntaxContext syntaxContext = prepareDocumentSyntaxContext(monitor, request);
        int position = request.getDocumentOffset();
        SQLScriptItemAtOffset scriptItem = syntaxContext.findScriptItem(position);
        if (scriptItem == null) {
            return SQLQueryCompletionContext.prepareOffquery(0, position);
        }

        scriptItem.item.setHasContextBoundaryAtLength(false);
        return SQLQueryCompletionContext.prepareCompletionContext(
            scriptItem,
            position,
            request.getContext().getExecutionContext(),
            request.getContext().getDataSource().getSQLDialect()
        );
    }

    @NotNull
    public static SQLDocumentSyntaxContext prepareDocumentSyntaxContext(
        @NotNull DBRProgressMonitor monitor,
        @NotNull SQLCompletionRequest request
    ) {
        String scriptText = request.getDocument().get();
        Document document = new Document(scriptText);
        SQLParserContext parserContext = new SQLParserContext(
            request.getContext().getDataSource(),
            request.getContext().getSyntaxManager(),
            request.getContext().getRuleManager(),
            document
        );

        SQLQueryRecognitionContext recognitionContext = new SQLQueryRecognitionContext(
            monitor,
            request.getContext().getExecutionContext(),
            true,
            DBWorkbench.getPlatform().getPreferenceStore().getBoolean(SQLModelPreferences.VALIDATE_FUNCTIONS),
            request.getContext().getSyntaxManager(),
            request.getContext().getDataSource().getSQLDialect()
        );

        SQLDocumentSyntaxContext syntaxContext = new SQLDocumentSyntaxContext();
        var scriptItems = SQLScriptParser.parseScript(
            parserContext.getDataSource(),
            parserContext.getDialect(),
            parserContext.getPreferenceStore(),
            document.get()
        );
        for (var item : scriptItems) {
            recognitionContext.reset();
            var model = SQLQueryModelRecognizer.recognizeQuery(recognitionContext, item.getOriginalText());
            syntaxContext.registerScriptItemContext(
                item.getOriginalText(),
                model,
                item.getOffset(),
                item.getLength(),
                true
            );
        }
        return syntaxContext;
    }
}
