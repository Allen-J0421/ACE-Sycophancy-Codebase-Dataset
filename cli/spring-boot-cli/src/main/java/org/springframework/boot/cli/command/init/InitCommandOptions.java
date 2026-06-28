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
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.springframework.boot.cli.command.options.OptionHandler;
import org.springframework.util.Assert;

/**
 * Defines the command-line options supported by {@link InitCommand} and translates the
 * parsed options into a {@link ProjectGenerationRequest}.
 *
 * @author Stephane Nicoll
 * @author Eddú Meléndez
 * @author Vignesh Thangavel Ilangovan
 */
class InitCommandOptions {

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> target;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<Void> listCapabilities;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> groupId;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> artifactId;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> version;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> name;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> description;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> packageName;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> type;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> packaging;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> build;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> format;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> javaVersion;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> language;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> bootVersion;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<String> dependencies;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<Void> extract;

	@SuppressWarnings("NullAway.Init")
	private OptionSpec<Void> force;

	void configure(OptionHandler optionHandler) {
		this.target = requiredOption(optionHandler, Arrays.asList("target"), "URL of the service to use")
			.defaultsTo(ProjectGenerationRequest.DEFAULT_SERVICE_URL);
		this.listCapabilities = flagOption(optionHandler, Arrays.asList("list"),
				"List the capabilities of the service. Use it to discover the dependencies and the types that are available");
		projectGenerationOptions(optionHandler);
		otherOptions(optionHandler);
	}

	boolean isListCapabilities(OptionSet options) {
		return options.has(this.listCapabilities);
	}

	String getTarget(OptionSet options) {
		return options.valueOf(this.target);
	}

	boolean isForce(OptionSet options) {
		return options.has(this.force);
	}

	ProjectGenerationRequest createProjectGenerationRequest(OptionSet options) {
		List<?> nonOptionArguments = new ArrayList<>(options.nonOptionArguments());
		Assert.state(nonOptionArguments.size() <= 1, "Only the target location may be specified");
		ProjectGenerationRequest request = new ProjectGenerationRequest();
		request.setServiceUrl(options.valueOf(this.target));
		applyConfiguredValue(options, this.bootVersion, request::setBootVersion);
		applyConfiguredDependencies(options, request);
		applyConfiguredValue(options, this.javaVersion, request::setJavaVersion);
		applyConfiguredValue(options, this.packageName, request::setPackageName);
		request.setBuild(options.valueOf(this.build));
		request.setFormat(options.valueOf(this.format));
		request.setDetectType(options.has(this.build) || options.has(this.format));
		applyConfiguredValue(options, this.type, request::setType);
		applyConfiguredValue(options, this.packaging, request::setPackaging);
		applyConfiguredValue(options, this.language, request::setLanguage);
		applyConfiguredValue(options, this.groupId, request::setGroupId);
		applyConfiguredValue(options, this.artifactId, request::setArtifactId);
		applyConfiguredValue(options, this.name, request::setName);
		applyConfiguredValue(options, this.version, request::setVersion);
		applyConfiguredValue(options, this.description, request::setDescription);
		request.setExtract(options.has(this.extract));
		if (nonOptionArguments.size() == 1) {
			String output = (String) nonOptionArguments.get(0);
			request.setOutput(output);
		}
		return request;
	}

	private void projectGenerationOptions(OptionHandler optionHandler) {
		this.groupId = requiredOption(optionHandler, Arrays.asList("group-id", "g"),
				"Project coordinates (for example 'org.test')");
		this.artifactId = requiredOption(optionHandler, Arrays.asList("artifact-id", "a"),
				"Project coordinates; infer archive name (for example 'test')");
		this.version = requiredOption(optionHandler, Arrays.asList("version", "v"),
				"Project version (for example '0.0.1-SNAPSHOT')");
		this.name = requiredOption(optionHandler, Arrays.asList("name", "n"), "Project name; infer application name");
		this.description = requiredOption(optionHandler, "description", "Project description");
		this.packageName = requiredOption(optionHandler, Arrays.asList("package-name"), "Package name");
		this.type = requiredOption(optionHandler, Arrays.asList("type", "t"),
				"Project type. Not normally needed if you use --build and/or --format. "
						+ "Check the capabilities of the service (--list) for more details");
		this.packaging = requiredOption(optionHandler, Arrays.asList("packaging", "p"),
				"Project packaging (for example 'jar')");
		this.build = requiredOption(optionHandler, "build", "Build system to use (for example 'maven' or 'gradle')")
			.defaultsTo("gradle");
		this.format = requiredOption(optionHandler, "format",
				"Format of the generated content (for example 'build' for a build file, 'project' for a project archive)")
			.defaultsTo("project");
		this.javaVersion = requiredOption(optionHandler, Arrays.asList("java-version", "j"),
				"Language level (for example '1.8')");
		this.language = requiredOption(optionHandler, Arrays.asList("language", "l"),
				"Programming language  (for example 'java')");
		this.bootVersion = requiredOption(optionHandler, Arrays.asList("boot-version", "b"),
				"Spring Boot version (for example '1.2.0.RELEASE')");
		this.dependencies = requiredOption(optionHandler, Arrays.asList("dependencies", "d"),
				"Comma-separated list of dependency identifiers to include in the generated project");
	}

	private void otherOptions(OptionHandler optionHandler) {
		this.extract = flagOption(optionHandler, Arrays.asList("extract", "x"),
				"Extract the project archive. Inferred if a location is specified without an extension");
		this.force = flagOption(optionHandler, Arrays.asList("force", "f"), "Force overwrite of existing files");
	}

	private void applyConfiguredDependencies(OptionSet options, ProjectGenerationRequest request) {
		if (!options.has(this.dependencies)) {
			return;
		}
		for (String dependency : options.valueOf(this.dependencies).split(",")) {
			request.getDependencies().add(dependency.trim());
		}
	}

	private void applyConfiguredValue(OptionSet options, OptionSpec<String> option, Consumer<String> setter) {
		if (options.has(option)) {
			setter.accept(options.valueOf(option));
		}
	}

	private ArgumentAcceptingOptionSpec<String> requiredOption(OptionHandler optionHandler, List<String> names,
			String description) {
		return optionHandler.option(names, description).withRequiredArg();
	}

	private ArgumentAcceptingOptionSpec<String> requiredOption(OptionHandler optionHandler, String name,
			String description) {
		return optionHandler.option(name, description).withRequiredArg();
	}

	private OptionSpec<Void> flagOption(OptionHandler optionHandler, List<String> names, String description) {
		return optionHandler.option(names, description);
	}

}
