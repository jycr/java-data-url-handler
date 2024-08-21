package io.github.jycr.javadataurlhandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * The data scheme URLConnection.
 * <p>The data URI scheme Data protocol Syntax:</p>
 * <pre>data:[<mediatype>][;base64],<data></pre>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc2397#section-2">RFC-2397</a>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/Data_URLs">mdn web docs - Data URLs</a>
 */
public class DataUriConnection extends URLConnection {

	/**
	 * Syntax of data URL scheme:
	 * <pre>
	 * dataurl    := "data:" [ mediatype ] [ ";base64" ] "," data
	 * mediatype  := [ type "/" subtype ] *( ";" parameter )
	 * data       := *urlchar
	 * parameter  := attribute "=" value
	 * </pre>
	 */
	private static final Pattern DATA_URL_SCHEME_PATTERN = Pattern.compile("data:(<mediatype>?(<contentType>?.*?/.*?)?(?:;(<paramKey>?.*?)=(<paramValue>?.*?))?)(?:;(<base64Flag>?base64)?)?,(<data>?.*)");

	private static final Charset DEFAULT_CONTENT_CHARSET = US_ASCII;
	/**
	 * Default mime type for data protocol.
	 * See: <a href="https://www.rfc-editor.org/rfc/rfc2397#section-2">RFC-2397 - Description</a>
	 */
	private static final String DEFAULT_MEDIATYPE = "text/plain;charset=" + DEFAULT_CONTENT_CHARSET.name();

	private final boolean valid;
	private final Charset charset;
	private final boolean isBase64;
	private final String data;
	private final String mediatype;

	public DataUriConnection(final URL url) throws MalformedURLException {
		super(url);
		final Matcher matcher = DATA_URL_SCHEME_PATTERN.matcher(url.toString());
		this.valid = matcher.matches();
		if (!this.valid) {
			throw new MalformedURLException("Invalid data URL: " + url);
		}
		this.data = matcher.group("data");

		String mediatypeGroup = matcher.group("mediatype");
		this.mediatype = (mediatypeGroup != null && !mediatypeGroup.isEmpty()) ? mediatypeGroup : DEFAULT_MEDIATYPE;
		this.isBase64 = "base64".equals(matcher.group("base64Flag"));

		String paramKey = matcher.group("paramKey");
		String paramValue = matcher.group("paramValue");
		this.charset = "charset".equals(paramKey) ? Charset.forName(paramValue) : DEFAULT_CONTENT_CHARSET;
	}

	@Override
	public void connect() {
		if (this.valid) {
			this.connected = true;
		}
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (!connected) {
			throw new IOException();
		}
		return new ByteArrayInputStream(getData());
	}

	/**
	 * <p>Returns the value of the content-type defined in data URL.</p>
	 * <p>This value is optional and if not defined, value is <code>{@value #DEFAULT_MEDIATYPE}</code></p>
	 */
	@Override
	public String getContentType() {
		if (!connected) {
			return null;
		}
		return mediatype;
	}

	private byte[] getData() throws UnsupportedEncodingException {
		if (isBase64) {
			return Base64.getDecoder().decode(data);
		}
		return URLDecoder.decode(data, charset).getBytes(data);
	}
}