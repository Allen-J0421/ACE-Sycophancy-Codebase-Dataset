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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InitCommandArgumentProcessor}.
 *
 * @author Stephane Nicoll
 */
class InitCommandArgumentProcessorTests {

	private final InitCommandArgumentProcessor processor = new InitCommandArgumentProcessor();

	@Test
	void convertsCamelCaseAssignments() {
		assertThat(this.processor.apply("--groupId=org.demo")).isEqualTo("--group-id=org.demo");
		assertThat(this.processor.apply("--artifactId sample")).isEqualTo("--artifact-id sample");
		assertThat(this.processor.apply("--packageName=demo.foo")).isEqualTo("--package-name=demo.foo");
		assertThat(this.processor.apply("--javaVersion=1.8")).isEqualTo("--java-version=1.8");
		assertThat(this.processor.apply("--bootVersion=1.2.0.RELEASE")).isEqualTo("--boot-version=1.2.0.RELEASE");
	}

	@Test
	void leavesOtherArgumentsUnchanged() {
		assertThat(this.processor.apply("--type=ant-project")).isEqualTo("--type=ant-project");
		assertThat(this.processor.apply("foo")).isEqualTo("foo");
	}

}
