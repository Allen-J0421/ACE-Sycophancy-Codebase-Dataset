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

	private final ProjectTypeResolver projectTypeResolver;

	ProjectGenerationRequestUrlFactory() {
		this(new ProjectTypeResolver());
	}

	ProjectGenerationRequestUrlFactory(ProjectTypeResolver projectTypeResolver) {
		this.projectTypeResolver = projectTypeResolver;
	}

	URI create(ProjectGenerationRequest request, InitializrServiceMetadata metadata) {
		try {
			URIBuilder builder = new URIBuilder(request.getServiceUrl());
			ProjectType projectType = this.projectTypeResolver.resolve(request, metadata);
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
