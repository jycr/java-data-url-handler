package io.github.jycr.javadataurlhandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * <p>The data scheme URLConnection.</p>
 * <p>Syntax of data URL scheme:</p>
 * <pre>
 * dataurl    := "data:" [ mediatype ] [ ";base64" ] "," data
 * mediatype  := [ type "/" subtype ] *( ";" parameter )
 * data       := *urlchar
 * parameter  := attribute "=" value
 * </pre>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc2397#section-2">RFC-2397</a>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/Data_URLs">mdn web docs - Data URLs</a>
 */
public class DataUriConnection extends URLConnection {

	private static final Charset DEFAULT_CONTENT_CHARSET = US_ASCII;
	private static final String DEFAULT_MEDIATYPE = "text/plain";

	private final Charset charset;
	private final boolean isBase64;
	private final String data;
	private final String contentType;

	public DataUriConnection(final URL url) throws MalformedURLException {
		super(url);
		String urlString = url.toString();
		if (!urlString.startsWith("data:")) {
			throw new MalformedURLException("Invalid data URL: " + url);
		}

		int commaIndex = urlString.indexOf(',');
		if (commaIndex == -1) {
			throw new MalformedURLException("Invalid data URL: " + url);
		}

		String metadata = urlString.substring(5, commaIndex);
		this.data = urlString.substring(commaIndex + 1);

		final String[] parts = metadata.split(";");
		final String mediatype = parts.length > 0 && !parts[0].isEmpty() ? parts[0] : DEFAULT_MEDIATYPE;

		boolean base64Flag = false;
		Charset extractedCharset = DEFAULT_CONTENT_CHARSET;
		for (String part : parts) {
			if ("base64".equals(part)) {
				base64Flag = true;
			} else if (part.startsWith("charset=")) {
				extractedCharset = Charset.forName(part.substring(8));
			}
		}
		this.isBase64 = base64Flag;
		this.charset = extractedCharset;
		this.contentType = mediatype + (isText(mediatype) ? ";charset=" + this.charset.name() : "");
		this.connected = true;
	}

	private static boolean isText(String mediatype) {
		return mediatype != null && (mediatype.startsWith("text/") || mediatype.endsWith("+xml"));
	}

	@Override
	public void connect() {
		this.connected = true;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(getData());
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	private byte[] getData() {
		return isBase64 ? Base64.getDecoder().decode(data) : URLDecoder.decode(data, charset).getBytes(charset);
	}

	Charset getCharset() {
		return charset;
	}

	@Override
	public String getHeaderField(String name) {
		if ("Content-Length".equalsIgnoreCase(name)) {
			return String.valueOf(getData().length);
		}
		return null;
	}
}