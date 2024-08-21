package io.github.jycr.javadataurlhandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class DataUriHandler extends URLStreamHandler {
	@Override
	protected URLConnection openConnection(final URL url) throws MalformedURLException {
		return new DataUriConnection(url);
	}
}