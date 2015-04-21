package com.jwm.stockwatch.fetcher;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jwm.stockwatch.PropertiesLoader;
import com.jwm.stockwatch.domain.UnitPrice;

/**
 * The numbers on this site are typically updated everyday
 * 
 * @param propsLoader
 */
public class FetcherFromQuickenImpl extends WebFetcher {

	private static Logger log = LogManager.getLogger(FetcherFromQuickenImpl.class);
	private String pageUrl;

	public FetcherFromQuickenImpl(PropertiesLoader propsLoader) {
		pageUrl = "http://quicken.intuit.com/investing/" + propsLoader.getProperties().getProperty("unitname_quicken");
	}

	@Override
	protected UnitPrice parseWebPage(Document doc) {
		try {
			log.debug(doc);
			String title = doc.getElementsByTag("title").first().html();
			String marketDataDesc = doc.getElementById("quoteDisclaimer").html().replace("Market data as of ", "");
			marketDataDesc = marketDataDesc.substring(0, marketDataDesc.indexOf("."));
			SimpleDateFormat simpleDataFmt = new SimpleDateFormat("hh:mma MM/dd/yy");
			Date quoteDate = simpleDataFmt.parse(marketDataDesc);
			Element dataTable = doc.getElementById("researchIntroNumbers");
			Element tBody = dataTable.getElementsByTag("tbody").first();
			Elements rows = tBody.getElementsByTag("td");
			log.debug(rows.get(0).html());
			String price_s = rows.get(0).html();
			price_s = price_s.substring(0, price_s.indexOf(" "));
			double price = Double.parseDouble(price_s);
			String priceChangePctChange_s = rows.get(1).html();
			int price_spaceIndex = priceChangePctChange_s.indexOf(" ");
			String priceChange_s = priceChangePctChange_s.substring(0, price_spaceIndex);
			String pctChange_s = priceChangePctChange_s.substring(price_spaceIndex + 1);
			pctChange_s = pctChange_s.replace("(", "").replace(")", "").replace("%", "").trim();
			double priceChange = Double.parseDouble(priceChange_s);
			double pctChange = Double.parseDouble(pctChange_s);
			UnitPrice unitPrice = new UnitPrice(title, quoteDate, price, priceChange, pctChange);
			log.debug("Successful parse:" + unitPrice);
			return unitPrice;
		} catch (Exception ex) {
			log.error(ex);
			return null;
		}
	}

	@Override
	protected String getPageUrl() {
		return pageUrl;
	}

}
