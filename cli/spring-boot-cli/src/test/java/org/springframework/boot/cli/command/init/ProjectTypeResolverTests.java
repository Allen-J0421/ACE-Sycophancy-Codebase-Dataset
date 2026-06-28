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
 * Tests for {@link ProjectTypeResolver}.
 *
 * @author Stephane Nicoll
 */
class ProjectTypeResolverTests {

	private final ProjectTypeResolver resolver = new ProjectTypeResolver();

	@Test
	void defaultType() {
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		assertThat(this.resolver.resolve(request, createDefaultMetadata())).isEqualTo(createDefaultType());
	}

	@Test
	void detectTypeWithSingleMatch() {
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		request.setBuild("gradle");
		request.setFormat("project");
		request.setDetectType(true);
		assertThat(this.resolver.resolve(request, readMetadata())).isEqualTo(new ProjectType("gradle-project",
				"Gradle Project", "/starter.zip", false, Collections.emptyMap()));
	}

	@Test
	void detectTypeWithNoMatches() {
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		request.setBuild("does-not-exist");
		request.setDetectType(true);
		assertThatExceptionOfType(ReportableException.class)
			.isThrownBy(() -> this.resolver.resolve(request, readMetadata()))
			.withMessageContaining("does-not-exist");
	}

	@Test
	void detectTypeWithMultipleMatches() {
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		request.setBuild("gradle");
		request.setDetectType(true);
		assertThatExceptionOfType(ReportableException.class)
			.isThrownBy(() -> this.resolver.resolve(request, readMetadata("types-conflict")))
			.withMessageContaining("gradle-project")
			.withMessageContaining("gradle-project-2");
	}

	@Test
	void noTypeAndNoDefault() {
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		assertThatExceptionOfType(ReportableException.class)
			.isThrownBy(() -> this.resolver.resolve(request, readMetadata("types-conflict")))
			.withMessageContaining("no default is defined");
	}

	@Test
	void explicitType() {
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		request.setType("custom");
		assertThat(this.resolver.resolve(request, createCustomMetadata()))
			.isEqualTo(new ProjectType("custom", "Custom Type", "/foo", true, Collections.emptyMap()));
	}

	@Test
	void invalidType() {
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		request.setType("does-not-exist");
		assertThatExceptionOfType(ReportableException.class)
			.isThrownBy(() -> this.resolver.resolve(request, createDefaultMetadata()));
	}

	private static ProjectType createDefaultType() {
		return new ProjectType("test-type", "The test type", "/starter.zip", true, Collections.emptyMap());
	}

	private static InitializrServiceMetadata createDefaultMetadata() {
		return new InitializrServiceMetadata(createDefaultType());
	}

	private static InitializrServiceMetadata createCustomMetadata() {
		return new InitializrServiceMetadata(new ProjectType("custom", "Custom Type", "/foo", true,
				Collections.emptyMap()));
	}

	private static InitializrServiceMetadata readMetadata() {
		return readMetadata("2.0.0");
	}

	private static InitializrServiceMetadata readMetadata(String version) {
		try {
			Resource resource = new ClassPathResource("metadata/service-metadata-" + version + ".json");
			try (InputStream stream = resource.getInputStream()) {
				JSONObject json = new JSONObject(StreamUtils.copyToString(stream, StandardCharsets.UTF_8));
				return new InitializrServiceMetadata(json);
			}
		}
		catch (IOException | JSONException ex) {
			throw new IllegalStateException("Failed to read metadata", ex);
		}
	}

}
