package io.github.jycr.javadataurlhandler;

import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;

public class DataUriHandlerProvider extends URLStreamHandlerProvider {
    public static final String DATA_PROTOCOL = "data";

	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
        if(DATA_PROTOCOL.equals(protocol)){
            return new DataUriHandler();
        }
        return null;
	}
}