package com.jwm.stockwatch.fetcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jwm.stockwatch.domain.UnitPrice;

/**
 * A fetcher fetches a UnitPrice from some source
 * @author Jeff
 *
 */
public abstract class WebFetcher {
	private static Logger log = LogManager.getLogger(WebFetcher.class);

	/**
	 * Fetch the current unit price
	 * @return
	 */
	public UnitPrice fetchPortfolioPrice() {

		try {
			Document doc = Jsoup.connect(getPageUrl()).timeout(5000).get();
			return parseWebPage(doc);
		} catch (Exception ex) {
			log.error(ex);
			return null;
		}
	}

	/**
	 * Parse the web page.
	 * @param doc
	 * @return
	 */
	protected abstract UnitPrice parseWebPage(Document doc);
	
	/**
	 * Return the particular web page url
	 * @return
	 */
	protected abstract String getPageUrl();

}
