/*******************************************************************************
 * Copyright 2013 André Rouél
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sf.uadetector.json.internal.data;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;

import net.sf.qualitycheck.exception.IllegalNullArgumentException;
import net.sf.uadetector.datastore.DataStore;
import net.sf.uadetector.internal.data.Data;
import net.sf.uadetector.internal.util.UrlUtil;
import net.sf.uadetector.json.SerDeOption;
import net.sf.uadetector.json.internal.data.deserializer.Deserialization;

import static org.fest.assertions.Assertions.assertThat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonConverterTest {

	private static final Logger LOG = LoggerFactory.getLogger(JsonConverterTest.class);

	@Test
	public void deserialize_corruptHash_doNotIgnore() throws IOException {
		final URL dataUrl = JsonConverterTest.class.getClassLoader().getResource("samples/uas_corrupt_hash.json");
		final Deserialization<Data> deserialization = JsonConverter.deserialize(UrlUtil.read(dataUrl, DataStore.DEFAULT_CHARSET),
				SerDeOption.HASH_VALIDATING);
		for (String warn : deserialization.getWarnings()) {
			LOG.debug(warn);
		}
		assertThat(deserialization.getWarnings()).hasSize(12);
		assertThat(deserialization.getData()).isNotSameAs(Data.EMPTY);
	}

	@Test
	public void deserialize_corruptHash_ignoreHash() throws IOException {
		final URL dataUrl = JsonConverterTest.class.getClassLoader().getResource("samples/uas_corrupt_hash.json");
		final Deserialization<Data> deserialization = JsonConverter.deserialize(UrlUtil.read(dataUrl, DataStore.DEFAULT_CHARSET));
		for (String warn : deserialization.getWarnings()) {
			LOG.debug(warn);
		}
		assertThat(deserialization.getWarnings()).hasSize(5);
		assertThat(deserialization.getData()).isNotSameAs(Data.EMPTY);
	}

	@Test
	public void deserialize_dirtyData() throws IOException {
		final URL dataUrl = JsonConverterTest.class.getClassLoader().getResource("samples/uas_dirty.json");
		final Deserialization<Data> deserialization = JsonConverter.deserialize(UrlUtil.read(dataUrl, DataStore.DEFAULT_CHARSET),
				SerDeOption.HASH_VALIDATING);
		for (final String warning : deserialization.getWarnings()) {
			LOG.debug(warning);
		}
		assertThat(deserialization.getWarnings()).isEmpty();
		assertThat(deserialization.getData()).isNotSameAs(Data.EMPTY);
	}

	@Test(expected = IllegalNullArgumentException.class)
	public void deserialize_withNullData() {
		JsonConverter.deserialize(null);
	}

	@Test
	public void giveMeCoverageForMyPrivateConstructor() throws Exception {
		// reduces only some noise in coverage report
		final Constructor<JsonConverter> constructor = JsonConverter.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test(expected = IllegalNullArgumentException.class)
	public void serialize_withNullData() {
		JsonConverter.serialize(null);
	}

	@Test
	public void serialize_withNullOptions() {
		final SerDeOption[] options = null;
		JsonConverter.serialize(Data.EMPTY, options);
	}

}
