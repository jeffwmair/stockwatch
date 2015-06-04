package com.jwm.stockwatch.fetcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jwm.stockwatch.PropertiesLoader;
import com.jwm.stockwatch.domain.UnitPrice;

/**
 * The numbers on Google Finance are usually about 2-3 days behind :(
 * @param propLoader
 */
public class FetcherFromGoogleFinanceImpl extends WebFetcher {

	private static Logger log = LogManager.getLogger(FetcherFromGoogleFinanceImpl.class);
	private String pageUrl;

	public FetcherFromGoogleFinanceImpl(PropertiesLoader propLoader) {
		pageUrl = "https://www.google.com/finance?q=" + propLoader.getProperties().getProperty("unitname_googlefinance");
	}

	private String getElementContentByPropName(Elements els, String attributeValue) {
		Element foundElement = null;
		String keyAtt = "itemprop";
		for (Element element : els) {
			if (element.hasAttr(keyAtt) && element.attr(keyAtt).equals(attributeValue)) {
				foundElement = element;
			}
		}
		return foundElement.attr("content");
	}

	@Override
	protected UnitPrice parseWebPage(Document doc) {
		try {
			Element marketDataDiv = doc.getElementById("sharebox-data");
			log.debug(marketDataDiv);
			Elements metaEls = marketDataDiv.getElementsByTag("meta");
			String name = getElementContentByPropName(metaEls, "name");
			double price = Double.parseDouble(getElementContentByPropName(metaEls, "price"));
			double changeAmount = Double.parseDouble(getElementContentByPropName(metaEls, "priceChange"));
			double changePct = Double.parseDouble(getElementContentByPropName(metaEls, "priceChangePercent"));
			String date_s = getElementContentByPropName(metaEls, "quoteTime").replace("T", " ").replace("Z", "");
			DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date quoteDate = dateParser.parse(date_s);
			return new UnitPrice(name, quoteDate, price, changeAmount, changePct);
		} catch (Exception ex) {
			log.error(ex, ex);
			return null;
		}
	}

	@Override
	protected String getPageUrl() {
		return pageUrl;
	}
}
