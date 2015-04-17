package com.jwm.stockwatch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoaderImpl implements PropertiesLoader {

	private Properties props = null;

	public PropertiesLoaderImpl(String propertiesPath) throws IOException {
		InputStream inputProps = new FileInputStream(propertiesPath);
		props = new Properties();
		props.load(inputProps);
	}

	public Properties getProperties() {
		return props;
	}

}
