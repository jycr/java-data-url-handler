package io.github.jycr.javadataurlhandler;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataUriHandlerTest {

	@Test
	void testOpenConnection() throws MalformedURLException {
		// Given
		URL dataUrl = URI.create("data:text/plain,Hello%2C%20World!").toURL();
		DataUriHandler handler = new DataUriHandler();

		// When
		URLConnection urlConnection = handler.openConnection(dataUrl);

		// Then
		assertThat(urlConnection).isNotNull();
	}
}