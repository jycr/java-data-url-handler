package io.github.jycr.javadataurlhandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataUriConnectionTest {
	@Test
	void testMinimal() throws IOException {
		assertDataUriAsString(
				"data:,A%20brief%20note",
				"text/plain;charset=US-ASCII",
				StandardCharsets.US_ASCII,
				12
		)
				.isEqualTo("A brief note");
	}

	@Test
	void testImage() throws IOException {
		DataUriConnection connection = assertDataUri(
				"data:image/gif;base64,R0lGODdhMAAwAPAAAAAAAP///ywAAAAAMAAwAAAC8IyPqcvt3wCcDkiLc7C0qwyGHhSWpjQu5yqmCYsapyuvUUlvONmOZtfzgFzByTB10QgxOR0TqBQejhRNzOfkVJ+5YiUqrXF5Y5lKh/DeuNcP5yLWGsEbtLiOSpa/TPg7JpJHxyendzWTBfX0cxOnKPjgBzi4diinWGdkF8kjdfnycQZXZeYGejmJlZeGl9i2icVqaNVailT6F5iJ90m6mvuTS4OK05M0vDk0Q4XUtwvKOzrcd3iq9uisF81M1OIcR7lEewwcLp7tuNNkM3uNna3F2JQFo97Vriy/Xl4/f1cf5VWzXyym7PHhhx4dbgYKAAA7",
				"image/gif",
				StandardCharsets.US_ASCII,
				273
		);
		assertThat(connection.getInputStream().readAllBytes())
				.isEqualTo(new byte[]{
						71, 73, 70, 56, 55, 97, 48, 0, 48, 0, -16, 0, 0, 0, 0, 0, -1, -1, -1, 44, 0, 0, 0, 0, 48, 0, 48, 0, 0, 2, -16, -116, -113, -87, -53,
						-19, -33, 0, -100, 14, 72, -117, 115, -80, -76, -85, 12, -122, 30, 20, -106, -90, 52, 46, -25, 42, -90, 9, -117, 26, -89, 43, -81, 81,
						73, 111, 56, -39, -114, 102, -41, -13, -128, 92, -63, -55, 48, 117, -47, 8, 49, 57, 29, 19, -88, 20, 30, -114, 20, 77, -52, -25, -28,
						84, -97, -71, 98, 37, 42, -83, 113, 121, 99, -103, 74, -121, -16, -34, -72, -41, 15, -25, 34, -42, 26, -63, 27, -76, -72, -114, 74,
						-106, -65, 76, -8, 59, 38, -110, 71, -57, 39, -89, 119, 53, -109, 5, -11, -12, 115, 19, -89, 40, -8, -32, 7, 56, -72, 118, 40, -89,
						88, 103, 100, 23, -55, 35, 117, -7, -14, 113, 6, 87, 101, -26, 6, 122, 57, -119, -107, -105, -122, -105, -40, -74, -119, -59, 106,
						104, -43, 90, -118, 84, -6, 23, -104, -119, -9, 73, -70, -102, -5, -109, 75, -125, -118, -45, -109, 52, -68, 57, 52, 67, -123, -44,
						-73, 11, -54, 59, 58, -36, 119, 120, -86, -10, -24, -84, 23, -51, 76, -44, -30, 28, 71, -71, 68, 123, 12, 28, 46, -98, -19, -72, -45,
						100, 51, 123, -115, -99, -83, -59, -40, -108, 5, -93, -34, -43, -82, 44, -65, 94, 94, 63, 127, 87, 31, -27, 85, -77, 95, 44, -90, -20,
						-15, -31, -121, 30, 29, 110, 6, 10, 0, 0, 59
				});
	}

	@Test
	void testGreekCharaters() throws IOException {
		assertDataUriAsString(
				"data:text/plain;charset=iso-8859-7,%D6%EF%E9%ED%E9%EA%DE%E9%E1+%E3%F1%DC%EC%EC%E1%F4%E1",
				"text/plain;charset=ISO-8859-7",
				Charset.forName("ISO-8859-7"),
				18
		)
				.isEqualTo("Φοινικήια γράμματα");
	}

	@Test
	void testApplicationData() throws IOException {
		assertDataUriAsString(
				"data:application/vnd-xxx-query,select_vcount,fcol_from_fieldtable/local",
				"application/vnd-xxx-query",
				StandardCharsets.US_ASCII,
				40
		)
				.isEqualTo("select_vcount,fcol_from_fieldtable/local");
	}

	@Test
	void testPlainText() throws IOException {
		assertDataUriAsString(
				"data:text/plain,Hello%2C%20World!",
				"text/plain;charset=US-ASCII",
				StandardCharsets.US_ASCII,
				13
		)
				.isEqualTo("Hello, World!");
	}

	private AbstractObjectAssert<?, String> assertDataUriAsString(
			String dataUri,
			String expectedContentType,
			Charset expectedCharset,
			long expectedContentLength
	) throws IOException {
		DataUriConnection connection = assertDataUri(dataUri, expectedContentType, expectedCharset, expectedContentLength);

		return assertThat(connection.getContent())
				.describedAs("Content")
				.isInstanceOf(InputStream.class)
				.extracting(content -> {
					try {
						return ((InputStream) content).readAllBytes();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				})
				.extracting(content -> new String(content, expectedCharset))
				;
	}


	private DataUriConnection assertDataUri(
			String dataUri,
			String expectedContentType,
			Charset expectedCharset,
			long expectedContentLength
	) throws IOException {
		DataUriConnection connection = new DataUriConnection(URI.create(dataUri).toURL());
		connection.connect();
		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(connection.getContentType())
		          .describedAs("Content type")
		          .isEqualTo(expectedContentType);
		assertions.assertThat(connection.getCharset())
		          .describedAs("Charset")
		          .isEqualTo(expectedCharset);
		assertions.assertThat(connection.getContentLengthLong())
		          .describedAs("Content length")
		          .isEqualTo(expectedContentLength);
		assertions.assertThat(connection.getContentEncoding())
		          .describedAs("Content encoding")
		          .isNull();
		assertions.assertAll();

		return connection;
	}
}