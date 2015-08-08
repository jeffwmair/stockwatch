package com.jwm.stockwatch.reporting;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.domain.UnitPriceCollection;
import com.jwm.stockwatch.service.UnitPriceFileService;

public class ReportGeneratorConsoleImpl implements ReportGenerator {

	private static Logger log = LogManager.getLogger(ReportGeneratorConsoleImpl.class);
	private UnitPriceFileService service;
	private final String NewLine = System.getProperty("line.separator");

	public ReportGeneratorConsoleImpl(UnitPriceFileService service) {
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
		int chartDays = 120;
		String chartUrl = service.getRecentPriceChartUrl(chartDays);
		System.out.println("\n" + chartUrl);
	}

}
