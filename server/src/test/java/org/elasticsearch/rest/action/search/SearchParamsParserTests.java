/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */
package org.elasticsearch.rest.action.search;

import org.elasticsearch.common.logging.HeaderWarning;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.ThreadContext;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.test.rest.FakeRestRequest;
import org.elasticsearch.test.rest.RestActionTestCase;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Verifies the CCS round-trips parsing helper keeps the request behavior and warning semantics intact.
 */
public class SearchParamsParserTests extends RestActionTestCase {

    @Override
    protected boolean enableWarningsCheck() {
        // This test asserts the warning header directly after calling the parser helper.
        return false;
    }

    public void testParseCcsMinimizeRoundtripsUsesRequestValueOutsideCps() {
        RestRequest request = new FakeRestRequest.Builder(xContentRegistry()).withParams(
            Map.of("ccs_minimize_roundtrips", "false")
        ).build();

        assertFalse(SearchParamsParser.parseCcsMinimizeRoundtrips(Optional.of(false), request, true));
        assertTrue(SearchParamsParser.parseCcsMinimizeRoundtrips(Optional.empty(), request, true));
    }

    public void testParseCcsMinimizeRoundtripsDefaultsToTrueInCpsWhenUnset() {
        RestRequest request = new FakeRestRequest.Builder(xContentRegistry()).build();

        assertTrue(SearchParamsParser.parseCcsMinimizeRoundtrips(Optional.of(true), request, false));
    }

    public void testParseCcsMinimizeRoundtripsWarnsWhenExplicitInCps() {
        ThreadContext threadContext = new ThreadContext(Settings.EMPTY);
        HeaderWarning.setThreadContext(threadContext);
        try {
            RestRequest request = new FakeRestRequest.Builder(xContentRegistry()).withParams(
                Map.of("ccs_minimize_roundtrips", "false")
            ).build();

            assertTrue(SearchParamsParser.parseCcsMinimizeRoundtrips(Optional.of(true), request, false));

            List<String> warnings = threadContext.getResponseHeaders().get("Warning");
            assertThat(warnings, hasSize(1));
            assertThat(
                HeaderWarning.extractWarningValueFromWarningHeader(warnings.get(0), false),
                equalTo(SearchParamsParser.MRT_SET_IN_CPS_WARN)
            );
        } finally {
            HeaderWarning.removeThreadContext(threadContext);
        }
    }
}
