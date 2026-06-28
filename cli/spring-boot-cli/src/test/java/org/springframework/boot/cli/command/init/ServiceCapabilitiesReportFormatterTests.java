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

import org.junit.jupiter.api.Test;

import org.springframework.boot.cli.json.JSONException;
import org.springframework.boot.cli.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ServiceCapabilitiesReportFormatter}.
 *
 * @author Stephane Nicoll
 */
class ServiceCapabilitiesReportFormatterTests {

	private final ServiceCapabilitiesReportFormatter formatter = new ServiceCapabilitiesReportFormatter();

	@Test
	void formatReport() throws Exception {
		InitializrServiceMetadata metadata = createMetadata("2.0.0");
		String content = this.formatter.format("http://localhost", metadata);
		assertThat(content).contains("Capabilities of http://localhost");
		assertThat(content).contains("Available dependencies:");
		assertThat(content).contains("security - Security: Security description");
		assertThat(content).contains("Available project types:");
		assertThat(content).contains("Defaults:");
	}

	private static InitializrServiceMetadata createMetadata(String version) throws IOException, JSONException {
		return new InitializrServiceMetadata(readJson(version));
	}

	private static JSONObject readJson(String version) throws IOException, JSONException {
		Resource resource = new ClassPathResource("metadata/service-metadata-" + version + ".json");
		try (InputStream stream = resource.getInputStream()) {
			return new JSONObject(StreamUtils.copyToString(stream, StandardCharsets.UTF_8));
		}
	}

}
