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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.core5.net.URIBuilder;
import org.jspecify.annotations.Nullable;

import org.springframework.util.StringUtils;

/**
 * Builds a project generation URI from a {@link ProjectGenerationRequest} and service
 * metadata.
 *
 * @author Stephane Nicoll
 * @author Eddú Meléndez
 */
class ProjectGenerationRequestUrlFactory {

	URI create(ProjectGenerationRequest request, InitializrServiceMetadata metadata) {
		try {
			URIBuilder builder = new URIBuilder(request.getServiceUrl());
			ProjectType projectType = determineProjectType(request, metadata);
			builder.setPath(resolvePath(builder.getPath(), projectType.getAction()));
			setParameter(builder, "dependencies", request.getDependencies().isEmpty()
					? null : StringUtils.collectionToCommaDelimitedString(request.getDependencies()));
			setParameter(builder, "groupId", request.getGroupId());
			setParameter(builder, "artifactId", resolveArtifactId(request));
			setParameter(builder, "version", request.getVersion());
			setParameter(builder, "name", request.getName());
			setParameter(builder, "description", request.getDescription());
			setParameter(builder, "packageName", request.getPackageName());
			builder.setParameter("type", projectType.getId());
			setParameter(builder, "packaging", request.getPackaging());
			setParameter(builder, "javaVersion", request.getJavaVersion());
			setParameter(builder, "language", request.getLanguage());
			setParameter(builder, "bootVersion", request.getBootVersion());
			return builder.build();
		}
		catch (URISyntaxException ex) {
			throw new ReportableException("Invalid service URL (" + ex.getMessage() + ")");
		}
	}

	private ProjectType determineProjectType(ProjectGenerationRequest request, InitializrServiceMetadata metadata) {
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

	private @Nullable String resolveArtifactId(ProjectGenerationRequest request) {
		if (request.getArtifactId() != null) {
			return request.getArtifactId();
		}
		if (request.getOutput() != null) {
			int i = request.getOutput().lastIndexOf('.');
			return (i != -1) ? request.getOutput().substring(0, i) : request.getOutput();
		}
		return null;
	}

	private static void filter(Map<String, ProjectType> projects, String tag, String tagValue) {
		projects.entrySet().removeIf((entry) -> !tagValue.equals(entry.getValue().getTags().get(tag)));
	}

	private String resolvePath(@Nullable String path, String action) {
		StringBuilder resolvedPath = new StringBuilder();
		if (path != null) {
			resolvedPath.append(path);
		}
		resolvedPath.append(action);
		return resolvedPath.toString();
	}

	private void setParameter(URIBuilder builder, String name, @Nullable String value) {
		if (value != null) {
			builder.setParameter(name, value);
		}
	}

}
