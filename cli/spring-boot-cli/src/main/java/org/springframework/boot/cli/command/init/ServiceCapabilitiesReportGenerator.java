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

/**
 * A helper class generating a report from the meta-data of a particular service.
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 */
class ServiceCapabilitiesReportGenerator {

	private final InitializrService initializrService;

	private final ServiceCapabilitiesReportFormatter formatter;

	/**
	 * Creates an instance using the specified {@link InitializrService}.
	 * @param initializrService the initializr service
	 */
	ServiceCapabilitiesReportGenerator(InitializrService initializrService) {
		this.initializrService = initializrService;
		this.formatter = new ServiceCapabilitiesReportFormatter();
	}

	/**
	 * Generate a report for the specified service. The report contains the available
	 * capabilities as advertised by the root endpoint.
	 * @param url the url of the service
	 * @return the report that describes the service
	 * @throws IOException if the report cannot be generated
	 */
	String generate(String url) throws IOException {
		Object content = this.initializrService.loadServiceCapabilities(url);
		if (content instanceof InitializrServiceMetadata metadata) {
			return this.formatter.format(url, metadata);
		}
		return content.toString();
	}

}
