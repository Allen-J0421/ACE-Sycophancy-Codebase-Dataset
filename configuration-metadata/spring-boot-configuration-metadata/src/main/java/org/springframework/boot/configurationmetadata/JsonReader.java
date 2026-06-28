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

package org.springframework.boot.configurationmetadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.springframework.boot.configurationmetadata.json.JSONArray;
import org.springframework.boot.configurationmetadata.json.JSONObject;

/**
 * Read standard JSON metadata format as {@link ConfigurationMetadataRepository}.
 *
 * @author Stephane Nicoll
 */
class JsonReader {

	private static final int BUFFER_SIZE = 4096;

	private final SentenceExtractor sentenceExtractor = new SentenceExtractor();

	RawConfigurationMetadata read(InputStream in, Charset charset) throws IOException {
		try {
			JSONObject json = readJson(in, charset);
			List<ConfigurationMetadataSource> groups = parseAllSources(json);
			List<ConfigurationMetadataItem> items = parseAllItems(json);
			List<ConfigurationMetadataHint> hints = parseAllHints(json);
			return new RawConfigurationMetadata(groups, items, hints);
		}
		catch (Exception ex) {
			if (ex instanceof IOException ioException) {
				throw ioException;
			}
			if (ex instanceof RuntimeException runtimeException) {
				throw runtimeException;
			}
			throw new IllegalStateException(ex);
		}
	}

	private List<ConfigurationMetadataSource> parseAllSources(JSONObject root) throws Exception {
		return parseObjectArray(root, "groups", this::parseSource);
	}

	private List<ConfigurationMetadataItem> parseAllItems(JSONObject root) throws Exception {
		return parseObjectArray(root, "properties", this::parseItem);
	}

	private List<ConfigurationMetadataHint> parseAllHints(JSONObject root) throws Exception {
		return parseObjectArray(root, "hints", this::parseHint);
	}

	private ConfigurationMetadataSource parseSource(JSONObject json) throws Exception {
		ConfigurationMetadataSource source = new ConfigurationMetadataSource();
		source.setGroupId(json.getString("name"));
		source.setType(json.optString("type", null));
		String description = json.optString("description", null);
		source.setDescription(description);
		source.setShortDescription(this.sentenceExtractor.getFirstSentence(description));
		source.setSourceType(json.optString("sourceType", null));
		source.setSourceMethod(json.optString("sourceMethod", null));
		return source;
	}

	private ConfigurationMetadataItem parseItem(JSONObject json) throws Exception {
		ConfigurationMetadataItem item = new ConfigurationMetadataItem();
		item.setId(json.getString("name"));
		item.setType(json.optString("type", null));
		String description = json.optString("description", null);
		item.setDescription(description);
		item.setShortDescription(this.sentenceExtractor.getFirstSentence(description));
		item.setDefaultValue(readItemValue(json.opt("defaultValue")));
		item.setDeprecation(parseDeprecation(json));
		item.setSourceType(json.optString("sourceType", null));
		item.setSourceMethod(json.optString("sourceMethod", null));
		return item;
	}

	private ConfigurationMetadataHint parseHint(JSONObject json) throws Exception {
		ConfigurationMetadataHint hint = new ConfigurationMetadataHint();
		hint.setId(json.getString("name"));
		hint.getValueHints().addAll(parseObjectArray(json, "values", this::parseValueHint));
		hint.getValueProviders().addAll(parseObjectArray(json, "providers", this::parseValueProvider));
		return hint;
	}

	private ValueHint parseValueHint(JSONObject json) throws Exception {
		ValueHint valueHint = new ValueHint();
		valueHint.setValue(readItemValue(json.get("value")));
		String description = json.optString("description", null);
		valueHint.setDescription(description);
		valueHint.setShortDescription(this.sentenceExtractor.getFirstSentence(description));
		return valueHint;
	}

	private ValueProvider parseValueProvider(JSONObject json) throws Exception {
		ValueProvider valueProvider = new ValueProvider();
		valueProvider.setName(json.getString("name"));
		if (json.has("parameters")) {
			JSONObject parameters = json.getJSONObject("parameters");
			Iterator<?> keys = parameters.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				valueProvider.getParameters().put(key, readItemValue(parameters.get(key)));
			}
		}
		return valueProvider;
	}

	private Deprecation parseDeprecation(JSONObject object) throws Exception {
		if (object.has("deprecation")) {
			JSONObject deprecationJsonObject = object.getJSONObject("deprecation");
			Deprecation deprecation = new Deprecation();
			deprecation.setLevel(parseDeprecationLevel(deprecationJsonObject.optString("level", null)));
			String reason = deprecationJsonObject.optString("reason", null);
			deprecation.setReason(reason);
			deprecation.setShortReason(this.sentenceExtractor.getFirstSentence(reason));
			deprecation.setReplacement(deprecationJsonObject.optString("replacement", null));
			return deprecation;
		}
		return object.optBoolean("deprecated") ? new Deprecation() : null;
	}

	private Deprecation.Level parseDeprecationLevel(String value) {
		if (value != null) {
			try {
				return Deprecation.Level.valueOf(value.toUpperCase(Locale.ENGLISH));
			}
			catch (IllegalArgumentException ex) {
				// let's use the default
			}
		}
		return Deprecation.Level.WARNING;
	}

	private Object readItemValue(Object value) throws Exception {
		if (value instanceof JSONArray array) {
			Object[] content = new Object[array.length()];
			for (int i = 0; i < array.length(); i++) {
				content[i] = array.get(i);
			}
			return content;
		}
		return value;
	}

	private <T> List<T> parseObjectArray(JSONObject json, String name, ObjectParser<T> parser) throws Exception {
		List<T> result = new ArrayList<>();
		if (!json.has(name)) {
			return result;
		}
		JSONArray array = json.getJSONArray(name);
		for (int i = 0; i < array.length(); i++) {
			result.add(parser.parse(array.getJSONObject(i)));
		}
		return result;
	}

	private JSONObject readJson(InputStream in, Charset charset) throws Exception {
		StringBuilder out = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(in, charset);
		char[] buffer = new char[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = reader.read(buffer)) != -1) {
			out.append(buffer, 0, bytesRead);
		}
		return new JSONObject(out.toString());
	}

	private interface ObjectParser<T> {

		T parse(JSONObject json) throws Exception;

	}

}
