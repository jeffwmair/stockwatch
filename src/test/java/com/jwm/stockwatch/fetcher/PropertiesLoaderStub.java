package com.jwm.stockwatch.fetcher;

import java.util.Properties;

import com.jwm.stockwatch.PropertiesLoader;

public class PropertiesLoaderStub implements PropertiesLoader {

	private Properties props;

	public PropertiesLoaderStub(Properties props) {
		this.props = props;
	}

	@Override
	public Properties getProperties() {
		return props;
	}

}
