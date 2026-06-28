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

import joptsimple.OptionSet;

import org.springframework.boot.cli.command.options.OptionHandler;
import org.springframework.boot.cli.command.status.ExitStatus;
import org.springframework.boot.cli.util.Log;

/**
 * {@link OptionHandler} for {@link InitCommand}.
 *
 * @author Stephane Nicoll
 * @author Eddú Meléndez
 * @author Vignesh Thangavel Ilangovan
 */
class InitOptionHandler extends OptionHandler {

	private final ServiceCapabilitiesReportGenerator serviceCapabilitiesReport;

	private final ProjectGenerator projectGenerator;

	private final InitCommandOptionDefinitions optionDefinitions;

	private final InitProjectGenerationRequestFactory requestFactory;

	InitOptionHandler(InitializrService initializrService) {
		super(new InitCommandArgumentProcessor());
		this.serviceCapabilitiesReport = new ServiceCapabilitiesReportGenerator(initializrService);
		this.projectGenerator = new ProjectGenerator(initializrService);
		this.optionDefinitions = new InitCommandOptionDefinitions();
		this.requestFactory = new InitProjectGenerationRequestFactory(this.optionDefinitions);
	}

	@Override
	protected void options() {
		this.optionDefinitions.configure(this);
	}

	@Override
	protected ExitStatus run(OptionSet options) throws Exception {
		try {
			if (this.optionDefinitions.isListCapabilities(options)) {
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
		Log.info(this.serviceCapabilitiesReport.generate(this.optionDefinitions.getTarget(options)));
	}

	protected void generateProject(OptionSet options) throws IOException {
		ProjectGenerationRequest request = createProjectGenerationRequest(options);
		this.projectGenerator.generateProject(request, this.optionDefinitions.isForce(options));
	}

	protected ProjectGenerationRequest createProjectGenerationRequest(OptionSet options) {
		return this.requestFactory.create(options);
	}

}
