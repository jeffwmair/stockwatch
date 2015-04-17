package com.jwm.stockwatch.fetcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jwm.stockwatch.domain.PortfolioUnitPrice;

/**
 * A fetcher fetches a PortfolioUnitPrice from some source
 * 
 * @author Jeff
 *
 */
public abstract class WebFetcher {
	private static Logger log = LogManager.getLogger(WebFetcher.class);

	public PortfolioUnitPrice fetchPortfolioPrice() {

		try {
			Document doc = Jsoup.connect(getPageUrl()).timeout(5000).get();
			return parseWebPage(doc);
		} catch (Exception ex) {
			log.error(ex);
			return null;
		}

	}

	protected abstract PortfolioUnitPrice parseWebPage(Document doc);
	protected abstract String getPageUrl();

}
