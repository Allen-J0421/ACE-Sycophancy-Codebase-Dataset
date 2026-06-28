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

package org.springframework.boot.cli.command.shell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jline.console.ConsoleReader;
import org.junit.jupiter.api.Test;

import org.springframework.boot.cli.command.AbstractCommand;
import org.springframework.boot.cli.command.status.ExitStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class CommandCompleterTests {

	@Test
	void completeWithNullBufferReturnsCommandCandidates() throws Exception {
		ConsoleReader console = mock(ConsoleReader.class);
		CommandCompleter completer = new CommandCompleter(console, new EscapeAwareWhiteSpaceArgumentDelimiter(),
				Collections.singletonList(new TestCommand("test")));
		List<CharSequence> candidates = new ArrayList<>();
		int completionIndex = completer.complete(null, 0, candidates);
		assertThat(completionIndex).isZero();
		assertThat(candidates).contains("test");
		then(console).should(never()).println("Usage:");
	}

	@Test
	void completeWithTrailingSpaceAndNoUsageHelpDoesNotPrintNull() throws Exception {
		ConsoleReader console = mock(ConsoleReader.class);
		CommandCompleter completer = new CommandCompleter(console, new EscapeAwareWhiteSpaceArgumentDelimiter(),
				Collections.singletonList(new TestCommand("test")));
		List<CharSequence> candidates = new ArrayList<>();
		completer.complete("test ", "test ".length(), candidates);
		then(console).should().println("Usage:");
		then(console).should().println("test");
		then(console).should(never()).println("test null");
	}

	private static final class TestCommand extends AbstractCommand {

		private TestCommand(String name) {
			super(name, "Test command");
		}

		@Override
		public ExitStatus run(String... args) {
			return ExitStatus.OK;
		}

	}

}
