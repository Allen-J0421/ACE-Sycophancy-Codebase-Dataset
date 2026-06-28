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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.configurationmetadata.ConfigurationMetadataProperty;
import org.springframework.boot.configurationmetadata.ConfigurationMetadataRepository;
import org.springframework.boot.configurationmetadata.Deprecation.Level;

/**
 * A changelog containing differences computed from two repositories of configuration
 * metadata.
 *
 * @param oldVersionNumber the name of the old version
 * @param newVersionNumber the name of the new version
 * @param differences the differences
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @author Yoobin Yoon
 */
record Changelog(String oldVersionNumber, String newVersionNumber, List<Difference> differences) {

	static Changelog of(String oldVersionNumber, ConfigurationMetadataRepository oldMetadata, String newVersionNumber,
			ConfigurationMetadataRepository newMetadata) {
		return new Changelog(oldVersionNumber, newVersionNumber, computeDifferences(oldMetadata, newMetadata));
	}

	static List<Difference> computeDifferences(ConfigurationMetadataRepository oldMetadata,
			ConfigurationMetadataRepository newMetadata) {
		Map<String, ConfigurationMetadataProperty> oldProperties = oldMetadata.getAllProperties();
		Map<String, ConfigurationMetadataProperty> newProperties = newMetadata.getAllProperties();
		Set<String> seenIds = new HashSet<>();
		List<Difference> differences = new ArrayList<>();
		addExistingPropertyDifferences(oldProperties.values(), newProperties, seenIds, differences);
		addNewPropertyDifferences(newProperties.values(), seenIds, differences);
		return List.copyOf(differences);
	}

	private static void addExistingPropertyDifferences(Collection<ConfigurationMetadataProperty> oldProperties,
			Map<String, ConfigurationMetadataProperty> newProperties, Set<String> seenIds,
			List<Difference> differences) {
		for (ConfigurationMetadataProperty oldProperty : oldProperties) {
			String id = oldProperty.getId();
			seenIds.add(id);
			ConfigurationMetadataProperty newProperty = newProperties.get(id);
			Difference difference = Difference.compute(oldProperty, newProperty);
			if (difference != null) {
				differences.add(difference);
			}
		}
	}

	private static void addNewPropertyDifferences(Collection<ConfigurationMetadataProperty> newProperties,
			Set<String> seenIds, List<Difference> differences) {
		for (ConfigurationMetadataProperty newProperty : newProperties) {
			if (!seenIds.contains(newProperty.getId())) {
				DifferenceType differenceType = getNewPropertyDifferenceType(newProperty);
				differences.add(new Difference(differenceType, null, newProperty));
			}
		}
	}

	private static DifferenceType getNewPropertyDifferenceType(ConfigurationMetadataProperty property) {
		if (!property.isDeprecated()) {
			return DifferenceType.ADDED;
		}
		return (property.getDeprecation().getLevel() == Level.ERROR) ? DifferenceType.DELETED : DifferenceType.ADDED;
	}

}
