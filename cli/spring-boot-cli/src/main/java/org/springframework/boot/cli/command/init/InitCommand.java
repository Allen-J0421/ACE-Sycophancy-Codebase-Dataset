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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joptsimple.OptionSet;

import org.springframework.boot.cli.command.Command;
import org.springframework.boot.cli.command.HelpExample;
import org.springframework.boot.cli.command.OptionParsingCommand;
import org.springframework.boot.cli.command.options.OptionHandler;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.boot.cli.util.Log;

/**
 * {@link Command} that initializes a project using Spring initializr.
 *
 * @author Stephane Nicoll
 * @author Eddú Meléndez
 * @author Vignesh Thangavel Ilangovan
 * @since 1.2.0
 */
public class InitCommand extends OptionParsingCommand {

	public InitCommand() {
		this(new InitOptionHandler(new InitializrService()));
	}

	public InitCommand(InitOptionHandler handler) {
		super("init", "Initialize a new project using Spring Initializr (start.spring.io)", handler);
	}

	@Override
	public String getUsageHelp() {
		return "[options] [location]";
	}

	@Override
	public Collection<HelpExample> getExamples() {
		List<HelpExample> examples = new ArrayList<>();
		examples.add(new HelpExample("To list all the capabilities of the service", "spring init --list"));
		examples.add(new HelpExample("To creates a default project", "spring init"));
		examples.add(new HelpExample("To create a web my-app.zip", "spring init -d=web my-app.zip"));
		examples.add(new HelpExample("To create a web/data-jpa gradle project unpacked",
				"spring init -d=web,jpa --build=gradle my-dir"));
		return examples;
	}

	/**
	 * {@link OptionHandler} for {@link InitCommand}.
	 */
	static class InitOptionHandler extends OptionHandler {

		/**
		 * Mapping from camelCase options advertised by the service to our kebab-case
		 * options.
		 */
		private static final Map<String, String> CAMEL_CASE_OPTIONS;
		static {
			Map<String, String> options = new HashMap<>();
			options.put("--groupId", "--group-id");
			options.put("--artifactId", "--artifact-id");
			options.put("--packageName", "--package-name");
			options.put("--javaVersion", "--java-version");
			options.put("--bootVersion", "--boot-version");
			CAMEL_CASE_OPTIONS = Collections.unmodifiableMap(options);
		}

		private final ServiceCapabilitiesReportGenerator serviceCapabilitiesReport;

		private final ProjectGenerator projectGenerator;

		private final InitCommandOptions commandOptions;

		InitOptionHandler(InitializrService initializrService) {
			super(InitOptionHandler::processArgument);
			this.serviceCapabilitiesReport = new ServiceCapabilitiesReportGenerator(initializrService);
			this.projectGenerator = new ProjectGenerator(initializrService);
			this.commandOptions = new InitCommandOptions();
		}

		@Override
		protected void options() {
			this.commandOptions.configure(this);
		}

		@Override
		protected ExitStatus run(OptionSet options) throws Exception {
			try {
				if (this.commandOptions.isListCapabilities(options)) {
					generateReport(options);
				}
				else {
					generateProject(options);
				}
				return ExitStatus.OK;
			}
			catch (ReportableException ex) {
				Log.error(ex.getMessage());
				return ExitStatus.ERROR;
			}
			catch (Exception ex) {
				Log.error(ex);
				return ExitStatus.ERROR;
			}
		}

		private void generateReport(OptionSet options) throws IOException {
			Log.info(this.serviceCapabilitiesReport.generate(this.commandOptions.getTarget(options)));
		}

		protected void generateProject(OptionSet options) throws IOException {
			ProjectGenerationRequest request = createProjectGenerationRequest(options);
			this.projectGenerator.generateProject(request, this.commandOptions.isForce(options));
		}

		protected ProjectGenerationRequest createProjectGenerationRequest(OptionSet options) {
			return this.commandOptions.createProjectGenerationRequest(options);
		}

		private static String processArgument(String argument) {
			for (Map.Entry<String, String> entry : CAMEL_CASE_OPTIONS.entrySet()) {
				String name = entry.getKey();
				if (argument.startsWith(name + " ") || argument.startsWith(name + "=")) {
					return entry.getValue() + argument.substring(name.length());
				}
			}
			return argument;
		}

	}

}
