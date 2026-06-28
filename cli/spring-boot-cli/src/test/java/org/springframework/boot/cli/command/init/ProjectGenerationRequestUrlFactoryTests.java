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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.boot.cli.json.JSONException;
import org.springframework.boot.cli.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link ProjectGenerationRequestUrlFactory}.
 *
 * @author Stephane Nicoll
 */
class ProjectGenerationRequestUrlFactoryTests {

	private final ProjectGenerationRequestUrlFactory factory = new ProjectGenerationRequestUrlFactory();

	@Test
	void defaultSettings() {
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		assertThat(this.factory.create(request, createDefaultMetadata())).isEqualTo(createDefaultUrl("?type=test-type"));
	}

	@Test
	void buildOneMatch() {
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		setBuildAndFormat(request, "gradle", null);
		assertThat(this.factory.create(request, readMetadata())).isEqualTo(createDefaultUrl("?type=gradle-project"));
	}

	@Test
	void customType() throws Exception {
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		request.setType("custom");
		request.getDependencies().add("data-rest");
		ProjectType projectType = new ProjectType("custom", "Custom Type", "/foo", true, Collections.emptyMap());
		InitializrServiceMetadata metadata = new InitializrServiceMetadata(projectType);
		assertThat(this.factory.create(request, metadata))
			.isEqualTo(new URI(ProjectGenerationRequest.DEFAULT_SERVICE_URL + "/foo?dependencies=data-rest&type=custom"));
	}

	@Test
	void invalidType() {
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		request.setType("does-not-exist");
		assertThatExceptionOfType(ReportableException.class)
			.isThrownBy(() -> this.factory.create(request, createDefaultMetadata()));
	}

	private static URI createUrl(String actionAndParam) {
		try {
			return new URI(ProjectGenerationRequest.DEFAULT_SERVICE_URL + actionAndParam);
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	private static URI createDefaultUrl(String param) {
		return createUrl("/starter.zip" + param);
	}

	private static InitializrServiceMetadata createDefaultMetadata() {
		ProjectType projectType = new ProjectType("test-type", "The test type", "/starter.zip", true,
				Collections.emptyMap());
		return new InitializrServiceMetadata(projectType);
	}

	private static InitializrServiceMetadata readMetadata() {
		try {
			Resource resource = new ClassPathResource("metadata/service-metadata-2.0.0.json");
			try (InputStream stream = resource.getInputStream()) {
				JSONObject json = new JSONObject(StreamUtils.copyToString(stream, StandardCharsets.UTF_8));
				return new InitializrServiceMetadata(json);
			}
		}
		catch (IOException | JSONException ex) {
			throw new IllegalStateException("Failed to read metadata", ex);
		}
	}

	private static void setBuildAndFormat(ProjectGenerationRequest request, String build, String format) {
		request.setBuild((build != null) ? build : "maven");
		request.setFormat((format != null) ? format : "project");
		request.setDetectType(true);
	}

}
