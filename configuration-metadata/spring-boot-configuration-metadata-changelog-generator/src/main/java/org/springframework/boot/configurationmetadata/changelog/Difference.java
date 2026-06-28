/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.configurationmetadata.changelog;

import java.util.Objects;

import org.springframework.boot.configurationmetadata.ConfigurationMetadataProperty;
import org.springframework.boot.configurationmetadata.Deprecation.Level;

/**
 * A difference in the metadata.
 *
 * @param type the type of the difference
 * @param oldProperty the old property
 * @param newProperty the new property
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @author Phillip Webb
 */
record Difference(DifferenceType type, ConfigurationMetadataProperty oldProperty,
		ConfigurationMetadataProperty newProperty) {

	static Difference compute(ConfigurationMetadataProperty oldProperty, ConfigurationMetadataProperty newProperty) {
		if (newProperty == null) {
			return computeDeletedDifference(oldProperty);
		}
		if (isNewlyDeprecated(oldProperty, newProperty)) {
			DifferenceType differenceType = getDeprecationDifferenceType(newProperty);
			return new Difference(differenceType, oldProperty, newProperty);
		}
		if (isDeprecationLevelChangedToError(oldProperty, newProperty)) {
			return new Difference(DifferenceType.DELETED, oldProperty, newProperty);
		}
		if (hasDefaultValueChanged(oldProperty, newProperty)) {
			return new Difference(DifferenceType.DEFAULT_CHANGED, oldProperty, newProperty);
		}
		return null;
	}

	private static Difference computeDeletedDifference(ConfigurationMetadataProperty oldProperty) {
		if (isErrorDeprecation(oldProperty)) {
			return null;
		}
		return new Difference(DifferenceType.DELETED, oldProperty, null);
	}

	private static boolean isNewlyDeprecated(ConfigurationMetadataProperty oldProperty,
			ConfigurationMetadataProperty newProperty) {
		return newProperty.isDeprecated() && !oldProperty.isDeprecated();
	}

	private static DifferenceType getDeprecationDifferenceType(ConfigurationMetadataProperty property) {
		Level level = property.getDeprecation().getLevel();
		return (level == Level.WARNING) ? DifferenceType.DEPRECATED : DifferenceType.DELETED;
	}

	private static boolean isDeprecationLevelChangedToError(ConfigurationMetadataProperty oldProperty,
			ConfigurationMetadataProperty newProperty) {
		return isWarningDeprecation(oldProperty) && isErrorDeprecation(newProperty);
	}

	private static boolean isWarningDeprecation(ConfigurationMetadataProperty property) {
		return property.isDeprecated() && property.getDeprecation().getLevel() == Level.WARNING;
	}

	private static boolean isErrorDeprecation(ConfigurationMetadataProperty property) {
		return property.isDeprecated() && property.getDeprecation().getLevel() == Level.ERROR;
	}

	private static boolean hasDefaultValueChanged(ConfigurationMetadataProperty oldProperty,
			ConfigurationMetadataProperty newProperty) {
		return !Objects.deepEquals(oldProperty.getDefaultValue(), newProperty.getDefaultValue());
	}

}
