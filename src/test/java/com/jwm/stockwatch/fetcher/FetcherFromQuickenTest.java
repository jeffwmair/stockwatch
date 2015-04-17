package com.jwm.stockwatch.fetcher;

import java.util.Properties;

import org.junit.Test;

import com.jwm.stockwatch.PropertiesLoader;
import com.jwm.stockwatch.domain.PortfolioUnitPrice;

public class FetcherFromQuickenTest {

	@Test
	public void basicTest() {
		Properties props = new Properties();
		props.put("unitname_quicken", "mutual-funds/MUTUAL%3ACIB837/CIBC-Managed-Balanced-Growth-Portfolio");
		PropertiesLoader propsLoader = new PropertiesLoaderStub(props);
		WebFetcher fetcher = new FetcherFromQuickenImpl(propsLoader);
		PortfolioUnitPrice foo = fetcher.fetchPortfolioPrice();
	}
}
