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

import joptsimple.OptionSet;
import org.junit.jupiter.api.Test;

import org.springframework.boot.cli.command.options.OptionHandler;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InitProjectGenerationRequestFactory}.
 *
 * @author Stephane Nicoll
 */
class InitProjectGenerationRequestFactoryTests {

	private final InitCommandOptionDefinitions definitions = new InitCommandOptionDefinitions();

	private final OptionHandler optionHandler = new OptionHandler();

	private final InitProjectGenerationRequestFactory factory = new InitProjectGenerationRequestFactory(
			this.definitions);

	InitProjectGenerationRequestFactoryTests() {
		this.definitions.configure(this.optionHandler);
	}

	@Test
	void createRequestWithDefaults() {
		OptionSet options = this.optionHandler.getParser().parse("--list", "--target=https://example.test");
		ProjectGenerationRequest request = this.factory.create(options);
		assertThat(request.getServiceUrl()).isEqualTo("https://example.test");
		assertThat(request.getBuild()).isEqualTo("gradle");
		assertThat(request.getFormat()).isEqualTo("project");
		assertThat(request.isDetectType()).isFalse();
	}

	@Test
	void createRequestAppliesConfiguredValues() {
		OptionSet options = this.optionHandler.getParser().parse("--target=https://example.test",
				"--group-id=org.demo", "--artifact-id=sample", "--version=1.0.0", "--name=sample",
				"--description=Demo project", "--package-name=demo.foo", "--type=ant-project", "--build=grunt",
				"--format=web", "--packaging=war", "--java-version=1.9", "--language=groovy",
				"--boot-version=1.2.0.RELEASE", "--dependencies=web,data-jpa", "output.zip");
		ProjectGenerationRequest request = this.factory.create(options);
		assertThat(request.getGroupId()).isEqualTo("org.demo");
		assertThat(request.getArtifactId()).isEqualTo("sample");
		assertThat(request.getVersion()).isEqualTo("1.0.0");
		assertThat(request.getName()).isEqualTo("sample");
		assertThat(request.getDescription()).isEqualTo("Demo project");
		assertThat(request.getPackageName()).isEqualTo("demo.foo");
		assertThat(request.getType()).isEqualTo("ant-project");
		assertThat(request.getBuild()).isEqualTo("grunt");
		assertThat(request.getFormat()).isEqualTo("web");
		assertThat(request.getPackaging()).isEqualTo("war");
		assertThat(request.getJavaVersion()).isEqualTo("1.9");
		assertThat(request.getLanguage()).isEqualTo("groovy");
		assertThat(request.getBootVersion()).isEqualTo("1.2.0.RELEASE");
		assertThat(request.getDependencies()).containsExactly("web", "data-jpa");
		assertThat(request.getOutput()).isEqualTo("output.zip");
	}

}
