package com.jwm.stockwatch.reporting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.domain.UnitPriceCollection;
import com.jwm.stockwatch.service.UnitPriceService;

public class ReportGeneratorConsoleImpl implements ReportGenerator {

	private static Logger log = LogManager.getLogger(ReportGeneratorConsoleImpl.class);
	private UnitPriceService service;
	private final String NewLine = System.getProperty("line.separator");

	public ReportGeneratorConsoleImpl(UnitPriceService service) {
		this.service = service;
	}

	/**
	 * Generate a report of unit prices, write to the console
	 */
	@Override
	public void generateReport() {
		UnitPriceCollection prices = service.getSavedPrices();
		StringBuilder messages = new StringBuilder();
		messages.append("*** Report ***" + NewLine);

		int reportIntervalInDays = 10;
		double netPriceChange = service.getNetChangeOverLastN(reportIntervalInDays);
		for (UnitPrice price : prices.getPrices()) {
			messages.append("\tPrice:" + price.toString() + "; Emailed ? " + service.hasSentNotificationForPrice(price) + NewLine);
		}
		
		messages.append(String.format("Last 10 changes, net: $%.2f", netPriceChange));

		log.debug(messages);
		System.out.println(messages);
		String chartUrl = service.getRecentPriceChartUrl(30);
		System.out.println("\n" + chartUrl);
	}

}
