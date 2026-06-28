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

package org.springframework.boot.cli.command.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.cli.command.AbstractCommand;
import org.springframework.boot.cli.command.CommandRunner;
import org.springframework.boot.cli.command.options.OptionHelp;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.boot.cli.util.MockLog;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.BDDMockito.then;

class HintCommandTests {

	private MockLog log;

	@BeforeEach
	void setup() {
		this.log = MockLog.attach();
	}

	@AfterEach
	void cleanup() {
		MockLog.clear();
	}

	@Test
	void commandHintsUseTypedPrefix() throws Exception {
		CommandRunner runner = new CommandRunner("spring");
		runner.addCommand(new TestCommand());
		new HintCommand(runner).run("1", "te");
		then(this.log).should().info(contains("test Test command"));
	}

	@Test
	void optionHintsIncludeCommandNameInCompletionContext() throws Exception {
		CommandRunner runner = new CommandRunner("spring");
		runner.addCommand(new TestCommand());
		new HintCommand(runner).run("2", "test", "--fo");
		then(this.log).should().info("--foo Foo option");
	}

	private static final class TestCommand extends AbstractCommand {

		private TestCommand() {
			super("test", "Test command");
		}

		@Override
		public Collection<OptionHelp> getOptionsHelp() {
			return Collections.singletonList(new OptionHelp() {

				@Override
				public Set<String> getOptions() {
					return Collections.singleton("--foo");
				}

				@Override
				public String getUsageHelp() {
					return "Foo option";
				}

			});
		}

		@Override
		public ExitStatus run(String... args) {
			return ExitStatus.OK;
		}

	}

}
