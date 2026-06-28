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
package org.jkiss.utils;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.junit.DBeaverUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class GeneralUtilsTest extends DBeaverUnitTest {

    public static final String VARIABLE_YEAR = "year";
    public static final String VARIABLE_MONTH = "month";
    public static final String VARIABLE_DAY = "day";
    public static final String VARIABLE_HOUR = "hour";
    public static final String VARIABLE_MINUTE = "minute";

    @Test
    public void testMakeDisplayStringFormatsArrays() {
        Assertions.assertEquals("[1, 2, 3]", GeneralUtils.makeDisplayString(new int[]{1, 2, 3}));
        Assertions.assertEquals("[true, false]", GeneralUtils.makeDisplayString(new boolean[]{true, false}));
        Assertions.assertEquals(
            "[[alpha], [beta, gamma]]",
            GeneralUtils.makeDisplayString(new String[][]{{"alpha"}, {"beta", "gamma"}}));
    }

    @Test
    public void testConvertStringHandlesPrimitiveAndBoxedTypes() {
        Assertions.assertNull(GeneralUtils.convertString("", Integer.class));
        Assertions.assertEquals("value", GeneralUtils.convertString("value", null));
        Assertions.assertEquals(Boolean.TRUE, GeneralUtils.convertString("true", Boolean.TYPE));
        Assertions.assertEquals(42L, GeneralUtils.convertString("42.7", Long.TYPE));
        Assertions.assertEquals(42, GeneralUtils.convertString("42.7", Integer.class));
        Assertions.assertEquals((short) 7, GeneralUtils.convertString("7.2", Short.TYPE));
        Assertions.assertEquals((byte) 3, GeneralUtils.convertString("3.9", Byte.class));
        Assertions.assertEquals(new BigInteger("123"), GeneralUtils.convertString("123.45", BigInteger.class));
        Assertions.assertEquals(Double.valueOf("1.25"), GeneralUtils.convertString("1.25", Double.TYPE));
        Assertions.assertEquals(Float.valueOf("1.5"), GeneralUtils.convertString("1.5", Float.class));
    }

    @Test
    public void testVariablesSubstitution() {
        @SuppressWarnings("deprecation")
        Date ts = new Date(90, 3, 21, 3, 20, 54);
        var patternsWithResults = Map.of(
            "${missingVariable}", "missingVariable",
            "${minute}", getVariableValue(VARIABLE_MINUTE, ts),
            "abracadabra${hour}", "abracadabra" + getVariableValue(VARIABLE_HOUR, ts),
            "hour${month}day", "hour" + getVariableValue(VARIABLE_MONTH, ts) + "day",
            "${year}${year}-${month}${year}-${month}${day}-${day}${month}",
                getVariableValue(VARIABLE_YEAR, ts) + getVariableValue(VARIABLE_YEAR, ts) + "-"
                + getVariableValue(VARIABLE_MONTH, ts) + getVariableValue(VARIABLE_YEAR, ts) + "-"
                + getVariableValue(VARIABLE_MONTH, ts) + getVariableValue(VARIABLE_DAY, ts) + "-"
                + getVariableValue(VARIABLE_DAY, ts) + getVariableValue(VARIABLE_MONTH, ts)
        );
        for (Map.Entry<String, String> entry : patternsWithResults.entrySet()) {
            String pattern = entry.getKey();
            String expectedResult = entry.getValue();
            String actualResult = GeneralUtils.replaceVariables(pattern, (name) -> getVariableValue(name, ts));
            Assertions.assertEquals(expectedResult, actualResult);
        }
    }

    @NotNull
    private static String getVariableValue(@NotNull String name, @NotNull Date ts) {
        switch (name) {
            case VARIABLE_YEAR:
                return new SimpleDateFormat("yyyy").format(ts);
            case VARIABLE_MONTH:
                return new SimpleDateFormat("MM").format(ts);
            case VARIABLE_DAY:
                return new SimpleDateFormat("dd").format(ts);
            case VARIABLE_HOUR:
                return new SimpleDateFormat("HH").format(ts);
            case VARIABLE_MINUTE:
                return new SimpleDateFormat("mm").format(ts);
            default:
                return name;
        }
    }
}
