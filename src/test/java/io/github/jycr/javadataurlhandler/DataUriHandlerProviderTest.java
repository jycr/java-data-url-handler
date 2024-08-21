package io.github.jycr.javadataurlhandler;

import java.net.spi.URLStreamHandlerProvider;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataUriHandlerProviderTest {

	@Test
	void testCreateURLStreamHandler() {
		URLStreamHandlerProvider provider = new DataUriHandlerProvider();
		assertThat(provider.createURLStreamHandler("data")).isNotNull();
		assertThat(provider.createURLStreamHandler("foo")).isNull();
		assertThat(provider.createURLStreamHandler("http")).isNull();
	}
}