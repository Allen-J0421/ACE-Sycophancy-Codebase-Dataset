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

package org.springframework.boot.cli.command.init;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves the project type to use for a project generation request.
 *
 * @author Stephane Nicoll
 * @author Eddú Meléndez
 */
class ProjectTypeResolver {

	ProjectType resolve(ProjectGenerationRequest request, InitializrServiceMetadata metadata) {
		if (request.getType() != null) {
			ProjectType result = metadata.getProjectTypes().get(request.getType());
			if (result == null) {
				throw new ReportableException(
						("No project type with id '" + request.getType() + "' - check the service capabilities (--list)"));
			}
			return result;
		}
		if (request.isDetectType()) {
			Map<String, ProjectType> types = new HashMap<>(metadata.getProjectTypes());
			if (request.getBuild() != null) {
				filter(types, "build", request.getBuild());
			}
			if (request.getFormat() != null) {
				filter(types, "format", request.getFormat());
			}
			if (types.size() == 1) {
				return types.values().iterator().next();
			}
			if (types.isEmpty()) {
				throw new ReportableException("No type found with build '" + request.getBuild() + "' and format '"
						+ request.getFormat() + "' check the service capabilities (--list)");
			}
			throw new ReportableException("Multiple types found with build '" + request.getBuild() + "' and format '"
					+ request.getFormat() + "' use --type with a more specific value " + types.keySet());
		}
		ProjectType defaultType = metadata.getDefaultType();
		if (defaultType == null) {
			throw new ReportableException(("No project type is set and no default is defined. "
					+ "Check the service capabilities (--list)"));
		}
		return defaultType;
	}

	private static void filter(Map<String, ProjectType> projects, String tag, String tagValue) {
		projects.entrySet().removeIf((entry) -> !tagValue.equals(entry.getValue().getTags().get(tag)));
	}

}
