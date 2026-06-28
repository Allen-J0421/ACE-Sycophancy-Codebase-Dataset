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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.springframework.util.Assert;

/**
 * Creates {@link ProjectGenerationRequest} instances from parsed CLI options.
 *
 * @author Stephane Nicoll
 * @author Eddú Meléndez
 * @author Vignesh Thangavel Ilangovan
 */
class InitProjectGenerationRequestFactory {

	private final InitCommandOptionDefinitions options;

	InitProjectGenerationRequestFactory(InitCommandOptionDefinitions options) {
		this.options = options;
	}

	ProjectGenerationRequest create(OptionSet options) {
		List<?> nonOptionArguments = new ArrayList<>(options.nonOptionArguments());
		Assert.state(nonOptionArguments.size() <= 1, "Only the target location may be specified");
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		request.setServiceUrl(options.valueOf(this.options.getTargetOption()));
		applyConfiguredValue(options, this.options.getBootVersionOption(), request::setBootVersion);
		applyConfiguredDependencies(options, request);
		applyConfiguredValue(options, this.options.getJavaVersionOption(), request::setJavaVersion);
		applyConfiguredValue(options, this.options.getPackageNameOption(), request::setPackageName);
		request.setBuild(options.valueOf(this.options.getBuildOption()));
		request.setFormat(options.valueOf(this.options.getFormatOption()));
		request.setDetectType(options.has(this.options.getBuildOption()) || options.has(this.options.getFormatOption()));
		applyConfiguredValue(options, this.options.getTypeOption(), request::setType);
		applyConfiguredValue(options, this.options.getPackagingOption(), request::setPackaging);
		applyConfiguredValue(options, this.options.getLanguageOption(), request::setLanguage);
		applyConfiguredValue(options, this.options.getGroupIdOption(), request::setGroupId);
		applyConfiguredValue(options, this.options.getArtifactIdOption(), request::setArtifactId);
		applyConfiguredValue(options, this.options.getNameOption(), request::setName);
		applyConfiguredValue(options, this.options.getVersionOption(), request::setVersion);
		applyConfiguredValue(options, this.options.getDescriptionOption(), request::setDescription);
		request.setExtract(options.has(this.options.getExtractOption()));
		if (nonOptionArguments.size() == 1) {
			String output = (String) nonOptionArguments.get(0);
			request.setOutput(output);
		}
		return request;
	}

	private void applyConfiguredDependencies(OptionSet options, ProjectGenerationRequest request) {
		OptionSpec<String> dependencies = this.options.getDependenciesOption();
		if (!options.has(dependencies)) {
			return;
		}
		for (String dependency : options.valueOf(dependencies).split(",")) {
			request.getDependencies().add(dependency.trim());
		}
	}

	private void applyConfiguredValue(OptionSet options, OptionSpec<String> option, Consumer<String> setter) {
		if (options.has(option)) {
			setter.accept(options.valueOf(option));
		}
	}

}
