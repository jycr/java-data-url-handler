[![License MIT](https://img.shields.io/github/license/jycr/java-data-url-handler)](https://opensource.org/license/mit)
[![Publish package to the Maven Central Repository](https://github.com/jycr/java-data-url-handler/actions/workflows/publish_to_maven_central.yml/badge.svg)](https://github.com/jycr/java-data-url-handler/actions/workflows/publish_to_maven_central.yml)
[![Version on Maven Central](https://img.shields.io/maven-central/v/io.github.jycr/java-data-url-handler)](https://search.maven.org/artifact/io.github.jycr/java-data-url-handler)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jycr_java-data-url-handler&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jycr_java-data-url-handler)

# java-data-url-handler

When adding this library as a dependency in your project, you will be able to handle "data URLs" ([RFC-2397](https://datatracker.ietf.org/doc/html/rfc2397)) in your application.

## Prerequisites

You need Java >= 11 to use this library.

## Usage

When you want to get the content of a "data URL":

```java
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class Test {
	public static void main(String[] args) throws IOException {
        URL url = URI.create("data:text/plain,Hello%2C%20World!").toURL();
        try(InputStream in = url.openStream()) {
            System.out.println(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }
    }
}
```

Without this library, you will get an error like this:

```
Exception in thread "main" java.net.MalformedURLException: unknown protocol: data
	at java.base/java.net.URL.<init>(URL.java:779)
	at java.base/java.net.URL.<init>(URL.java:654)
	at java.base/java.net.URL.<init>(URL.java:590)
```

With this library in your classpath, you can handle the data URL and get the content of the data URL.

The output of the code above will be:

```
Hello, World!
```
